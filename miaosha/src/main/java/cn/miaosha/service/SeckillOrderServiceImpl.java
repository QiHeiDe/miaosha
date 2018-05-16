package cn.miaosha.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import cn.miaosha.mapper.SeckillGoodsMapper;
import cn.miaosha.mapper.SeckillOrderMapper;
import cn.miaosha.pojo.IdRedis;
import cn.miaosha.pojo.SeckillGoods;
import cn.miaosha.pojo.SeckillOrder;
import cn.miaosha.pojo.SeckillOrderExample;
import cn.miaosha.pojo.SeckillOrderExample.Criteria;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
	@Autowired//秒杀订单mapper
	private SeckillOrderMapper seckillOrderMapper;
	@Autowired//商品mapper
	private SeckillGoodsMapper seckillGoodsMapper;
	@Autowired//redis模板
	private RedisTemplate redisTemplate;
	
	@Override
	public long submitOrder(Long seckillId,String userId) {
		//1:从缓存中查询秒杀商品
		SeckillGoods seckillGoods=(SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		if(seckillGoods==null){
			throw new RuntimeException("商品不存在！！！");
		}
		if(seckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已被抢空！！！");
		}
		//扣减(redis)库存
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		//放回缓存
		redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);
		//如果商品被秒光，同步数据库，删除redis缓存
		if(seckillGoods.getStockCount()==0){
			//同步数据库
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			//删除缓存
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
		}
		//保存订单到缓存
		SeckillOrder seckillOrder = new SeckillOrder();
		//订单id及缓存自增
		RedisAtomicLong redisAtomicLong = new RedisAtomicLong("seckillGoodsId", redisTemplate.getConnectionFactory());
		//获取redis自增
		long seckillOrderId = redisAtomicLong.getAndIncrement();
		seckillOrder.setId(seckillOrderId);//id
		seckillOrder.setCreateTime(new Date());//创建时间
		seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
		seckillOrder.setSeckillId(seckillId);//商品id
		//seckillOrder.setSellerId(seckillGoods.getSellerId());//商家id
		seckillOrder.setUserId(userId);//用户id
		seckillOrder.setStatus("0");//状态
		redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
		return seckillOrderId;
	}

	@Override
	public void saveOrderFromRedisToDb(String userId,String out_trade_no,String seckillGoodsId) {
		SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if(seckillOrder==null){
			throw new RuntimeException("订单不存在");
		}
		
		if(seckillOrder.getSeckillId().longValue()!=Long.parseLong(seckillGoodsId)){
			throw new RuntimeException("商品不相符");
		}
		//如果与传递过来的订单号不符 略
		seckillOrder.setPayTime(new Date());//支付时间
		seckillOrder.setStatus("1");//状态
		seckillOrderMapper.insert(seckillOrder);//保存到数据库
		redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除
	}
	//查询用户所有的已秒杀订单
	public List<SeckillOrder> findAllseckillOrder(String userId){
		
		SeckillOrderExample example = new SeckillOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<SeckillOrder> list = seckillOrderMapper.selectByExample(example );
		for (SeckillOrder seckillOrder : list) {
			SeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
			seckillOrder.setSellerId(seckillGoods.getTitle());//借用商家显示商品
		}
		
		return list;
	}

	//删除缓存中的订单，将缓存中商品的数量加1
	public void deleteOrderFromRedis(String userId, Long orderId) {
		//根据用户ID查询日志
		SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if(seckillOrder!=null && seckillOrder.getId() == orderId){
			redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除缓存中的订单
		}
		//恢复库存
		Long seckillId = seckillOrder.getSeckillId();//根据id找到商品缓存
		SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		if(seckillGoods!=null){
			seckillGoods.setStockCount(seckillGoods.getStockCount()+1);//数量加1
			 redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);
		}
	}


}

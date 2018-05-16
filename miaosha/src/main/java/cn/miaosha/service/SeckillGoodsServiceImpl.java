package cn.miaosha.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.miaosha.mapper.SeckillGoodsMapper;
import cn.miaosha.pojo.SeckillGoods;
import cn.miaosha.pojo.SeckillGoodsExample;
import cn.miaosha.pojo.SeckillGoodsExample.Criteria;
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
	
	@Autowired //引入商品mapper
	private SeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	//添加
	public void add(SeckillGoods seckillGoods) {
		seckillGoods.setCreateTime(new Date());
		seckillGoods.setStatus("0");
		seckillGoodsMapper.insert(seckillGoods);
		
	}
	//根据审核状态查询商品
	public List<SeckillGoods> findByStatus(String status) {
		List<SeckillGoods> list=null;
		if(status==null){//如果是null查所有
			list = seckillGoodsMapper.selectByExample(null);
		}else{
			SeckillGoodsExample example = new SeckillGoodsExample();
			Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo(status);
			list = seckillGoodsMapper.selectByExample(example );
		}
		
		return list;
	}
	
	//审核
	public void updateStatus(String status, Long id) {
		SeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
		seckillGoods.setCheckTime(new Date());
		seckillGoods.setStatus(status);
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
		redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
	}
	//查询需要显示的秒杀商品：状态为1，时间范围包含当前时间  
	//从缓存中获取
	public List<SeckillGoods> findSeckillGoods() {
		//查询缓存中的所有商品数据
		List<SeckillGoods> SeckillGoodsList  = redisTemplate.boundHashOps("seckillGoods").values();
		//如果不存在在数据库查找
		if(SeckillGoodsList==null || SeckillGoodsList.size()==0){
			SeckillGoodsExample example = new SeckillGoodsExample();
			Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");//条件1：审核通过
			criteria.andStockCountGreaterThan(0);//剩余库存大于0
			criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于且等于现在时间
			criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间
			SeckillGoodsList = seckillGoodsMapper.selectByExample(example);
			System.out.println("将秒杀商品列表装入缓存");
			//将商品存入缓存中
			for (SeckillGoods seckillGoods : SeckillGoodsList) {
				redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
			}
		}
		return SeckillGoodsList;
	}
	//详情页
	public SeckillGoods findOneFromRedis(Long id) {
		SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
		return seckillGoods;
	}

	



}

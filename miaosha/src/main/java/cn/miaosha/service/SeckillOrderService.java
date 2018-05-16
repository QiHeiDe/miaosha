package cn.miaosha.service;

import java.util.List;

import cn.miaosha.pojo.SeckillOrder;

public interface SeckillOrderService {

	public long submitOrder(Long seckillId,String userId);

	
	public List<SeckillOrder> findAllseckillOrder(String userId);

	public void saveOrderFromRedisToDb(String id, String out_trade_no, String seckillGoodsId);
	
	public void deleteOrderFromRedis(String userId, Long orderId);
	
	
}

package cn.miaosha.service;

import java.util.List;

import cn.miaosha.pojo.SeckillGoods;

public interface SeckillGoodsService {

	public void add(SeckillGoods seckillGoods);

	public List<SeckillGoods> findByStatus(String string);


	public void updateStatus(String status, Long id);

	public List<SeckillGoods> findSeckillGoods();

	public SeckillGoods findOneFromRedis(Long id);

	
}

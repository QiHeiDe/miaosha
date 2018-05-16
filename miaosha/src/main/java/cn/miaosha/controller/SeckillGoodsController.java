package cn.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.miaosha.pojo.SeckillGoods;
import cn.miaosha.service.SeckillGoodsService;

@Controller
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
	
	@Autowired //引入秒杀业务层
	private SeckillGoodsService seckillGoodsService;
	
	//页面跳转:添加页面
	@RequestMapping("/toAdd")
	public String toAdd(){
		return "seckillGoodsAdd";
	}
	//保存
	@RequestMapping("/add")
	public String add(SeckillGoods seckillGoods,HttpServletRequest request){
		seckillGoodsService.add(seckillGoods);
		
		return "forward:list.do";
	}
	//查询
	@RequestMapping("/list")
	public String findByStatus(Model model,String status){
		List<SeckillGoods> list = seckillGoodsService.findByStatus(status);
		
		model.addAttribute("list", list);
		return "goodsList";
	}
	//更改审核状态 ： 1：通过，2：未通过
	@RequestMapping("/updateStatus")
	public String updateStatus(String status,Long id){
		seckillGoodsService.updateStatus(status,id);
		return "forward:list.do";
	}
	
	
	//显示秒杀页面：显示所有审核通过，且是秒杀时间内的商品
	@RequestMapping("/findSeckillGoods")
	public String findSeckillGoods(Model model){
		List<SeckillGoods> list =seckillGoodsService.findSeckillGoods();
		model.addAttribute("list", list);
		return "seckillGoodsList";
	}
	//秒杀详情页
	@RequestMapping("/findOneFromRedis")
	public String findOneFromRedis(Model model,Long id){
		SeckillGoods seckillGoods= seckillGoodsService.findOneFromRedis(id);
		model.addAttribute("seckillGoods", seckillGoods);
		return "SeckillGoodsInfo";
	}
}

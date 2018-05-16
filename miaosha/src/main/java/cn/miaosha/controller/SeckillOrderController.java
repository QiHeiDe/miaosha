package cn.miaosha.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;

import cn.miaosha.pojo.SeckillGoods;
import cn.miaosha.pojo.SeckillOrder;
import cn.miaosha.pojo.User;
import cn.miaosha.service.SeckillGoodsService;
import cn.miaosha.service.SeckillOrderService;
import cn.miaosha.utils.HttpClient;
import cn.miaosha.utils.PayConfig;

@Controller
@RequestMapping("/seckillOrder")
public class SeckillOrderController {
	@Autowired
	private SeckillGoodsService seckillGoodsService;
	@Autowired
	private SeckillOrderService  seckillOrderService;
	@RequestMapping("/submitOrder")
	public String submitOrder(HttpServletRequest request,Long seckillId,Model model){
		User user = (User) request.getSession().getAttribute("longUser");
		if(user==null){//未登陆返回登陆页面
			return "login";
		}
		
		try {//将订单保存到redis
			 long seckillOrderId = seckillOrderService.submitOrder(seckillId,user.getId());
			SeckillGoods seckillGoods = seckillGoodsService.findOneFromRedis(seckillId);
			model.addAttribute("seckillGoods", seckillGoods);
			model.addAttribute("seckillOrderId", seckillOrderId);//缓存中订单的id
			return "pay";//成功跳转支付页面
		} catch (RuntimeException e) {//商品被抢空或者不存在
			e.printStackTrace();
			model.addAttribute("message", e.getMessage());
			return "errorSeckill";
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "秒杀失败！！");
			return "errorSeckill";
		}
	} 
	
	//生成二维码需要的url
	@RequestMapping("/createQRCode")
	@ResponseBody
	public String createQRCode(SeckillGoods seckillGoods,HttpServletResponse response){
		//1.创建发送的数据（XML）  
        Map<String,String> prarm=new HashMap<String, String>();  
        prarm.put("appid", PayConfig.appid);//公众账号ID  
        prarm.put("mch_id",PayConfig.partner);//商户号  
        prarm.put("nonce_str", WXPayUtil.generateNonceStr() );//随机字符串  
        prarm.put("body", seckillGoods.getTitle());//商品信息  
        prarm.put("out_trade_no", WXPayUtil.getCurrentTimestamp()+"");//订单号(后边查询会用)  
        prarm.put("total_fee", seckillGoods.getCostPrice()+"");//金额（分）  
        prarm.put("spbill_create_ip", "127.0.0.1");//终端IP  
        prarm.put("notify_url",PayConfig.notifyurl);//回调地址(不起作用，但是必须给)  
        prarm.put("product_id", seckillGoods.getId()+"");
        prarm.put("attach", seckillGoods.getId()+"");
        prarm.put("trade_type", "NATIVE");//扫码支付  
       
        //使用微信工具类生成xml
        try {
			String xmlParam = WXPayUtil.generateSignedXml(prarm, PayConfig.partnerkey);
			System.out.println("----请求的参数----");  
            System.out.println(xmlParam);  
            System.out.println("----------------");
            
          //2.通过httpClient向远端发送数据  
            String url="https://api.mch.weixin.qq.com/pay/unifiedorder";  
            HttpClient client=new HttpClient(url);  
            client.setHttps(true);  
            client.setXmlParam(xmlParam);  
            client.post();//发送请求  
            
            //3.获取返回结果（XML）并解析  
            String xmlResult = client.getContent();//请求的结果(XML)  
            System.out.println("----响应的结果-----");  
            System.out.println(xmlResult);  
            System.out.println("-----------------");
            
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);  
            Map<String,String> map=new HashMap<String, String>();
              
            if(mapResult.get("return_code").equals("SUCCESS")){//如果成功  
                map.put("code_url", mapResult.get("code_url"));//生成二维码的url  
                map.put("return_code", "SUCCESS");                
            }else{  
                map.put("code_url", "");  
                map.put("return_code", "ERROR");      
            }  
            map.put("out_trade_no", prarm.get("out_trade_no"));//订单号  
            String jsonString = JSON.toJSONString(map);
            return jsonString;
           /*  
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(jsonString); */ 
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
	}
	//查询状态
	@RequestMapping("PayStatusCheck")
	public String PayStatusCheck(HttpServletRequest request,HttpServletResponse response,
			String out_trade_no,String seckillOrderId,Model model){
		System.out.println("订单号："+out_trade_no);
		User user = (User) request.getSession().getAttribute("longUser");
		int x =0;
		while(true){
			Map<String,String> map = queryOrder(out_trade_no);
			
			System.out.println("查询响应的结果:"+map);
			if(map==null){//交易出错
				try {
					response.getWriter().println("交易失败！！");
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
			
			if("SUCCESS".equals(map.get("trade_state"))){
				//交易成功//保存订单
				try {
					
					
					seckillOrderService.saveOrderFromRedisToDb(user.getId(),out_trade_no,map.get("attach"));
					
					break;
				} catch (RuntimeException e) {
					try {
						response.getWriter().print(e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
			
			try {
				Thread.sleep(3000);//每3秒查询一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			x++;
			if(x>=20){//1分钟超时
				//1.调用微信的关闭订单接口
				Map<String,String> closeMap = closePay(out_trade_no);
				//2:判断是正常关闭还是超时关闭
				if("SUCCESS".equals(closeMap.get("return_code"))){//2.1正常关闭
					if("ORDERPAID".equals(closeMap.get("err_code"))){//支付成功,创建订单
						//订单已支付
						seckillOrderService.saveOrderFromRedisToDb(user.getId(),out_trade_no,map.get("attach"));
						break;
					}
				}
					//2.2如果是超时,取消订单
					
					seckillOrderService.deleteOrderFromRedis(user.getId(),Long.parseLong(seckillOrderId));
					model.addAttribute("message", "付款等待超时");
					return "errorSeckill";
			
		}
					
		}
		
		List<SeckillOrder> list = seckillOrderService.findAllseckillOrder(user.getId());
		model.addAttribute("list", list);
		return "seckillOrderList";
		
	}
	@RequestMapping("/findAllseckillOrder")
	public  String findAllseckillOrder(HttpServletRequest request,Model model){
		User user = (User) request.getSession().getAttribute("longUser");
		if(user==null){
			return "login";
		}
		List<SeckillOrder> list = seckillOrderService.findAllseckillOrder(user.getId());
		model.addAttribute("list", list);
		return "seckillOrderList";
	}
	
	
	//调用订单查询接口  
	private Map queryOrder(String out_trade_no){  
	      
	    Map param = new HashMap<String,String>();  
	    param.put("appid", PayConfig.appid);  
	    param.put("mch_id", PayConfig.partner);  
	    param.put("out_trade_no", out_trade_no);  
	    param.put("nonce_str", WXPayUtil.generateNonceStr());  
	    try {  
	        String xmlParam = WXPayUtil.generateSignedXml(param, PayConfig.partnerkey);  
	        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");  
	        httpClient.setHttps(true);  
	        httpClient.setXmlParam(xmlParam);  
	        httpClient.post();  
	        String resultMap = httpClient.getContent();  
	        Map<String, String> map = WXPayUtil.xmlToMap(resultMap);  
	        System.out.println(map);  
	        return map ;  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return null;  
	    } 
	}
	//关闭微信订单接口
	private Map<String,String> closePay(String out_trade_no){
		Map<String,String> param=new HashMap();
		param.put("appid", PayConfig.appid);//公众账号ID
		param.put("mch_id", PayConfig.partner);//商户号
		param.put("out_trade_no", out_trade_no);//订单号
		param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
		String url="https://api.mch.weixin.qq.com/pay/closeorder";
		try {
			String xmlParam = WXPayUtil.generateSignedXml(param, PayConfig.partnerkey);
			HttpClient client = new HttpClient(url);
			client.setHttps(true);
			client.setXmlParam(xmlParam);
			client.post();
			String result = client.getContent();
			Map<String, String> map = WXPayUtil.xmlToMap(result);
			System.out.println("关闭微信订单得到的数据："+map);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

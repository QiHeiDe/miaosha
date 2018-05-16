<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript">
	$(function(){
		//获取开始时间和结束时间
		var start = $("#start_time").val();
		var end = $("#end_time").val();
		//获取毫秒值
		var start_time =new Date(start).getTime();
		var end_time =new Date(end).getTime();
		//获取重开始到结束的秒数
		var allSecond = Math.floor((end_time-start_time)/1000);
		//var allSecond=10;测试
		var timeString;
		//每秒执行一次
		var time = setInterval(function(){
			allSecond = allSecond-1;//每秒钟减一
			//生成时分秒
			timeString = convertTimeString(allSecond);
			//赋值给页面
			$("#remainingTime").html(timeString);
			if(allSecond<=0){
				clearInterval(time);
				$("#remainingTime").html("秒杀时间已过");
			}
		},1000)
		
		//转换秒为   天小时分钟秒格式  XXX天 10:22:33
		convertTimeString = function(allsecond){
			var days = Math.floor(allsecond/(60*60*24));//天数
			var hours = Math.floor((allsecond-days*60*60*24)/(60*60));//小数数
			var minutes = Math.floor((allsecond - days*60*60*24 - hours*60*60)/60);//分钟数
			var seconds = allsecond - days*60*60*24 - hours*60*60 -minutes*60; //秒数
			var timeString = "";
			if(days > 0){
				timeString = days + "天 ";
			}
			return timeString + hours + ":" + minutes + ":" + seconds;
		}
	});
</script>
</head>
<body>
	
		<table border="1px" align="center">
			
			<tr>
				<th colspan="2">商品详情页<th>
				<input type="hidden" name="id" value="${seckillGoods.id }">
				<!-- 开始时间和结束时间 -->
				<input type="hidden" id="start_time"  name="startTime" value="${seckillGoods.startTime }">
				<input type="hidden" id="end_time"  name="endTime" value="${seckillGoods.endTime }">
			</tr>
			<tr>
				<th>商品标题</th>
					
				<td><input type="text" name="title" value="${seckillGoods.title }"/></td>
			</tr>
			<tr>
				<th>商城价格</th>
				<td><input type="text" name="price" value="${seckillGoods.price }"/></td>
			</tr>
			<tr>
				<th>秒杀价</th>
				<td><input type="text" name="costPrice" value="${seckillGoods.costPrice }"/></td>
			</tr>
			<tr>
				<th>介绍</th>
				<td><input type="text" name="introduction" value="${seckillGoods.introduction }"/></td>
			</tr>
			<tr>
				<th>剩余库存</th>
				<td><input type="text" name="stockCount" value="${seckillGoods.stockCount }"/></td>
			</tr>
			<tr>
				<th>剩余时间</th>
				<td>
				<h5><span id="remainingTime"></span></h5>
				</td>
			</tr>
			<tr>
				<th colspan="2">
				<a href="${pageContext.request.contextPath }/seckillOrder/submitOrder.do?seckillId=${seckillGoods.id}">秒杀下单</a>
				</th>
				
			</tr>
		</table>
	
</body>
</html>
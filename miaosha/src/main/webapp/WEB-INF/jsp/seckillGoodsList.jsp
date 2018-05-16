<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>秒杀页面</title>
</head>
<body>
	<table align="center" border="1px">
		
		<tr>
			<th>商品标题</th>
			<th>商城价格</th>
			<th>秒杀价</th>
			<th>已售</th>
			<th>剩余件数</th>
			<th>立即抢购</th>
			
		<tr>
		<c:forEach items="${list }" var="seckillGoods">
			
			<tr>
				
				<td>${seckillGoods.title }</td>
				<td>${seckillGoods.price }</td>
				<td>${seckillGoods.costPrice }</td>
				<td>
				<fmt:formatNumber type="number" value="${((seckillGoods.num-seckillGoods.stockCount)/seckillGoods.num*100) }" maxFractionDigits="0"/>%
				</td>
				<td>剩余 ${seckillGoods.stockCount } 件</td>
				<td><a href="${pageContext.request.contextPath }/seckillGoods/findOneFromRedis.do?id=${seckillGoods.id}">立即抢购</a></td>
				
			</tr>
			
		</c:forEach>
	
	</table>
	
</li>
</body>
</html>
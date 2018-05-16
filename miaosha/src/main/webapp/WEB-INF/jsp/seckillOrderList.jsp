<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table align="center" border="1px">
		
		<tr>
			<th>订单号</th>
			<th>商品名称</th>
			<th>秒杀价</th>
			<th>支付时间</th>
			
			
		<tr>
		<c:forEach items="${list}" var="seckillOrder">
			
			<tr>
				
				<td>${seckillOrder.id }</td>
				<td>${seckillOrder.sellerId }</td>
				<td>${seckillOrder.money }</td>
				<td>
				<fmt:formatDate value="${seckillOrder.payTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			
			</tr>
			
		</c:forEach>
	
	</table>
</body>
</html>
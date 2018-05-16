<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>秒杀商品审核列表</title>
</head>
<body>
	<table align="center" border="1px">
		
		<tr>
			<th>商品标题</th>
			<th>商城价格</th>
			<th>秒杀价</th>
			<th>开始时间</th>
			<th>结束时间</th>
			<th>秒杀商品数</th>
			<th>剩余库存数</th>
			<th>描述</th>
			<th>状态</th>
			<th>审核</th>
		<tr>
		<c:forEach items="${list }" var="seckillGoods">
			
			<tr>
				
				<td>${seckillGoods.title }</td>
				<td>${seckillGoods.price }</td>
				<td>${seckillGoods.costPrice }</td>
				<td>
					<fmt:formatDate value="${seckillGoods.startTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${seckillGoods.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>${seckillGoods.num }</td>
				<td>${seckillGoods.stockCount }</td>
				<td>${seckillGoods.introduction }</td>
				<td>
					<c:if test="${seckillGoods.status=='0' }">
						未审核
					</c:if>
					<c:if test="${seckillGoods.status=='1' }">
						审核通过
					</c:if>
					<c:if test="${seckillGoods.status=='2' }">
						审核未通过
					</c:if>
				</td>
				<td>
					<a href="${pageContext.request.contextPath }/seckillGoods/updateStatus.do?id=${seckillGoods.id }&status=1">通过</a><br>
					<a href="${pageContext.request.contextPath }/seckillGoods/updateStatus.do?id=${seckillGoods.id }&status=2">不合格</a>
				</td>
			</tr>
			
		</c:forEach>
		<tr>
			<td colspan="10">
				<a href="${pageContext.request.contextPath }/seckillGoods/list.do?status=0">查询未审核</a><br>
				<a href="${pageContext.request.contextPath }/seckillGoods/list.do?status=1">查询已审核</a><br>
				<a href="${pageContext.request.contextPath }/seckillGoods/list.do?status=2">查询未通过</a><br>
			</td>
		</tr>
	</table>	
</body>
</html>
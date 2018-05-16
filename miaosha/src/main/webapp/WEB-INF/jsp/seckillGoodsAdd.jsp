<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品添加</title>
</head>
<body>	
		<form action="${pageContext.request.contextPath }/seckillGoods/add.do" method="post">
		<table border="1px" align="center">
			
			<tr>
				<th colspan="2">秒杀商品添加<th>
			</tr>
			<tr>
				<th>商品标题</th>
				<td><input type="text" name="title"/></td>
			</tr>
			<tr>
				<th>商城价格</th>
				<td><input type="text" name="price"/></td>
			</tr>
			<tr>
				<th>秒杀价</th>
				<td><input type="text" name="costPrice"/></td>
			</tr>
			<tr>
				<th>开始时间</th>
				<td><input type="text" name="startTime"/></td>
			</tr>
			<tr>
				<th>结束时间</th>
				<td><input type="text" name="endTime"/></td>
			</tr>
			<tr>
				<th>秒杀商品数</th>
				<td><input type="text" name="num"/></td>
			</tr>
			<tr>
				<th>剩余库存数</th>
				<td><input type="text" name="stockCount"/></td>
			</tr>
			<tr>
				<th>描述</th>
				<td><input type="text" name="introduction"/></td>
			</tr>
			<tr>
				<th colspan="2"><input type="submit" value="提交"/></th>
				
			</tr>
		</table>
		</form>
</body>
</html>
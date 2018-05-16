<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信支付</title>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/qrcode.js"></script>
<script type="text/javascript">
	$(function(){
		var id = $("input[name='id']").val();
		var title = $("input[name='title']").val();
		var seckillOrderId = $("input[name='seckillOrderId']").val();
		$.post("${pageContext.request.contextPath }/seckillOrder/createQRCode.do",
				{"id":id,"title":title,"costPrice":1},
				function(data){
					var ewm = qrcode("10","H");//参数1: 二维码大小 2-10  参数2:容错级别
					ewm.addData(data.code_url);
					ewm.make();
					document.getElementById("qr").innerHTML=ewm.createImgTag();
					//显示订单号
					var out_trade_no  = data.out_trade_no;
					$("#out_trade_no").html(out_trade_no);//给页面赋值
					//查询订单状态
					checkPayStatus(out_trade_no,seckillOrderId);
		},"json");
		//监听交易状态
		checkPayStatus=function(out_trade_no){
			location.href="${pageContext.request.contextPath }/seckillOrder/PayStatusCheck.do?out_trade_no="+out_trade_no+"&seckillOrderId="+seckillOrderId;
		}
	});
</script>
</head>
<body>
	<h1>支付页面,后期改为二维码支付</h1>
	<div id="qr"></div>
	订单号：<span id="out_trade_no"></span>
	<input type="hidden" name="id" value="${seckillGoods.id }"/>
	<input type="hidden" name="title" value="${seckillGoods.title }"/>
	<input type="hidden" name="seckillOrderId" value="${seckillOrderId}"/>
	
			
			

</body>
</html>
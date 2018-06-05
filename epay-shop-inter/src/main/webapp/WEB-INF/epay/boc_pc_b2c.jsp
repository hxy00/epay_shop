<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<script LANGUAGE="JavaScript">
	function goBocnet(){
		document.form1.submit();
	}
</script>

<body onload="goBocnet();">
  <!--  发送网上银行支付订单（普通B2C商户）（RecvOrder.do） -->
	<form name="form1" method="post" action="${action}">
		<!-- 商户号 --><!-- 必填 -->
		<input type="hidden" name="merchantNo" value="${merchantNo}"><br/>
		<!-- 支付类型 --><!-- 必填 -->
		<input type="hidden" name="payType" value="${payType}"><br/>
		<!-- 商户订单号 --><!-- 必填 -->
		<input type="hidden" name="orderNo" value="${orderNo}"><br/>
		<!-- 订单币种 --><!-- 必填 -->
		<input type="hidden" name="curCode" value="${curCode}"><br/>
		<!-- 订单金额 --><!-- 必填 -->
		<input type="hidden" name="orderAmount" value="${orderAmount}"><br/>
		<!-- 订单时间 --><!-- 必填 -->
		<input type="hidden" name="orderTime" value="${orderTime}"><br/>
		<!-- 订单说明--><!-- 必填 -->
		<input type="hidden" name="orderNote" value="${orderNote}"><br/>
		<!-- 商户接收通知URL--><!-- 必填 -->
		<input type="hidden" name="orderUrl" value="${orderUrl}"><br/>
		<!-- 超时时间 --><!-- 选填 -->
		<input type="hidden" name="orderTimeoutDate" value="${orderTimeoutDate}"><br/>
		<!-- 商户获取的客户IP地址 --><!-- 选填 -->
		<input type="hidden" name="mchtCustIP" value="${mchtCustIP}"><br/>
		<!-- 签名数据 --><!-- 必填 -->
		<input type="hidden" name="signData" value="${signData}"><br/>
	</form>
</body>
</html>
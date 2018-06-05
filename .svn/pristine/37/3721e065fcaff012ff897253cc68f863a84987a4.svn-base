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
  <!-- 发送B2B订单支付请求(直付商户) （NB2BRecvOrder.do）-->
	<form name="form1" method="post" action="${action}">
		<!-- 商户号 --><!-- 必填 -->
		<input type="hidden" name="merchantNo" value="${merchantNo}"><br/>
		<!-- 版本号 --><!-- 必填 -->
		<input type="hidden" name="version" value="${version}"><br/>
		<!-- 交易码 --><!-- 必填 -->
		<input type="hidden" name="messageId" value="${messageId}"><br/>
		<!-- 签名格式 --><!-- 必填 -->
		<input type="hidden" name="security" value="${security}"><br/>
		<!-- 签名结果 --><!-- 必填 -->
		<input type="hidden" name="signature" value="${signature}"><br/>
		<!-- 请求报文内容--><!-- 必填 -->
		<input type="hidden" name="message" value="${message}"><br/>
	</form>
</body>
</html>
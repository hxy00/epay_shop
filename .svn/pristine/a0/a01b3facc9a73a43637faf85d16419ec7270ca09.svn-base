<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>正在为您准备数据</title>
    
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<style>
		input{
			width: 500px;
		}
		
		label{
			width: 140px;
			text-align: right;
			display: inline-block;
			font-weight: bold;
		}
		
		div{
			margin-top: 10px;
		}
		
		.popup{
			width: 100%;
			height: 100%;
			z-index: 99;
			position: fixed;
			top:0;
			left:0;
			text-align: center;
			margin-top: 20px;
		}
	</style>
	
  </head>
  
  <body onload="document.getElementById('alipaysubmit').submit();">
    <form id="alipaysubmit" name="alipaysubmit" action="${submitUrl }" method="get">
    	<div class="popup">
			数据正在加载中......
		</div>
    	<div style="display:none;">
    		<div>
    			<label>service:</label><input type="text" name="service" value="${service }" />
    		</div>
    		<div>
    			<label>partner:</label><input type="text" name="partner" value="${partner }" />
    		</div>    		
    		<div>
    			<label>_input_charset:</label><input type="text" name="_input_charset" value="${_input_charset }" />
    		</div>
    		<div>
    			<label>seller_email:</label><input type="text" name="seller_email" value="${seller_email }" />
    		</div>
    		<div>
    			<label>sign_type:</label><input type="text" name="sign_type" value="${sign_type }" />
    		</div>
    		<div>
    			<label>out_trade_no:</label><input type="text" name="out_trade_no" value="${out_trade_no }" />
    		</div>
    		<div>
    			<label>subject:</label><input type="text" name="subject" value="${subject }" />
    		</div>
    		<div>
    			<label>payment_type:</label><input type="text" name="payment_type" value="${payment_type }" />
    		</div>
    		<div>
    			<label>total_fee:</label><input type="text" name="total_fee" value="${total_fee }" />
    		</div>
    		<div>
    			<label>sign:</label><input type="text" name="sign" value="${sign }" />
    		</div>
    		<div>
    			<label>return_url:</label><input type="text" name="return_url" value="${return_url }" />
    		</div>
    		<div>
    			<label>
    				notify_url:
    			</label>
    			<input type="text" id="notify_url" name="notify_url" value="${notify_url }" />
    		</div>
    		<div>
    			<label>error_notify_url:</label><input type="text" name="error_notify_url" value="${error_notify_url }" />
    		</div>
    		<div>
    			<label>
    			</label>
    			<input type="submit" value="提交到支付宝"/>
    		</div>
    	</div>
    </form>
  </body>
</html>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=GBK"/>
    <base href="<%=basePath%>">
    
    <title>数据正在加载中</title>
    
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
  
  <body><!-- https://101.231.204.80:5000/gateway/api/frontTransReq.do -->
  <!-- https://gateway.95516.com/gateway/api/frontTransReq.do -->
    <form id="pay_form" name="pay_form" action="https://gateway.95516.com/gateway/api/frontTransReq.do" method="post">
    	<div style="display:none;">
	    	<div>
    			<label>
    				version:
    			</label>
    			<input type="text" id="version" name="version" value="${version }" />
    		</div>
    		<div>
    			<label>
    				encoding:
    			</label>
    			<input type="text" id="encoding" name="encoding" value="${encoding }" />
    		</div>
    		<div>
    			<label>
    				signMethod:
    			</label>
    			<input type="text" id="signMethod" name="signMethod" value="${signMethod }" />
    		</div>
    		<div>
    			<label>
    				txnType:
    			</label>
    			<input type="text" id="txnType" name="txnType" value="${txnType }" />
    		</div>
    		<div>
    			<label>
    				txnSubType:
    			</label>
    			<input type="text" id="txnSubType" name="txnSubType" value="${txnSubType }" />
    		</div>
    		<div>
    			<label>
    				bizType:
    			</label>
    			<input type="text" id="bizType" name="bizType" value="${bizType }" />
    		</div>
    		<div>
    			<label>
    				channelType:
    			</label>
    			<input type="text" id="channelType" name="channelType" value="${channelType }" />
    		</div>
    		<div>
    			<label>
    				merId:
    			</label>
    			<input type="text" id="merId" name="merId" value="${merId }" />
    		</div>
    		<div>
    			<label>
    				accessType:
    			</label>
    			<input type="text" id="accessType" name="accessType" value="${accessType }" />
    		</div>
    		<div>
    			<label>
    				orderId:
    			</label>
    			<input type="text" id="orderId" name="orderId" value="${orderId }" />
    		</div>
    		<div>
    			<label>
    				txnTime:
    			</label>
    			<input type="text" id="txnTime" name="txnTime" value="${txnTime }" />
    		</div>
    		<div>
    			<label>
    				currencyCode:
    			</label>
    			<input type="text" id="currencyCode" name="currencyCode" value="${currencyCode }" />
    		</div>
    		<div>
    			<label>
    				txnAmt:
    			</label>
    			<input type="text" id="txnAmt" name="txnAmt" value="${txnAmt }" />
    		</div>
    		<div>
    			<label>
    				frontUrl:
    			</label>
    			<input type="text" id="frontUrl" name="frontUrl" value="${frontUrl }" />
    		</div>
    		<div>
    			<label>
    				backUrl:
    			</label>
    			<input type="text" id="backUrl" name="backUrl" value="${backUrl }" />
    		</div>
    		<div>
    			<label>
    				certId:
    			</label>
    			<input type="text" id="certId" name="certId" value="${certId }" />
    		</div>
    		<div>
    			<label>
    				signature:
    			</label>
    			<input type="text" id="signature" name="signature" value="${signature }" />
    		</div>    		
    		<div>
    			<label>
    				accType:
    			</label>
    			<input type="text" id="accType" name="accType" value="${accType }" />
    		</div>
    	</div>
    </form>
  </body>
</html>

<script>
	document.getElementById("pay_form").submit();
</script>

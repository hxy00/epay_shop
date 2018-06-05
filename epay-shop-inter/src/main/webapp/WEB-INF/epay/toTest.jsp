<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	
  </head>
  
  <body>
    	<form id="frm" action="<%=path %>/UnionpayResp" method="post">
    		<input type="text" name="amount" value="1" />
    		<input type="text" name="orderId" value="CTYS201611180000002" />
    		<input type="text" name="sign" value="signDatea" />
    		<input type="text" name="interfaceName" value="interfaceName" />
    		<input type="text" name="status" value="1" />
    		<input type="text" name="respCode" value="00" /><br/>
    		<input type="submit" value="提交">
    	</form>
    	
  </body>
</html>

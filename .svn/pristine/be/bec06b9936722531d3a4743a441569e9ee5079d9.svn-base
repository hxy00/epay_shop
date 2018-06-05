<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/global.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>请先登录</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<%@ include file="/Include/easyui_head_index.jsp"%>
	
	<script>
		var checkInput = function(){
			if($("#userName").val() == ""){
				alert("请输入用户名！");
				return false;
			}
			
			if($("#pwd").val() == ""){
				alert("请输入密码！");
				return false;
			}
			return true;
		};
	</script>
	
  </head>
  
  <body>
  	<form action="${ctx}/epay/manager/loginSys" method="post" onsubmit="return checkInput();">
		<div>
			用户名：<input type="text" id="userName" name="userName"/>
		</div>
		<div>
			密码：<input type="password" id="pwd" name="pwd"/>
		</div>
		<div>
			<input type="submit" value="登录" id="btnLogin" />
		</div>
	</form>
  </body>
</html>

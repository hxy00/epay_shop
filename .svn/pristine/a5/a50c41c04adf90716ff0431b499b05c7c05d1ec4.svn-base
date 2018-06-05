<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>icbc</title>
<style>
	body{
		font-family: Verdana, Arial, Helvetica, sans-serif;
		font-size: 14px;
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
<body onload="posttran()">
		<FORM id=FORM1 name=FORM1 action="${wap_b2c_url}" method="post" >
		<div class="popup">
			数据正在加载中......
		</div>
			<INPUT ID="clientType" NAME="clientType" TYPE="Hidden" value="${clientType}" size="120">
			<font face='Arial' size='4' color='white' style="display:none;">商户订单数据签名页面</font>
			<table width="98%"  border="1" style="display:none;">
				<tr>
					<td width="9%">接口名称</td>
					<td width="91%"><INPUT ID="interfaceName" NAME="interfaceName" TYPE="text" value="${interfaceName}" size="120" ></td>
				</tr>
				<tr>
					<td width="9%">接口版本号</td>
					<td width="91%"><INPUT ID="interfaceVersion" NAME="interfaceVersion" TYPE="text" value="${interfaceVersion}" size="120"></td>
				</tr>
				<tr>
					<td width="9%">接口数据</td>
					<td width="91%"><textarea ID="tranData" name="tranData" cols="120" rows="5">${tranData}</textarea>
				</tr>
				<tr>
					<td width="9%">签名数据</td>
					<td width="91%"><INPUT ID="merSignMsg" NAME="merSignMsg" TYPE="text" size="120" value="${merSignMsg}">
				</tr>
				<tr>
					<td width="9%">证书数据</td>
					<td width="91%"><INPUT ID="merCert" NAME="merCert" TYPE="text" size="120" value="${merCert}">
				</tr>
			</table>
			<table style="display:none;">
				<tr>
					<td><INPUT TYPE="submit" value=" 提 交 订 单 "></td>
					<td><INPUT  type="button" value=" 返 回 修 改 " onClick="self.history.back();"></td>
				</tr>
			</table>
		</FORM>
</body>
</html>

<script type="text/javascript">
	function posttran(){
		document.getElementById('FORM1').submit();
	}
</script>
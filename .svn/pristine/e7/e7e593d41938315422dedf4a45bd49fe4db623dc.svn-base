<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>农行-用户退款</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link rel="stylesheet" href="<%=path %>/epay/gzbank/css/gzbank_pc.css">
<style type="text/css">
	body{background: #f9f9f9;}
	.from_box{
        padding-top:40px;
        padding-bottom:60px;
        width: 50%;
        margin: 0 auto;
        background: #ffffff;border-bottom:2px solid #d2d2d2;
    }
</style>
<script src="<%=path %>/JsLib/jquery-1.11.3.min.js" type="text/javascript"></script>
<script type="text/javascript">
	$(function(){
		$("#subRefund").click(function(){
			var txtOrderDate = $("#txtOrderDate").val();
			var txtOrderTime = $("#txtOrderTime").val();
			var txtOrderNo = $("#txtOrderNo").val();
			var txtNewOrderNo = $("#txtNewOrderNo").val();
			var txtCurrencyCode = $("#txtCurrencyCode").val();
			var txtTrxAmount = $("#txtTrxAmount").val();
			if(txtOrderDate == null || txtOrderDate == ""){
				alert("请输入订单日期");$("#txtOrderDate").select();return;
			}
			if(txtOrderTime == null || txtOrderTime == ""){
				alert("请输入订单时间");$("#txtOrderTime").select();return;
			}
			if(txtOrderNo == null || txtOrderNo == ""){
				alert("请输入订单编号");$("#txtOrderNo").select();return;
			}
			if(txtNewOrderNo == null || txtNewOrderNo == ""){
				alert("请输入退款单号");$("#txtNewOrderNo").select();return;
			}
			if(txtCurrencyCode == null || txtCurrencyCode == ""){
				alert("请输入交易币种");$("#txtCurrencyCode").select();return;
			}
			if(txtTrxAmount == null || txtTrxAmount == ""){
				alert("请输入交易金额");$("#txtTrxAmount").select();return;
			} else if(isNaN(txtTrxAmount)) {
				alert("退款金额不合法");
				$("#txtTrxAmount").focus();
				return;
			}
			$("#form1").submit();
		});
	});	
</script>

</head>

<body style="font-size: 14px;">
	<!--页头star-->
	<div id="header">
	    <div id="nav">
	        <h1 class="logo">
	            <a href="https://www.cmaotai.com/download.html" target="_blank"><img src="https://www.cmaotai.com/r/images/index/logo.png" alt=""></a>
	        </h1>
	        <ul>
	            <li><a href="https://www.cmaotai.com/download.html" target="_blank">首页</a></li>
	            <li><a href="https://www.cmaotai.com/r/SecurityKnowledge.html" target="_blank">鉴真知识</a></li>
	            <li><a href="https://www.cmaotai.com/r/Service.html" target="_blank">客户服务</a></li>
	            <li><a href="https://www.cmaotai.com/r/Aboutus.html" target="_blank">关于我们</a></li>
	            <li class="downloadNav">
	                <a href="javascript:;">下载云商</a>
	                <ul class="toggleNav">
	                    <li><a href="http://sys.cmaotai.com/sys/download/get?s=1" target="_blank" onclick="_hmt.push(['_trackEvent', 'AppDownLoad', 'Android'])">安卓版下载</a></li>
	                    <li><a href="https://itunes.apple.com/cn/app/mao-tai-yun-shang/id1056507597?l=zh&amp;ls=1&amp;mt=8#userconsent#" target="_blank" onclick="_hmt.push(['_trackEvent', 'AppDownLoad', 'IOS'])">iPhone版下载</a></li>
	                </ul>
	            </li>
	        </ul>
	    </div>
	</div>
	<form id="form1" action='<%=path %>/epay/abc/refund' method="post">
		<div class="from_box">
			<table align="center" width="98%" style="border: 1px;">
				<tr>
					<td colspan="2" height="40px" align="center">
						<strong style="font-size: 22px;">农行支付-退款</strong>
					</td>
				</tr>
				<tr>
					<td colspan="2" height="40px">
						<hr>
					</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">订单日期：</td>
					<td width="60%" height="40px"><input name='txtOrderDate' id='txtOrderDate' value='' style="width: 180px"/> *必输（YYYY/MM/DD）</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">订单时间：</td>
					<td width="60%" height="40px"><input name='txtOrderTime' id='txtOrderTime' value='' style="width: 180px"/> *必输（HH:MM:SS）</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">商户退款账号：</td>
					<td width="60%" height="40px"><input name='txtMerRefundAccountNo' id='txtMerRefundAccountNo' value='' style="width: 180px"/> &nbsp;&nbsp;选输</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">商户退款名：</td>
					<td width="60%" height="40px"><input name='txtMerRefundAccountName' id='txtMerRefundAccountName' value='' style="width: 180px"/> &nbsp;&nbsp;选输</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">原交易编号：</td>
					<td width="60%" height="40px"><input name='txtOrderNo' id='txtOrderNo' value='' style="width: 180px"/> *必输(订单号)</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">交易编号：</td>
					<td width="60%" height="40px"><input name='txtNewOrderNo' id='txtNewOrderNo' value='TK<%=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) %>' style="width: 180px"/> *必输(退款单号)</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">交易币种：</td>
					<td width="60%" height="40px"><input name='txtCurrencyCode' id='txtCurrencyCode' value='156' style="width: 180px"/> *必输 （156:人民币）</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">金额：</td>
					<td width="60%" height="40px"><input name='txtTrxAmount' id='txtTrxAmount' value='' style="width: 180px"/> *必输(单位 元)</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">附言：</td>
					<td width="60%" height="40px"><input name='txtMerchantRemarks' id='txtMerchantRemarks' value='' style="width: 180px"/> &nbsp;&nbsp;选输，不超过100个字符</td>
				</tr>
				<tr>
					<td align="right" width="40%" height="40px">&nbsp;</td>
					<td width="60%" height="40px">
					<input type="button" id="subRefund" value=" 提交退款 " /></td>
				</tr>
			</table>
		</div>
	</form>
	<div id="footer">
	   	贵州茅台集团电子商务股份有限公司版权所有&copy; 2016&nbsp;黔ICP备15014020号-1
	</div>
</body>
</html>

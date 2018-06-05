<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/global.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>订单银行状态查询</title>
<%@ include file="/Include/easyui_head_index.jsp"%>
<style type="text/css">
body,table {
	font-size: 12px;
}

table1 {
	table-layout: fixed;
	empty-cells: show;
	border-collapse: collapse;
	margin: 0 auto;
}

td {
	height: 30px;
}

.table1 {
	border: 1px solid #cad9ea;
	color: #666;
}

.table1 th {
	background-repeat: repeat-x;
	height: 30px;
}

.table1 td,.table1 th {
	border: 1px solid #cad9ea;
	padding: 0 1em 0;
}

.table1 tr.alter {
	background-color: #f5fafe;
}
</style>
<body>
	<div class="easyui-panel" title="订单银行状态查询" style="background:#fafafa;">

		<div class="row">
			<ul>
				<li>订单号:</li>
				<li><input id="orderId" class="easyui-textbox" style="width:200px;"></li>
				<li>&nbsp;&nbsp;&nbsp;<a href="#" class="easyui-linkbutton"
					onclick="searchData();" data-options="iconCls:'icon-search'">查询</a>
					&nbsp;&nbsp;&nbsp;<a href="#" class="easyui-linkbutton"
					onclick="reset();" data-options="iconCls:'icon-cancel'">重置</a></li>
			</ul>
		</div>
		<br />

	</div>
	<table width="90%" class="table1">
		<tr>
			<th style="width: 150px; bgcolor:#cad9ea;">订单号</th>
			<th style="width: 150px; bgcolor:#cad9ea;">支付方式</th>
			<th style="width: 150px; bgcolor:#cad9ea;">支付金额</th>
			<th style="width: 150px; bgcolor:#cad9ea;">支付日期</th>
			<th style="width: 80px;  bgcolor:#cad9ea;">状态码</th>
			<th style="width: 350px; bgcolor:#cad9ea;">返回消息</th>
		</tr>
		<tbody id="tbody">

		</tbody>
	</table>
</body>

<script type="text/javascript">
	function searchData() {
		var orderid = $("#orderId").val();

		var param = {
			"orderId" : orderid
		};

		$.post("${ctx}/epay/query/queryBankOrdersa", param, function(data) {
			var tbody = $("#tbody");
			tbody.html("");
			if (data.retcode != 0) {
				alert(data.retmsg);
			} else {
				 
				for (var i = 0, len = data.data.length; i < len; i++) {
					var item = data.data[i];
					var tr = $("<tr></tr>");
					var td = $("<td></td>");
					if (item != null){
						td.html(item.orderid);
						tr.append(td);

						td = $("<td></td>");
						td.html(item.payCompany);
						tr.append(td);
						td = $("<td></td>");
						td.html(item.amount / 100);
						tr.append(td);

						td = $("<td></td>");
						td.html(item.Create_date);
						tr.append(td);

						td = $("<td></td>");
						td.html(item.tranStat);
						tr.append(td);

						td = $("<td></td>");
						td.html(item.comment);
						tr.append(td);
					}else{
					 

						td = $("<td colspan='6'></td>");
						td.html("银行系统中没有查到该订单支付数据!");
						tr.append(td);

					}
				
					tbody.append(tr);
				}
			}
		}, "json");
	}
</script>
</html>


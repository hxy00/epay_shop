<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/global.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>订单支付查询</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<%@ include file="/Include/easyui_head_index.jsp"%>
	
	<script>
		$(function(){
			$("#btnQuery").click(function(){
				var param = {
					orderId: $("#orderId").val()
				};
				
				$.post("${ctx}/epay/manager/search", param, function(data){
					if(data.retcode != 0){
						alert(data.retmsg);					
					} else {
						var body = $("#body");
						body.html("");
						for(var i = 0, len = data.data.length; i < len; i++){
							var item = data.data[i];
							var tr = $("<tr></tr>");
							var td = $("<td></td>");
							td.html(item.orderid);
							tr.append(td);
							
							td = $("<td></td>");
							td.html(item.payCompany);
							tr.append(td);
							
							td = $("<td></td>");
							td.html(item.interfaceName);
							tr.append(td);
							
							td = $("<td></td>");
							td.html(item.amount);
							tr.append(td);
							
							td = $("<td></td>");
							td.html(item.tranStat);
							tr.append(td);
							
							td = $("<td></td>");
							td.html("<input type='button' value='更新' onclick='updateEpay(\"" + item.orderid + "\", \"" + item.payCompany + "\")' />");
							tr.append(td);
							
							body.append(tr);
						}
					}
				}, "json");
			});
		});
		
		var updateEpay = function(orderid, payCompany){
			var param = {
				"orderId": orderid,
				"payCompany": payCompany
			};
			
			$.post("${ctx}/epay/manager/updateEpay", param, function(data){
				if(data.retcode != 0){
					alert(data.retmsg);
				} else {
					alert("已更新！");
					var body = $("#body");
					body.html("");
				}
			}, "json");
		};
	</script>
	
  </head>
  
  <body>
	<div>
		请输入订单号：<input type="text" id="orderId" />
	</div>
	<div>
		<input type="button" value="查询" id="btnQuery" />
	</div>
	<div>
		<table>
			<tr>
				<th style="width: 150px;">orderid</th>
				<th style="width: 150px;">payCompany</th>
				<th style="width: 150px;">interfaceName</th>
				<th style="width: 150px;">amount</th>
				<th style="width: 150px;">tranStat</th>
				<th style="width: 150px;">操作</th>
			</tr>
			<tbody id="body">
				
			</tbody>
		</table>
	</div>
  </body>
</html>

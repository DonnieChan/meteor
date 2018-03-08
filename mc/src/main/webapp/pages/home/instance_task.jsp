<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	request.setAttribute("ctx", path);
%>

<!DOCTYPE html>
<html lang="en">

	<head>
		<meta charset="utf-8">
		<title>任务实例</title>
		<!-- header js css -->
		<%@include file="../../commons/import_header_js_css.html"%>
		
		<!-- our custom commons js -->
		<%@include file="../../commons/import_all_custom_js.html"%>

		<style type="text/css">
		</style>
		
		<SCRIPT type="text/javascript">
			$(function (){
				$('form input').bind("blur",function(){ this.value = this.value.trim(); });
				onLoadWindowParent();
			});
		</SCRIPT>
		
	</head>

<body>


	<div class="row-fluid sortable">
		<!-- 切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="">	
    </div>	

		<div class="container-fluid">
			<div class="row-fluid">
				<div id="content">
					
					<div id="breadcrumb_div" name="breadcrumb_div">
						<ul class="breadcrumb">
							<li>各时间片数据运行实例<span class="divider">/</span></li>
							<li>${defFileSys.fileName}<span class="divider">/</span></li>
							<li>${defFileSys.fileId}<span class="divider">/</span></li>
							<li><a href="${ctx}/schedule/index.do?projectId=${defFileSys.projectId}&fileId=${defFileSys.fileId}" target="mainFrame">返回任务定义</a></li>
						</ul>
					</div>
				<form name="searhForm" id="searchForm" method="post" action="${ctx}/instanceTask.do">		
					<div class="form-inline" style="margin-bottom:20px;">
						<input name="pageNumber" id="pageNumber" type="hidden" />
						<input id="fileId" name="fileId" type="hidden" value="${instanceTaskQuery.fileId}" />
						<label class="control-label">起止时间：</label>
						<input type="text" class="Wdate" size="12" id="startTime" name="startTime" onClick="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm:ss'})" value="${instanceTaskQuery.startTime}"/> - 
						<input type="text" class="Wdate" size="12" id="endTime" name="endTime" onClick="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm:ss'})" value="${instanceTaskQuery.endTime}"/>
						&nbsp;&nbsp;&nbsp;&nbsp;<input id="submitId" class="btn btn-primary" type="submit" value="查询" />
						&nbsp;&nbsp;&nbsp;&nbsp;<input class="btn btn-primary" type="button" onclick="$('#endTime').val(new Date().format('yyyy-MM-dd hh:mm:ss')); $('#startTime').val(new Date(new Date().getTime() - 60*60*1000).format('yyyy-MM-dd hh:mm:ss')); $('#submitId').click();" value="最近1小时" />
					</div>	

				</form>
							              
				<table class="table table-bordered sortable">
				  <caption></caption>
				  <thead>
				    <tr>
				      <th></th>
				      <th>状态</th>
				      <th>时长(毫秒)</th>
				      <th>准备好时间</th>
				      <th>开始时间</th>
				      <th>结束时间</th>
				      <th>已重试次数</th>
				      <th>运行中时间片数</th>
				      <th>等待中时间片数</th>
				      <th>流程实例ID</th>
				      <th>最后修改时间</th>
				      <th>日志</th>
				    </tr>
				  </thead>
				  <tbody>
				  
				  	<c:set var="status" value="${firstIndex}"></c:set>
				  	<c:forEach var="item" items="${instanceTasks}"  varStatus="idx">
				  		<tr>
				  		  <td>${status+1}</td>
				  		  <td>${item.status}</td>
				  		  <td>
		      			  	<c:if test="${item.endTime != null}">
					      		<fmt:formatNumber value="${item.endTime.time-item.startTime.time}" pattern="" />
					      	</c:if>
					      </td>
					      <td><fmt:formatDate value="${item.readyTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					      <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					      <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					      <td>${item.retriedTimes}</td>
					      <td>${item.poolActiveCount}</td>
					      <td>${item.poolQueueSize}</td>
					      <td>${item.instanceFlowId}</td>
					      <td><fmt:formatDate value="${item.updateTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					      <td><a href="${ctx}/instanceTaskLog.do?instanceFlowId=${item.instanceFlowId}&fileId=${defFileSys.fileId}" target="_blank">log</a></td>
					    </tr>
					    <c:set var="status" value="${status+1}"></c:set>
			  	   </c:forEach>							  
				  
				  </tbody>
				</table>

				<c:if test="${pageMax>0}">
					<div style="width:400px;margin:10px auto;">
					    （共${pageMax}页，当前页:${pageNow}）
							  <a href="javascript:void(0)" onclick="queryForm('1')">首页</a> | 
							  <c:if test="${pageBack!=0}">
							  <a href="javascript:void(0)" onclick="queryForm('${pageBack}')">上一页</a> |
							  </c:if>
							   <c:if test="${pageNext<=pageMax}">
							  <a href="javascript:void(0)" onclick="queryForm('${pageNext}')">下一页</a> | 
							  </c:if>
							  <a href="javascript:void(0)" onclick="queryForm('${pageMax}')">尾页</a>
					</div>											
				</c:if>	
								
			</div>
		</div>	
	</div>

		<script src="${ctx}/js/commons/iframe.js"></script>
		<script src="${ctx}/js/commons/string.js"></script>
		<script type="text/javascript" >

			function queryForm(pageNumber){
				$("#pageNumber").val(pageNumber);
				$('#searchForm').submit();
			}

			function onLoadWindowSelf() {
			    onLoadWindowParent(); // 最顶端页面，无须再调用
			}

			window.onload = function (){
				onLoadWindowSelf();
			} 

		</script>
					
</body>
</html>

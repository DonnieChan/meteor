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
		<title>当前时间片任务运行实例</title>
		<!-- header js css -->
		<%@include file="../../commons/import_header_js_css.html"%>
		
		<!-- our custom commons js -->
		<%@include file="../../commons/import_all_custom_js.html"%>

		<style type="text/css">
		</style>
		
		<SCRIPT type="text/javascript">
			$(function (){
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
							<li>当前时间片任务运行实例<span class="divider">/</span></li>
							<li><a onclick="history.go(-1);" >返&nbsp;回</a></li>
						</ul>
					</div>

				<table class="table table-bordered">
				  <caption></caption>
				  <thead>
				    <tr>
				      <th>总任务实例数据</th>
				      <th>时长(毫秒)</th>
				      <th>成功</th>
				      <th>失败</th>
				    </tr>
				  </thead>
				  <tbody>
				  		<tr>
					      <td>${allInstanceTaskSize}</td>
					      <td>${duration}</td>
					      <td>${sucessInstanceTaskSize}</td>
					      <td>${failInstanceTaskSize}</td>
					    </tr>
				  </tbody>
				</table>
											              
				<table class="table table-bordered sortable">
				  <caption></caption>
				  <thead>
				    <tr>
				      <th></th>
				      <th>任务ID</th>
				      <th>任务名称</th>
				      <th>状态</th>
				      <th>时长(毫秒)</th>
				      <th>准备好时间</th>
				      <th>开始时间</th>
				      <th>结束时间</th>
				      <th>已重试次数</th>
				      <th>运行中时间片数</th>
				      <th>等待中时间片数</th>
				      <th>日志</th>
				    </tr>
				  </thead>
				  <tbody>
				  
				  	<c:set var="status" value="${firstIndex}"></c:set>
				  	<c:forEach var="item" items="${instanceTasks}"  varStatus="idx">
				  		<tr>
				  		  <td>${status+1}</td>
				  		  <td>${item.fileId}</td>
				  		  <td>${item.fileName}</td>
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
					      <td><a href="${ctx}/instanceTaskLog.do?instanceFlowId=${item.instanceFlowId}&fileId=${item.fileId}" target="_blank">log</a></td>
					    </tr>
					    <c:set var="status" value="${status+1}"></c:set>
			  	   </c:forEach>							  
				  
				  </tbody>
				</table>
								
			</div>
	
		</div>	
		
	</div>

		<script src="${ctx}/js/commons/iframe.js"></script>
		<script src="${ctx}/js/commons/string.js"></script>
		<script type="text/javascript" >

			function onLoadWindowSelf() {
			    onLoadWindowParent(); // 最顶端页面，无须再调用
			}

			window.onload = function (){
				onLoadWindowSelf();
			} 

		</script>
					
</body>
</html>

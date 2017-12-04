<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
	request.setAttribute("ctx", com.duowan.meteor.mc.utils.ControllerUtils.httpFlag);
%>

<!DOCTYPE html>
<html lang="en">

	<head>
		<meta charset="utf-8">
		<title>导出统计结果至kafka</title>
		<!-- header js css -->
		<%@include file="../../../commons/import_header_js_css.html"%>
		
		<!-- our custom commons js -->
		<%@include file="../../../commons/import_all_custom_js.html"%>

		<style type="text/css">
		</style>
		
		<SCRIPT type="text/javascript">
			$(function (){
				// form input trim
				$('form input').bind("blur",function(){ this.value = this.value.trim(); });
				
				<!-- 初始化table，设置属性，前置依赖 -->
				initTable();
				
				// parent window
				onLoadWindowParent();
			});
		</SCRIPT>
		
	</head>
	
	<body>
		
		<div class="row-fluid sortable">
			<!-- 切换皮肤需求引用 -->
			<input type="hidden" id="ctx" value="${ctx}">
			<input type="hidden" id="url" value="">
		</div>
		
		<div class="container-fluid">
			<div class="row-fluid">
			
				<div id="content">
					
					<div id="breadcrumb_div" name="breadcrumb_div">
						<ul class="breadcrumb">
							<li>导出统计结果数据<span class="divider">/</span></li>
							<li>导出至kafka<span class="divider">/</span></li>
							<li>${defFileTask.fileId}</li>
						</ul>
					</div>
					
					<form name="defFileTaskDataForm" class="form-horizontal" method="post" id="defFileTaskDataForm" action="${ctx}/task/writeExportKafkaTask.do" onsubmit=""> 
						<input id="fileType" name="fileType" type="hidden" value="ExportKafka" />
						<input id="fileId" name="fileId" type="hidden" value="${defFileTask.fileId}" />
						<input id="projectId" name="projectId" type="hidden" value="${defFileTask.projectId}" />
						<input id="parentFileId" name="parentFileId" type="hidden" value="${defFileTask.parentFileId}" />
						
						<div class="control-group">
							<label class="control-label">任务名称</label>
							<div class="controls">
								<input name="fileName" id="fileName" type="text" value="${defFileTask.fileName}" class="ht-width-p30" required></input> 
							</div>
						</div>
						
						
						<div class="control-group">
							<label class="control-label">数据提取SQL</label>
							<div class="controls">
								<textarea name="fetchSql" id="fetchSql" rows="13" class="ht-width-p80" style="overflow:auto;" required>${defFileTask.fetchSql}</textarea>
								<div><a class="muted" href="https://github.com/meteorchenwu/meteor/blob/master/SQL.md" target="_blank">查看sql帮助文档</a></div>
								<p class="muted">要想把统计结果纳入监控，select的字段中必须包含stime，格式为yyyy-MM-dd HH:mm:ss</p>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">toBrokers</label>
							<div class="controls">
								<input name="toBrokers" id="toBrokers" type="text" value="${defFileTask.toBrokers}" class="ht-width-p80" required></input>
								<p class="muted">kafka的连接信息，如：11.11.11.11:9092,22.22.22.22:9092,33.33.33.33:9092</p>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">toTopic</label>
							<div class="controls">
								<input name="toTopic" id="toTopic" type="text" value="${defFileTask.toTopic}" class="ht-width-p30" required></input>
								<p class="muted">topic相当于kafka中流的名字，提取的数据将会以json字符串的格式发送</p> 
							</div>
						</div>
						
						<!--  前置依赖 选择 -->
						<%@include file="../file_properties/pre_depend_set_choose.html"%>
						
						<!--  前置依赖 展示 -->
						<%@include file="../file_properties/pre_depend_set_display.html"%>
						
						<div class="control-group">
							<label class="control-label">备注</label>
							<div class="controls">
								<textarea name="remarks" id="remarks" rows="3" class="ht-width-p30">${defFileTask.remarks}</textarea>
							</div>
						</div>
						
						<!-- 高级属性 -->
						<%@include file="../file_properties/advanced_properties_body.html"%>
						
						<!-- targetAndSubmitForm -->
						<%@include file="./targetAndSubmitForm.html"%>
						
					</form> 
					
				</div>
			</div>
		</div>
		
		<!-- fixedAtPageButtomToAutoFitPage.html -->
		<%@include file="../../../commons/fixedAtPageButtomToAutoFitPage.html"%>
		
		<!-- plug-in components js -->
		<%@include file="../../../commons/import_all_plugin_js.html"%>
		
		<!-- our custom commons js -->
		<%@include file="../../../commons/import_all_custom_js.html"%>
		
		<script type="text/javascript" >
			 
			$(function () {
			
				<!-- 高级属性 -->
				<%@include file="../file_properties/advanced_properties_js_init.html"%>
				
				<!-- defFileSysTree 初始化的js -->
				<%@include file="../def_file_sys_tree/file_tree_js_init.html"%>
			});
			 
			<!--  前置依赖 -->
			<%@include file="../file_properties/pre_depend_set_js.html"%>
			
			<!-- defFileSysTree 普通常用的js，为该tree而特有的 -->
			<%@include file="../def_file_sys_tree/file_tree_js.html"%>
			 
			/** defFileSysTreeOnClick 需要实现，相当于实现接口中的方法 */
			function defFileSysTreeOnClick(event, treeId, treeNode) {
				displaySelectedTreeNode(event, treeId, treeNode);
			}
			
			/** 重新设置页面高度 */
			function onLoadWindowSelf() {
				// reSetIFrameHeight("${iFrameId}");
				onLoadWindowParent();
			}
			
			/** window.onload */
			window.onload = function (){
				// alert("call: window.onload");
				onLoadWindowSelf();
			} 

		</script>
		
	</body>
 	
</html>


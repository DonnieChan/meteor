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
		<title>创建sql任务</title>
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
							<li>数据模型构建<span class="divider">/</span></li>
							<li>sql<span class="divider">/</span></li>
							<li>${defFileTask.fileId}</li>
						</ul>
					</div>
					
					<form name="defFileTaskDataForm" class="form-horizontal" method="post" id="defFileTaskDataForm" action="${ctx}/task/writeSqlTask.do" onsubmit=""> 
						<input id="fileType" name="fileType" type="hidden" value="SqlTask" />
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
							<label class="control-label">SQL</label>
							<div class="controls">
								<textarea name="sql" id="sql" rows="13" class="ht-width-p80" style="overflow:auto;" required>${defFileTask.sql}</textarea>
								<div><a class="muted" href="https://github.com/meteorchenwu/meteor/blob/master/SQL.md" target="_blank">查看sql帮助文档</a></div>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">结果表重分区数</label>
							<div class="controls">
								<input name="repartition" id="repartition" type="number" value="${defFileTask.repartition}" class="ht-width-p30" required></input>
								<p class="muted">后续用该表做运算时，表的各分区是并行运算的。0：表示不变更原有分区数。重分区会影响当前任务的性能，建议用默认值0。</p>
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
				onLoadWindowSelf();
			} 

		</script>
		
	</body>
 	
</html>


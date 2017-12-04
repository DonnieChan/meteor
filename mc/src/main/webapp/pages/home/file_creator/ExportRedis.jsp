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
		<title>创建导出表数据至redis(用于join)</title>
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
							<li>导出表数据(用于join)<span class="divider">/</span></li>
							<li>导出至redis<span class="divider">/</span></li>
							<li>${defFileTask.fileId}</li>
						</ul>
					</div>
					
					<form name="defFileTaskDataForm" class="form-horizontal" method="post" id="defFileTaskDataForm" action="${ctx}/task/writeExportRedisTask.do" onsubmit=""> 
						<input id="fileType" name="fileType" type="hidden" value="ExportRedis" />
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
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">存至redis的“库.表”名</label>
							<div class="controls">
								<input name="toTable" id="toTable" type="text" value="${defFileTask.toTable}" class="ht-width-p30" required></input>
								<span class="muted">相当于redis的key的前半部分</span>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">时间分区列名</label>
							<div class="controls">
								<input name="partitionKey" id="partitionKey" type="text" value="${defFileTask.partitionKey}" class="ht-width-p30"></input>
								<span class="muted">分区，相当于redis的key的后半部分，空表示无分区</span>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">主键列名</label>
							<div class="controls">
								<input name="tableKeys" id="tableKeys" type="text" value="${defFileTask.tableKeys}" class="ht-width-p30" required></input>
								<span class="muted">数据主键列，即哪几列能确定唯一一行数据，多列组合主键用英文逗号分隔</span>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">数据已存在是否覆盖</label>
							<div class="controls">
								<select name="isOverride" required>
									<option value="1" <c:if test="${defFileTask.isOverride==1}">selected</c:if>>覆盖</option>
									<option value="0" <c:if test="${defFileTask.isOverride==0}">selected</c:if>>不覆盖</option>
								</select>
								<span class="muted">表示在当前表，当前分区，已存在当前主键的数据，是否覆盖</span>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">数据过期时间(秒)</label>
							<div class="controls">
								<input name="expireSeconds" id="expireSeconds" type="number" value="${defFileTask.expireSeconds}" class="ht-width-p30" required></input>
								<span class="muted">表示数据过期清除时间</span>
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


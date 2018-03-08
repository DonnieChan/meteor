<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	request.setAttribute("ctx", path);

	request.setAttribute("isDebug", "0");
	request.setAttribute("iFrameId", "scheduleFrame");
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>流星-实时数据开发平台</title>
		
		<!-- header js css -->
		<%@include file="../../../commons/import_header_js_css.html"%>
		
		<style type="text/css">
			.ztree * {
				font-size: 14px !important;
				line-height: 18px !important;
				font-family:'Courier New', Courier, monospace !important;
			}
			
			.panel-body { overflow-y:hidden !important; }
		</style>
		<SCRIPT type="text/javascript">
			$(function () {
				// $('input').bind("blur",function(){ this.value = this.value.trim(); } );
			});
		</SCRIPT>
	</head>

	<body>
		<!-- 在在dynamic-page.js中，切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="${ctx}">

		<div class="easyui-layout" style="width:100%;height:100%;" id='${iFrameId}Jeasyui'>
			<div data-options="region:'west',split:true" title="任务目录" style="width:20%;height:100%;" id='${iFrameId}JeasyuiWest'>
				<!-- defFileSysTree body -->
				<div id="rpFileTreeScheduleIndex" name="rpFileTreeScheduleIndex" >
					<%@include file="../def_file_sys_tree/file_tree_body.html"%>
				</div>
				
				<!-- fixedAtPageButtomToAutoFitPage.html -->
				<%@include file="../../../commons/fixedAtPageButtomToAutoFitPage.html"%>
			</div>
			
			<div data-options="region:'center'" style="width:78%;height:100%;" id='${iFrameId}JeasyuiCenter'>
				<!-- selfFrame ${ctx}/schedule/project.do?projectId=${projectId} -->
				<div id="selfFrame" name="selfFrame">
					<script src="${ctx}/js/commons/iframe.js"></script>
					<script src="${ctx}/js/commons/string.js"></script>
					<iframe id="${iFrameId}" name="${iFrameId}" src="" width="100%" height="100%" scrolling="no" frameborder="0" padding="0" margin="0" 
						onload="initSetIFrame('${iFrameId}');" onreadystatechange="initSetIFrame('${iFrameId}')" > 
					</iframe>
				</div>
				
				<!-- fixedAtPageButtomToAutoFitPage.html -->
				<%@include file="../../../commons/fixedAtPageButtomToAutoFitPage.html"%>
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
				<!-- defFileSysTree 初始化的js -->
				<%@include file="../def_file_sys_tree/file_tree_js_init.html"%>
			});
			 
			<!-- defFileSysTree 普通常用的js，为该tree而特有的 -->
			<%@include file="../def_file_sys_tree/file_tree_js.html"%>
			 
			/** defFileSysTreeOnClick 需要实现，相当于实现接口中的方法 */
			function defFileSysTreeOnClick(event, treeId, treeNode) {
				// goto scheduleFrame url
				goToScheduleFrameUrl(treeNode);
			}
			
			/** 重新设置页面高度 */
			function onLoadWindowSelf() {
				reSetIFrameHeight("${iFrameId}");
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

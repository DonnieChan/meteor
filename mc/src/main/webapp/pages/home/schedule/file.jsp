<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	request.setAttribute("ctx", path);

	request.setAttribute("isDebug", "0");
	request.setAttribute("iFrameId", "scheduleFileFrame");
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>流星-实时数据开发平台</title>
		
		<!-- header js css -->
		<%@include file="../../../commons/import_header_js_css.html"%>
		
		<SCRIPT type="text/javascript">
			$(function () {
				// form input trim
				$('form input').bind("blur",function(){ this.value = this.value.trim(); });
			});
		</SCRIPT>
	</head>

	<body>
		<!-- 在在dynamic-page.js中，切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="${ctx}">

		<div class="container-fluid">
			<div class="row-fluid">
				<div id="content">
					<div class="span11">
							<script src="${ctx}/js/commons/iframe.js"></script>
							<script src="${ctx}/js/commons/string.js"></script>
							<iframe id="${iFrameId}" name="${iFrameId}" src="${ctx}/schedule/displayDefFileTask.do?projectId=${projectId}&fileId=${fileId}" width="100%" height="100%" scrolling="no" frameborder="0" padding="0" margin="0" 
								onload="initSetIFrame('${iFrameId}');" onreadystatechange="initSetIFrame('${iFrameId}')" > 
							</iframe>
					</div>
					
					<div class="span1">
						<br />
						<br />
						
						<c:if test="${defFileSys.fileType=='ImportKafka'}">
						<div>
							<div class="control-group">
								<label class="control-label">
									<a href="${ctx}/instanceFlow.do?sourceTaskId=${defFileSys.fileId}">
										<button id="fileInstanceBtn" name="fileInstanceBtn" type="button" class="btn btn-primary" >各时间片数据运行实例</button>
									</a>
								</label>
							</div>
						</div>
						<br />
						</c:if>
						
						<c:if test="${defFileSys.fileType=='Cron'}">
						<div>
							<div class="control-group">
								<label class="control-label">
									<a href="${ctx}/instanceFlow.do?sourceTaskId=${defFileSys.fileId}">
										<button id="fileInstanceBtn" name="fileInstanceBtn" type="button" class="btn btn-primary" >各触发点任务运行实例</button>
									</a>
								</label>
							</div>
						</div>
						<br />
						</c:if>
								
						
						<c:if test="${defFileSys.fileType!='Cron'}">
						<div>
							<div class="control-group">
								<label class="control-label">
									<a href="${ctx}/instanceTask.do?fileId=${defFileSys.fileId}">
										<button id="fileInstanceBtn" name="fileInstanceBtn" type="button" class="btn btn-primary" >任务实例</button>
									</a>
								</label>
							</div>
						</div>
						<br />
						</c:if>
						
						<!-- 移动目录 -->
						<div> <%@include file="../file_and_dir/dir_move_body.html"%> </div>
						
						<!-- 删除任务 -->
						<div> <%@include file="../file_and_dir/file_delete_body.html"%> </div>
						
						<!-- 下线任务 -->
						<div> <%@include file="../file_and_dir/file_offline_body.html"%> </div>
					</div>
				</div>
			</div>
		</div>
		
		<!-- fixedAtPageButtomToAutoFitPage -->
		<%@include file="../../../commons/fixedAtPageButtomToAutoFitPage.html"%>
		
		<!-- plug-in components js -->
		<%@include file="../../../commons/import_all_plugin_js.html"%>
		
		<!-- our custom commons js -->
		<%@include file="../../../commons/import_all_custom_js.html"%>

		<script type="text/javascript" >

			$(function () {
				<!-- defFileSysTree 初始化的js 注意：这里是dir的，不是 file的 ！！ -->
				<%@include file="../def_file_sys_tree/dir_tree_js_init.html"%>
			});
			 
			<!-- defFileSysTree 普通常用的js，为该tree而特有的，此处  dir 与 file的 都要有！！ -->
			<%@include file="../def_file_sys_tree/dir_tree_js.html"%>
			<%@include file="../def_file_sys_tree/file_tree_js.html"%>
			 
			 
			/** defFileSysTreeOnClick 需要实现，相当于实现接口中的方法 */
			function defFileSysTreeOnClick(event, treeId, treeNode) {
			}
			
			/** 重新设置页面高度 */
			function onLoadWindowSelf() {
				reSetIFrameHeight("${iFrameId}");
				onLoadWindowParent();
			}
			
			/** window.onload */
			window.onload = function (){
				onLoadWindowSelf();
			} 
		</script>

	</body>
</html>

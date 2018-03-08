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
		<title>创建从kafak导入源头数据</title>
		<!-- header js css -->
		<%@include file="../../../commons/import_header_js_css.html"%>
		
		<!-- our custom commons js -->
		<%@include file="../../../commons/import_all_custom_js.html"%>
		
		<SCRIPT type="text/javascript">
			$(function (){
				// form input trim
				$('form input').bind("blur",function(){ this.value = this.value.trim(); });
				
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
							<li>导入源头数据<span class="divider">/</span></li>
							<li>从kafak导入<span class="divider">/</span></li>
							<li>${defFileTask.fileId}</li>
						</ul>
					</div>
					
					<form name="defFileTaskDataForm" class="form-horizontal" method="post" id="defFileTaskDataForm" action="${ctx}/task/writeImportKafkaTask.do" onsubmit=""> 
						<input id="fileType" name="fileType" type="hidden" value="ImportKafka" />
						<input id="fileId" name="fileId" type="hidden" value="${defFileTask.fileId}" />
						<input id="projectId" name="projectId" type="hidden" value="${defFileTask.projectId}" />
						<input id="parentFileId" name="parentFileId" type="hidden" value="${defFileTask.parentFileId}" />
						
						<div class="control-group">
							<label class="control-label">任务名称</label>
							<div class="controls">
								<input name="fileName" id="fileName" type="text" value="${defFileTask.fileName}" class="ht-width-p30" required /> 
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">brokers</label>
							<div class="controls">
								<input name="brokers" id="brokers" type="text" value="${defFileTask.brokers}" class="ht-width-p80" required /> 
								<p class="muted">kafka的连接信息，如：11.11.11.11:9092,22.22.22.22:9092,33.33.33.33:9092</p>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">topics</label>
							<div class="controls">
								<input name="topics" id="topics" type="text" value="${defFileTask.topics}" class="ht-width-p80" required />
								<p class="muted">多个topic用英文逗号分隔，topic相当于kafka中流的名字，如：topicName1,topicName2。另外流中的数据必须为json字符串格式</p> 
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">groupId</label>
							<div class="controls">
								<input name="groupId" id="groupId" type="text" value="${defFileTask.groupId}" class="ht-width-p30" required />
								<p class="muted">相当于消费这个流的唯一用户标志，kafka用于记录用户消费到流的哪个位置，以便程序重启后，继续从重启前的位置继续消费</p>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">注册表名</label>
							<div class="controls">
								<input name="regTable" id="regTable" type="text" value="${defFileTask.regTable}" class="ht-width-p30" required />
								<p class="muted">系统按固定时间间隔(如1分钟)拉取数据，叫时间片数据。系统把每一时间片的数据注册成唯一表，表名=“注册表名”_uuid，以使各时间片数据独立运算</p> 
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">表重分区数</label>
							<div class="controls">
								<input name="rePartitionNum" id="rePartitionNum" type="number" value="${defFileTask.rePartitionNum}" class="ht-width-p30" required />
								<p class="muted">后续用该表做运算时，表的各分区是并行运算的。0：表示不变更原有分区数,原有分区数=kafka的topic分区数。重分区会影响当前任务的性能，建议用默认值0。</p>
							</div>
						</div>
						
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
			});
			
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


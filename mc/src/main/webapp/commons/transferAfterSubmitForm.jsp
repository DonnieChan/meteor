<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	request.setAttribute("ctx", path);
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>流星-实时数据开发平台</title>
		<link href="${ctx}/css/custom.css" rel="stylesheet" type="text/css" >
	</head>

	<body>
		<div class="ht-hero-unit">
			${messageAfterSubmitForm}
			<br /><br />
			即将跳转页面......
		</div>
		
		<!-- fixedAtPageButtomToAutoFitPage.html -->
		<%@include file="./fixedAtPageButtomToAutoFitPage.html"%>
		
		<!-- js -->
		<%@include file="./import_all_custom_js.html"%>
		<%@include file="./import_all_plugin_js.html"%>
		
		<SCRIPT type="text/javascript">
			$(function () {
			});
			
			window.onload = function(){
				// 判断form提交后的跳转
				var action = "${actionAfterSubmitForm}";
				var target = "${targetAfterSubmitForm}";
				if(!isOrUndefinedNullEmpty(action)){
					// 1s后执行跳转
					setTimeout(function(){ openUrl("${ctx}"+action, target) }, 500); 
				}
			}
		</SCRIPT>
	</body>
</html>

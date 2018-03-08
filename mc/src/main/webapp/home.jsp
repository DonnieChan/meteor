<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	request.setAttribute("ctx", path);

	request.setAttribute("isDebug", "0");
	request.setAttribute("iFrameId", "mainFrame");
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>流星实时数据开发平台</title>
		
		<!-- header js css -->
		<%@include file="./commons/import_header_js_css.html"%>
		
		<style type="text/css">
			<%@include file="./header/header_css.html"%>
			
			* {
				font-family:'Courier New', Courier, monospace !important;
			}
			
			body{
			  padding-top:55px;
			}
		</style>
		<SCRIPT type="text/javascript">
		$(function () {
		});
		</SCRIPT>
	</head>

	<body>
		<!-- 在在dynamic-page.js中，切换皮肤需求引用 -->
		<input type="hidden" id="ctx" value="${ctx}">

		<!-- header -->
		<%@include file="./header/header_body.html"%>
		
		<div class="container-fluid">
			<div class="row-fluid">

				<!-- selfFrame -->
				<div id="selfFrame" name="selfFrame">
					<!-- our custom commons js -->
					<%@include file="./commons/import_all_custom_js.html"%>
					<iframe id="${iFrameId}" name="${iFrameId}" src="${ctx}/schedule/index.do" width="100%" height="100%" scrolling="no" frameborder="0" padding="0" margin="0" 
						onload="initSetIFrame('${iFrameId}');" onreadystatechange="initSetIFrame('${iFrameId}')" > 
					</iframe>
				</div>

			</div>
		</div>

		<jsp:include page="footer.jsp" flush="true" />

		<!-- our custom commons js -->
		<%@include file="./commons/import_all_custom_js.html"%>
		<script type="text/javascript" >
			
			$(function () {
			});
			 

			function onLoadWindowSelf() {
				reSetIFrameHeight("${iFrameId}");
				// onLoadWindowParent(); // 最顶端页面，无须再调用
			}

			window.onload = function (){
				onLoadWindowSelf();
			} 

		</script>

	</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title></title>
		
		<!-- header js css -->
		<%@include file="./import_header_js_css.html"%>
		
	</head>
	
	<body style="background-color: #ffffff;">
		<div class="container-fluid">
			<div class="row-fluid">
			
				<div id="content">

					<div class="form-actions" style="border-bottom: 1px solid #e5e5e5;">
						<div style="text-align:center"><button type="button" class="btn btn-danger" onclick="doBack()" >返&nbsp;回</button></div>
					</div>

					<% 
						Exception ex = (Exception) request.getAttribute("exception");
					%>
					<H2 style="margin-top:10px;">Exception:<%=ex.getMessage()%></H2>

					<div>详情如下：<%=ex.toString()%></div>
					<div>
						<% 
							for(StackTraceElement trace:ex.getStackTrace()){
								out.print(trace.toString()+"<br>");
							}
						%>
					</div>
				</div>
			</div>
		</div>

		<script src="../js/commons/iframe.js"></script>
		<script type="text/javascript" >
			/**
			* 修改iframe高宽度
			*/
			window.onload = function (){
				onLoadWindowParent(); 
			}

			function doBack() {
				history.go(-1);
			}
		</script>
	</body>

</html>

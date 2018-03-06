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
		<link rel="stylesheet" href="${ctx}/js/bootstrap-3.3.1/cerulean/css/bootstrap.min.css" media="screen">
		<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
		<!--[if lt IE 9]>
			<script src="../bower_components/html5shiv/dist/html5shiv.js"></script>
			<script src="../bower_components/respond/dest/respond.min.js"></script>
		<![endif]-->
		<link id="bs-css" href="${ctx}/css/skin/bootstrap-cerulean.css" rel="stylesheet">
		<link href="${ctx}/css/dynamic-page.css" rel="stylesheet">
		<link href="${ctx}/css/zTreeStyle.css" rel="stylesheet">
		<link rel="shortcut icon" href="${ctx}/img/YY-LOGO80.png">
		<link href="${ctx}/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="${ctx}/css/charisma-app.css" rel="stylesheet">  
		<style type="text/css">
		
		/* bootstrap修改默认字体，更换为微软雅黑或其他字体 */
		/* body,button, input, select, textarea,h1 ,h2, h3, h4, h5, h6 { font-family: Microsoft YaHei,'宋体' , Tahoma, Helvetica, Arial, "\5b8b\4f53", sans-serif;} */
		body,button, input, select, textarea,h1 ,h2, h3, h4, h5, h6 { 
			font-family: Microsoft YaHei,'宋体' , Tahoma, Helvetica, Arial, "\5b8b\4f53", sans-serif;
		}
		
		.nav-pills > li.active > a, .nav-pills > li.active > a:hover, .nav-pills > li.active > a:focus{
			background-color:rgba(23, 137, 203, 1);
		}
		
		.label.blue.ui, .labels.blue.ui .label{
			background-color:rgba(23, 137, 203, 1) !important;
		}
		</style>
	</head>
	
	<body style="background-color: #ffffff;">
	
		<div style="width:500px;margin:0 auto;">
			<div class="row">
				<div style="margin-top:100px;text-align:center">
					<H3>对不起，您没有权限，请找相关管理员开通！</H3>
					<div style="margin-top:30px;"><button type="button" class="btn btn-danger" onclick="doBack()">返&nbsp;回</button></div>
				</div>
			</div>
		</div>

		<script src="${ctx}/js/jquery.min.js"></script>
		<script src="${ctx}/js/bootstrap.min.js"></script>
	</body>

	<script type="text/javascript">
		$(function() {
		
		});

		function doBack() {
			history.go(-1);
		}
	</script>
</html>

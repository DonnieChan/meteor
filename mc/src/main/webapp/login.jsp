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
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet">
		<link rel="shortcut icon" href="${ctx}/img/YY-LOGO80.png">
		<style type="text/css">
		* {
		margin: 0;
		padding: 0;
		}

		body {
		background: #444 url(${ctx}/img/carbon_fibre_big.png)
		}

		.loginBox {
		width: 420px;
		height: 230px;
		padding: 0 20px;
		border: 1px solid #fff;
		color: #000;
		margin-top: 40px;
		border-radius: 8px;
		background: white;
		box-shadow: 0 0 15px #222;
		background: -moz-linear-gradient(top, #fff, #efefef 8%);
		background: -webkit-gradient(linear, 0 0, 0 100%, from(#f6f6f6), to(#f4f4f4) );
		font: 11px/1.5em 'Microsoft YaHei';
		position: absolute;
		left: 50%;
		top: 50%;
		margin-left: -210px;
		margin-top: -115px;
		}

		.loginBox h2 {
		height: 45px;
		font-size: 20px;
		font-weight: normal;
		}

		.loginBox .left {
		border-right: 1px solid #ccc;
		height: 100%;
		padding-right: 20px;
		}
		</style>
	</head>

	<body >
		<div class="container">
			<section class="loginBox row-fluid">
				<section class="span7 left">
					<form action="${ctx}/login.do" method="post" id="loginform">
						<h2>流星-实时数据开发平台</h2>
						<p>
							<input type="text" name="passport" placeholder="passport"  value=""/>
						</p>
						<p>
							<input type="password" name="password" placeholder="password"  value=""/>
						</p>
						<section class="row-fluid">
							<div class="span8 checkbox">
								<input type="checkbox" name="rememberme">Remember me</input>
							</div>
							<div class="span4">
								<input type="submit" value="Login" class="btn btn-primary"></input>
							</div>
						</section>
					</form>
				</section>
				<section class="span5 right">
					<h2>Welcome</h2>
					<section>
						<p>Have a nice day!</p>
					</section>
					<section>
						<p><font style="color: red;">${errorMessage}</font></p>
					</section>
				</section>
			</section>
		<!-- /loginBox -->
		</div>
		<!-- /container -->

		<!-- external javascript
		================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->

		<script src="${ctx}/js/jquery.min.js"></script>
		<script src="${ctx}/js/bootstrap.min.js"></script>

	</body>
</html>

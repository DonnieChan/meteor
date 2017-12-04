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
		<title>日志</title>
	</head>

<body>
<pre>
===========================日志============================
${instanceTask.log}


===========================任务实例=========================
${instanceTask.fileBody}
</pre>   

</body>
</html>

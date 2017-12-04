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
		<title>用前必读</title>
		<!-- header js css -->
		<%@include file="../../commons/import_header_js_css.html"%>
	</head>

<body style="padding-left:20px;padding-top:20px;">
<h4>1、特色</h4>
<pre>
1)0误差
2)支持任何量级的大表join
3)基于hive sql，能进行任何复杂业务的sql运算。号称：只有想不到，没有做不到！
4)时延：分钟级
</pre>

<h4>2、使用技术</h4>
<pre>
kafka，spark-stream，spark-sql，redis集群，cassandra（可选）
</pre>

<h4>3、示意图</h4>
<img src="${ctx}/img/overview.jpg" />

<h4>4、理念</h4>
<pre>
1)系统按固定间隔（如1min）去kafka拉数据，叫时间片数据。
2)系统对各时间片数据独立无干扰进行运算。 
3)系统将各时间片数据转换成表，基于sql进行运算，每个表系统都会自动加上当前时间片的uuid。
4)通过定制自定义函数：c_sum、c_distinct、c_join、c_max和c_min，在自定义函数中利用分布式存储redis或cassandra，对所有时间片进行统一运算。
</pre>

<br />
<h4>5、sql帮助文档</h4>
<a href="${ctx}/pages/help/SqlTask.jsp" target="_blank"><pre>查看sql帮助文档</pre></a>

<h4>6、其他</h4>
<pre>
细节详情可看各“创建任务”的表单注释
</pre>
</div>
</body>
</html>
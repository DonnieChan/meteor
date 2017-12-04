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
		<title>sql帮助文档</title>
		<!-- header js css -->
		<%@include file="../../commons/import_header_js_css.html"%>
	</head>

<body style="padding-left:20px;padding-top:20px;">
流星实时平台的sql是在<a href="https://cwiki.apache.org/confluence/display/Hive/LanguageManual" target="_blank">hive sql</a>的基础上，做了如下的改动：<br /><br />
<div>
<a href="#1"><small>1、生成中间表</small></a><br/>
<a href="#2"><small>2、累加c_sum，等同于sum和count</small></a><br/>
<a href="#3"><small>3、去重c_distinct，可以配合c_sum做count(distinct x)，也可以生成用于join的数据</small></a><br/>
<a href="#4"><small>4、join</small></a><br/>
<a href="#5"><small>5、max</small></a><br/>
<a href="#6"><small>6、min</small></a>
</div>

<div id="1">
<br />
<h4>1、生成中间表</h4>
<pre>
1)语法：cache table [tableName] as select ...
</pre>
<pre>
2)示例：
cache table dwd_table1 as
select col1, col2, uid, num, stime_yyyyMMdd, stime_yyyyMMddHH
from ods_table1
where col1='xx'
</pre>
</div>
<div id="2">
<br />
<h4>2、累加c_sum，等同于sum和count</h4>
<pre>
1)语法：c_sum(table: String, partition: String, key: String, value: Long, redisExpireSeconds: Integer)

table：为用哪个唯一表做全量累加，原理是redis的hashmap的名字前半部分。
partition：为用哪个字段做表的分区(一般用统一的时间字段，如能表示天或小时)，相当于group by的其中一个字段。原理是redis的hashmap的名字后半部分。
key：相当于做累加的group by剩余字段，多个字段用concat加竖线分隔。原理是redis的hashmap的内部的key值。
value：为累加的值。需要乘以1L，转为Long类型。原理是redis的hashmap做hincrby的value值。
redisExpireSeconds：为当前表当前分区的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的hashmap的过期时间。
</pre>

<pre>
2)示例：
select col1, col2, 
  c_sum('db1.table1_sum', stime_yyyyMMdd, CONCAT(col1, '|', col2), sum(num)*1L, 90000) sum_result,
  c_sum('db1.table1_count', stime_yyyyMMdd, CONCAT(col1, '|', col2), count(1)*1L, 90000) count_result
from dwd_table1
group by stime_yyyyMMdd, col1, col2

上述sql的含义是先做当前时间片的group by统计，再用c_sum函数做当天全量的统计。
</pre>
</div>
<div id="3">
<br />
<h4>3、去重c_distinct，可以配合c_sum做count(distinct x)，也可以生成用于join的数据</h4>
<pre>
1)语法：c_distinct(table: String, toCassandra: Boolean, cassandraExpireSeconds: Integer, redisExpireSeconds: Integer, partition: String, key: String, value: String)
当前唯一返回true，不唯一返回false

table:为用哪个唯一表做全量去重
toCassandra：false表示只用redis做去重（性能更优）。true表示在cassandra做去重，并在上面搭一层redis缓存（能支持超大数据量，如算历史新用户）。无论为true或false，数据在redis中为set "table|partition|key" "value".
cassandraExpireSeconds：当toCassandra=true有效，表示数据在cassandra的过期时间，单位秒，0表示永不过期。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。
redisExpireSeconds：为当前数据在redis的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的key的过期时间。
partition：为用哪个字段做表的分区(一般用统一的时间字段，如能表示天或小时).
key：表示在分区partition的基础上，用哪些值做去重，多个字段用concat加竖线分隔。
value：必须为json格式字符串，或空字符串''。可以固定死''空字符串，表示存到表中的value字段，后续可配合下面第4点作join用。
</pre>

<pre>
2)示例1：count(distinct x)
select col1, col2, 
  c_sum('db1.table1_count_distinct', stime_yyyyMMdd, CONCAT(col1, '|', col2), 1L, 90000) count_distinct_result
from dwd_table1
where c_distinct('db1.table1_distinct', false, 0, 90000, stime_yyyyMMdd, CONCAT(col1, '|', col2, '|', uid), '')

上述sql先用c_distinct去重，如果是唯一，则用c_sum累加1L
</pre>

<pre>
3)示例2：去重算新用户，并生成join表，配合下面第4点作join用
select col1, col2, uid, num
from dwd_table1
where c_distinct('db1.table1_distinct_join', false, 0, 90000, stime_yyyyMMdd, CONCAT(col1, '|', col2, '|', uid), 
  CONCAT('{',
    '"col1":"', col1, '",'
    '"col2":"', col2, '",'
    '"uid":"', uid, '",'
    '"num":"', num, '"'
  '}')
)
</pre>
</div>
<div id="4">
<br />
<h4>4、join</h4>
<pre>
1)语法：c_join(table: String, toCassandra: Boolean, useLocalCache: Boolean, cassandraExpireSeconds: Integer, redisExpireSeconds: Integer, partition: String, key: String)

table：join的表名，该表必须redis或cassandra中。
toCassandra：join的表，是否在cassandra中，是用true，否用false。
useLocalCache：join的数据是否缓存在集群的各台机器本地，一般用true，可以提高性能。
cassandraExpireSeconds：数据在cassandra中的过期清除时间，单位秒，0表示永不过期。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。当toCassandra=true时有效，假如join的表在cassandra还不存在，系统则自动根据table名和当前参数创建。
redisExpireSeconds：join的数据在redis中缓存的过期时间，单位秒。一般设置30min=1800s.
partition：join的表分区(一般用统一的时间字段，如能表示天或小时)。
key: join的主键，只支持主键join。多个字段用concat加竖线分隔。
</pre>

<pre>
2)要join表，必须在redis或cassandra中，其生成途径
a、用c_distinct函数
b、创建“导出表数据(用于join)”类别任务
c、创建“维表定时刷新(用于join)”类别任务
</pre>

<pre>
3)注意：c_join函数必须要跟LATERAL VIEW c_json_tuple配合使用，c_json_tuple的使用方式跟hive sql原来的json_tuple一致，只是修正了其在实时场景的BUG。
</pre>

<pre>
4)示例：
select t1.col1, t1.col2, t1.uid, jt_col1, jt_col2, jt_uid, jt_num
from dwd_table2 t1
LATERAL VIEW c_json_tuple(c_join('db1.dwd_table1', false, true, 0, 1800, t1.stime_yyyyMMdd, CONCAT(t1.col1, '|', t1.col2, '|', t1.uid)), 'col1', 'col2', 'uid', 'num') jt as jt_col1, jt_col2, jt_uid, jt_num
</pre>
</div>
<div id="5">
<br />
<h4>5、max</h4>
<pre>
1)语法：c_max(table: String, partition: String, value: Long, redisExpireSeconds: Integer)

table：用哪个唯一表做全量max，原理是redis的sort set的名字前半部分。
partition：为用哪个字段做表的分区(一般用统一的时间字段，如能表示天或小时)，相当于group by的其中一个字段。原理是redis的sort set的名字后半部分。
value：要做max的数字。
redisExpireSeconds：为当前表当前分区的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的sort set的过期时间。
</pre>

<pre>
2)示例：
select col1, col2, 
  c_max('db1.table1_max', stime_yyyyMMdd, max(num), 90000) max_result
from dwd_table1
group by col1, col2

上述sql的含义是先做当前时间片的group by统计，再用c_max函数做当天全量的统计。
</pre>
</div>
<div id="6">
<br />
<h4>6、min</h4>
<pre>
1)语法：c_min(table: String, partition: String, value: Long, redisExpireSeconds: Integer)

table：用哪个唯一表做全量min，原理是redis的sort set的名字前半部分。
partition：为用哪个字段做表的分区(一般用统一的时间字段，如能表示天或小时)，相当于group by的其中一个字段。原理是redis的sort set的名字后半部分。
value：要做min的数字。
redisExpireSeconds：为当前表当前分区的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的sort set的过期时间。
</pre>

<pre>
2)示例：
select col1, col2, 
  c_min('db1.table1_max', stime_yyyyMMdd, min(num), 90000) min_result
from dwd_table1
group by col1, col2

上述sql的含义是先做当前时间片的group by统计，再用c_min函数做当天全量的统计。
</pre>
</div>
</body>
</html>
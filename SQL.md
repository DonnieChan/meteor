sql帮助文档
=================

流星实时平台的sql是在[hive sql](https://cwiki.apache.org/confluence/display/Hive/LanguageManual)的基础上，做了如下的改动：

##目录
* [1、生成中间表](#1生成中间表)
* [2、count distinct](#2count distinct)
* [3、累加c_sum](#3累加c_sum)
* [4、max](#4max)
* [5、min](#5min)
* [6、去重c_distinct](#6去重c_distinct)
* [7、join](#7join)
* [8、csql_group_by_n自定义groupBy](#8csql_group_by_n自定义groupBy)
* [9、c_count_distinct_rua在线时长重复度低类去重](#9c_count_distinct_rua在线时长重复度低类去重)



1、生成中间表
-------------
<pre>
1)语法：cache table [tableName] as select ...
</pre>
<pre>
2)示例：
cache table dwd_table1 as
select col1, col2, uid, num, stime_yyyyMMdd, stime_yyyyMMddHH, CAST(1 AS BIGINT) count_value
from ods_table1
where col1='xx'
</pre>


2、count distinct
---------------------------
<pre>
1)语法：c_counst_distinct(table: String, key(keyCol: String, ...), value(valueCol: String, ...), batchSize:Integer, resultNThreads: Integer, redisExpireSeconds: Integer, isAccurate: Boolean)
在sql最前面需要包含“csql_group_by_n:”关键字

table：为用哪个唯一表做去重计数，原理是redis的set集合或hyperloglog的名字前半部分。
key：相当于group by的字段，多个字段在key里用逗号分隔。原理是redis的set集合或hyperloglog的名字后半部分。
value：相当于做count distinct的字段，多个字段在value里用逗号分隔。原理是redis的set集合或hyperloglog里面的值。
batchSize：每个group by成员，提交去重明细至redis的批量大小，一般填1000就行了。
resultNThreads：访问redis的并行度，一般用1就行了。
redisExpireSeconds：为当前表当前分区的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的hashmap的过期时间。
isAccurate：是否精确去重，true为使用redis的set集合精确去重；false为使用hyperloglog高精确度去重，节省redis的内存使用。
</pre>

<pre>
2)示例：
csql_group_by_n:
select stime_yyyyMMdd, col1, col2, 
  c_counst_distinct('db1.table1_cd', key(stime_yyyyMMdd, col1, col2), value(uid), 1000, 1, 90000) cd_result
from dwd_table1
group by stime_yyyyMMdd, col1, col2
</pre>


3、累加c_sum
---------------------------
<pre>
等同于sum和count
1)语法：c_sum(table: String, partition: String, key(keyCol: String, ...), value: Long, resultNThreads: Integer, redisExpireSeconds: Integer)
在sql最前面需要包含“csql_group_by_n:”关键字

table：为用哪个唯一表做全量累加，原理是redis的hashmap的名字前半部分。
partition：为用哪个字段做表的分区(一般用统一的时间字段，如能表示天或小时)，相当于group by的其中一个字段。原理是redis的hashmap的名字后半部分。
key：相当于做累加的group by剩余字段，多个字段在key里用逗号分隔。原理是redis的hashmap的内部的key值。
value：为累加的值。原理是redis的hashmap做hincrby的value值。
resultNThreads：访问redis的并行度，一般用1就行了。
redisExpireSeconds：为当前表当前分区的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的hashmap的过期时间。
</pre>

<pre>
2)示例：
csql_group_by_n:
select stime_yyyyMMdd, col1, col2, 
  c_sum('db1.table1_sum', stime_yyyyMMdd, key(col1, col2), num, 1, 90000) sum_result,
  c_sum('db1.table1_count', stime_yyyyMMdd, key(col1, col2), count_value, 1, 90000) count_result
from dwd_table1
group by stime_yyyyMMdd, col1, col2
</pre>

4、max
-----------------------
<pre>
1)语法：c_max(table: String, key(keyCol: String, ...), value: Long, resultNThreads: Integer, redisExpireSeconds: Integer)
在sql最前面需要包含“csql_group_by_n:”关键字

table：用哪个唯一表做全量max，原理是redis的sort set的名字前半部分。
key：相当于做max的group by的字段，多个字段在key里用逗号分隔。原理是redis的sort set的名字后半部分
value：要做max的数字。
resultNThreads：访问redis的并行度，一般用1就行了。
redisExpireSeconds：为当前表当前分区的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的hashmap的过期时间。
</pre>

<pre>
2)示例：
csql_group_by_n:
select stime_yyyyMMdd, col1, col2, 
  c_max('db1.table1_max', key(stime_yyyyMMdd, col1, col2), num, 1, 90000) max_result
from dwd_table1
group by stime_yyyyMMdd, col1, col2
</pre>


5、min
---------------------------
<pre>
1)语法：c_min(table: String, key(keyCol: String, ...), value: Long, resultNThreads: Integer, redisExpireSeconds: Integer)
在sql最前面需要包含“csql_group_by_n:”关键字

table：用哪个唯一表做全量min，原理是redis的sort set的名字前半部分。
key：相当于做min的group by的字段，多个字段在key里用逗号分隔。原理是redis的sort set的名字后半部分
value：要做min的数字。
resultNThreads：访问redis的并行度，一般用1就行了。
redisExpireSeconds：为当前表当前分区的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的hashmap的过期时间。
</pre>

<pre>
2)示例：
csql_group_by_n:
select stime_yyyyMMdd, col1, col2, 
  c_max('db1.table1_min', key(stime_yyyyMMdd, col1, col2), num, 1, 90000) max_result
from dwd_table1
group by stime_yyyyMMdd, col1, col2
</pre>


6、去重c_distinct
---------------------
<pre>
1)语法：c_distinct(table: String, redisExpireSeconds: Integer, useCassandra: Boolean, cassandraExpireSeconds: Integer, partition: String, key: String, value: String)
当前唯一返回1，不唯一返回0

table:为用哪个唯一表做全量去重
redisExpireSeconds：为当前数据在redis的过期清除时间，单位秒。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。原理是redis的key的过期时间。
useCassandra：false表示只用redis做去重（性能更优）。true表示在cassandra做去重，并在上面搭一层redis缓存（能支持超大数据量，如算历史新用户）。无论为true或false，数据在redis中为set "table|partition|key" "value".
cassandraExpireSeconds：当toCassandra=true有效，表示数据在cassandra的过期时间，单位秒，0表示永不过期。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。
partition：为用哪个字段做表的分区(一般用统一的时间字段，如能表示天或小时).
key：表示在分区partition的基础上，用哪些值做去重，多个字段用concat加竖线分隔。
value：必须为json格式字符串，或空字符串''。可以固定死''空字符串，表示存到表中的value字段，后续可配合下面的c_join用。
</pre>

<pre>
2)示例：去重算新用户，并生成join表，配合下面第4点作join用
select col1, col2, uid, num
from dwd_table1
where c_distinct('db1.table1_distinct_join', 90000, false, 0, stime_yyyyMMdd, CONCAT_WS('|', col1, col2, uid), 
  CONCAT('{',
    '"col1":"', col1, '",'
    '"col2":"', col2, '",'
    '"uid":"', uid, '",'
    '"num":"', num, '"'
  '}')
)
</pre>


7、join
--------------------
<pre>
1)语法：c_join(table: String, useLocalCache: Boolean, cacheEmpty: Boolean, useRedis: Boolean, redisExpireSeconds: Integer, useCassandra: Boolean, cassandraExpireSeconds: Integer, partition: String, key: String)

table：join的表名，该表必须redis或cassandra中。
useLocalCache：join的数据是否缓存在集群的各台机器本地，一般用true，可以提高性能。
cacheEmpty：若当次join的数据不存在redis或cassandra里，是否存一个空的值在本地缓存，以便下次不用再去访问redis或cassandra。一般用true，可以提高性能。
useRedis：是否使用redis
redisExpireSeconds：join的数据，从cassandra缓存至redis中，在redis中缓存的过期时间，单位秒。一般设置30min=1800s.
useCassandra：join的表，是否在cassandra中，是用true，否用false。
cassandraExpireSeconds：数据在cassandra中的过期清除时间，单位秒，0表示永不过期。如天分区可以设为25h=90000s，小时分区可以设为2h=7200s。当toCassandra=true时有效，假如join的表在cassandra还不存在，系统则自动根据table名和当前参数创建。
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
LATERAL VIEW c_json_tuple(c_join('db1.dwd_table1', true, true, true, 1800, false, 0, t1.stime_yyyyMMdd, CONCAT_WS('|', t1.col1, t1.col2, t1.uid)), 'col1', 'col2', 'uid', 'num') jt as jt_col1, jt_col2, jt_uid, jt_num
</pre>


8、csql_group_by_n自定义groupBy
--------------------
<pre>
1)通过在sql最前面带上“csql_group_by_n:”，可改造spark原有group by的执行逻辑（用shuffle，涉及数据排序，网络交互，读写磁盘），使执行group by时，各分区独立跟redis之类交互，不需要shuffle。从而使性能提升10倍以上。
另外，使用“csql_group_by_n:”，不支持在group by同级的where过滤，要用where，需要用子查询。

2)使用示例
a、示例一
	csql_group_by_n:
	select stime_yyyyMMdd, col1, col2, c_counst_distinct('db1.table1_cd', key(stime_yyyyMMdd, col1, col2), value(uid), 1000, 1, 90000) cd_result
	from dwd_table1
	group by stime_yyyyMMdd, col1, col2

b、示例二：用spark原有的group by对结果再做一次聚合，从而减少结果的输出量
	csql_group_by_n:
	select stime_yyyyMMdd, col1, col2, c_counst_distinct('db1.table1_cd', key(stime_yyyyMMdd, col1, col2), value(uid), 1000, 1, 90000) cd_result
	from dwd_table1
	group by stime_yyyyMMdd, col1, col2
	;
	select stime_yyyyMMdd, col1, col2, MAX(cd_result) cd_result
	from $targetTable
	group by stime_yyyyMMdd, col1, col2

c、示例三：在模型构建sql功能中，把自定义group by后的执行结果cache起来
	csql_group_by_n:
	select stime_yyyyMMdd, col1, col2, c_counst_distinct('db1.table1_cd', key(stime_yyyyMMdd, col1, col2), value(uid), 1000, 1, 90000) cd_result
	from dwd_table1
	group by stime_yyyyMMdd, col1, col2
	;
	cache table test_table1 as 
	select *
	from $targetTable
d、示例四：where过滤
	csql_group_by_n:
	select stime_yyyyMMdd, col1, col2, c_counst_distinct('db1.table1_cd', key(stime_yyyyMMdd, col1, col2), value(uid), 1000, 1, 90000) cd_result
	from (
	  select *
	  from dwd_table1
	  where col1='a'
	) t 
	group by stime_yyyyMMdd, col1, col2
e、示例五：多层group by 
	csql_group_by_n:
	select stime_yyyyMMdd, col1, c_max('db1.table1_max', key(stime_yyyyMMdd, col1), cd_result, 1, 90000) cd_result_max
	from (
	  select stime_yyyyMMdd, col1, col2, c_counst_distinct('db1.table1_cd', key(stime_yyyyMMdd, col1, col2), value(uid), 1000, 1, 90000) cd_result
	  from dwd_table1
	  group by stime_yyyyMMdd, col1, col2
	) t
	group by stime_yyyyMMdd, col1
</pre>

9、c_count_distinct_rua在线时长重复度低类去重
---------------------------
<pre>
1)语法：c_count_distinct_rua(redisMultiName: String, redisPreKey: String, key(keyCol: String, ...), value(valueCol: String, ...), batchSize: Integer, redisExpireSeconds: Integer)
在sql最前面需要包含“csql_group_by_n:”关键字

redisMultiName：请在/data/apps/spark/conf/meteor.properties配置多个单机redis连接信息，如：meteor.redisMultiHostPorts multi_redis=redis1.multi.huya.com:6379,redis2.multi.huya.com:6379,redis3.multi.huya.com:6379
</pre>

<pre>
2)示例：
csql_group_by_n:
select uid, col1, col2, 
  c_count_distinct_rua('multi_redis', 'db1.table1_cdr', key(uid, col1, col2), value(stime_yyyyMMddHHmm), 10000, 86700) max_result
from dwd_table1
group by uid, col1, col2
</pre>



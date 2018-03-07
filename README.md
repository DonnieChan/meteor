流星近实时数据开发平台
===================

一、简介
---------------------
<pre>
一个基于kafka + spark-stream + spark-sql + redis rua脚本的近实时计算平台

支撑一天百亿级、千亿级，甚至更大量不成问题，可按需扩容

纯sql业务开发

支持任意维度组合的，精准去重、高精度去重、sum、count、max、min和topN等

支持小表join
</pre>

二、示例
---------------------
<pre>
1、kafka的数据格式如下：
	key                 value
	product1            uid1|2018-03-07 19:25:00|login
	product1            uid1|2018-03-07 19:26:00|heartbeat
	product1            uid1|2018-03-07 19:27:00|heartbeat
	product1            uid2|2018-03-07 19:28:00|logout
	product1            uid2|2018-03-07 20:25:00|login
	product1            uid2|2018-03-07 20:26:00|heartbeat
	product1            uid2|2018-03-07 20:27:00|heartbeat
	product1            uid2|2018-03-07 20:28:00|logout

2、将kafka的数据注册成表ods_user_log，比如配置spark-stream按分钟拉取

3、转换sql1
	cache table dwd_user_log_tmp as
	select key as product, split(value, '|') value_arr
	from ods_user_log

4、转换sql2
	cache table dwd_user_log as
	select product, value_arr[0] uid, value_arr[1] stime, value_arr[2] action,
	  date_format(value_arr[1], 'yyyyMMdd') stime_yyyyMMdd, 
	  date_format(value_arr[1], 'yyyyMMddHH') stime_yyyyMMddHH, 
	  date_format(value_arr[1], 'yyyyMMddHHmm') stime_yyyyMMddHHmm,
	  cast(1 as bigint) one_num
	from dwd_user_log_tmp

5、统计天UV
	csql_group_by_n:
	select stime, stime_yyyyMMdd,
	    c_count_distinct('rediss_multil0', 'redis_prekey_uv', key(stime_yyyyMMdd), value(uid), 5000, ${DateUtils2.expireAtDay(1, 1, 30)}, 0) uv_val
	from dwd_user_log
	group by stime_yyyyMMdd
	;
	select MAX(stime) stime, stime_yyyyMMdd, MAX(uv_val) uv_val
	from $targetTable
	group by stime_yyyyMMdd

</pre>
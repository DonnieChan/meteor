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
	product1            uid1|2018-03-07 19:25:00|game1|server1|100
	product2            uid2|2018-03-07 19:26:00|game1|server2|200
	product3            uid3|2018-03-07 19:27:00|game1|server3|300
	product4            uid4|2018-03-07 19:28:00|game1|server4|400

2、将kafka的数据注册成表ods_pay_log，比如配置spark-stream按分钟拉取

3、转换sql1
	cache table dwd_pay_log_tmp as
	select key as product, split(value, '|') value_arr
	from ods_pay_log

4、转换sql2
	cache table dwd_pay_log as
	select product, value_arr[0] uid, value_arr[1] stime, value_arr[2] game, value_arr[3] server, 
	  cast(value_arr[4] as bigint) pay_coin,
	  t2.dp_platform as platform, 
	  date_format(value_arr[1], 'yyyyMMdd') stime_yyyyMMdd, 
	  date_format(value_arr[1], 'yyyyMMddHH') stime_yyyyMMddHH, 
	  date_format(value_arr[1], 'yyyyMMddHHmm') stime_yyyyMMddHHmm,
	  cast(1 as bigint) one_num
	from dwd_pay_log_tmp t1
	--备注： dim_product 为每隔10分钟，从mysql刷新至redis，此处用于join
	LATERAL VIEW json_tuple(c_join('rediss_multil0', 'dim_product', t1.product, true, true), 'platform') t2 as dp_platform

5、统计
	csql_group_by_n:
	select stime, stime_yyyyMMdd, platform, product, game, server,
        --备注：相当于 group by stime_yyyyMMdd, platform; count(distinct uid) 
	    c_count_distinct('rediss_multil0', 'redis_platform_uv', key(stime_yyyyMMdd, platform), value(uid), 5000, ${DateUtils2.expireAtDay(1, 0, 50)}, 0) platform_uv,
	    --备注：相当于 group by stime_yyyyMMdd, product; count(1) 
	    c_sum('rediss_multil0', 'redis_product_count', stime_yyyyMMdd, key(product), one_num, 5000, ${DateUtils2.expireAtDay(1, 0, 50)}) product_count,
	    --备注：相当于 group by stime_yyyyMMdd, game, server; sum(pay_coin) 
	    c_sum('rediss_multil0', 'redis_game_server_pay_sum', stime_yyyyMMdd, key(game, server), pay_coin, 5000, ${DateUtils2.expireAtDay(1, 0, 50)}) game_server_pay_sum,
	    --备注：相当于 group by stime_yyyyMMdd, game, server; max(pay_coin) 
	    c_max('rediss_multil0', 'redis_game_server_pay_max', stime_yyyyMMdd, key(game, server), pay_coin, 5000, ${DateUtils2.expireAtDay(1, 0, 50)}) game_server_pay_max,
	    --备注：相当于 group by stime_yyyyMMdd, game, server; sum(pay_coin) 
	    c_min('rediss_multil0', 'redis_game_server_pay_min', stime_yyyyMMdd, key(game, server), pay_coin, 5000, ${DateUtils2.expireAtDay(1, 0, 50)}) game_server_pay_min,
	    --备注：相当于group by stime_yyyyMMdd, game; 取top 20的server及对应的pay_coin
	    c_max_top_n('rediss_multil0', 'redis_game_server_top_20', key(stime_yyyyMMdd, game), server, pay_coin, 20, 5000, ${DateUtils2.expireAtDay(1, 0, 50)}) game_server_top_20
	from dwd_pay_log
	group by stime_yyyyMMdd, platform, product, game, server
	;
	--备注：相当于对各分区做一次去重复，减少输出结果的条数
	cache table dm_day_stat
	select MAX(stime) stime, stime_yyyyMMdd, platform, product, game, server,
	    MAX(platform_uv) platform_uv,
	    MAX(product_cnt) product_count,
	    MAX(game_server_pay_sum) game_server_pay_sum,
	    MAX(game_server_pay_max) game_server_pay_max,
	    MAX(game_server_pay_min) game_server_pay_min,
	    MAX(game_server_top_20) game_server_top_20
	from $targetTable
	group by stime_yyyyMMdd, platform, product, game, server
</pre>

三、安装
---------------------
<pre>
待录
</pre>
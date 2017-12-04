流星实时数据开发平台
===================

一个实时地干离线的活的流式计算平台！<br />
基于[hive sql](https://cwiki.apache.org/confluence/display/Hive/LanguageManual)，能进行各种复杂业务的sql运算。<br />
UV，PV，新UV，跟踪类指标，在线时长、在线人数等，都可以算。并且可以是0误差。<br /><br />
本平台已用每天几十亿行的数据验证通过。<br />


#### 一、特点：
<pre>
1、支持0误差去重。
2、支持大表join。
3、基于hive sql，支持创建中间表。
4、分钟级时延，一般为2分钟。（依赖于spark stream去kafka取数据的间隔频次）
</pre>

#### 二、使用技术
<pre>
1、框架：kafka，spark-stream，spark-sql，redis单机+集群，cassandra（可选），mysql
2、开发语言：java，scala
</pre>

#### 三、数据运行特点
<pre>
1、系统按固定间隔（如1min）去kafka拉数据，叫时间片数据。
2、系统将各时间片数据转换成表，基于hive sql进行运算。
3、系统对各时间片数据独立无干扰进行运算。每个表系统都会自动加上当前时间片的uuid。
4、通过自定义函数利用redis或cassandra,对所有时间片进行全局运算。
5、时间片数据运算完后，马上从spark内存删除。
</pre>

#### 四、SQL帮助文档
[查看详情](https://github.com/meteorchenwu/meteor/blob/master/SQL.md)

#### 五、demo安装示例
[demo安装](https://github.com/meteorchenwu/meteor/blob/master/INSTALL.md)

#### 六、平台介绍ppt
[平台介绍ppt](https://github.com/meteorchenwu/meteor/tree/master/doc/ppt/流星实时开发平台介绍.pptx)

<br/><br/>
开发者：欢聚时代陈武、林育灿


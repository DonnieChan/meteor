安装并用一台机器运行demo示例说明
====================================
以下安装说明，只是为了说明流程步骤，相关优化配置，自己按需调整。另外如果自己要用的spark、kafka、redis和cassandra是不同的版本，请相应修改本平台pom.xml里的驱动版本

##目录
* [一、创建spark用户和ssh无密码登录](#一创建spark用户和ssh无密码登录)
* [二、java](#二java)
* [三、安装spark](#三安装spark)
* [四、安装kafka](#四安装kafka)
* [五、安装redis集群](#五安装redis集群)
* [六、安装cassandra](#六安装cassandra)
* [七、配置hosts](#七配置host)
* [八、下载本平台源码，编译，启动](#八下载本平台源码编译启动)

一、创建spark用户和ssh无密码登录
---------------------
<pre>
sudo -s su - root
useradd -s /bin/bash -p 123456789 -m spark -d /data/spark
mkdir -p /data/apps/meteor
chown -R spark:spark /data/apps

sudo -s su - spark
ssh-keygen -t rsa（一直按空格键）

cat /data/spark/.ssh/id_rsa.pub >> /data/spark/.ssh/authorized_keys
</pre>

二、java
---------------------
安装Java HotSpot 1.7
<br />

三、安装spark
---------------------
##### 1、下载spark：http://www.apache.org/dyn/closer.lua/spark/spark-1.6.2/spark-1.6.2-bin-hadoop2.4.tgz
##### 2、cd /data/apps/，把包放到这个目录下，解压：tar -zxvf spark-1.6.2-bin-hadoop2.4.tgz
##### 3、ln -s spark-1.6.2-bin-hadoop2.4 spark
##### 4、cd /data/apps/spark/conf
##### 5、cp slaves.template slaves
##### 6、cp log4j.properties.template log4j.properties
##### 7、mkdir -p /data/apps/spark/work/
##### 8、cp spark-env.sh.template spark-env.sh，并修改成如下:

<pre>
export SPARK_DAEMON_MEMORY=512m
export JAVA_HOME=/usr/local/jdk
export SPARK_HOME=/data/apps/spark

export SPARK_WORKER_CORES=60
export SPARK_WORKER_MEMORY=2g
export SPARK_WORKER_DIR=$SPARK_HOME/work

export SPARK_LOCAL_DIRS=/tmp（或者如果是ubuntu，内存有多余的情况下，直接用tmpfs内存目录：/run/shm，提升shuffle性能）
</pre>

##### 9、cp spark-defaults.conf.template spark-defaults.conf
##### 10、启动spark集群：/data/apps/spark/sbin/start-all.sh，通过http://本机外网IP:8080

四、安装kafka
---------------------
##### 1、下载kafka：https://www.apache.org/dyn/closer.cgi?path=/kafka/0.8.2.0/kafka_2.10-0.8.2.0.tgz
##### 2、cd /data/apps/，把包放到这个目录下，解压：tar -zxvf kafka_2.10-0.8.2.0.tgz
##### 3、ln -s kafka_2.10-0.8.2.0 kafka
##### 4、vim /data/apps/kafka/conf/server.properties，增加配置auto.create.topics.enable=true
##### 5、启动zookeeper:
/data/apps/kafka/bin/zookeeper-server-start.sh /data/apps/kafka/config/zookeeper.properties > /tmp/startup_zookeeper.log 2>&1 &
##### 6、启动kafka：
/data/apps/kafka/bin/kafka-server-start.sh /data/apps/kafka/config/server.properties > /tmp/startup_kafka.log 2>&1 &
<br />

五、安装redis集群
---------------------
##### 1、下载redis-3.0.7：http://redis.io/download
##### 2、找一个临时目录，解压：tar -zxvf redis-3.0.7.tar.gz
##### 3、cd redis-3.0.7，执行make命令
##### 4、复制make结果

<pre>
mkdir -p /data/apps/redis-3.0.7/conf 
mkdir -p /data/apps/redis-3.0.7/bin 
mkdir -p /data/apps/redis-3.0.7/data
ln -s /data/apps/redis-3.0.7 /data/apps/redis
cp redis.conf sentinel.conf /data/apps/redis/conf
cp runtest* /data/apps/redis/bin
cd src
cp mkreleasehdr.sh redis-benchmark redis-check-aof redis-check-dump redis-cli redis-sentinel redis-server redis-trib.rb /data/apps/redis/bin/
</pre>

##### 5、配置集群
###### 节点1
cp /data/apps/redis/conf/redis.conf /data/apps/redis/conf/redis-6379.conf<br />
vim /data/apps/redis/conf/redis-6379.conf
<pre>
daemonize yes
pidfile /var/run/redis-6379.pid
port 6379

#save 900 1
#save 300 10
#save 60 10000

dbfilename dump-6379.rdb
dir /data/apps/redis/data

maxmemory 1g
maxmemory-policy allkeys-lru
maxmemory-samples 3

cluster-enabled yes
cluster-config-file /data/apps/redis/conf/nodes-6379.conf
</pre>

###### 节点2
cp /data/apps/redis/conf/redis.conf /data/apps/redis/conf/redis-6380.conf<br />
vim /data/apps/redis/conf/redis-6380.conf
<pre>
daemonize yes
pidfile /var/run/redis-6380.pid
port 6380

#save 900 1
#save 300 10
#save 60 10000

dbfilename dump-6380.rdb
dir /data/apps/redis/data

maxmemory 1g
maxmemory-policy allkeys-lru
maxmemory-samples 3

cluster-enabled yes
cluster-config-file /data/apps/redis/conf/nodes-6380.conf
</pre>

###### 节点3
cp /data/apps/redis/conf/redis.conf /data/apps/redis/conf/redis-6381.conf<br />
vim /data/apps/redis/conf/redis-6381.conf
<pre>
daemonize yes
pidfile /var/run/redis-6381.pid
port 6381

#save 900 1
#save 300 10
#save 60 10000

dbfilename dump-6381.rdb
dir /data/apps/redis/data

maxmemory 1g
maxmemory-policy allkeys-lru
maxmemory-samples 3

cluster-enabled yes
cluster-config-file /data/apps/redis/conf/nodes-6381.conf
</pre>

##### 6、启动各节点
<pre>
/data/apps/redis/bin/redis-server /data/apps/redis/conf/redis-6379.conf
/data/apps/redis/bin/redis-server /data/apps/redis/conf/redis-6380.conf
/data/apps/redis/bin/redis-server /data/apps/redis/conf/redis-6381.conf
</pre>

##### 7、装集群启动命令环境
<pre>
exit
sudo -s su - root
apt-get update
apt-get install ruby1.9.3
apt-get install rubygems
gem install redis
</pre>

##### 8、构建集群
<pre>
sudo -s su - spark
/data/apps/redis/bin/redis-trib.rb create 127.0.0.1:6379 127.0.0.1:6380 127.0.0.1:6381
</pre>

六、安装cassandra
---------------------
可选，涉及超大量级去重、join才需要用到，如基于历史数据算新UV，join成为新用户对应的来源渠道数据。
<br />

七、配置host
---------------------
<pre>
sudo -s su - root
vim /etc/hosts

127.0.0.1 kafka1
127.0.0.1 kafka2
127.0.0.1 kafka3
</pre>


八、下载本平台源码，编译，启动
---------------------
##### 1、下载该平台源码，假设本地路径为：/data/meteor，在你的mysql中执行如下sql脚本
/data/meteor/doc/sql/create.sql<br />
/data/meteor/doc/sql/init_demo.sql<br />

##### 2、将/data/meteor/dao/src/main/resources/meteor-app.properties的内容，改为你的mysql连接信息

##### 3、打包，执行mvn clean install -Dmaven.test.skip=true
其中下载scala包会很慢，因为是在国外的，可以从http://pan.baidu.com/s/1bpxBhrL 这里下载并解压到你的maven respository/org/目录下

##### 4、启动前台管理系统程序，通过http://x.x.x.x:8070 登录
java -Xms128m -Xmx128m -cp /data/meteor/jetty-server/target/meteor-jetty-server-2.0-SNAPSHOT-jar-with-dependencies.jar com.meteor.jetty.server.JettyServer "/data/meteor/mc/target/meteor-mc-2.0-SNAPSHOT.war" "/" "8070" > mc.log 2>&1 & 
<br />
平台任务操作细节详情，可查看里面的帮助文档和表单注释<br />

##### 5、启动模拟源头数据程序
java -Xms128m -Xmx128m -cp /data/meteor/demo/target/meteor-demo-2.0-SNAPSHOT-jar-with-dependencies.jar com.meteor.demo.DemoSourceData
<br />

##### 6、启动后台实时计算程序server
1)按需修改/data/meteor/conf/meteor.properties, 并复制到/data/apps/spark/conf目录<br />
2)cp /data/meteor/hiveudf/target/meteor-hiveudf-2.0-SNAPSHOT-jar-with-dependencies.jar /data/spark_lib_ext/<br />
3)cp /data/meteor/conf/log4j.properties /data/apps/spark/conf/<br />
4)cp /data/meteor/conf/fairscheduler.xml /data/apps/spark/conf/<br />
5)vim /data/apps/spark/conf/spark-defaults.conf<br />
<pre>
spark.driver.extraClassPath  /data/spark_lib_ext/*
spark.executor.extraClassPath  /data/spark_lib_ext/*
</pre>
6)启动程序
<pre>
/data/apps/spark/bin/spark-submit \
  --class com.meteor.server.MeteorServer \
  --master spark://你的内网IP:7077 \
  --executor-memory 1G \
  --total-executor-cores 16 \
  --driver-cores 4 \
  --driver-memory 1G \
  --supervise \
  --verbose \
  /data/meteor/server/target/meteor-server-2.0-SNAPSHOT-jar-with-dependencies.jar \
  "/data/apps/spark/conf/meteor.properties"
</pre>
首次启动会因kafka的一些topic没有，报错而自动创建<br />
可通过http://本机外网IP:4040查看<br/>
7)优雅关闭程序，使重启不丢失信息<br/>
java -Xms128m -Xmx128m -cp /data/meteor/server/target/meteor-server-2.0-SNAPSHOT-jar-with-dependencies.jar com.meteor.server.util.ServerCloseCmd   -zk zk1:2181 -app MeteorServer -cmd close

##### 7、启动日志转发程序，也可以更改里面PerformanceConsumerThread类的源码，定制监控逻辑
用于把执行日志导回mysql，方便前台管理系统查看<br />
java -Xms128m -Xmx128m -cp /data/meteor/jetty-server/target/meteor-jetty-server-2.0-SNAPSHOT-jar-with-dependencies.jar com.meteor.jetty.server.JettyServer "/data/meteor/transfer/target/meteor-transfer-2.0-SNAPSHOT.war" "/" "8090" > transfer.log 2>&1 & 
<br />

##### 8、查看统计结果
/data/apps/kafka/bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:2181 --topic uv_ref_hour














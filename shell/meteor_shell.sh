#! /bin/bash
source /etc/profile

cmd=$1
java=/usr/local/jdk/bin/java
meteor_path=/data/apps/meteor
spark_path=/data/apps/spark
appName=MeteorServer_test
master=192.168.1.10
zookeeperCon=192.168.1.20:2181,192.168.1.21:2181,192.168.1.22:2181

function start(){
${spark_path}/bin/spark-submit  --class com.duowan.meteor.server.MeteorServer --master spark://${master}:7077  --executor-memory 24G  --total-executor-cores 180  --driver-cores 60 --driver-memory 8G  --supervise --verbose  ${meteor_path}/meteor-server-1.0-SNAPSHOT-jar-with-dependencies.jar > ${spark_path}/work/meteor_server.log 2>&1 &
}

function close(){
${java} -cp ${meteor_path}/meteor-server-1.0-SNAPSHOT-jar-with-dependencies.jar com.duowan.meteor.server.util.ServerCloseCmd -zk ${zookeeperCon} -app ${appName} -cmd close
}


if [ "$cmd" = "restart" ];then
	close
	meteor_thread=`ps -ef | grep -v grep | egrep "com.duowan.meteor.server.MeteorServer"`
	while :
	do
		meteor_thread=`ps -ef | grep -v grep | egrep "com.duowan.meteor.server.MeteorServer"`
		echo "waiting meteor server to close"
		if [ -z "$meteor_thread" ]; then
		echo "closed meteor server"
		start
		echo "restart meteor server"
		break
		fi
		sleep 3
	done


elif [ "$cmd" = "reset" ];then
    ${java} -cp ${meteor_path}/meteor-server-1.0-SNAPSHOT-jar-with-dependencies.jar com.duowan.meteor.server.util.ServerCloseCmd  -app ${appName} -zk ${zookeeperCon} -cmd reset -of $2
elif [ "$cmd" = "close" ];then
	close
elif [ "$cmd" = "start" ];then
	start
fi

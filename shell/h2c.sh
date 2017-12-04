#!/bin/bash
source /etc/profile

date='20150525'
while [[ "${date}" < '20160309' ]];do
	enddate=$(date -d"${date} 6 day" +"%F")
	echo "===================${date} ~ ${enddate}==================="
	java -Xms5012m -Xmx5012m -cp /data/apps/meteor/meteor-datasync-1.0-SNAPSHOT-jar-with-dependencies.jar com.duowan.datasync.ITaskExecutor 431 Hive2Cassandra ${date} --et ${enddate}
	date=$(date -d"${date} 6 day" +"%Y%m%d")
done
echo "===================DONE==================="


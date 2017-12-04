#!/bin/bash
source /etc/profile

pre5date=`date -d "-5 day" +%F`
echo "delete data of $pre5date"

echo "delete spark log"
`rm -fr /data/apps/spark/work/work.log.$pre5date`
`ssh master "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh cassandra1 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh cassandra2 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh cassandra3 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave1 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave2 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave3 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave4 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave5 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave6 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave7 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave8 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave9 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave10 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave13 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave11 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave12 "rm -fr /data/apps/spark/work/work.log.$pre5date"`
`ssh slave14 "rm -fr /data/apps/spark/work/work.log.$pre5date"`

echo "delete queueswaplog"
`ssh cassandra1 "rm -fr /data/apps/spark/work/queueswap_PayData.log.$pre5date"`
`ssh cassandra3 "rm -fr /data/apps/spark/work/queueswap_HuyaData.log.$pre5date"`
`ssh master "rm -fr /data/apps/spark/work/queueswap_HuyaData.log.$pre5date"`
`ssh slave1 "rm -fr /data/apps/spark/work/queueswap_HuyaData.log.$pre5date"`
`ssh slave2 "rm -fr /data/apps/spark/work/queueswap_HuyaData.log.$pre5date"`
`ssh slave3 "rm -fr /data/apps/spark/work/queueswap_HuyaData.log.$pre5date"`
`ssh slave4 "rm -fr /data/apps/spark/work/queueswap_ActionData.log.$pre5date"`
`ssh slave5 "rm -fr /data/apps/spark/work/queueswap_ActionData.log.$pre5date"`
`ssh slave6 "rm -fr /data/apps/spark/work/queueswap_ActionData.log.$pre5date"`

`ssh slave11 "rm -fr /data/apps/spark/work/queueswap_ZHGameEventData.log.$pre5date"`
`ssh slave11 "rm -fr /data/apps/spark/work/queueswap_ZHPasEventDat.log.$pre5date"`
`ssh slave11 "rm -fr /data/apps/spark/work/queueswap_ZHWebEventData.log.$pre5date"`
`ssh slave12 "rm -fr /data/apps/spark/work/queueswap_ZHGameEventData.log.$pre5date"`
`ssh slave12 "rm -fr /data/apps/spark/work/queueswap_ZHPasEventDat.log.$pre5date"`
`ssh slave12 "rm -fr /data/apps/spark/work/queueswap_ZHWebEventData.log.$pre5date"`
`ssh slave12 "rm -fr /data/apps/spark/work/queueswap_ZHLogTotalData.log.$pre5date"`

echo "delete userinfo_server log"
`ssh cassandra1 "rm -fr /data/apps/spark/work/userinfo_server.log.$pre5date"`

echo "delete cron job log"
`ssh cassandra1 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh cassandra2 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh cassandra3 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh slave1 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh slave2 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh slave3 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh slave4 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh slave5 "rm -fr /data/apps/spark/work/cron/$pre5date"`
`ssh slave6 "rm -fr /data/apps/spark/work/cron/$pre5date"`


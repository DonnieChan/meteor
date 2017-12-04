package com.duowan.meteor.server.checkpoint

import java.util.concurrent.{TimeUnit, ConcurrentHashMap}

import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.factory.TaskThreadPoolFactory
import com.duowan.meteor.server.util.{Logging, CuratorUtil}
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type
import org.apache.curator.framework.recipes.cache.{PathChildrenCacheEvent, PathChildrenCacheListener}
import org.apache.spark.streaming.StreamingContext
import org.codehaus.jackson.map.ObjectMapper

class ZkOffsetCheckPoint extends Logging with OffsetCheckpoint {
  override def registerCheckpointListener(streamContext:StreamingContext): Unit = {
      CuratorUtil.init(ExecutorContext.zkConnection, 60 * 1000, ExecutorContext.appCloseZkNamesparce)
      CuratorUtil.tryCreatePersistNode("/" + ExecutorContext.appName, "")
      val checkpointState=s"/${ExecutorContext.appName}/state"
      CuratorUtil.tryCreatePersistNode(checkpointState, "")
      val checkpointNode = s"/${ExecutorContext.appName}/checkpoint"
      //从zk上得到checkpoint的offset信息
      if (CuratorUtil.isExist(checkpointNode)) {
        val objMapper = new ObjectMapper
        val lastCheckpointMap: ConcurrentHashMap[String, String] = objMapper.readValue(CuratorUtil.getData(checkpointNode), ExecutorContext.topicAndPartitions.getClass)
        ExecutorContext.topicAndPartitions.putAll(lastCheckpointMap)
      }

      var isEnd=false
      logInfo(s"register listener on ${checkpointNode}")
      CuratorUtil.registerListenerOnChildren("/"+ExecutorContext.appName, new PathChildrenCacheListener {
        override def childEvent(curatorFramework: CuratorFramework, event: PathChildrenCacheEvent): Unit = {
          if (Type.CHILD_UPDATED.equals(event.getType())) {
            val path = event.getData().getPath()
            val cmd=new String(event.getData.getData)
            if (path.endsWith(checkpointState)&&"close".equals(cmd)) {
              streamContext.stop(false, false)
              log.info(s"closing app[${ExecutorContext.appName}]")

              isEnd=true
              var hasActive=true;

              while(hasActive){
                TimeUnit.SECONDS.sleep(5)
                hasActive=false;
                for ((id,taskExecutor)<-TaskThreadPoolFactory.threadPoolMap){
                  if(taskExecutor.getActiveCount!=0 || taskExecutor.getQueue.size()!=0){
                    hasActive=true
                    log.info(s"waiting task complete,active task id=${id},active size=${taskExecutor.getActiveCount},brock size=${taskExecutor.getQueue.size()}")
                  }
                }
                if (TaskThreadPoolFactory.cachedThreadPool.getActiveCount>0||TaskThreadPoolFactory.cachedThreadPool.getQueue.size()>0){
                  hasActive=true
                }
              }
              writeToZk(checkpointNode)
              log.info(s"closed app[${ExecutorContext.appName}]")
              System.exit(0)
            }
          }
        }
      })

      logInfo("registered offset checkpoint listener!")
      val thread=new Thread(new Runnable {
        override def run(): Unit = {
          logInfo("start offset checkpoint thread!")
          while (!isEnd) {
            TimeUnit.MINUTES.sleep(1)
            writeToZk(checkpointNode)
          }
        }
      })
      thread.setDaemon(true);
      thread.setName("offset-checkpoint-thread")
      thread.start()

    }

    def writeToZk(appNode:String):Unit={
      val objMapper=new ObjectMapper
      if (CuratorUtil.isExist(appNode)) {
        val lastCheckpointMap:ConcurrentHashMap[String,String] = objMapper.readValue(CuratorUtil.getData(appNode),ExecutorContext.topicAndPartitions.getClass)
        lastCheckpointMap.putAll(ExecutorContext.topicAndPartitions)
        val mapJson = objMapper.writeValueAsString(lastCheckpointMap)
        CuratorUtil.setData(appNode, mapJson);
        log.info(s"writed app[${ExecutorContext.appName}] offset ${mapJson} info to zookeeper")
      } else {
        val mapJson = objMapper.writeValueAsString(ExecutorContext.topicAndPartitions)
        CuratorUtil.createPersistNode(appNode, mapJson)
        log.info(s"writed app[${ExecutorContext.appName}] offset ${mapJson} info to zookeeper")
      }
  }

}

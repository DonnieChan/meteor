package com.duowan.meteor.server.executor.instance

import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import scala.collection.JavaConversions.asScalaSet
import org.apache.commons.lang.SerializationUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.ToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle
import org.apache.kafka.clients.producer.ProducerRecord
import com.duowan.meteor.model.enumtype.ExecStatus
import com.duowan.meteor.model.instance.InstanceFlow
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.context.KafkaProducerSingleton
import com.duowan.meteor.server.factory.TaskThreadPoolFactory
import com.duowan.meteor.server.util.DropTableUtil
import com.duowan.meteor.server.util.Logging
import org.apache.spark.rdd.RDD
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.kafka.clients.consumer.ConsumerRecord

class InstanceFlowExecutor extends Serializable with Logging {

  val taskExecutorMap = scala.collection.mutable.Map[Integer, InstanceTaskExecutor]()

  val successSet = java.util.Collections.synchronizedSet(new java.util.HashSet[Integer]())

  val runningTaskSet = java.util.Collections.synchronizedSet(new java.util.HashSet[Integer]())

  val instanceFlow = new InstanceFlow();
  var isDoFinish = false
  
  var rdd: RDD[ConsumerRecord[String, String]] = null
  var time = System.currentTimeMillis()

  def startup(taskId: Integer, paramMap: Map[String, Any]): Unit = {
    instanceFlow.setStartTime(new Date)
    instanceFlow.setStatus(ExecStatus.Running.name())
    exec(taskId, paramMap)
  }

  def exec(taskId: Integer, paramMap: Map[String, Any]): Unit = {
    runningTaskSet.add(taskId)
    val instanceTaskExecutor = taskExecutorMap.get(taskId).get
    instanceTaskExecutor.exec(paramMap)
  }

  def doNext(taskId: Integer): Unit = {
    val instanceTask = taskExecutorMap.get(taskId).get.instanceTask
    val task = instanceTask.getTask
    if (StringUtils.equals(instanceTask.getStatus, ExecStatus.Success.name()) || (StringUtils.equals(instanceTask.getStatus, ExecStatus.Fail.name()) && task.getIsIgnoreError == 1)) {
      for (postTaskId: Integer <- task.getPostDependSet) {
        val postTaskExecutor = taskExecutorMap.get(postTaskId).get
        val postUnFinishedPreSet = postTaskExecutor.unFinishedPreSet
        synchronized {
          postUnFinishedPreSet -= taskId
        }
        if (postUnFinishedPreSet.isEmpty) {
          TaskThreadPoolFactory.cachedThreadPool.submit(new Runnable() {
            override def run(): Unit = {
              postTaskExecutor.exec(null)
            }
          })
          runningTaskSet.add(postTaskId)
        }
      }
      successSet.add(taskId)
    }
    runningTaskSet.remove(taskId)
  }

  def doClean(taskId: Integer): Unit = {
    val instanceTaskExecutor = taskExecutorMap.get(taskId).get
    val instanceTask = instanceTaskExecutor.instanceTask
    val task = instanceTask.getTask
    for (preTaskId: Integer <- task.getPreDependSet) {
      val preTaskExecutor = taskExecutorMap.get(preTaskId).get
      val preUnFinishedPostSet = preTaskExecutor.unFinishedPostSet
      synchronized {
        preUnFinishedPostSet -= taskId
        if (preUnFinishedPostSet.isEmpty && StringUtils.isNotBlank(preTaskExecutor.table)) {
          DropTableUtil.dropTable(preTaskExecutor.table)
        }
      }
    }
    if (instanceTaskExecutor.unFinishedPostSet.isEmpty || (StringUtils.equals(instanceTask.getStatus, ExecStatus.Fail.name()) && task.getIsIgnoreError != 1)) {
      DropTableUtil.dropTable(instanceTaskExecutor.table)
    }
  }

  def tryFinish(): Unit = {
    if (runningTaskSet.isEmpty) {
      doFinish()
    }
  }

  def doFinish(): Unit = synchronized {
    if (isDoFinish) {
      return
    }
    if (rdd != null) {
      try {
    	  rdd.unpersist(false)
      } catch {
        case e: Exception => logError(ExceptionUtils.getFullStackTrace(e))
      }
    }
//    InstanceFlowRunContext.finish(instanceFlow.getSourceTaskId)
    isDoFinish = true
    instanceFlow.setEndTime(new Date)
    if (taskExecutorMap.size == successSet.size) {
      instanceFlow.setStatus(ExecStatus.Success.name())
    } else {
      instanceFlow.setStatus(ExecStatus.Fail.name())
    }
    val df = new SimpleDateFormat("HH:mm:ss")
    logInfo(f"\n\n***${instanceFlow.getSourceTaskId}%5d  ${df.format(instanceFlow.getStartTime)}  ${df.format(instanceFlow.getEndTime)}  ${instanceFlow.getEndTime.getTime - instanceFlow.getStartTime.getTime}%5d  ${instanceFlow.getInstanceFlowId}%32s\n\n")
    sendStatusToKafka()
  }

  def sendStatusToKafka(): Unit = {
    TaskThreadPoolFactory.cachedThreadPool.submit(new Runnable() {
      override def run(): Unit = {
        val producer = KafkaProducerSingleton.getInstanceByte(ExecutorContext.kafkaClusterHostPorts);
        val message = new ProducerRecord[String, Array[Byte]](ExecutorContext.instanceFlowTopic, UUID.randomUUID().toString, SerializationUtils.serialize(instanceFlow));
        producer.send(message)
      }
    })
  }

  override def toString(): String = {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
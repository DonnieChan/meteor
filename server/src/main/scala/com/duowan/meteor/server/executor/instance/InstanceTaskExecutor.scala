package com.duowan.meteor.server.executor.instance

import java.util.Date
import java.util.UUID

import org.apache.commons.lang.SerializationUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.ToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle
import org.apache.kafka.clients.producer.ProducerRecord

import com.duowan.meteor.model.enumtype.ExecStatus
import com.duowan.meteor.model.instance.InstanceTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.context.KafkaProducerSingleton
import com.duowan.meteor.server.factory.TaskThreadPoolFactory
import com.duowan.meteor.server.util.Logging

class InstanceTaskExecutor extends Serializable with Logging {

  val unFinishedPreSet = scala.collection.mutable.Set[Integer]()
  val unFinishedPostSet = scala.collection.mutable.Set[Integer]()
  var table: String = null
  val instanceTask = new InstanceTask()
  var instanceFlowExecutor: InstanceFlowExecutor = _

  def exec(paramMap: Map[String, Any]): Unit = synchronized {
    if (!StringUtils.equals(instanceTask.getStatus, ExecStatus.Init.name())) {
      return
    }
    val task = instanceTask.getTask
    instanceTask.setReadyTime(new Date)
    val instanceTaskExecutor = this
    val threadPool = TaskThreadPoolFactory.threadPoolMap.get(task.getFileId).get
    val instanceTaskThread = new InstanceTaskThread(instanceTaskExecutor, threadPool, instanceFlowExecutor.instanceFlow.getSourceTaskId, instanceFlowExecutor.time, paramMap)
    val thread = threadPool.submit(instanceTaskThread)
    thread.get
    if (task.getFinishSleepTime > 0) Thread.sleep(task.getFinishSleepTime)

    instanceFlowExecutor.doNext(task.getFileId)
    instanceFlowExecutor.doClean(task.getFileId)
    instanceFlowExecutor.tryFinish()
    sendStatusToKafka()
  }

  def sendStatusToKafka(): Unit = {
    TaskThreadPoolFactory.cachedThreadPool.submit(new Runnable() {
      override def run(): Unit = {
        val producer = KafkaProducerSingleton.getInstanceByte(ExecutorContext.kafkaClusterHostPorts);
        val message = new ProducerRecord[String, Array[Byte]](ExecutorContext.instanceTaskTopic, UUID.randomUUID().toString, SerializationUtils.serialize(instanceTask));
        producer.send(message)
      }
    })
  }

  override def toString(): String = {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
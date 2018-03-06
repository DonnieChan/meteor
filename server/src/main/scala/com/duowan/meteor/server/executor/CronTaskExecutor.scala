package com.duowan.meteor.server.executor

import scala.collection.JavaConversions.asScalaSet
import com.duowan.meteor.model.view.AbstractTaskDepend
import com.duowan.meteor.model.view.cron.CronTask
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.factory.DefTaskFactory
import com.duowan.meteor.server.factory.TaskThreadPoolFactory
import com.duowan.meteor.server.context.KafkaProducerSingleton
import kafka.producer.KeyedMessage
import com.duowan.meteor.server.context.ExecutorContext
import java.util.UUID
import org.apache.commons.lang.SerializationUtils
import com.duowan.meteor.model.instance.InstanceFlow
import com.duowan.meteor.model.enumtype.ExecStatus
import org.apache.kafka.clients.producer.ProducerRecord

class CronTaskExecutor extends AbstractTaskExecutor {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[CronTask]

    val instanceFlow = new InstanceFlow()
    val uuid = UUID.randomUUID().toString
    instanceFlow.setInstanceFlowId(uuid)
    instanceFlow.setSourceTaskId(task.getFileId)
    val date = new java.util.Date
    instanceFlow.setInitTime(date)
    instanceFlow.setStartTime(date)
    instanceFlow.setEndTime(date)
    instanceFlow.setStatus(ExecStatus.Success.name)
    val producer = KafkaProducerSingleton.getInstanceByte(ExecutorContext.kafkaClusterHostPorts);
    val message = new ProducerRecord[String, Array[Byte]](ExecutorContext.instanceFlowTopic, uuid, SerializationUtils.serialize(instanceFlow));
    producer.send(message)

    val paramMap = Map("instanceFlowId" -> uuid)
    for (taskId: Integer <- task.getPostDependSet) {
      TaskThreadPoolFactory.cachedThreadPool.submit(new Runnable() {
        override def run(): Unit = {
          val postTask = DefTaskFactory.defAllValid.getDefAllMap().get(taskId).asInstanceOf[AbstractTaskDepend]
          val executor = Class.forName(postTask.getProgramClass).newInstance().asInstanceOf[AbstractTaskExecutor]
          val instanceTaskExecutor = new InstanceTaskExecutor()
          instanceTaskExecutor.instanceTask.setTask(postTask)
          executor.exec(instanceTaskExecutor, paramMap)
        }
      })
    }
    
  }

}
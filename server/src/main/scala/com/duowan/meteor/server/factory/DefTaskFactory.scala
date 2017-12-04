package com.duowan.meteor.server.factory

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import org.apache.commons.lang3.SerializationUtils

import com.duowan.meteor.model.custom.DefAllValid
import com.duowan.meteor.model.view.AbstractBase
import com.duowan.meteor.model.view.AbstractTaskDepend
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.task.TaskManager

object DefTaskFactory extends Logging {

  var defAllValid: DefAllValid = _
  val scheduledExecutor = Executors.newScheduledThreadPool(1)
  var taskManager: TaskManager = _

  def startup(): Unit = {
    taskManager = TaskManager.getInstance(ExecutorContext.jdbcDriver, ExecutorContext.jdbcUrl, ExecutorContext.jdbcUsername, ExecutorContext.jdbcPassword)
    getDefAllValid()
    scheduledExecutor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        try {
          getDefAllValid()
        } catch {
          case e: Exception => logError("定时刷新任务定义失败", e)
        }
      }
    }, 1, 1, TimeUnit.MINUTES)
  }

  def getDefAllValid(): Unit = {
    logInfo("Does getDefAllValid")
    val defAllValidData = taskManager.getDefAllValid
    if (defAllValidData != null && !defAllValidData.getDefAllMap.isEmpty()) {

      if (!ExecutorContext.execSourceTaskIds.isEmpty) {
        for (taskId <- defAllValidData.getCronSet.toArray()) {
          if (!ExecutorContext.execSourceTaskIds.contains(taskId)) {
            defAllValidData.getCronSet.remove(taskId)
            ExecutorContext.excludeTaskIds += taskId.asInstanceOf[Int]
          }
        }

        for (taskId <- defAllValidData.getImportQueueSet.toArray()) {
          if (!ExecutorContext.execSourceTaskIds.contains(taskId)) {
            defAllValidData.getImportQueueSet.remove(taskId)
            ExecutorContext.excludeTaskIds += taskId.asInstanceOf[Int]
          }
        }
      }

      for (taskId <- ExecutorContext.excludeTaskIds) {
        val task = defAllValidData.getDefAllMap.get(taskId).asInstanceOf[AbstractTaskDepend]
        if (task != null) {
          for (preTaskId <- task.getPreDependSet.toArray()) {
            defAllValidData.getDefAllMap.get(preTaskId).asInstanceOf[AbstractTaskDepend].getPostDependSet.remove(taskId)
          }
          for (postTaskId <- task.getPostDependSet.toArray()) {
            defAllValidData.getDefAllMap.get(postTaskId).asInstanceOf[AbstractTaskDepend].getPreDependSet.remove(taskId)
          }
          defAllValidData.getDefAllMap.remove(taskId)
        }
      }

      synchronized {
        defAllValid = defAllValidData
      }
    }
  }

  def getCloneById(id: Integer): AbstractBase = {
    var task: AbstractBase = null
    synchronized {
      task = defAllValid.getDefAllMap.get(id)
    }
    SerializationUtils.clone(task)
  }

}
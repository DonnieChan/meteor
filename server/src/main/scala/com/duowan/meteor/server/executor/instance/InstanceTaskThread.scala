package com.duowan.meteor.server.executor.instance

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.ThreadPoolExecutor
import org.apache.commons.lang.exception.ExceptionUtils
import com.duowan.meteor.model.enumtype.ExecStatus
import com.duowan.meteor.server.executor.AbstractTaskExecutor
import com.duowan.meteor.server.util.Logging
import java.util.concurrent.Callable

class InstanceTaskThread(instanceTaskExecutor: InstanceTaskExecutor, threadPool: ThreadPoolExecutor, sourceTaskId: Integer, time: Long, paramMap: Map[String, Any]) extends Callable[String] with Comparable[InstanceTaskThread] with Serializable with Logging {

  val ttime = time
  val df = new SimpleDateFormat("HH:mm:ss")
  
  override def call(): String = {
    val instanceTask = instanceTaskExecutor.instanceTask
    val task = instanceTask.getTask
    instanceTask.setStartTime(new Date)
    instanceTask.setPoolQueueSize(threadPool.getQueue.size)
    instanceTask.setPoolActiveCount(threadPool.getActiveCount)
    if (task.getBeginSleepTime > 0) Thread.sleep(task.getBeginSleepTime)
    instanceTask.setStatus("Running")
    val executor = Class.forName(task.getProgramClass).newInstance().asInstanceOf[AbstractTaskExecutor]
    var retriedTimes = -1
    while (retriedTimes < task.getMaxRetryTimes) {
      try {
        //            ExecutorContext.streamContext.sparkContext.setLocalProperty("spark.scheduler.pool", task.getPriority.toString())
        executor.exec(instanceTaskExecutor, paramMap)
        instanceTask.setStatus(ExecStatus.Success.name())
        retriedTimes = task.getMaxRetryTimes
      } catch {
        case e: Exception => {
          retriedTimes += 1
          val errorMsg = ExceptionUtils.getFullStackTrace(e)
          logError(s"${retriedTimes} -> ${instanceTask.getInstanceFlowId}, ${task.getFileId}, ${task.getFileName}, \n$errorMsg")
          instanceTask.setRetriedTimes(retriedTimes)
          if (retriedTimes == task.getMaxRetryTimes) instanceTask.setStatus(ExecStatus.Fail.name())
          instanceTask.setLog(instanceTask.getLog + errorMsg + "===============\n\n")
          if (task.getRetryInterval > 0) Thread.sleep(task.getRetryInterval)
        }
      }
    }
    instanceTask.setEndTime(new Date)
    logInfo(f"\n\n===${sourceTaskId}%5d  ${task.getFileId}%5d  ${df.format(instanceTask.getStartTime)}  ${df.format(instanceTask.getEndTime)}  ${instanceTask.getEndTime.getTime - instanceTask.getStartTime.getTime}%5d  ${instanceTask.getPoolActiveCount}%5d  ${instanceTask.getPoolQueueSize}%5d  ${instanceTask.getInstanceFlowId}%32s  ${task.getFileName}%-65s")
    instanceTask.getStatus
  }
  
  override def compareTo(o: InstanceTaskThread): Int = {
    val c = ttime - o.ttime
    if (c > 0) {
      1
    } else if (c < 0) {
      -1
    } else {
      0
    }
  }
}
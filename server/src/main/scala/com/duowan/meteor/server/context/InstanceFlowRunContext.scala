package com.duowan.meteor.server.context

import java.util.concurrent.ConcurrentHashMap

object InstanceFlowRunContext {

  val runningCountMap = new ConcurrentHashMap[Integer, Integer]()

  def check(sourceTaskId: Integer, maxConcurrentSize: Integer): Boolean = synchronized {
    var result = false
    val count = runningCountMap.get(sourceTaskId)
    if (count == null) {
      runningCountMap.put(sourceTaskId, 1)
      result = true
    } else if (count < maxConcurrentSize) {
      runningCountMap.put(sourceTaskId, count + 1)
      result = true
    }
    result
  }

  def finish(sourceTaskId: Integer): Unit = synchronized {
    val count = runningCountMap.get(sourceTaskId)
    runningCountMap.put(sourceTaskId, count - 1)
  }  
}
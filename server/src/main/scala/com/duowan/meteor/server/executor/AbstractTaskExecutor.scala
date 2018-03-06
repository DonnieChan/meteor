package com.duowan.meteor.server.executor

import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor

abstract class AbstractTaskExecutor {

  def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit

}
package com.duowan.meteor.server.factory

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object TaskThreadPoolFactory {
  val cachedThreadPool = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 180L, TimeUnit.SECONDS, new SynchronousQueue[Runnable]())
  val threadPoolMap = scala.collection.mutable.Map[Integer, ThreadPoolExecutor]()
}
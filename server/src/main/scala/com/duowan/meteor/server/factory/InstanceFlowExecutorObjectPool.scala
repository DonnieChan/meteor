package com.duowan.meteor.server.factory

import org.apache.commons.pool.BasePoolableObjectFactory
import org.apache.commons.pool.impl.GenericObjectPool

object InstanceFlowExecutorObjectPool {

  val poolMap = scala.collection.mutable.Map[Integer, GenericObjectPool]()

  val config = new GenericObjectPool.Config();
  //最小的空闲对象数
  config.minIdle = 5;
  //休眠多少时间的对象设置为过期
  config.minEvictableIdleTimeMillis = 5 * 60 * 1000l;
  //先进先出策略
  config.lifo = false;

  def getPool(sourceTaskId: Integer): GenericObjectPool = {
    var pool = poolMap.getOrElse(sourceTaskId, null)
    if (pool == null) {
      pool = makeObjectPool(sourceTaskId)
      poolMap += sourceTaskId -> pool
    }
    pool
  }

  def makeObjectPool(sourceTaskId: Integer): GenericObjectPool = {
    val factory = new InstanceFlowOjectPoolFactory(sourceTaskId);
    new GenericObjectPool(factory, config);
  }

  class InstanceFlowOjectPoolFactory(sourceTaskId: Integer) extends BasePoolableObjectFactory {
    override def makeObject(): Object = {
      return InstanceFlowExecutorFactory.getInstanceFlowExecutor(sourceTaskId);
    }
  }
}
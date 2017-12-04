package com.duowan.meteor.server.context

import org.apache.commons.lang3.StringUtils

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ProtocolVersion
import com.datastax.driver.core.QueryOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.SocketOptions
import com.datastax.driver.core.policies.ConstantReconnectionPolicy
import com.datastax.driver.core.policies.RoundRobinPolicy
import com.datastax.driver.core.policies.TokenAwarePolicy
import com.duowan.meteor.server.util.Logging

/**
 * Created by chenwu on 2015/5/12 0012.
 */
object CassandraContextSingleton extends Logging {
  @transient private var session: Session = null
  private val preparedStatementMap = scala.collection.mutable.Map[String, PreparedStatement]()

  def getSession(): Session = {
    var result = session
    if (result == null) result = initSession()
    result
  }

  def initSession(): Session = synchronized {
    if (session == null) {
      logInfo("CassandraContextSingleton.initSession")
      val contactPoints = StringUtils.split(ExecutorContext.cassandraClusterHosts, ",")
      val cluster = Cluster.builder().addContactPoints(contactPoints(0), contactPoints(1), contactPoints(2))
        .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.ONE).setSerialConsistencyLevel(ConsistencyLevel.LOCAL_SERIAL))
        .withLoadBalancingPolicy(new TokenAwarePolicy(new RoundRobinPolicy()))
        .withProtocolVersion(ProtocolVersion.V3)
        .withPoolingOptions(new PoolingOptions().setMaxSimultaneousRequestsPerHostThreshold(HostDistance.LOCAL, 32768).setMaxSimultaneousRequestsPerHostThreshold(HostDistance.REMOTE, 8192))
        //        .withRetryPolicy(FallthroughRetryPolicy.INSTANCE)
        .withReconnectionPolicy(new ConstantReconnectionPolicy(1000))
        //socket参数含义参考：http://elf8848.iteye.com/blog/1739598
        .withSocketOptions(new SocketOptions().setKeepAlive(true).setTcpNoDelay(true))
        .build()
      session = cluster.connect()
    }
    session
  }

  def getPreparedStatement(sql: String, table: String, expireSeconds: Integer): PreparedStatement = {
    var result = preparedStatementMap.getOrElse(sql, null)
    if (result == null) result = initPreparedStatement(sql, table, expireSeconds)
    result
  }

  def initPreparedStatement(sql: String, table: String, expireSeconds: Integer): PreparedStatement = synchronized {
    var result = preparedStatementMap.getOrElse(sql, null)
    if (result == null) {
      val keySpace = StringUtils.substringBefore(table, ".")
      getSession().execute(s"CREATE KEYSPACE IF NOT EXISTS $keySpace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}")
      getSession().execute(s"CREATE TABLE IF NOT EXISTS $table (key text, value text, primary key(key)) WITH bloom_filter_fp_chance=0.1 AND compaction={'class': 'SizeTieredCompactionStrategy'} AND caching={'keys':'ALL', 'rows_per_partition':'NONE'} AND default_time_to_live=$expireSeconds")

      logInfo(s"CassandraContextSingleton.initPreparedStatement: $sql")
      result = getSession().prepare(sql)
      preparedStatementMap += sql -> result
    }
    result
  }

}

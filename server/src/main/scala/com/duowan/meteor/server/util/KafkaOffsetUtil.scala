package com.duowan.meteor.server.util

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.factory.TaskThreadPoolFactory
import kafka.common.TopicAndPartition
import kafka.client.ClientUtils
import kafka.api.PartitionOffsetRequestInfo
import kafka.consumer.SimpleConsumer
import kafka.api.OffsetRequest
import scala.collection.mutable

/**
 * 参考kafka.tools.GetOffsetShell
 */
object KafkaOffsetUtil extends Logging {

  def getLastOffset(brokerList: String, topics: mutable.Set[String], clientId: String): mutable.Map[TopicAndPartition, Long] = {
    val result = mutable.Map[TopicAndPartition, Long]()
    for (topic: String <- topics) {
      result ++= getOffset(brokerList, topic, clientId, -1)
    }
    result
  }
  
  def getEarliestOffset(brokerList: String, topics: mutable.Set[String], clientId: String): mutable.Map[TopicAndPartition, Long] = {
    val result = mutable.Map[TopicAndPartition, Long]()
    for (topic: String <- topics) {
      result ++= getOffset(brokerList, topic, clientId, -2)
    }
    result
  }
  
  def getOffset(brokerList: String, topic: String, clientId: String, time:Long): mutable.Map[TopicAndPartition, Long] = {
    val result = mutable.Map[TopicAndPartition, Long]()
    val metadataTargetBrokers = ClientUtils.parseBrokerList(brokerList)
    val maxWaitMs = 10000
    val nOffsets = 1
    val topicsMetadata = ClientUtils.fetchTopicMetadata(Set(topic), metadataTargetBrokers, clientId, maxWaitMs).topicsMetadata
    if(topicsMetadata.size != 1 || !topicsMetadata(0).topic.equals(topic)) {
      doException(("Error: no valid topic metadata for topic: %s,  probably the topic does not exist.").format(topic))
    }
    val partitions = topicsMetadata.head.partitionsMetadata.map(_.partitionId)
    partitions.foreach { partitionId =>
      val partitionMetadataOpt = topicsMetadata.head.partitionsMetadata.find(_.partitionId == partitionId)
      partitionMetadataOpt match {
        case Some(metadata) =>
          metadata.leader match {
            case Some(leader) =>
              var consumer: SimpleConsumer = null
              try {
                consumer = new SimpleConsumer(leader.host, leader.port, 10000, 100000, clientId)
                val topicAndPartition = TopicAndPartition(topic, partitionId)
                val request = OffsetRequest(Map(topicAndPartition -> PartitionOffsetRequestInfo(time, nOffsets)))
                val offsets = consumer.getOffsetsBefore(request).partitionErrorAndOffsets(topicAndPartition).offsets
                result += topicAndPartition -> offsets.mkString(",").toLong
              } finally {
                if (consumer != null) {
                  consumer.close()
                }
              }
            case None => doException("Error: partition %d does not have a leader. Skip getting offsets".format(partitionId))
          }
        case None => doException("Error: partition %d does not exist".format(partitionId))
      }
    }
    result
  }
  
  def doException(errorMsg: String): Unit = {
    log.error(errorMsg)
    throw new Exception(errorMsg)
  }
}

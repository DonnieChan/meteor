package com.duowan.meteor.server.util

import java.util
import java.util.Properties

import scala.collection.JavaConverters
import scala.collection.mutable

import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import com.duowan.meteor.server.context.ExecutorContext

import kafka.common.TopicAndPartition

/**
 * 参考kafka.tools.GetOffsetShell
 */
object KafkaOffsetUtil {
  def main(args: Array[String]) {
    print(Math.abs("33".hashCode) % 40);
  }
  private val log: Log = LogFactory.getLog(KafkaOffsetUtil.getClass)
  def getLastOffset(brokerList: String, topics: mutable.Set[String], clientId: String): mutable.Map[TopicAndPartition, Long] = {
    val result = mutable.Map[TopicAndPartition, Long]()
    for (topic: String <- topics) {
      result ++= getLastOffset(brokerList, topic, clientId)
    }
    result
  }

  def getEarliestOffset(brokerList: String, topics: mutable.Set[String], clientId: String): mutable.Map[TopicAndPartition, Long] = {
    val result = mutable.Map[TopicAndPartition, Long]()
    for (topic: String <- topics) {
      result ++= getEarliestOffset(brokerList, topic, clientId)
    }
    result
  }

  def getLastOffset(brokerList: String, topic: String, clientId: String): mutable.Map[TopicAndPartition, Long] = {
    val result = mutable.Map[TopicAndPartition, Long]()

    val properties = new Properties()
    val deserializer = (new StringDeserializer).getClass.getName
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList)
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, clientId + "_getLastOffset")
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer)
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
    if (StringUtils.equals(ExecutorContext.isKafkaAuth, "true")) {
      properties.put("security.protocol", "SASL_PLAINTEXT")
      properties.put("sasl.mechanism", "PLAIN")
    }
    import scala.collection.JavaConverters._
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer(properties)

    try {
      val partitions: mutable.Buffer[PartitionInfo] = consumer.partitionsFor(topic).asScala
      val topicPartitions = new util.ArrayList[TopicPartition]()
      for (partition: PartitionInfo <- partitions) {
        topicPartitions.add(new TopicPartition(partition.topic(), partition.partition()))
      }
      val topicSet = new util.HashSet[String]()
      topicSet.add(topic)
      consumer.subscribe(topicSet)
      var isEmpty = false
      while (!isEmpty) {
        val result = consumer.poll(100)
        if (!result.isEmpty) {
          isEmpty = true
        }
      }
      consumer.seekToEnd(topicPartitions)
      log.info("====assignment:" + partitions)
      for (partition: TopicPartition <- topicPartitions.asScala) {
        result += (new TopicAndPartition(partition.topic(), partition.partition()) -> consumer.position(partition))
      }
    } finally {
      consumer.close()
    }
    result
  }

  def getEarliestOffset(brokerList: String, topic: String, clientId: String): mutable.Map[TopicAndPartition, Long] = {
    val result = mutable.Map[TopicAndPartition, Long]()

    val properties = new Properties()
    val deserializer = (new StringDeserializer).getClass.getName
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList)
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, clientId + "getEarliestOffset")
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer)
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
    if (StringUtils.equals(ExecutorContext.isKafkaAuth, "true")) {
      properties.put("security.protocol", "SASL_PLAINTEXT")
      properties.put("sasl.mechanism", "PLAIN")
    }

    import scala.collection.JavaConverters._
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer(properties)
    try {
      val partitions: mutable.Buffer[PartitionInfo] = consumer.partitionsFor(topic).asScala
      val topicPartitions = new util.ArrayList[TopicPartition]()
      for (partition: PartitionInfo <- partitions) {
        topicPartitions.add(new TopicPartition(partition.topic(), partition.partition()))
      }

      val topicSet = new util.HashSet[String]()
      topicSet.add(topic)
      consumer.subscribe(topicSet)
      var isEmpty = false
      while (!isEmpty) {
        val result = consumer.poll(100)
        if (!result.isEmpty) {
          isEmpty = true
        }
      }
      consumer.seekToBeginning(topicPartitions)
      log.info("====assignment:" + partitions)
      for (partition: TopicPartition <- topicPartitions.asScala) {
        result += (new TopicAndPartition(partition.topic(), partition.partition()) -> consumer.position(partition))
      }
    } finally {
      consumer.close()
    }
    result
  }

  def doException(errorMsg: String): Unit = {
    throw new Exception(errorMsg)
  }
}

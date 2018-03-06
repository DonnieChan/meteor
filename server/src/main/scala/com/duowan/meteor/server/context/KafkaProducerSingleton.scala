package com.duowan.meteor.server.context

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer

/**
 * Created by chenwu on 2015/5/12 0012.
 */
object KafkaProducerSingleton {

  private val producerStrMap = scala.collection.mutable.Map[String, KafkaProducer[String, String]]()
  private val producerByteMap = scala.collection.mutable.Map[String, KafkaProducer[String, Array[Byte]]]()

  def getInstance(brokers: String): KafkaProducer[String, String] = {
    val key = brokers + "Str"
    var result = producerStrMap.getOrElse(key, null)
    if (result == null) result = initInstance(brokers, key)
    result
  }

  def initInstance(brokers: String, key: String): KafkaProducer[String, String] = synchronized {
    var producer = producerStrMap.getOrElse(key, null)
    if (producer == null) {
      val props = new Properties()
      props.put("bootstrap.servers", brokers)
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("acks", "1")
      props.put("retries", new Integer(5));
      props.put("batch.size", new Integer(102400));
      props.put("linger.ms", new Integer(100));
      props.put("buffer.memory", new Integer(33554432));
      producer = new KafkaProducer[String, String](props)
      producerStrMap += key -> producer
    }
    producer
  }

  def getInstanceByte(brokers: String): KafkaProducer[String, Array[Byte]] = {
    val key = brokers + "Byte"
    var result = producerByteMap.getOrElse(key, null)
    if (result == null) result = initInstanceByte(brokers, key)
    result
  }

  def initInstanceByte(brokers: String, key: String): KafkaProducer[String, Array[Byte]] = synchronized {
    var producer = producerByteMap.getOrElse(key, null)
    if (producer == null) {
      val props = new Properties()
      props.put("bootstrap.servers", brokers)
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
      props.put("acks", "1")
      props.put("retries", new Integer(5));
      props.put("batch.size", new Integer(16384));
      props.put("linger.ms", new Integer(500));
      props.put("buffer.memory", new Integer(33554432));
      producer = new KafkaProducer[String, Array[Byte]](props)
      producerByteMap += key -> producer
    }
    producer
  }

}

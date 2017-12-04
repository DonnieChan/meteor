package com.duowan.meteor.server.executor

import scala.collection.mutable.ListBuffer
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.annotation.Experimental
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.MapType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType
import org.apache.spark.storage.StorageLevel
import com.duowan.meteor.model.view.importqueue.ImportKafkaTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor

class ImportKafkaTaskExecutor extends AbstractTaskExecutor {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
		val spark = ExecutorContext.spark
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[ImportKafkaTask]
    val rdd = paramMap.get("rdd").get.asInstanceOf[RDD[String]]
		instanceTaskExecutor.instanceFlowExecutor.rdd = rdd
		
		rdd.persist(StorageLevel.MEMORY_ONLY)
    val one = rdd.first()
    val oneRDD: RDD[String] = spark.sparkContext.parallelize(Seq(one), 1)
//    import spark.implicits._
//    val oneDataSet = spark.createDataset(oneRDD)
    val oneSchema = spark.read.json(oneRDD).schema
    

    val list = ListBuffer[StructField]()
    for (sf <- oneSchema.iterator) {
      if (sf.dataType.isInstanceOf[StructType]) {
        list += StructField(sf.name, MapType(StringType, StringType, true))
      } else {
        list += sf
      }
    }
    val mapTypeOneSchema = StructType(list.seq)

//    val rddDataSet = spark.createDataset(rdd)
    val reader = spark.read
    reader.schema(mapTypeOneSchema)
    reader.json(rdd).createOrReplaceTempView(task.getRegTable)
    spark.sql(s"CACHE TABLE ${task.getRegTable}")

//    rdd.unpersist(false)
  }

}
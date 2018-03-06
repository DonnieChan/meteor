package com.duowan.meteor.server.util

import java.io.StringReader
import java.util.ArrayList
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Future

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONObject

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.storage.StorageLevel

import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.factory.TaskThreadPoolFactory

import net.sf.jsqlparser.expression.AllComparisonExpression
import net.sf.jsqlparser.expression.AnalyticExpression
import net.sf.jsqlparser.expression.AnyComparisonExpression
import net.sf.jsqlparser.expression.CaseExpression
import net.sf.jsqlparser.expression.CastExpression
import net.sf.jsqlparser.expression.DateValue
import net.sf.jsqlparser.expression.DoubleValue
import net.sf.jsqlparser.expression.ExpressionVisitor
import net.sf.jsqlparser.expression.ExtractExpression
import net.sf.jsqlparser.expression.HexValue
import net.sf.jsqlparser.expression.IntervalExpression
import net.sf.jsqlparser.expression.JdbcNamedParameter
import net.sf.jsqlparser.expression.JdbcParameter
import net.sf.jsqlparser.expression.JsonExpression
import net.sf.jsqlparser.expression.KeepExpression
import net.sf.jsqlparser.expression.LongValue
import net.sf.jsqlparser.expression.MySQLGroupConcat
import net.sf.jsqlparser.expression.NullValue
import net.sf.jsqlparser.expression.NumericBind
import net.sf.jsqlparser.expression.OracleHierarchicalExpression
import net.sf.jsqlparser.expression.OracleHint
import net.sf.jsqlparser.expression.Parenthesis
import net.sf.jsqlparser.expression.RowConstructor
import net.sf.jsqlparser.expression.SignedExpression
import net.sf.jsqlparser.expression.StringValue
import net.sf.jsqlparser.expression.TimeValue
import net.sf.jsqlparser.expression.TimestampValue
import net.sf.jsqlparser.expression.UserVariable
import net.sf.jsqlparser.expression.WhenClause
import net.sf.jsqlparser.expression.WithinGroupExpression
import net.sf.jsqlparser.expression.operators.arithmetic.Addition
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor
import net.sf.jsqlparser.expression.operators.arithmetic.Concat
import net.sf.jsqlparser.expression.operators.arithmetic.Division
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction
import net.sf.jsqlparser.expression.operators.conditional.AndExpression
import net.sf.jsqlparser.expression.operators.conditional.OrExpression
import net.sf.jsqlparser.expression.operators.relational.Between
import net.sf.jsqlparser.expression.operators.relational.EqualsTo
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression
import net.sf.jsqlparser.expression.operators.relational.GreaterThan
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals
import net.sf.jsqlparser.expression.operators.relational.InExpression
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression
import net.sf.jsqlparser.expression.operators.relational.LikeExpression
import net.sf.jsqlparser.expression.operators.relational.Matches
import net.sf.jsqlparser.expression.operators.relational.MinorThan
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.AllColumns
import net.sf.jsqlparser.statement.select.AllTableColumns
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SelectExpressionItem
import net.sf.jsqlparser.statement.select.SelectItem
import net.sf.jsqlparser.statement.select.SelectItemVisitor
import net.sf.jsqlparser.statement.select.SubSelect

/**
 * Created by Administrator on 2015/8/19 0019.
 */
object CustomSQLUtil extends Logging {

  JSON.globalNumberParser = x => {
    if (x.contains(".")) {
      x.toDouble
    } else {
      x.toLong
    }
  }

  def execSql(tablePreStr: String, sql: String): String = {
    val stmt = CCJSqlParserUtil.parse(new StringReader(sql))
    val selectBody = stmt.asInstanceOf[Select].getSelectBody().asInstanceOf[PlainSelect]
    val groupByItems = selectBody.getGroupByColumnReferences()
    val fromTable = selectBody.getFromItem()

    var resultTableName = ""
    var fromTableName = ""
    var dropTableFlag = true
    if (fromTable.isInstanceOf[SubSelect]) {
      fromTableName = execSql(tablePreStr, fromTable.asInstanceOf[SubSelect].getSelectBody.toString())
    } else {
      fromTableName = fromTable.toString()
      dropTableFlag = false
    }

    if (groupByItems == null) {
      resultTableName = s"${tablePreStr}_${UUID.randomUUID().toString().replace("-", "")}"
      selectBody.setFromItem(new Table(fromTableName));
      ExecutorContext.spark.sql(s"cache table $resultTableName as ${selectBody.toString()}")
    } else {
      resultTableName = execMapPartitions(tablePreStr, sql, fromTableName)
    }

    if (dropTableFlag) {
      DropTableUtil.dropTable(fromTableName)
    }
    resultTableName
  }

  def execMapPartitions(tablePreStr: String, sql: String, fromTableName: String): String = {
    val dataFrame: DataFrame = ExecutorContext.spark.table(fromTableName)
    val spark = ExecutorContext.spark
    import spark.implicits._
    val mapPartitionRDD = dataFrame.mapPartitions { p =>
      {
        val result = scala.collection.mutable.ListBuffer[String]()
        val aggResultMapList = CustomSQLUtil.exec(sql, p)
        for (aggResultMap <- aggResultMapList) {
          for (aggRowMap <- aggResultMap) {
            result += JSONObject(aggRowMap.toMap).toString()
          }
        }
        result.toIterator
      }
    }

    var resultTableName = s"${tablePreStr}_${UUID.randomUUID().toString().replace("-", "")}"
    mapPartitionRDD.persist(StorageLevel.MEMORY_ONLY)

    var one: String = null
    try {
      one = mapPartitionRDD.first()
    } catch {
      case e: Exception => logError(fromTableName + ", " + e.getMessage)
    }
    if (one != null) {
      val oneRDD = ExecutorContext.spark.sparkContext.parallelize(Seq(one), 1)
      //      val oneDataSet = spark.createDataset(oneRDD)
      val oneSchema = spark.read.json(oneRDD).schema

      val reader = spark.read
      reader.schema(oneSchema)
      reader.json(mapPartitionRDD.rdd).createOrReplaceTempView(resultTableName)

      ExecutorContext.spark.sql(s"CACHE TABLE $resultTableName")
    } else {
      resultTableName = ""
    }
    mapPartitionRDD.unpersist(false)
    resultTableName
  }

  def exec(sql: String, p: Iterator[Row]): ListBuffer[Iterable[scala.collection.mutable.Map[String, Any]]] = {
    val dataList = p.toList
    val stmt = CCJSqlParserUtil.parse(new StringReader(sql))
    val selectBody = stmt.asInstanceOf[Select].getSelectBody().asInstanceOf[PlainSelect]

    val selectItems = selectBody.getSelectItems().toList

    val groupByList = selectBody.getGroupByColumnReferences()
    var nThread = 1L
    val groupByListLastIndex = groupByList.size() - 1
    val lastExp = groupByList.get(groupByListLastIndex)
    if (lastExp.isInstanceOf[LongValue]) {
      nThread = lastExp.asInstanceOf[LongValue].getValue
      groupByList.remove(groupByListLastIndex)
    }
    val groupByItems = groupByList.toArray().map { x => x.toString() }

    val result = ListBuffer[Iterable[scala.collection.mutable.Map[String, Any]]]()
    if (nThread <= 1L || dataList.size <= nThread) {
      result += execSub(dataList, groupByItems, selectItems)
    } else {
      val splitSize = dataList.size / nThread
      var i = 0
      var subElemList = ListBuffer[Row]()
      val futureList = ListBuffer[Future[Iterable[scala.collection.mutable.Map[String, Any]]]]()
      for (r <- dataList) {
        i += 1
        subElemList += r
        if (i == splitSize) {
          futureList += execSubThread(subElemList.toList, groupByItems, selectItems)
          i = 0
          subElemList = ListBuffer[Row]()
        }
      }

      if (i > 0) {
        futureList += execSubThread(subElemList.toList, groupByItems, selectItems)
      }

      for (future <- futureList) {
        result += future.get
      }
    }

    result
  }

  def execSubThread(p: List[Row], groupByItems: Array[String], selectItems: List[SelectItem]): Future[Iterable[scala.collection.mutable.Map[String, Any]]] = {
    TaskThreadPoolFactory.cachedThreadPool.submit(new Callable[Iterable[scala.collection.mutable.Map[String, Any]]]() {
      override def call(): Iterable[scala.collection.mutable.Map[String, Any]] = {
        execSub(p, groupByItems, selectItems)
      }
    })
  }

  def execSub(p: List[Row], groupByItems: Array[String], selectItems: List[SelectItem]): Iterable[scala.collection.mutable.Map[String, Any]] = {
    val aggResultMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Any]]()
    val allFuncOpMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Any]]()
    val colFutureList = ListBuffer[Future[Tuple2[String, scala.collection.mutable.Map[String, Any]]]]()

    for (selectItem <- selectItems) {
      selectItem.accept(new SelectItemVisitor() {
        override def visit(allColumns: AllColumns): Unit = {
          // TODO Auto-generated method stub

        }

        override def visit(allTableColumns: AllTableColumns): Unit = {
          // TODO Auto-generated method stub

        }

        override def visit(selectExpressionItem: SelectExpressionItem): Unit = {
          var alias = ""
          if (selectExpressionItem.getAlias != null) alias = selectExpressionItem.getAlias.getName
          selectExpressionItem.getExpression().accept(new ExpressionVisitor() {

            override def visit(longValue: LongValue): Unit = {
            }

            override def visit(stringValue: StringValue): Unit = {
            }

            override def visit(tableColumn: Column): Unit = {
            }

            override def visit(function: net.sf.jsqlparser.expression.Function): Unit = {
              val funcName = function.getName()
              if (StringUtils.equalsIgnoreCase(funcName, "c_count_distinct")) {
                val future = TaskThreadPoolFactory.cachedThreadPool.submit(new Callable[Tuple2[String, scala.collection.mutable.Map[String, Any]]]() {
                  override def call(): Tuple2[String, scala.collection.mutable.Map[String, Any]] = {
                    val paramList = function.getParameters().getExpressions()
                    val redisMultiName = paramList(0).asInstanceOf[StringValue].getValue
                    val redisPreKey = paramList(1).asInstanceOf[StringValue].getValue
                    val keyCols = paramList(2).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }
                    val valCols = paramList(3).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }
                    val batchSize = paramList(4).asInstanceOf[LongValue].getValue
                    val expireSeconds = paramList(5).asInstanceOf[LongValue].getValue
                    val isAccurate = paramList(6).asInstanceOf[LongValue].getValue
                    val funcOpMap = scala.collection.mutable.Map[String, Any]()
                    val valSetMap = scala.collection.mutable.Map[String, java.util.HashSet[String]]()

                    for (r <- p) {
                      val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                      val valColValues = valCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                      val redisKey = s"$keyColValues|$redisPreKey"
                      val redisKeyMD5 = DigestUtils.md5Hex(redisKey)
                      val valSet = valSetMap.getOrElseUpdate(redisKeyMD5, new java.util.HashSet[String]())
                      valSet.add(valColValues)
                    }

                    val jedisList = RedisMultiUtil.getResourceList(redisMultiName)
                    val jedisListSize = jedisList.size

                    val keyListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                    val valListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                    for (i <- 0 until jedisListSize) {
                      keyListList.add(new ArrayList[String]())
                      val valList = new ArrayList[String]()
                      valList.add(isAccurate.toString())
                      valList.add(expireSeconds.toString())
                      valListList.add(valList)
                    }

                    val n = batchSize.toInt
                    try {
                      for ((redisKeyMD5, valSet) <- valSetMap) {
                        val index = Math.abs(redisKeyMD5.hashCode() % jedisListSize)
                        val keyList = keyListList.get(index)
                        val valList = valListList.get(index)
                        if (valSet.size() <= batchSize) {
                          keyList.add(redisKeyMD5)
                          valList.add(valSet.size().toString())
                          valList.addAll(valSet)
                        } else {
                          val tmpValList = new ArrayList[String](valSet)
                          val part = tmpValList.size() / n
                          val remain = tmpValList.size() % n
                          for (i <- 0 until part) {
                            val subList = tmpValList.subList(n * i, n * (i + 1))
                            keyList.add(redisKeyMD5)
                            valList.add(n.toString())
                            valList.addAll(subList)
                          }
                          if (remain > 0) {
                            val subList = tmpValList.subList(n * part, tmpValList.size())
                            keyList.add(redisKeyMD5)
                            valList.add(remain.toString())
                            valList.addAll(subList)
                          }
                        }
                        if (valList.size() >= batchSize) {
                          val result = jedisList(index).evalsha(RedisMultiUtil.uvScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                          val resultSize = result.size - 2
                          var i = 0
                          while (i <= resultSize) {
                            funcOpMap.put(result.get(i).toString(), result.get(i + 1))
                            i = i + 2
                          }
                          keyList.clear()
                          valList.clear()
                          valList.add(isAccurate.toString())
                          valList.add(expireSeconds.toString())
                        }
                      }
                      valSetMap.clear()

                      for (index <- 0 until jedisListSize) {
                        val keyList = keyListList(index)
                        if (!keyList.isEmpty()) {
                          val valList = valListList(index)
                          val result = jedisList(index).evalsha(RedisMultiUtil.uvScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                          val resultSize = result.size - 2
                          var i = 0
                          while (i <= resultSize) {
                            funcOpMap.put(result.get(i).toString(), result.get(i + 1))
                            i = i + 2
                          }
                          keyList.clear()
                          valList.clear()
                        }
                      }
                    } finally {
                      jedisList.map(jedis => jedis.close)
                    }
                    (alias, funcOpMap)
                  }
                })
                colFutureList += future

              } else if (StringUtils.equalsIgnoreCase(funcName, "c_sum")) {
                val future = TaskThreadPoolFactory.cachedThreadPool.submit(new Callable[Tuple2[String, scala.collection.mutable.Map[String, Any]]]() {
                  override def call(): Tuple2[String, scala.collection.mutable.Map[String, Any]] = {
                    val paramList = function.getParameters().getExpressions()
                    val redisMultiName = paramList(0).asInstanceOf[StringValue].getValue
                    val redisPreKey = paramList(1).asInstanceOf[StringValue].getValue
                    val partitionCol = paramList(2).asInstanceOf[Column].getColumnName
                    val keyCols = paramList(3).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }
                    val valCol = paramList(4).asInstanceOf[Column].getColumnName
                    val batchSize = paramList(5).asInstanceOf[LongValue].getValue
                    val expireSeconds = paramList(6).asInstanceOf[LongValue].getValue
                    val funcOpMap = scala.collection.mutable.Map[String, Any]()
                    val parMapKeyMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Long]]()

                    for (r <- p) {
                      val partitionColValue = r.getAs(partitionCol).toString()
                      val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                      val pKeyColValues = s"$keyColValues|$partitionColValue"
                      val pKeyColValuesMD5 = DigestUtils.md5Hex(pKeyColValues)
                      val valColValue = r.getAs[Long](valCol)
                      val keyMap = parMapKeyMap.getOrElseUpdate(partitionColValue, scala.collection.mutable.Map[String, Long]())
                      val sum = keyMap.getOrElseUpdate(pKeyColValuesMD5, 0L)
                      keyMap.put(pKeyColValuesMD5, sum + valColValue)
                    }

                    val jedisList = RedisMultiUtil.getResourceList(redisMultiName)
                    val jedisListSize = jedisList.size

                    try {
                      for ((partitionColValue, keyMap) <- parMapKeyMap) {
                        val redisKey = s"$redisPreKey|$partitionColValue"
                        val keyListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                        val valListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                        for (i <- 0 until jedisListSize) {
                          val keyList = new ArrayList[String]()
                          keyListList.add(keyList)
                          val valList = new ArrayList[String]()
                          valList.add(redisKey)
                          valList.add(expireSeconds.toString())
                          valListList.add(valList)
                        }

                        for ((pKeyColValuesMD5, sum) <- keyMap) {
                          val index = Math.abs(pKeyColValuesMD5.hashCode() % jedisListSize)
                          val keyList = keyListList.get(index)
                          val valList = valListList.get(index)
                          keyList.add(pKeyColValuesMD5)
                          valList.add(sum.toString())
                          if (keyList.size() >= batchSize) {
                            val result = jedisList(index).evalsha(RedisMultiUtil.sumScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                            val resultSize = result.size - 2
                            var i = 0
                            while (i <= resultSize) {
                              funcOpMap.put(result.get(i).toString(), result.get(i + 1))
                              i = i + 2
                            }
                            keyList.clear()
                            valList.clear()
                            valList.add(redisKey)
                            valList.add(expireSeconds.toString())
                          }
                        }

                        for (index <- 0 until jedisListSize) {
                          val keyList = keyListList(index)
                          if (!keyList.isEmpty()) {
                            val valList = valListList(index)
                            val result = jedisList(index).evalsha(RedisMultiUtil.sumScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                            val resultSize = result.size - 2
                            var i = 0
                            while (i <= resultSize) {
                              funcOpMap.put(result.get(i).toString(), result.get(i + 1))
                              i = i + 2
                            }
                            keyList.clear()
                            valList.clear()
                          }
                        }
                      }
                      parMapKeyMap.clear()
                    } finally {
                      jedisList.map(jedis => jedis.close)
                    }
                    (alias, funcOpMap)
                  }
                })
                colFutureList += future

              } else if (StringUtils.equalsIgnoreCase(funcName, "c_max")) {
                val future = TaskThreadPoolFactory.cachedThreadPool.submit(new Callable[Tuple2[String, scala.collection.mutable.Map[String, Any]]]() {
                  override def call(): Tuple2[String, scala.collection.mutable.Map[String, Any]] = {
                    val paramList = function.getParameters().getExpressions()
                    val redisMultiName = paramList(0).asInstanceOf[StringValue].getValue
                    val redisPreKey = paramList(1).asInstanceOf[StringValue].getValue
                    val partitionCol = paramList(2).asInstanceOf[Column].getColumnName
                    val keyCols = paramList(3).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }
                    val valCol = paramList(4).asInstanceOf[Column].getColumnName
                    val batchSize = paramList(5).asInstanceOf[LongValue].getValue
                    val expireSeconds = paramList(6).asInstanceOf[LongValue].getValue
                    val funcOpMap = scala.collection.mutable.Map[String, Any]()
                    val parMapKeyMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Long]]()

                    for (r <- p) {
                      val partitionColValue = r.getAs(partitionCol).toString()
                      val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                      val pKeyColValues = s"$keyColValues|$partitionColValue"
                      val pKeyColValuesMD5 = DigestUtils.md5Hex(pKeyColValues)
                      val valColValue = r.getAs[Long](valCol)
                      val keyMap = parMapKeyMap.getOrElseUpdate(partitionColValue, scala.collection.mutable.Map[String, Long]())
                      val max = keyMap.getOrElseUpdate(pKeyColValuesMD5, 0L)
                      if (max < valColValue) {
                        keyMap.put(pKeyColValuesMD5, valColValue)
                      }
                    }

                    val jedisList = RedisMultiUtil.getResourceList(redisMultiName)
                    val jedisListSize = jedisList.size

                    try {
                      for ((partitionColValue, keyMap) <- parMapKeyMap) {
                        val redisKey = s"$redisPreKey|$partitionColValue"
                        val keyListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                        val valListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                        for (i <- 0 until jedisListSize) {
                          val keyList = new ArrayList[String]()
                          keyListList.add(keyList)
                          val valList = new ArrayList[String]()
                          valList.add(redisKey)
                          valList.add(expireSeconds.toString())
                          valListList.add(valList)
                        }

                        for ((pKeyColValuesMD5, max) <- keyMap) {
                          val index = Math.abs(pKeyColValuesMD5.hashCode() % jedisListSize)
                          val keyList = keyListList.get(index)
                          val valList = valListList.get(index)
                          keyList.add(pKeyColValuesMD5)
                          valList.add(max.toString())
                          if (keyList.size() >= batchSize) {
                            val result = jedisList(index).evalsha(RedisMultiUtil.maxScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                            val resultSize = result.size - 2
                            var i = 0
                            while (i <= resultSize) {
                              funcOpMap.put(result.get(i).toString(), result.get(i + 1).toString().toLong)
                              i = i + 2
                            }
                            keyList.clear()
                            valList.clear()
                            valList.add(redisKey)
                            valList.add(expireSeconds.toString())
                          }
                        }

                        for (index <- 0 until jedisListSize) {
                          val keyList = keyListList(index)
                          if (!keyList.isEmpty()) {
                            val valList = valListList(index)
                            val result = jedisList(index).evalsha(RedisMultiUtil.maxScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                            val resultSize = result.size - 2
                            var i = 0
                            while (i <= resultSize) {
                              funcOpMap.put(result.get(i).toString(), result.get(i + 1).toString().toLong)
                              i = i + 2
                            }
                            keyList.clear()
                            valList.clear()
                          }
                        }
                      }
                      parMapKeyMap.clear()
                    } finally {
                      jedisList.map(jedis => jedis.close)
                    }
                    (alias, funcOpMap)
                  }
                })
                colFutureList += future

              } else if (StringUtils.equalsIgnoreCase(funcName, "c_min")) {
                val future = TaskThreadPoolFactory.cachedThreadPool.submit(new Callable[Tuple2[String, scala.collection.mutable.Map[String, Any]]]() {
                  override def call(): Tuple2[String, scala.collection.mutable.Map[String, Any]] = {
                    val paramList = function.getParameters().getExpressions()
                    val redisMultiName = paramList(0).asInstanceOf[StringValue].getValue
                    val redisPreKey = paramList(1).asInstanceOf[StringValue].getValue
                    val partitionCol = paramList(2).asInstanceOf[Column].getColumnName
                    val keyCols = paramList(3).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }
                    val valCol = paramList(4).asInstanceOf[Column].getColumnName
                    val batchSize = paramList(5).asInstanceOf[LongValue].getValue
                    val expireSeconds = paramList(6).asInstanceOf[LongValue].getValue
                    val funcOpMap = scala.collection.mutable.Map[String, Any]()
                    val parMapKeyMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Long]]()

                    for (r <- p) {
                      val partitionColValue = r.getAs(partitionCol).toString()
                      val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                      val pKeyColValues = s"$keyColValues|$partitionColValue"
                      val pKeyColValuesMD5 = DigestUtils.md5Hex(pKeyColValues)
                      val valColValue = r.getAs[Long](valCol)
                      val keyMap = parMapKeyMap.getOrElseUpdate(partitionColValue, scala.collection.mutable.Map[String, Long]())
                      val min = keyMap.getOrElseUpdate(pKeyColValuesMD5, 0L)
                      if (min > valColValue) {
                        keyMap.put(pKeyColValuesMD5, valColValue)
                      }
                    }

                    val jedisList = RedisMultiUtil.getResourceList(redisMultiName)
                    val jedisListSize = jedisList.size

                    try {
                      for ((partitionColValue, keyMap) <- parMapKeyMap) {
                        val redisKey = s"$redisPreKey|$partitionColValue"
                        val keyListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                        val valListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                        for (i <- 0 until jedisListSize) {
                          val keyList = new ArrayList[String]()
                          keyListList.add(keyList)
                          val valList = new ArrayList[String]()
                          valList.add(redisKey)
                          valList.add(expireSeconds.toString())
                          valListList.add(valList)
                        }

                        for ((pKeyColValuesMD5, min) <- keyMap) {
                          val index = Math.abs(pKeyColValuesMD5.hashCode() % jedisListSize)
                          val keyList = keyListList.get(index)
                          val valList = valListList.get(index)
                          keyList.add(pKeyColValuesMD5)
                          valList.add(min.toString())
                          if (keyList.size() >= batchSize) {
                            val result = jedisList(index).evalsha(RedisMultiUtil.minScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                            val resultSize = result.size - 2
                            var i = 0
                            while (i <= resultSize) {
                              funcOpMap.put(result.get(i).toString(), result.get(i + 1).toString().toLong)
                              i = i + 2
                            }
                            keyList.clear()
                            valList.clear()
                            valList.add(redisKey)
                            valList.add(expireSeconds.toString())
                          }
                        }

                        for (index <- 0 until jedisListSize) {
                          val keyList = keyListList(index)
                          if (!keyList.isEmpty()) {
                            val valList = valListList(index)
                            val result = jedisList(index).evalsha(RedisMultiUtil.minScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                            val resultSize = result.size - 2
                            var i = 0
                            while (i <= resultSize) {
                              funcOpMap.put(result.get(i).toString(), result.get(i + 1).toString().toLong)
                              i = i + 2
                            }
                            keyList.clear()
                            valList.clear()
                          }
                        }
                      }
                      parMapKeyMap.clear()
                    } finally {
                      jedisList.map(jedis => jedis.close)
                    }
                    (alias, funcOpMap)
                  }
                })
                colFutureList += future
                
              } else if (StringUtils.equalsIgnoreCase(funcName, "c_max_top_n")) {
                val future = TaskThreadPoolFactory.cachedThreadPool.submit(new Callable[Tuple2[String, scala.collection.mutable.Map[String, Any]]]() {
                  override def call(): Tuple2[String, scala.collection.mutable.Map[String, Any]] = {
                    val paramList = function.getParameters().getExpressions()
                    val redisMultiName = paramList(0).asInstanceOf[StringValue].getValue
                    val redisPreKey = paramList(1).asInstanceOf[StringValue].getValue
                    val keyCols = paramList(2).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }
                    val valCol = paramList(3).asInstanceOf[Column].getColumnName
                    val scoreCol = paramList(4).asInstanceOf[Column].getColumnName
                    val topN = paramList(5).asInstanceOf[LongValue].getValue
                    val batchSize = paramList(6).asInstanceOf[LongValue].getValue
                    val expireSeconds = paramList(7).asInstanceOf[LongValue].getValue
                    val funcOpMap = scala.collection.mutable.Map[String, Any]()
                    val keyTopNMap = scala.collection.mutable.Map[String, java.util.TreeMap[Long, java.util.HashSet[String]]]()
                    val keyTopNCountMap = scala.collection.mutable.Map[String, Int]()
                    val tmpDataMap = new java.util.HashMap[String, Long]()

                    for (r <- p) {
                      val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                      val valColValue = r.getAs(valCol).toString()
                      val scoreColValue = r.getAs[Long](scoreCol)
                      val redisKey = s"$keyColValues|$redisPreKey"
                      val redisKeyMD5 = DigestUtils.md5Hex(redisKey)
                      val topNMap = keyTopNMap.getOrElseUpdate(redisKeyMD5, new java.util.TreeMap[Long, java.util.HashSet[String]](new java.util.Comparator[Long]() {
                        override def compare(o1: Long, o2: Long): Int = {
                          0 - o1.compareTo(o2)
                        }
                      }))
                      var keyTopNCount = keyTopNCountMap.getOrElseUpdate(redisKeyMD5, 0)
                      if (keyTopNCount < topN || scoreColValue > topNMap.lastKey()) {
                        val tmpData = s"$valColValue|$redisKeyMD5"
                        val tmpScore = tmpDataMap.get(tmpData)
                        var isNeedRM = false
                        if (tmpScore == null || topNMap.get(tmpScore) == null || !topNMap.get(tmpScore).contains(valColValue)) {
                          tmpDataMap.put(tmpData, scoreColValue)                        
                          var topNItemSet = topNMap.get(scoreColValue)
                          if (topNItemSet == null) {
                          	topNItemSet = new java.util.HashSet[String]()
                          	topNMap.put(scoreColValue, topNItemSet)
                          }
                          topNItemSet.add(valColValue)
                          keyTopNCount = keyTopNCount + 1
                          keyTopNCountMap.put(redisKeyMD5, keyTopNCount)
                          isNeedRM = true
                        } else if(tmpScore < scoreColValue) {
                          tmpDataMap.put(tmpData, scoreColValue)
                          val preTopNItemSet = topNMap.get(tmpScore)
                          preTopNItemSet.remove(valColValue)
                          if (preTopNItemSet.isEmpty()) {
                            topNMap.remove(tmpScore)
                          }
                          var topNItemSet = topNMap.get(scoreColValue)
                          if (topNItemSet == null) {
                          	topNItemSet = new java.util.HashSet[String]()
                          	topNMap.put(scoreColValue, topNItemSet)
                          }
                          topNItemSet.add(valColValue)
                          isNeedRM = true
                        }
                        if (isNeedRM) {
                          keyTopNCount =  keyTopNCount - topNMap.lastEntry().getValue().size()
                          if (keyTopNCount >= topN) {
                            topNMap.remove(topNMap.lastKey())
                            keyTopNCountMap.put(redisKeyMD5, keyTopNCount)
                          }
                        }
                      }
                    }

                    val jedisList = RedisMultiUtil.getResourceList(redisMultiName)
                    val jedisListSize = jedisList.size

                    val keyListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                    val valListList: ArrayList[ArrayList[String]] = new ArrayList[ArrayList[String]]()
                    for (i <- 0 until jedisListSize) {
                      keyListList.add(new ArrayList[String]())
                      val valList = new ArrayList[String]()
                      valList.add(topN.toString())
                      valList.add(expireSeconds.toString())
                      valListList.add(valList)
                    }

                    var n = 0
                    try {
                      for ((redisKeyMD5, topNMap) <- keyTopNMap) {
                        val index = Math.abs(redisKeyMD5.hashCode() % jedisListSize)
                        val keyList = keyListList.get(index)
                        val valList = valListList.get(index)
                        keyList.add(redisKeyMD5)
                        
                        val tmpKeyScoreList = new ArrayList[String]()
                        var c = 0
                        val topNIter = topNMap.entrySet().iterator()
                        while (c < topN && topNIter.hasNext()) {
                          val entry = topNIter.next()
                          val score = entry.getKey()
                          val keySet = entry.getValue()
                          val keyIter = keySet.iterator()
                          while (c < topN && keyIter.hasNext()) {
                            val keyItem = keyIter.next()
                            tmpKeyScoreList.add(keyItem)
                            tmpKeyScoreList.add(score.toString())
                            c = c + 1
                          }
                        }
                        
                        valList.add(c.toString())
                        valList.addAll(tmpKeyScoreList)
                        n = n + c
                        if (n >= batchSize) {
                          n = 0
                          val result = jedisList(index).evalsha(RedisMultiUtil.maxTopNScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                          val curTimeTmp = System.currentTimeMillis()
                          val resultSize = result.size - 2
                          var i = 0
                          while (i <= resultSize) {
                            funcOpMap.put(result.get(i).toString(), curTimeTmp + "\001" + result.get(i + 1))
                            i = i + 2
                          }
                          keyList.clear()
                          valList.clear()
                          valList.add(topN.toString())
                          valList.add(expireSeconds.toString())
                        }
                      }
                      keyTopNMap.clear()
                      tmpDataMap.clear()

                      for (index <- 0 until jedisListSize) {
                        val keyList = keyListList(index)
                        if (!keyList.isEmpty()) {
                          val valList = valListList(index)
                          val result = jedisList(index).evalsha(RedisMultiUtil.maxTopNScriptSha1, keyList, valList).asInstanceOf[ArrayList[Object]]
                          val curTimeTmp = System.currentTimeMillis()
                          val resultSize = result.size - 2
                          var i = 0
                          while (i <= resultSize) {
                            funcOpMap.put(result.get(i).toString(), curTimeTmp + "\001" + result.get(i + 1))
                            i = i + 2
                          }
                          keyList.clear()
                          valList.clear()
                        }
                      }
                    } finally {
                      jedisList.map(jedis => jedis.close)
                    }
                    (alias, funcOpMap)
                  }
                })
                colFutureList += future
              }
            }

            override def visit(v: NullValue): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: SignedExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: JdbcParameter): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: JdbcNamedParameter): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: DoubleValue): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: HexValue): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: DateValue): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: TimeValue): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: TimestampValue): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Parenthesis): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Addition): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Division): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Multiplication): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Subtraction): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: AndExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: OrExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Between): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: EqualsTo): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: GreaterThan): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: GreaterThanEquals): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: InExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: IsNullExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: LikeExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: MinorThan): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: MinorThanEquals): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: NotEqualsTo): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: SubSelect): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: CaseExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: WhenClause): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: ExistsExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: AllComparisonExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: AnyComparisonExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Concat): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Matches): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: BitwiseAnd): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: BitwiseOr): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: BitwiseXor): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: CastExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: Modulo): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: AnalyticExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: WithinGroupExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: ExtractExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: IntervalExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: OracleHierarchicalExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: RegExpMatchOperator): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: JsonExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: RegExpMySQLOperator): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: UserVariable): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: NumericBind): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: KeepExpression): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: MySQLGroupConcat): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: RowConstructor): Unit = {
              // TODO Auto-generated method stub

            }

            override def visit(v: OracleHint): Unit = {
              // TODO Auto-generated method stub

            }

          })
        }
      })
    }

    for (future <- colFutureList) {
      val result = future.get()
      allFuncOpMap.put(result._1, result._2)
    }

    for (r <- p) {
      val groupByKeyStr = groupByItems.map { x => r.getAs(x).toString() }.toList.mkString("|")
      val aggRowMap = aggResultMap.getOrElseUpdate(groupByKeyStr, scala.collection.mutable.Map[String, Any]())

      for (selectItem <- selectItems) {
        selectItem.accept(new SelectItemVisitor() {
          override def visit(allColumns: AllColumns): Unit = {
            // TODO Auto-generated method stub

          }

          override def visit(allTableColumns: AllTableColumns): Unit = {
            // TODO Auto-generated method stub

          }

          override def visit(selectExpressionItem: SelectExpressionItem): Unit = {
            var alias = ""
            if (selectExpressionItem.getAlias != null) alias = selectExpressionItem.getAlias.getName
            selectExpressionItem.getExpression().accept(new ExpressionVisitor() {

              override def visit(longValue: LongValue): Unit = {
                aggRowMap.getOrElseUpdate(alias, longValue.getValue)
              }

              override def visit(stringValue: StringValue): Unit = {
                aggRowMap.getOrElseUpdate(alias, stringValue.getValue)
              }

              override def visit(tableColumn: Column): Unit = {
                if (StringUtils.isNotBlank(alias)) {
                  aggRowMap.getOrElseUpdate(alias, r.getAs[Any](tableColumn.getColumnName))
                } else {
                  aggRowMap.getOrElseUpdate(tableColumn.getColumnName, r.getAs[Any](tableColumn.getColumnName))
                }
              }

              override def visit(function: net.sf.jsqlparser.expression.Function): Unit = {
                val funcName = function.getName()
                if (StringUtils.equalsIgnoreCase(funcName, "max_str")) {
                  val paramList = function.getParameters().getExpressions()
                  val value = r.getAs(paramList(0).asInstanceOf[Column].getColumnName).toString()
                  val sourceValue = aggRowMap.getOrElseUpdate(alias, value).toString()
                  if (!StringUtils.equals("OTHER", value)) {
                    if (StringUtils.equals("OTHER", sourceValue) || value.compareToIgnoreCase(sourceValue) > 0) {
                      aggRowMap.put(alias, value)
                    }
                  }

                } else if (StringUtils.equalsIgnoreCase(funcName, "max")) {
                  val paramList = function.getParameters().getExpressions()
                  val value = r.getAs[Long](paramList(0).asInstanceOf[Column].getColumnName)
                  val sourceValue = aggRowMap.getOrElseUpdate(alias, value)
                  if (value - sourceValue.asInstanceOf[Long] > 0) {
                    aggRowMap.put(alias, value)
                  }

                } else if (StringUtils.equalsIgnoreCase(funcName, "c_uuid")) {
                  aggRowMap.getOrElseUpdate(alias, UUID.randomUUID().toString())

                } else {
                  val hasValue = aggRowMap.getOrElse(alias, null)
                  if (hasValue == null) {
                    val curFuncOpMap = allFuncOpMap.getOrElse(alias, null)
                    if (curFuncOpMap == null) {
                      aggRowMap.put(alias, 0L)
                    } else {
                      if (StringUtils.equalsIgnoreCase(funcName, "c_count_distinct")) {
                        val paramList = function.getParameters().getExpressions()
                        val redisPreKey = paramList(1).asInstanceOf[StringValue].getValue
                        val keyCols = paramList(2).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }

                        val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                        val redisKey = s"$keyColValues|$redisPreKey"
                        val redisKeyMD5 = DigestUtils.md5Hex(redisKey)

                        val bufferObj = curFuncOpMap.getOrElse(redisKeyMD5, null)
                        if (bufferObj != null) {
                          val result = bufferObj.asInstanceOf[Long]
                          aggRowMap.put(alias, result)
                        } else {
                          aggRowMap.put(alias, 0L)
                        }

                      } else if (StringUtils.equalsIgnoreCase(funcName, "c_sum")) {
                        val paramList = function.getParameters().getExpressions()
                        val partitionCol = paramList(2).asInstanceOf[Column].getColumnName
                        val keyCols = paramList(3).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }

                        val partitionColValue = r.getAs(partitionCol).toString()
                        val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                        val pKeyColValues = s"$keyColValues|$partitionColValue"
                        val pKeyColValuesMD5 = DigestUtils.md5Hex(pKeyColValues)

                        val bufferObj = curFuncOpMap.getOrElse(pKeyColValuesMD5, null)
                        if (bufferObj != null) {
                          val result = bufferObj.asInstanceOf[Long]
                          aggRowMap.put(alias, result)
                        } else {
                          aggRowMap.put(alias, 0L)
                        }

                      } else if (StringUtils.equalsIgnoreCase(funcName, "c_max")) {
                        val paramList = function.getParameters().getExpressions()
                        val partitionCol = paramList(2).asInstanceOf[Column].getColumnName
                        val keyCols = paramList(3).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }

                        val partitionColValue = r.getAs(partitionCol).toString()
                        val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                        val pKeyColValues = s"$keyColValues|$partitionColValue"
                        val pKeyColValuesMD5 = DigestUtils.md5Hex(pKeyColValues)

                        val bufferObj = curFuncOpMap.getOrElse(pKeyColValuesMD5, null)
                        if (bufferObj != null) {
                          val result = bufferObj.asInstanceOf[Long]
                          aggRowMap.put(alias, result)
                        } else {
                          aggRowMap.put(alias, 0L)
                        }

                      } else if (StringUtils.equalsIgnoreCase(funcName, "c_min")) {
                        val paramList = function.getParameters().getExpressions()
                        val partitionCol = paramList(2).asInstanceOf[Column].getColumnName
                        val keyCols = paramList(3).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }

                        val partitionColValue = r.getAs(partitionCol).toString()
                        val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                        val pKeyColValues = s"$keyColValues|$partitionColValue"
                        val pKeyColValuesMD5 = DigestUtils.md5Hex(pKeyColValues)

                        val bufferObj = curFuncOpMap.getOrElse(pKeyColValuesMD5, null)
                        if (bufferObj != null) {
                          val result = bufferObj.asInstanceOf[Long]
                          aggRowMap.put(alias, result)
                        } else {
                          aggRowMap.put(alias, 0L)
                        }
                        
                      } else if (StringUtils.equalsIgnoreCase(funcName, "c_max_top_n")) {
                        val paramList = function.getParameters().getExpressions()
                        val redisPreKey = paramList(1).asInstanceOf[StringValue].getValue
                        val keyCols = paramList(2).asInstanceOf[net.sf.jsqlparser.expression.Function].getParameters.getExpressions.map { x => x.asInstanceOf[Column].getColumnName }

                        val keyColValues = keyCols.map { x => r.getAs(x).toString() }.toList.mkString("|")
                        val redisKey = s"$keyColValues|$redisPreKey"
                        val redisKeyMD5 = DigestUtils.md5Hex(redisKey)

                        val bufferObj = curFuncOpMap.getOrElse(redisKeyMD5, null)
                        if (bufferObj != null) {
                          val result = bufferObj.toString()
                          aggRowMap.put(alias, result)
                        } else {
                          aggRowMap.put(alias, "")
                        }
                      }
                    }
                  }
                }
              }

              override def visit(v: NullValue): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: SignedExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: JdbcParameter): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: JdbcNamedParameter): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: DoubleValue): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: HexValue): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: DateValue): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: TimeValue): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: TimestampValue): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Parenthesis): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Addition): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Division): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Multiplication): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Subtraction): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: AndExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: OrExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Between): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: EqualsTo): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: GreaterThan): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: GreaterThanEquals): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: InExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: IsNullExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: LikeExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: MinorThan): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: MinorThanEquals): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: NotEqualsTo): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: SubSelect): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: CaseExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: WhenClause): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: ExistsExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: AllComparisonExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: AnyComparisonExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Concat): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Matches): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: BitwiseAnd): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: BitwiseOr): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: BitwiseXor): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: CastExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: Modulo): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: AnalyticExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: WithinGroupExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: ExtractExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: IntervalExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: OracleHierarchicalExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: RegExpMatchOperator): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: JsonExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: RegExpMySQLOperator): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: UserVariable): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: NumericBind): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: KeepExpression): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: MySQLGroupConcat): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: RowConstructor): Unit = {
                // TODO Auto-generated method stub

              }

              override def visit(v: OracleHint): Unit = {
                // TODO Auto-generated method stub

              }

            })
          }
        })
      }
    }
    aggResultMap.values
  }
}

package com.duowan.meteor.server.executor

import java.util.Date
import java.util.Random

import org.apache.commons.lang3.time.DateFormatUtils

import com.duowan.meteor.model.view.AbstractTaskDepend
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.util.SSHUtil

class ShellTaskExecutor extends AbstractTaskExecutor with Logging {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[AbstractTaskDepend]
    val sshExecCronTaskMachines = ExecutorContext.sshExecCronTaskMachines
    val date = new Date()
    val execTime = DateFormatUtils.format(date, "yyyyMMddHHmmss")
    val tdate = DateFormatUtils.format(date, "yyyy-MM-dd")
    
    val host = sshExecCronTaskMachines(new Random().nextInt(sshExecCronTaskMachines.length))
    
    val program = s"""mkdir -p ${ExecutorContext.cronTaskLogPath}/$tdate/ && source /etc/profile """ +
        s""" && java -Xms1024m -Xmx1024m -cp ${ExecutorContext.cronTaskExecJar} com.duowan.meteor.datasync.DatasyncMain """ +
        s""" '${task.getFileId}' '${execTime}' '${execTime}' '${execTime}' '${execTime}' """ +
        s""" '${ExecutorContext.kafkaClusterHostPorts}' """ +
        s""" '${ExecutorContext.cassandraClusterHosts}' """ +
        s""" '${ExecutorContext.redisClusterHostPorts}' """ +
        s""" '${ExecutorContext.jdbcDriver}' '${ExecutorContext.jdbcUrl}' '${ExecutorContext.jdbcUsername}' '${ExecutorContext.jdbcPassword}' """ +
        s""" '${ExecutorContext.cronTaskLogPath}/$tdate/' """ +
        s""" '${paramMap("instanceFlowId")}' """ +
        s""" > ${ExecutorContext.cronTaskLogPath}/$tdate/${task.getFileId}_${execTime}_${execTime}.log 2>&1 &"""
    logInfo(s"${host}:\n${program}\n")
    SSHUtil.exeCmd(host, program)
  }

}
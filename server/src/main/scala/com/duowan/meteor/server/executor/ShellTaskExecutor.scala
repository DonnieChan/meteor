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
    val time = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")
    
    val host = sshExecCronTaskMachines(new Random().nextInt(sshExecCronTaskMachines.length))
    
    val program = s"""source /etc/profile """ +
        s""" && java -Xms1024m -Xmx1024m -cp ${ExecutorContext.cronTaskExecJar} com.duowan.meteor.datasync.DatasyncMain """ +
        s""" '${task.getFileId}' '${ExecutorContext.propFilePath}' '${paramMap("instanceFlowId")}' """ +
        s""" > ${ExecutorContext.cronTaskLogPath}/${task.getFileId}_${time}.log 2>&1 &"""
    logInfo(s"${host}:\n${program}\n")
    SSHUtil.exeCmd(host, program)
  }

}
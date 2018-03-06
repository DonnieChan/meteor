package com.duowan.meteor.server.cron

import java.util.HashSet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import scala.collection.JavaConversions.asScalaSet

import org.apache.commons.lang.StringUtils
import org.quartz.CronTrigger
import org.quartz.JobKey
import org.quartz.TriggerKey
import org.quartz.impl.matchers.GroupMatcher

import com.duowan.meteor.model.view.cron.CronTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.factory.DefTaskFactory
import com.duowan.meteor.server.util.Logging

object CronTaskLoader extends Logging {

  val scheduledExecutor = Executors.newScheduledThreadPool(1)
  val CRONGROUP = "CRON"

  def startup(): Unit = {
    refresh()
    scheduledExecutor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        try {
          refresh()
        } catch {
          case e: Exception => logError("刷新定时任务失败", e)
        }
      }
    }, 7, 5, TimeUnit.MINUTES)
  }

  def refresh(): Unit = {
    logInfo("Refresh")
    val jobKeys = QuartzScheduler.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(CRONGROUP));
    val cronSet = DefTaskFactory.defAllValid.getCronSet
    val newTaskSet = new HashSet[Integer](cronSet)
    if (jobKeys != null) {
      for (jobKey: JobKey <- jobKeys) {
        val triggerKey = new TriggerKey(jobKey.getName(), CRONGROUP)
        val ct = QuartzScheduler.scheduler.getTrigger(triggerKey).asInstanceOf[CronTrigger]
        val taskId = Integer.parseInt(jobKey.getName())
        //删除
        if (cronSet == null || !cronSet.contains(taskId)) {
          logInfo(s"Delete cron ${jobKey.getName()} ${ct.getCronExpression()}")
          QuartzScheduler.delScheduleJob(jobKey.getName(), CRONGROUP)
        } //修改
        else {
          val task = DefTaskFactory.defAllValid.getDefAllMap().get(taskId).asInstanceOf[CronTask]
          if (task != null && !StringUtils.equals(task.getCronExp, ct.getCronExpression())) {
            logInfo(s"Alter cron ${jobKey.getName()} from ${ct.getCronExpression()} to ${task.getCronExp}")
            QuartzScheduler.delScheduleJob(jobKey.getName(), CRONGROUP)
            QuartzScheduler.addScheduleJob(classOf[QuartzJob], jobKey.getName(), CRONGROUP, task.getCronExp)
          }
          newTaskSet.remove(taskId)
        }
      }
    }
    //新增
    if (newTaskSet != null) {
      for (taskId: Integer <- newTaskSet) {
        val cronExp = DefTaskFactory.defAllValid.getDefAllMap().get(taskId).asInstanceOf[CronTask].getCronExp
        logInfo(s"Add cron ${taskId} ${cronExp}")
        if (StringUtils.equals("true", ExecutorContext.execCronTaskOnStartup)) {
          new QuartzJob().exec(taskId)
        }
        QuartzScheduler.addScheduleJob(classOf[QuartzJob], String.valueOf(taskId), CRONGROUP, cronExp);
      }
    }
  }

}
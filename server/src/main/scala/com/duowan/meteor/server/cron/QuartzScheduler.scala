package com.duowan.meteor.server.cron

import scala.collection.JavaConversions.collectionAsScalaIterable
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Job

object QuartzScheduler {

  val factory = new StdSchedulerFactory()
  val scheduler = factory.getScheduler()
  scheduler.start()

  /**
   * 增加定时任务
   * @param jobClass
   * @param jobName
   * @param cronExp
   * @throws SchedulerException
   */
  def addScheduleJob(jobClass: Class[_ <: Job], jobName: String, jobGroup: String, cronExp: String): Unit = {
    addScheduleJob(jobClass, jobName, jobGroup, jobName, jobGroup, cronExp)
  }

  /**
   * 删除定时任务
   * @param jobName
   * @throws SchedulerException
   */
  def delScheduleJob(jobName: String, jobGroup: String): Unit = {
    delScheduleJob(jobName, jobGroup, jobName, jobGroup)
  }

  /**
   * 增加定时任务
   * @param jobClass
   * @param jobName
   * @param jobGroup
   * @param cronTriggerName
   * @param cronTriggerGroup
   * @param cronExp
   * @throws SchedulerException
   */
  def addScheduleJob(jobClass: Class[_ <: Job], jobName: String, jobGroup: String, cronTriggerName: String, cronTriggerGroup: String, cronExp: String): Unit = {
    val job = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build()
    val trigger = TriggerBuilder.newTrigger().withIdentity(cronTriggerName, cronTriggerGroup).withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).build()
    scheduler.scheduleJob(job, trigger)
  }

  /**
   * 删除定时任务
   * @param jobName
   * @param jobGroup
   * @param cronTriggerName
   * @param cronTriggerGroup
   * @throws SchedulerException
   */
  def delScheduleJob(jobName: String, jobGroup: String, cronTriggerName: String, cronTriggerGroup: String): Unit = {
    val triggerKey = new TriggerKey(cronTriggerName, cronTriggerGroup);
    val jobKey = new JobKey(jobName, jobGroup);
    scheduler.pauseTrigger(triggerKey);
    scheduler.unscheduleJob(triggerKey);
    scheduler.deleteJob(jobKey);
  }

  /**
   * 销毁定时器
   * @throws SchedulerException
   */
  def shutdownAll(): Unit = synchronized {
    val schedulerList = factory.getAllSchedulers();
    for (scheduler: Scheduler <- schedulerList) {
      scheduler.shutdown(true);
    }
  }

}
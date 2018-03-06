package com.duowan.meteor.mc.controller;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duowan.meteor.mc.common.controller.UserBaseController;
import com.duowan.meteor.mc.utils.ControllerUtils;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.model.view.buildmodel.SqlTask;
import com.duowan.meteor.model.view.cron.CronTask;
import com.duowan.meteor.model.view.export.ExportCassandraTask;
import com.duowan.meteor.model.view.export.ExportJDBCTask;
import com.duowan.meteor.model.view.export.ExportKafkaTask;
import com.duowan.meteor.model.view.export.ExportOuterRedisTask;
import com.duowan.meteor.model.view.export.ExportRedisTask;
import com.duowan.meteor.model.view.importcassandra.ImportHiveToCassandraTask;
import com.duowan.meteor.model.view.importcassandra.ImportMysqlToCassandraTask;
import com.duowan.meteor.model.view.importqueue.ImportKafkaTask;
import com.duowan.meteor.model.view.importredis.ImportMysqlToRedisTask;
import com.duowan.meteor.service.DefFileSysExtService;
import com.duowan.meteor.service.DefFileSysService;
import com.duowan.meteor.service.DefProjectService;
import com.duowan.meteor.service.TaskService;

@Controller
@RequestMapping("/task")
public class TaskController extends UserBaseController {

	private static Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private DefFileSysService defFileSysService;
	
	@Autowired
	private DefFileSysExtService defFileSysExtService;
	
	@Autowired
	private DefProjectService defProjectService;

	@RequestMapping("/read.do")
	public String read(ModelMap model, Integer projectId, Integer parentFileId, Integer fileId, String fileType) throws Exception {
		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		Assert.isTrue(fileId != null || parentFileId != null, "Wrong!! fileId and parentFileId must not be null, at the same!");

		AbstractBase defFileTask = null;
		if (fileId == null) {
			defFileTask = (AbstractBase) FileType.getFileTypeByName(fileType).getRefClass().newInstance();
			defFileTask.setProjectId(projectId);
			defFileTask.setParentFileId(parentFileId);
			defFileTask.setOfflineTime(DateUtils.parseDate("2099-01-01 00:00:00", new String[] { "yyyy-MM-dd HH:mm:ss" }));
			defFileTask.setContacts(getLoginPassport());
		} else {
			defFileTask = taskService.getTask(fileId);
			model.put("defPreDependFileList", defFileSysExtService.getPreDependById(fileId));
			model.put("fileId", defFileTask.getFileId());
			model.put("projectId", defFileTask.getProjectId());
		}
		model.put("defFileTask", defFileTask);

		return "pages/home/file_creator/" + fileType;
	}
	
	
	@RequestMapping("/writeImportKafkaTask.do")
	public String writeImportKafkaTask(ModelMap model, ImportKafkaTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeSqlTask.do")
	public String writeSqlTask(ModelMap model, SqlTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeExportRedisTask.do")
	public String writeExportRedisTask(ModelMap model, ExportRedisTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeExportOuterRedisTask.do")
	public String writeExportOuterRedisTask(ModelMap model, ExportOuterRedisTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeExportCassandraTask.do")
	public String writeExportCassandraTask(ModelMap model, ExportCassandraTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeExportKafkaTask.do")
	public String writeExportKafkaTask(ModelMap model, ExportKafkaTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeExportJDBCTask.do")
	public String writeExportJDBCTask(ModelMap model, ExportJDBCTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeImportMysqlToRedisTask.do")
	public String writeImportMysqlToRedisTask(ModelMap model, ImportMysqlToRedisTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeImportMysqlToCassandraTask.do")
	public String writeImportMysqlToCassandraTask(ModelMap model, ImportMysqlToCassandraTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeImportHiveToCassandraTask.do")
	public String writeImportHiveToCassandraTask(ModelMap model, ImportHiveToCassandraTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	@RequestMapping("/writeCronTask.do")
	public String writeCronTask(ModelMap model, CronTask defFileTask, String targetAfterSubmitForm) throws Exception {
		return write(model, defFileTask, targetAfterSubmitForm);
	}
	
	public String write(ModelMap model, AbstractBase defFileTask, String targetAfterSubmitForm) throws Exception {
		Assert.notNull(defFileTask, "Wrong!! 提交上来的任务为空！");
		logger.info("插入|更新任务: " + defFileTask.getFileName());
		Integer fileId = defFileTask.getFileId();

		String passport = getLoginPassport();
		if (defFileTask.getCreateUser() == null) {
			defFileTask.setCreateUser(passport);
		}
		defFileTask.setUpdateUser(passport);
		if (fileId == null) {
			taskService.addTask(defFileTask);
		} else { 
			taskService.updateTask(defFileTask);
		}
		
		String actionAfterSubmitForm = "/schedule/index.do?projectId=" + defFileTask.getProjectId() + "&fileId=" + defFileTask.getFileId();
		String messageAfterSubmitForm = "文件ID：" + defFileTask.getFileId() + "<br />"
				+ "文件名称：" + defFileTask.getFileName() + "<br />"
				+ "<br />"
				+ "提交成功！";
		model.put("messageAfterSubmitForm", messageAfterSubmitForm);
		model.put("actionAfterSubmitForm", actionAfterSubmitForm);
		model.put("targetAfterSubmitForm", targetAfterSubmitForm);

		return "commons/transferAfterSubmitForm";
	}
	
	
	/**
	 * 删除任务
	 */
	@RequestMapping("/delete.do")
	public String deleteFile(Integer fileId, ModelMap model) throws Exception {
		Assert.notNull(fileId, "Wrong!! fileId must not be null!");
		DefFileSys defFileSys = defFileSysService.getById(fileId);
		taskService.deleteTask(fileId);
		return "redirect:" + ControllerUtils.httpFlag + "/schedule/index.do?projectId=" + defFileSys.getProjectId() + (fileId == 0 ? "" : ("&fileId=" + defFileSys.getParentFileId()));
	}

	/**
	 * 下线任务
	 */
	@RequestMapping("/offline.do")
	public String offlineFile(Integer fileId, ModelMap model) throws Exception {
		Assert.notNull(fileId, "Wrong!! fileId must not be null!");
		DefFileSys defFileSys = defFileSysService.getById(fileId);
		defFileSys.setOfflineTime(DateUtils.addMinutes(new Date(), -1));
		defFileSysService.update(defFileSys);
		return "redirect:" + ControllerUtils.httpFlag + "/schedule/index.do?projectId=" + defFileSys.getProjectId() + (fileId == 0 ? "" : ("&fileId=" + defFileSys.getParentFileId()));
	}

}

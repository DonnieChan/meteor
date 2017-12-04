package com.duowan.meteor.task;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.util.Assert;

import com.duowan.meteor.model.custom.DefAllValid;
import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.query.DefDependQuery;
import com.duowan.meteor.model.query.DefFileSysQuery;
import com.duowan.meteor.model.util.ViewDBConverters;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.model.view.AbstractTaskDepend;
import com.duowan.meteor.task.dao.DefDependDao;
import com.duowan.meteor.task.dao.DefFileSysDao;
import com.duowan.meteor.util.DataSourceProvider;

public class TaskManager {

	private static TaskManager taskManager = null;
	private DefFileSysDao defFileSysDao = null;
	private DefDependDao defDependDao = null;
	
	private TaskManager(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
		DataSource ds = DataSourceProvider.getDataSource(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
		defFileSysDao = new DefFileSysDao();
		defFileSysDao.init(ds);
		defDependDao = new DefDependDao();
		defDependDao.init(ds);
	}
	
	public static TaskManager getInstance(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
		if(taskManager == null) {
			taskManager = initInstance(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
		}
		return taskManager;
	}
	
	public static synchronized TaskManager initInstance(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
		if(taskManager == null) {
			taskManager = new TaskManager(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
		}
		return taskManager;
	}
	
	public DefAllValid getDefAllValid() throws Exception {
		Date curDate = new Date();
		DefFileSysQuery defFileSysQuery = new DefFileSysQuery();
		defFileSysQuery.setIsValid(1);
		defFileSysQuery.setOfflineTimeBegin(curDate);
		List<DefFileSys> defFileSysList = defFileSysDao.getByDefFileSysQuery(defFileSysQuery);
		if (defFileSysList == null || defFileSysList.isEmpty()) {
			return null;
		}

		DefDependQuery defDependQuery = new DefDependQuery();
		defDependQuery.setIsValid(1);
		defDependQuery.setOfflineTimeBegin(curDate);
		List<DefDepend> defDependList = defDependDao.getByDefDependQuery(defDependQuery);

		DefAllValid defAllValid = new DefAllValid();
		for (DefFileSys defFileSys : defFileSysList) {
			AbstractBase abstractBase = ViewDBConverters.buildView(defFileSys);
			if (abstractBase != null) {
				defAllValid.getDefAllMap().put(abstractBase.getFileId(), abstractBase);
				FileType fileType = Enum.valueOf(FileType.class, abstractBase.getFileType());
				if (fileType == FileType.ImportKafka) {
					defAllValid.getImportQueueSet().add(abstractBase.getFileId());
				}
				if (fileType == FileType.Cron) {
					defAllValid.getCronSet().add(abstractBase.getFileId());
				}
			}
		}
		
		if(defDependList != null) {
			for(DefDepend defDepend : defDependList) {
				AbstractTaskDepend forPredependTask = (AbstractTaskDepend) defAllValid.getDefAllMap().get(defDepend.getFileId());
				if(forPredependTask != null) {
					forPredependTask.getPreDependSet().add(defDepend.getPreFileId());
				}
				
				AbstractTaskDepend forPostdependTask = (AbstractTaskDepend) defAllValid.getDefAllMap().get(defDepend.getPreFileId());
				if(forPostdependTask != null) {
					forPostdependTask.getPostDependSet().add(defDepend.getFileId());
				}
			}
		}

		return defAllValid;
	}
	
	
	public AbstractBase getTask(Integer fileId) throws Exception {
		Assert.isTrue(fileId != null, "fileId不可为空！");
		Date curDate = new Date();
		DefFileSysQuery defFileSysQuery = new DefFileSysQuery();
		defFileSysQuery.setFileId(fileId);
		defFileSysQuery.setIsValid(1);
		defFileSysQuery.setOfflineTimeBegin(curDate);
		DefFileSys defFileSys = null;
		List<DefFileSys> defFileSysList = defFileSysDao.getByDefFileSysQuery(defFileSysQuery);
		if(defFileSysList != null && defFileSysList.size() > 0) {
			defFileSys = defFileSysList.get(0);
		}
		DefDependQuery defDependQuery = new DefDependQuery();
		defDependQuery.setIsValid(1);
		defDependQuery.setOfflineTimeBegin(curDate);
		List<DefDepend> defDepends = defDependDao.getByDefDependQuery(defDependQuery);
		AbstractBase abstractBase = ViewDBConverters.convertToViewObject(defFileSys, defDepends);
		return abstractBase;
	}
}

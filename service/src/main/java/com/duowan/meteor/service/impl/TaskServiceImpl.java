package com.duowan.meteor.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.duowan.meteor.dao.DefDependDao;
import com.duowan.meteor.dao.DefFileSysDao;
import com.duowan.meteor.model.custom.DefAllValid;
import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.query.DefDependQuery;
import com.duowan.meteor.model.query.DefFileSysQuery;
import com.duowan.meteor.model.util.ViewDBConverters;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.model.view.AbstractTaskDepend;
import com.duowan.meteor.service.TaskService;
import com.duowan.meteor.service.util.CheckLoopUtil;

/**
 * 任务业务操作
 * 
 * @author liuchaohong
 * 
 */
@Service("taskService")
@Transactional
public class TaskServiceImpl implements TaskService {

	@Autowired
	private DefFileSysDao defFileSysDao;
	@Autowired
	private DefDependDao defDependDao;

	@Override
	public AbstractBase addTask(AbstractBase task) throws Exception {
		doCheck(task);

		// 添加任务
		DefFileSys defFileSys = ViewDBConverters.convertToDefFileSys(task);
		DefFileSys returnDefFileSys = defFileSysDao.insertWithReturnObject(defFileSys);
		if (returnDefFileSys.getFileId() == null) {
			throw new RuntimeException("任务插入异常");
		}
		// if(defFileSysDao.insert(defFileSys) != 1) {
		// throw new RuntimeException("任务插入异常,返回结果不为1");
		// }

		// 添加依赖
		task.setFileId(returnDefFileSys.getFileId());
		List<DefDepend> defDepends = ViewDBConverters.convertToDefDependList(task);
		if (!CollectionUtils.isEmpty(defDepends)) {
			for (DefDepend defDepend : defDepends) {
				if (defDependDao.insert(defDepend) != 1) {
					throw new RuntimeException("依赖插入异常,返回结果不为1");
				}
			}
		}
		return task;
	}

	@Override
	public boolean updateTask(AbstractBase task) throws Exception {
		doCheck(task);
		// 判断是否有回环，修改才需要判断，因新增id是自增的
		doDependAssert(task);
		Assert.isTrue(task.getFileId() != null, "fileId不可为空！");

		DefFileSys defFileSys = ViewDBConverters.convertToDefFileSys(task);
		if (defFileSysDao.update(defFileSys) != 1) {
			throw new RuntimeException("任务更新异常,返回结果不为1");
		}

		// 先删除原来的依赖，再添加依赖
		defDependDao.deleteByFileId(task.getFileId());
		List<DefDepend> defDepends = ViewDBConverters.convertToDefDependList(task);
		if (!CollectionUtils.isEmpty(defDepends)) {
			for (DefDepend defDepend : defDepends) {
				if (defDependDao.insert(defDepend) != 1) {
					throw new RuntimeException("依赖更新异常,返回结果不为1");
				}
			}
		}
		return true;
	}

	@Override
	public AbstractBase getTask(Integer fileId) throws Exception {
		Assert.isTrue(fileId != null, "fileId不可为空！");
		DefFileSys defFileSys = defFileSysDao.getById(fileId);
		List<DefDepend> defDepends = defDependDao.getByDefDependQuery(new DefDependQuery(fileId));
		AbstractBase abstractBase = ViewDBConverters.convertToViewObject(defFileSys, defDepends);
		return abstractBase;
	}

	/**
	 * @param fileId
	 * @return
	 */
	@Override
	public boolean deleteTask(Integer fileId) {
		Assert.isTrue(fileId != null, "fileId不可为空！");
		defFileSysDao.deleteById(fileId);
		defDependDao.deleteByFileId(fileId);
		defDependDao.deleteByPreFileId(fileId);
		return true;
	}

	@Override
	public DefAllValid getDefAllValid() throws Exception {
		DefFileSysQuery defFileSysQuery = new DefFileSysQuery();
		defFileSysQuery.setIsValid(1);
		List<DefFileSys> defFileSysList = defFileSysDao.getByDefFileSysQuery(defFileSysQuery);
		if (defFileSysList == null || defFileSysList.isEmpty()) {
			return null;
		}

		DefDependQuery defDependQuery = new DefDependQuery();
		defDependQuery.setIsValid(1);
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

	/**
	 * 对task及其父类进行Assert/trim操作
	 * 
	 * @param task
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws InstantiationException
	 */
	private void doCheck(AbstractBase task) throws IllegalArgumentException, IllegalAccessException, SecurityException, InstantiationException {
		Assert.notNull(task, task + "不可为空！");
		task.doTrim();
		task.doAssert();
	}

	/**
	 * 判断依赖是否有回环
	 * 
	 * @param task
	 */
	private void doDependAssert(AbstractBase task) {
		List<DefDepend> defDepends = ViewDBConverters.convertToDefDependList(task);
		if (!CollectionUtils.isEmpty(defDepends)) {
			List<DefDepend> defDependList = new ArrayList<DefDepend>();
			defDependList.addAll(defDepends);
			defDependList.addAll(defDependDao.getAll());
			List<Integer> fileIds = CheckLoopUtil.isLoop(defDependList);
			Assert.isTrue(CollectionUtils.isEmpty(fileIds), "依赖规则不合法：流程内的任务依赖存在回环；请检查添加的依赖中是否包含以下任务ID：" + fileIds);
		}
	}

}

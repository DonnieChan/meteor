package com.duowan.meteor.service;

import com.duowan.meteor.model.custom.DefAllValid;
import com.duowan.meteor.model.view.AbstractBase;

/**
 * 业务任务操作逻辑
 * @author liuchaohong
 *
 */
public interface TaskService {

	public AbstractBase addTask(AbstractBase task) throws Exception;
	
	public boolean updateTask(AbstractBase task) throws Exception;
	
	public AbstractBase getTask(Integer fileId) throws Exception;
	
	public boolean deleteTask(Integer fileId);

	public DefAllValid getDefAllValid() throws Exception;
}

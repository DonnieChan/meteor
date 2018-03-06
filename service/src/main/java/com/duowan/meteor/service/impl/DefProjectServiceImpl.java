/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.DefProjectDao;
import com.duowan.meteor.model.db.DefProject;
import com.duowan.meteor.model.query.DefProjectQuery;
import com.duowan.meteor.service.DefProjectService;

/**
 * [DefProject] 的业务操作实现类
 * 
 * @author taosheng
 * @version 1.0
 * @since 1.0
 */
@Service("defProjectService")
@Transactional
public class DefProjectServiceImpl implements DefProjectService {

	protected static final Logger log = LoggerFactory.getLogger(DefProjectServiceImpl.class);

	@Autowired
	private DefProjectDao defProjectDao;

	/** 
	 * 创建DefProject
	 **/
	public int insert(DefProject defProject) {
		Assert.notNull(defProject, "'defProject' must be not null");
		return defProjectDao.insert(defProject);
	}

	/** 
	 * 更新DefProject
	 **/
	public int update(DefProject defProject) {
		Assert.notNull(defProject, "'defProject' must be not null");
		return defProjectDao.update(defProject);
	}

	/** 
	 * 删除DefProject
	 * @return 
	 **/
	public int deleteById(int projectId) {
		return defProjectDao.deleteById(projectId);
	}

	/** 
	 * 根据ID得到DefProject
	 **/
	public DefProject getById(int projectId) {
		return defProjectDao.getById(projectId);
	}

	/** 
	 * 分页查询: DefProject
	 **/
	@Override
	public List<DefProject> getByDefProjectQuery(DefProjectQuery query) {
		return defProjectDao.getByDefProjectQuery(query);
	}

	@Override
	public Map<Integer, DefProject> getAllValidBuildMap() {
		DefProjectQuery query = new DefProjectQuery();
		List<DefProject> defProjectList = getByDefProjectQuery(query);
		if (defProjectList == null || defProjectList.isEmpty()) {
			return null;
		}
		Map<Integer, DefProject> defProjectMap = new HashMap<Integer, DefProject>();
		for (DefProject defProject : defProjectList) {
			defProjectMap.put(defProject.getProjectId(), defProject);
		}
		return defProjectMap;
	}

	@Override
	public int getMaxProjectId() {
		return defProjectDao.getMaxProjectId();
	}

	@Override
	public Integer getFirstProjectId() {
		// TODO Auto-generated method stub
		return defProjectDao.getFirstProjectId();
	}
}

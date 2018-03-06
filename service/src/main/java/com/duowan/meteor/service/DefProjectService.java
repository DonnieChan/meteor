/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.service;

import java.util.List;
import java.util.Map;

import com.duowan.meteor.model.db.DefProject;
import com.duowan.meteor.model.query.DefProjectQuery;


/**
 * [DefProject] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public interface DefProjectService {

	/** 
	 * 创建DefProject
	 **/
	public int insert(DefProject defProject);

	/** 
	 * 更新DefProject
	 **/
	public int update(DefProject defProject);

	/** 
	 * 删除DefProject
	 **/
	public int deleteById(int projectId);

	/** 
	 * 根据ID得到DefProject
	 **/
	public DefProject getById(int projectId);

	/** 
	 * 分页查询: DefProject
	 **/
	public List<DefProject> getByDefProjectQuery(DefProjectQuery query);
	
	/**
	 * 
	 * @return
	 */
	public Map<Integer, DefProject> getAllValidBuildMap();

	public int getMaxProjectId();

	public Integer getFirstProjectId();

}

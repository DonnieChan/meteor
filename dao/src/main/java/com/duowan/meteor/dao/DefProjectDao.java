/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.db.DefProject;
import com.duowan.meteor.model.query.DefProjectQuery;

/**
 * tableName: def_project
 * [DefProject] 的Dao操作
 * 
 * @author taosheng
 * @version 1.0
 * @since 1.0
*/
public interface DefProjectDao {

	public int insert(DefProject entity);

	public int update(DefProject entity);

	public int deleteById(int projectId);

	public DefProject getById(int projectId);

	public List<DefProject> getByDefProjectQuery(DefProjectQuery query);

	public int getMaxProjectId();

	public Integer getFirstProjectId();

}

/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.dao.impl;

import java.util.List;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.DefProjectDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.db.DefProject;
import com.duowan.meteor.model.query.DefProjectQuery;


@Repository("defProjectDao")
public class DefProjectDaoImpl extends BaseSpringJdbcDao implements DefProjectDao {

	private RowMapper<DefProject> entityRowMapper = new BeanPropertyRowMapper<DefProject>(getEntityClass());

	static final private String COLUMNS = "project_id,project_name,remarks,is_valid,create_time,update_time,create_user,update_user";
	static final private String SELECT_FROM = "select " + COLUMNS + " from def_project";

	@Override
	public Class<DefProject> getEntityClass() {
		return DefProject.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "projectId";
	}

	public RowMapper<DefProject> getEntityRowMapper() {
		return entityRowMapper;
	}

	public int insert(DefProject entity) {
		String sql = "insert into def_project "
				+ " (project_id,project_name,remarks,is_valid,create_time,update_time,create_user,update_user) "
				+ " values "
				+ " (:projectId,:projectName,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		return insertWithGeneratedKey(entity, sql);
	}

	public int update(DefProject entity) {
		String sql = "update def_project set "
				+ " project_name=:projectName,remarks=:remarks,is_valid=:isValid,create_time=:createTime,update_time=:updateTime,create_user=:createUser,update_user=:updateUser "
				+ " where  project_id = :projectId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int deleteById(int projectId) {
		String sql = "delete from def_project where  project_id = ? ";
		return getSimpleJdbcTemplate().update(sql, projectId);
	}

	public DefProject getById(int projectId) {
		String sql = SELECT_FROM + " where  project_id = ? ";
		return (DefProject) DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(), projectId));
	}

	public List<DefProject> getByDefProjectQuery(DefProjectQuery query) {
		StringBuilder sql = new StringBuilder("select " + COLUMNS + " from def_project where 1=1 ");

		if (query != null) {
			if (isNotEmpty(query.getProjectId())) {
				sql.append(" and project_id = :projectId ");
			}
			if (isNotEmpty(query.getProjectName())) {
				sql.append(" and project_name like '%:projectName%'");
			}
			if (isNotEmpty(query.getRemarks())) {
				sql.append(" and remarks like '%:remarks%'");
			}
			if (isNotEmpty(query.getIsValid())) {
				sql.append(" and is_valid = :isValid ");
			}
			if (isNotEmpty(query.getCreateTimeBegin())) {
				sql.append(" and create_time >= :createTimeBegin ");
			}
			if (isNotEmpty(query.getCreateTimeEnd())) {
				sql.append(" and create_time <= :createTimeEnd ");
			}
			if (isNotEmpty(query.getUpdateTimeBegin())) {
				sql.append(" and update_time >= :updateTimeBegin ");
			}
			if (isNotEmpty(query.getUpdateTimeEnd())) {
				sql.append(" and update_time <= :updateTimeEnd ");
			}
			if (isNotEmpty(query.getCreateUser())) {
				sql.append(" and create_user = :createUser ");
			}
			if (isNotEmpty(query.getUpdateUser())) {
				sql.append(" and update_user = :updateUser ");
			}
		}
		
		query = query != null ? query : new DefProjectQuery();
		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), getEntityRowMapper());
	}

	@Override
	public int getMaxProjectId() {
		String sql = " SELECT MAX(project_id) maxid FROM def_project t1 ";
		return getSimpleJdbcTemplate().queryForInt(sql);
	}

	@Override
	public Integer getFirstProjectId() {
		String sql = " SELECT project_id FROM def_project t1 LIMIT 1";
		return (int) getSimpleJdbcTemplate().queryForLong(sql);
	}
}

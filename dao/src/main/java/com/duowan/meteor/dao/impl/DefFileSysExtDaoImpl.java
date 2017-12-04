/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.DefFileSysExtDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;

/**
 * tableName: def_file_sys
 * [DefFileSysExt] 的Dao操作 
 *  
 * @author taosheng
*/
@Repository("defFileSysExtDao")
public class DefFileSysExtDaoImpl extends BaseSpringJdbcDao implements DefFileSysExtDao {

	private RowMapper<DefFileSysExt> entityExtRowMapper = new BeanPropertyRowMapper<DefFileSysExt>(getEntityClass());

	@Override
	public Class<DefFileSysExt> getEntityClass() {
		return DefFileSysExt.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "fileId";
	}

	public RowMapper<DefFileSysExt> getEntityRowMapper() {
		return entityExtRowMapper;
	}

	/**
	 * SQL
	 */
	public static String getSQLSelectFromJoin() {
		return "" +
				" SELECT t1.*, t2.project_name, t3.file_type_desc, t3.file_type_category, 1 is_can_depend " +
				" FROM def_file_sys  t1 " +
				" LEFT JOIN def_project   t2 ON t1.project_id=t2.project_id " +
				" LEFT JOIN def_file_type t3 ON t1.file_type=t3.file_type ";
	}

	public static String getSQLSelectFromJoinWhere() {
		return getSQLSelectFromJoin()
				+ " WHERE 1 = 1 ";
	}

	public static String getSimpleSelectFromJoinWhere() {
		return "" +
				" SELECT t1.* " +
				" FROM def_file_sys t1 ";
	}

	/**
	 * 查询fileId
	 */
	@Override
	public DefFileSysExt getById(int fileId) {
		StringBuilder sql = new StringBuilder(getSQLSelectFromJoinWhere()
				+ " AND t1.file_id = ? ");

		return (DefFileSysExt) DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql.toString(), getEntityRowMapper(), fileId));
	}

	@Override
	public int getMaxFileId() {
		String sql = " SELECT MAX(file_id) maxid FROM def_file_sys t1 ";
		return getSimpleJdbcTemplate().queryForInt(sql);
	}

	/**
	 * 获取前缀依赖
	 */
	@Override
	public List<DefFileSysExt> getPreDependById(int fileId) {
		StringBuilder sql = new StringBuilder(getSQLSelectFromJoin()
				+ " JOIN ( SELECT file_id, pre_file_id from def_depend ) t4 ON t1.file_id=t4.pre_file_id "
				+ " WHERE 1 = 1 "
				+ " AND t4.file_id = :fileId ");

		DefFileSysExtQuery query = new DefFileSysExtQuery();
		query.setFileId(fileId);
		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), getEntityRowMapper());
	}

	/**
	 * 层次节点信息
	 */
	@Override
	public List<DefFileSysExt> getDefFileNodeChainById(int fileId) {
		List<DefFileSysExt> chain = new ArrayList<DefFileSysExt>();
		DefFileSysExt defFileSysExt;
		while (true) {
			defFileSysExt = getById(fileId);
			if (defFileSysExt == null) {
				break;
			}
			chain.add(0, defFileSysExt);
			fileId = defFileSysExt.getParentFileId();
			if (fileId <= 0) {
				break;
			}
		}
		return chain;
	}

	/**
	 * 综合查询
	 */
	@Override
	public List<DefFileSysExt> getByDefFileSysExtQuery(DefFileSysExtQuery query) {
		StringBuilder sql = new StringBuilder(getSQLSelectFromJoinWhere());
		if (query != null) {
			/** def_file_sys t1 */
			if (query.getFileId() != null) {
				sql.append(" AND t1.file_id = :fileId ");
			}
			if (query.getParentFileId() != null) {
				sql.append(" AND t1.parent_file_id = :parentFileId ");
			}
			if (query.getProjectId() != null) {
				sql.append(" AND t1.project_id = :projectId ");
			}
			if (StringUtils.isNotBlank(query.getFileName())) {
				sql.append(" AND t1.file_name like '%:fileName%' ");
			}
			if (StringUtils.isNotBlank(query.getFileType())) {
				sql.append(" AND t1.file_type = :fileType ");
			}
			if (StringUtils.isNotBlank(query.getFileBody())) {
				sql.append(" AND t1.file_body like '%:fileBody%' ");
			}
			if (query.getIsDir() != null) {
				sql.append(" AND t1.is_dir = :isDir ");
			}
			if (query.getOfflineTimeBegin() != null) {
				sql.append(" AND t1.offline_time >= :offlineTimeBegin ");
			}
			if (query.getOfflineTimeEnd() != null) {
				sql.append(" AND t1.offline_time <= :offlineTimeEnd ");
			}
			if (query.getIsValid() != null) {
				sql.append(" AND t1.is_valid = :isValid ");
			}
			if (StringUtils.isNotBlank(query.getCreateUser())) {
				sql.append(" AND t1.create_user = :createUser ");
			}
			if (StringUtils.isNotBlank(query.getUpdateUser())) {
				sql.append(" AND t1.update_user = :updateUser ");
			}

			/** def_project  t2 */
			if (StringUtils.isNotBlank(query.getProjectName())) {
				sql.append(" AND t2.project_name like '%:projectName%' ");
			}

			/** def_file_type t3 */
			if (StringUtils.isNotBlank(query.getFileTypeDesc())) {
				sql.append(" AND t3.file_type_desc like '%:fileTypeDesc%' ");
			}
			if (StringUtils.isNotBlank(query.getFileTypeCategory())) {
				sql.append(" AND t3.file_type_category like '%:fileTypeCategory%' ");
			}
			if (query.getIsCanDepend() != null) {
				sql.append(" AND t3.is_can_depend = isCanDepend ");
			}
		} else {
			query = new DefFileSysExtQuery();
		}

		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), getEntityRowMapper());
	}
}

/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.DefFileSysDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;
import com.duowan.meteor.model.query.DefFileSysQuery;

/**
 * tableName: def_file_sys
 * [DefFileSys] 的Dao操作 
 *  
 * @author chenwu
*/
@Repository("defFileSysDao")
public class DefFileSysDaoImpl extends BaseSpringJdbcDao implements DefFileSysDao {

	private RowMapper<DefFileSys> entityRowMapper = new BeanPropertyRowMapper<DefFileSys>(getEntityClass());

	static final private String COLUMNS = "file_id,parent_file_id,project_id,file_name,file_type,file_body,is_dir,offline_time,contacts,remarks,is_valid,create_time,update_time,create_user,update_user";
	static final private String SELECT_FROM = "select " + COLUMNS + " from def_file_sys";

	@Override
	public Class<DefFileSys> getEntityClass() {
		return DefFileSys.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "fileId";
	}

	public RowMapper<DefFileSys> getEntityRowMapper() {
		return entityRowMapper;
	}

	public int insert(DefFileSys entity) {
		String sql = "insert into def_file_sys "
				+ " (file_id,parent_file_id,project_id,file_name,file_type,file_body,is_dir,offline_time,contacts,remarks,is_valid,create_time,update_time,create_user,update_user) "
				+ " values "
				+ " (:fileId,:parentFileId,:projectId,:fileName,:fileType,:fileBody,:isDir,:offlineTime,:contacts,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		return insertWithGeneratedKey(entity, sql);
	}

	public int update(DefFileSys entity) {
		String sql = "update def_file_sys set update_time=:updateTime";
		if (entity.getParentFileId() != null) {
			sql +=",parent_file_id=:parentFileId";
		}
		if (entity.getProjectId() != null) {
			sql +=",project_id=:projectId";
		}
		if (StringUtils.isNotBlank(entity.getFileName())) {
			sql +=",file_name=:fileName";
		}
		if (StringUtils.isNotBlank(entity.getFileType())) {
			sql +=",file_type=:fileType";
		}
		if (StringUtils.isNotBlank(entity.getFileBody())) {
			sql +=",file_body=:fileBody";
		}
		if (entity.getIsDir() != null) {
			sql +=",is_dir=:isDir";
		}	
		if (entity.getOfflineTime() != null) {
			sql +=",offline_time=:offlineTime";
		}
		if (StringUtils.isNotBlank(entity.getContacts())) {
			sql +=",contacts=:contacts";
		}
		if (StringUtils.isNotBlank(entity.getRemarks())) {
			sql +=",remarks=:remarks";
		}	
		if (entity.getIsValid() != null) {
			sql +=",is_valid=:isValid";
		}	
		if (StringUtils.isNotBlank(entity.getContacts())) {
			sql +=",contacts=:contacts";
		}			
		if (StringUtils.isNotBlank(entity.getUpdateUser())) {
			sql +=",update_user=:updateUser";
		}		
		sql += " where file_id = :fileId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int deleteById(int fileId) {
		String sql = "delete from def_file_sys where  file_id = ? ";
		return getSimpleJdbcTemplate().update(sql, fileId);
	}

	public DefFileSys getById(int fileId) {
		String sql = SELECT_FROM + " where  file_id = ? ";
		return (DefFileSys) DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(), fileId));
	}

	@Override
	public List<DefFileSys> getByDefFileSysQuery(DefFileSysQuery query) {

		StringBuilder sql = new StringBuilder("select " + COLUMNS + " from def_file_sys where 1=1 ");
		if (query != null) {
			if (query.getFileId() != null) {
				sql.append(" and file_id = :fileId ");
			}
			if (query.getParentFileId() != null) {
				sql.append(" and parent_file_id = :parentFileId ");
			}
			if (query.getProjectId() != null) {
				sql.append(" and project_id = :projectId ");
			}
			if (StringUtils.isNotBlank(query.getFileName())) {
				sql.append(" and file_name like '%:fileName%' ");
			}
			if (StringUtils.isNotBlank(query.getFileType())) {
				sql.append(" and file_type = :fileType ");
			}
			if (StringUtils.isNotBlank(query.getFileBody())) {
				sql.append(" and file_body like '%:fileBody%' ");
			}
			if (query.getIsDir() != null) {
				sql.append(" and is_dir = :isDir ");
			}
			if (query.getOfflineTimeBegin() != null) {
				sql.append(" and offline_time >= :offlineTimeBegin ");
			}
			if (query.getOfflineTimeEnd() != null) {
				sql.append(" and offline_time <= :offlineTimeEnd ");
			}
			if (query.getIsValid() != null) {
				sql.append(" and is_valid = :isValid ");
			}
			if (StringUtils.isNotBlank(query.getCreateUser())) {
				sql.append(" and create_user = :createUser ");
			}
			if (StringUtils.isNotBlank(query.getUpdateUser())) {
				sql.append(" and update_user = :updateUser ");
			}
		} else {
			query = new DefFileSysQuery();
		}
		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), getEntityRowMapper());
	}

	/**
	 * @param fileType
	 * @param fileTypeCategory
	 * @param isCanDepend
	 */
	@Override
	public List<DefFileSys> getByDefFileType(String fileType, String fileTypeCategory, Integer dependFlag) {
		StringBuilder sql = new StringBuilder("select " + COLUMNS + " from def_file_sys t1 " +
				" inner join (SELECT file_type ftype, file_type_category, depend_flag from def_file_type) t2 on t2.ftype=t1.file_type " +
				" where t1.is_valid = 1 ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(fileType)) {
			sql.append(" and t2.ftype = :fileType ");
			paramMap.put("fileType", fileType);
		}
		if (StringUtils.isNotBlank(fileTypeCategory)) {
			sql.append(" and t2.file_type_category = :fileTypeCategory ");
			paramMap.put("fileTypeCategory", fileTypeCategory);
		}
		if (dependFlag != null) {
			sql.append(" and t2.depend_flag = :dependFlag ");
			paramMap.put("dependFlag", dependFlag);
		}
		return getNamedParameterJdbcTemplate().query(sql.toString(), paramMap, getEntityRowMapper());
	}

	@Override
	public DefFileSys insertWithReturnObject(DefFileSys entity) {
		String sql = "insert into def_file_sys "
				+ " (file_id,parent_file_id,project_id,file_name,file_type,file_body,is_dir,offline_time,contacts,remarks,is_valid,create_time,update_time,create_user,update_user) "
				+ " values "
				+ " (:fileId,:parentFileId,:projectId,:fileName,:fileType,:fileBody,:isDir,:offlineTime,:contacts,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		return (DefFileSys) insertWithGeneratedKeyReturnObject(entity, sql);
	}

	@Override
	public List<DefFileSysExt> getByDefFileSysExtQuery(DefFileSysExtQuery query) {
		StringBuilder sql = new StringBuilder(" SELECT t1.*, t2.project_name, t3.file_type_desc, t3.file_type_category, 1 is_can_depend "
				+ " FROM def_file_sys  t1 "
				+ " LEFT JOIN def_project   t2 ON t1.project_id=t2.project_id "
				+ " LEFT JOIN def_file_type t3 ON t1.file_type=t3.file_type "
				+ " WHERE 1=1 ");
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

		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), new BeanPropertyRowMapper<DefFileSysExt>(DefFileSysExt.class));
	}
}

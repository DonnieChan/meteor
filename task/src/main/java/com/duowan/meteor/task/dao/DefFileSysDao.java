package com.duowan.meteor.task.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.query.DefFileSysQuery;

public class DefFileSysDao extends DaoCommon {
	
	private RowMapper<DefFileSys> entityRowMapper = new BeanPropertyRowMapper<DefFileSys>(DefFileSys.class);
	private String COLUMNS = "file_id,parent_file_id,project_id,file_name,file_type,file_body,is_dir,offline_time,contacts,remarks,is_valid,create_time,update_time,create_user,update_user";
	
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
		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), entityRowMapper);
	}
}

package com.duowan.meteor.task.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.query.DefDependQuery;

public class DefDependDao extends DaoCommon {

	private RowMapper<DefDepend> entityRowMapper = new BeanPropertyRowMapper<DefDepend>(DefDepend.class);

	public List<DefDepend> getByDefDependQuery(DefDependQuery query) {
		StringBuilder sql = new StringBuilder("select t1.file_id, t1.pre_file_id, t1.project_id, t1.remarks, t1.is_valid, t1.create_time, t1.update_time, t1.create_user, t1.update_user \n")
			.append("from def_depend t1 \n")
			.append("join def_file_sys t2 on t2.file_id=t1.file_id \n")
			.append("join def_file_sys t3 on t3.file_id=t1.pre_file_id \n")
			.append("where 1=1 ");
		if (query != null) {
			if (query.getFileId() != null) {
				sql.append(" and t1.file_id = :fileId ");
			}
			if (query.getPreFileId() != null) {
				sql.append(" and t1.pre_file_id = :preFileId ");
			}
			if (query.getProjectId() != null) {
				sql.append(" and t1.project_id = :projectId ");
			}
			if (query.getIsValid() != null) {
				sql.append(" and t1.is_valid = :isValid ");
				sql.append(" and t2.is_valid = :isValid ");
				sql.append(" and t3.is_valid = :isValid ");
			}
			if (StringUtils.isNotBlank(query.getCreateUser())) {
				sql.append(" and t1.create_user = :createUser ");
			}
			if (StringUtils.isNotBlank(query.getUpdateUser())) {
				sql.append(" and t1.update_user = :updateUser ");
			}
			if (query.getOfflineTimeBegin() != null) {
				sql.append(" and t2.offline_time >= :offlineTimeBegin ");
				sql.append(" and t3.offline_time >= :offlineTimeBegin ");
			}
			if (query.getOfflineTimeEnd() != null) {
				sql.append(" and t2.offline_time <= :offlineTimeEnd ");
				sql.append(" and t3.offline_time <= :offlineTimeEnd ");
			}
		} else {
			query = new DefDependQuery();
		}
		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), entityRowMapper);
	}

}

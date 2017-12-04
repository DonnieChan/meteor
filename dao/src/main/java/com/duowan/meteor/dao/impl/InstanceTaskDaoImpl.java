package com.duowan.meteor.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.InstanceTaskDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.instance.InstanceTaskDB;
import com.duowan.meteor.model.query.InstanceTaskQuery;

@Repository("instanceTaskDao")
public class InstanceTaskDaoImpl extends BaseSpringJdbcDao implements InstanceTaskDao {

	private RowMapper<InstanceTaskDB> entityRowMapper = new BeanPropertyRowMapper<InstanceTaskDB>(getEntityClass());

	static final private String COLUMNS = "instance_flow_id,file_id,file_body,ready_time,start_time,end_time,status,retried_times,pool_active_count,pool_queue_size,log,remarks,is_valid,create_user,update_user,create_time,update_time";
	@Override
	public Class<InstanceTaskDB> getEntityClass() {
		return InstanceTaskDB.class;
	}

	public RowMapper<InstanceTaskDB> getEntityRowMapper() {
		return entityRowMapper;
	}

	/**
	 * @return
	 */
	private String getInsertSql() {
		String sql = "insert into instance_task "
				+ " (instance_flow_id,file_id,file_body,ready_time,start_time,end_time,status,retried_times,pool_active_count,pool_queue_size,log,remarks,is_valid,create_user,update_user,create_time,update_time) "
				+ " values "
				+ " (:instanceFlowId,:fileId,:fileBody,:readyTime,:startTime,:endTime,:status,:retriedTimes,:poolActiveCount,:poolQueueSize,:log,:remarks,:isValid,:createUser,:updateUser,:createTime,:updateTime)";
		return sql;
	}

	@Override
	public int[] batchInsert(List<InstanceTaskDB> entityList) {
		SqlParameterSource[] batchArgs = SqlParameterSourceUtils.createBatch(entityList.toArray());
		return getNamedParameterJdbcTemplate().batchUpdate(getInsertSql(), batchArgs);
	}

	/**
	 * @param minKeepTime
	 * @return
	 */
	@Override
	public int cleanHistory(Date minKeepTime) {
		String sql = "delete from instance_task where create_time < ?";
		return getSimpleJdbcTemplate().update(sql, minKeepTime);
	}

	@Override
	public List<InstanceTaskDB> getByQuery(InstanceTaskQuery query) {
		StringBuilder sql = new StringBuilder("select " + COLUMNS + " from instance_task where is_valid=1 ");
		if (query != null) {
			if (query.getInstanceFlowId() != null) {
				sql.append(" and instance_flow_id = :instanceFlowId");
			}
			if (query.getFileId() != null) {
				sql.append(" and file_id = :fileId");
			}
			if (StringUtils.isNotBlank(query.getStatus())) {
				sql.append(" and status = :status");
			}
			if (StringUtils.isNotBlank(query.getStartTime())) {
				sql.append(" and start_time >= :startTime");
			}
			if (StringUtils.isNotBlank(query.getEndTime())) {
				sql.append(" and end_time <= :endTime");
			}
			sql.append(" ORDER BY start_time DESC ");
			if (query.getFirstIndex() != null && query.getPageCount() != null) {
				sql.append(" limit :firstIndex,:pageCount");
			}
		}
		return getSlaveNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), getEntityRowMapper());
	}

	@Override
	public InstanceTaskDB getById(String instanceFlowId, int fileId) {
		String sql = "select "+ COLUMNS + " from instance_task where  instance_flow_id=? and file_id = ? ";
		return (InstanceTaskDB) DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(), instanceFlowId, fileId));
	}
}

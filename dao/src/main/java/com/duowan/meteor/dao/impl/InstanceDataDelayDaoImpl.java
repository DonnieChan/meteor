package com.duowan.meteor.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.InstanceDataDelayDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.instance.InstanceDataDelay;

@Repository("instanceDataDelayDao")
public class InstanceDataDelayDaoImpl extends BaseSpringJdbcDao implements InstanceDataDelayDao {

	private RowMapper<InstanceDataDelay> entityRowMapper = new BeanPropertyRowMapper<InstanceDataDelay>(getEntityClass());

	static final private String COLUMNS = "ttime,source_task_id,task_id,delay_millis,create_time";
	static final private String SELECT_FROM = "select " + COLUMNS + " from instance_data_delay";

	@Override
	public Class<InstanceDataDelay> getEntityClass() {
		return InstanceDataDelay.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "ttime";
	}

	public RowMapper<InstanceDataDelay> getEntityRowMapper() {
		return entityRowMapper;
	}

	public void insert(InstanceDataDelay entity) {
		String sql = getInsertSql();
		getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	private String getInsertSql() {
		return "insert ignore into instance_data_delay (ttime,source_task_id,task_id,delay_millis,create_time) values (:ttime,:sourceTaskId,:taskId,:delayMillis,:createTime)";
	}

	public int update(InstanceDataDelay entity) {
		String sql = "update instance_data_delay set delay_millis=:delayMillis where  ttime = :ttime and source_task_id = :sourceTaskId and task_id = :taskId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int deleteById(Date ttime, int sourceTaskId, int taskId) {
		String sql = "delete from instance_data_delay where  ttime = ? and source_task_id = ? and task_id = ? ";
		return getSimpleJdbcTemplate().update(sql, ttime, sourceTaskId, taskId);
	}

	public InstanceDataDelay getById(Date ttime, int sourceTaskId, int taskId) {
		String sql = SELECT_FROM + " where  ttime = ? and source_task_id = ? and task_id = ? ";
		return (InstanceDataDelay) DataAccessUtils.singleResult(getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), ttime, sourceTaskId, taskId));
	}

	@Override
	public void batchInsert(List<InstanceDataDelay> list) {
		SqlParameterSource[] batchArgs = SqlParameterSourceUtils.createBatch(list.toArray());
		getNamedParameterJdbcTemplate().batchUpdate(getInsertSql(), batchArgs);
	}

	@Override
	public int cleanHistory(Date minKeepTime) {
		String sql = "delete from instance_data_delay where create_time < ?";
		return getSimpleJdbcTemplate().update(sql, minKeepTime);
	}

	@Override
	public List<InstanceDataDelay> getRecentDelays(Date startCreateTime) {
		String sql = "select source_task_id, max(delay_millis) delay_millis from instance_data_delay where create_time>=? group by source_task_id ";
		return getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), startCreateTime);
	}

}

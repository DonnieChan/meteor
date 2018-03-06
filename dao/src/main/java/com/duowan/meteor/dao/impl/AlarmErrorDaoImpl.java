package com.duowan.meteor.dao.impl;

import java.util.List;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.AlarmErrorDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.alarm.AlarmError;

@Repository("alarmErrorDao")
public class AlarmErrorDaoImpl extends BaseSpringJdbcDao implements AlarmErrorDao {

	private RowMapper<AlarmError> entityRowMapper = new BeanPropertyRowMapper<AlarmError>(getEntityClass());

	static final private String COLUMNS = "source_task_id,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user";
	static final private String SELECT_FROM = "select " + COLUMNS + " from alarm_error";

	@Override
	public Class<AlarmError> getEntityClass() {
		return AlarmError.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "sourceTaskId";
	}

	public RowMapper<AlarmError> getEntityRowMapper() {
		return entityRowMapper;
	}

	public void insert(AlarmError entity) {
		String sql = "insert into alarm_error (source_task_id,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user) values "
				+ " (:sourceTaskId,:alarmGapMin,:alarmUsers,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int update(AlarmError entity) {
		String sql = "update alarm_error set "
				+ " alarm_gap_min=:alarmGapMin,alarm_users=:alarmUsers,remarks=:remarks,is_valid=:isValid,create_time=:createTime,update_time=:updateTime,create_user=:createUser,update_user=:updateUser "
				+ " where  source_task_id = :sourceTaskId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int deleteById(int sourceTaskId) {
		String sql = "delete from alarm_error where  source_task_id = ? ";
		return getSimpleJdbcTemplate().update(sql, sourceTaskId);
	}

	public AlarmError getById(int sourceTaskId) {
		String sql = SELECT_FROM + " where  source_task_id = ? ";
		return (AlarmError) DataAccessUtils.singleResult(getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), sourceTaskId));
	}

	@Override
	public List<AlarmError> getAllValid() {
		String sql = SELECT_FROM + " where is_valid=1 order by source_task_id";
		return getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper());
	}
}

package com.duowan.meteor.dao.impl;

import java.util.List;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.AlarmDataDelayDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.alarm.AlarmDataDelay;

@Repository("alarmDataDelayDao")
public class AlarmDataDelayDaoImpl extends BaseSpringJdbcDao implements AlarmDataDelayDao {

	private RowMapper<AlarmDataDelay> entityRowMapper = new BeanPropertyRowMapper<AlarmDataDelay>(getEntityClass());

	static final private String COLUMNS = "source_task_id,delay_second,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user";
	static final private String SELECT_FROM = "select " + COLUMNS + " from alarm_data_delay";

	@Override
	public Class<AlarmDataDelay> getEntityClass() {
		return AlarmDataDelay.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "sourceTaskId";
	}

	public RowMapper<AlarmDataDelay> getEntityRowMapper() {
		return entityRowMapper;
	}

	public void insert(AlarmDataDelay entity) {
		String sql = "insert into alarm_data_delay (source_task_id,delay_second,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user) values "
				+ " (:sourceTaskId,:delaySecond,:alarmGapMin,:alarmUsers,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int update(AlarmDataDelay entity) {
		String sql = "update alarm_data_delay set "
				+ " delay_second=:delaySecond,alarm_gap_min=:alarmGapMin,alarm_users=:alarmUsers,remarks=:remarks,is_valid=:isValid,create_time=:createTime,update_time=:updateTime,create_user=:createUser,update_user=:updateUser "
				+ " where  source_task_id = :sourceTaskId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int deleteById(int sourceTaskId) {
		String sql = "delete from alarm_data_delay where  source_task_id = ? ";
		return getSimpleJdbcTemplate().update(sql, sourceTaskId);
	}

	public AlarmDataDelay getById(int sourceTaskId) {
		String sql = SELECT_FROM + " where  source_task_id = ? ";
		return (AlarmDataDelay) DataAccessUtils.singleResult(getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), sourceTaskId));
	}

	@Override
	public List<AlarmDataDelay> getAllValid() {
		String sql = SELECT_FROM + " where is_valid=1 order by source_task_id";
		return getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper());
	}
}

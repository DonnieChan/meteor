package com.duowan.meteor.dao.impl;

import java.util.List;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.AlarmSliceDelayDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.alarm.AlarmSliceDelay;

@Repository("alarmSliceDelayDao")
public class AlarmSliceDelayDaoImpl extends BaseSpringJdbcDao implements AlarmSliceDelayDao {

	private RowMapper<AlarmSliceDelay> entityRowMapper = new BeanPropertyRowMapper<AlarmSliceDelay>(getEntityClass());

	static final private String COLUMNS = "source_task_id,delay_second,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user";
	static final private String SELECT_FROM = "select " + COLUMNS + " from alarm_slice_delay";

	@Override
	public Class<AlarmSliceDelay> getEntityClass() {
		return AlarmSliceDelay.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "sourceTaskId";
	}

	public RowMapper<AlarmSliceDelay> getEntityRowMapper() {
		return entityRowMapper;
	}

	public void insert(AlarmSliceDelay entity) {
		String sql = "insert into alarm_slice_delay (source_task_id,delay_second,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user) values "
				+ " (:sourceTaskId,:delaySecond,:alarmGapMin,:alarmUsers,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int update(AlarmSliceDelay entity) {
		String sql = "update alarm_slice_delay set "
				+ " delay_second=:delaySecond,alarm_gap_min=:alarmGapMin,alarm_users=:alarmUsers,remarks=:remarks,is_valid=:isValid,create_time=:createTime,update_time=:updateTime,create_user=:createUser,update_user=:updateUser "
				+ " where  source_task_id = :sourceTaskId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int deleteById(int sourceTaskId) {
		String sql = "delete from alarm_slice_delay where  source_task_id = ? ";
		return getSimpleJdbcTemplate().update(sql, sourceTaskId);
	}

	public AlarmSliceDelay getById(int sourceTaskId) {
		String sql = SELECT_FROM + " where  source_task_id = ? ";
		return (AlarmSliceDelay) DataAccessUtils.singleResult(getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), sourceTaskId));
	}

	@Override
	public List<AlarmSliceDelay> getAllValid() {
		String sql = SELECT_FROM + " where is_valid = 1 order by source_task_id";
		return getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper());
	}

}

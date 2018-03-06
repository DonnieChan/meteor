package com.duowan.meteor.dao.impl;

import java.util.List;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.AlarmLoopCompareDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.alarm.AlarmLoopCompare;

@Repository("alarmLoopCompareDao")
public class AlarmLoopCompareDaoImpl extends BaseSpringJdbcDao implements AlarmLoopCompareDao {

	private RowMapper<AlarmLoopCompare> entityRowMapper = new BeanPropertyRowMapper<AlarmLoopCompare>(getEntityClass());

	static final private String COLUMNS = "source_task_id,reduce_rate,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user";
	static final private String SELECT_FROM = "select " + COLUMNS + " from alarm_loop_compare";

	@Override
	public Class<AlarmLoopCompare> getEntityClass() {
		return AlarmLoopCompare.class;
	}

	@Override
	public String getIdentifierPropertyName() {
		return "sourceTaskId";
	}

	public RowMapper<AlarmLoopCompare> getEntityRowMapper() {
		return entityRowMapper;
	}

	public void insert(AlarmLoopCompare entity) {
		String sql = "insert into alarm_loop_compare (source_task_id,reduce_rate,alarm_gap_min,alarm_users,remarks,is_valid,create_time,update_time,create_user,update_user) "
				+ " values (:sourceTaskId,:reduceRate,:alarmGapMin,:alarmUsers,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int update(AlarmLoopCompare entity) {
		String sql = "update alarm_loop_compare set "
				+ " loop_min=:loopMin,reduce_rate=:reduceRate,alarm_gap_min=:alarmGapMin,alarm_users=:alarmUsers,remarks=:remarks,is_valid=:isValid,create_time=:createTime,update_time=:updateTime,create_user=:createUser,update_user=:updateUser "
				+ " where  source_task_id = :sourceTaskId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}

	public int deleteById(int sourceTaskId) {
		String sql = "delete from alarm_loop_compare where  source_task_id = ? ";
		return getSimpleJdbcTemplate().update(sql, sourceTaskId);
	}

	public AlarmLoopCompare getById(int sourceTaskId) {
		String sql = SELECT_FROM + " where  source_task_id = ? ";
		return (AlarmLoopCompare) DataAccessUtils.singleResult(getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), sourceTaskId));
	}

	@Override
	public List<AlarmLoopCompare> getAllValid() {
		String sql = SELECT_FROM + " where is_valid=1 order by source_task_id";
		return getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper());
	}
}

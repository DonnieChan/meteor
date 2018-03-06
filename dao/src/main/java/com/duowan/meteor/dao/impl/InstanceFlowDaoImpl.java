package com.duowan.meteor.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.InstanceFlowDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.alarm.LoopRate;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.query.InstanceFlowQuery;

@Repository("instanceFlowDao")
public class InstanceFlowDaoImpl extends BaseSpringJdbcDao implements InstanceFlowDao {
	
	private RowMapper<InstanceFlow> entityRowMapper = new BeanPropertyRowMapper<InstanceFlow>(getEntityClass());
	private RowMapper<LoopRate> loopRateRowMapper = new BeanPropertyRowMapper<LoopRate>(LoopRate.class);
	
	static final private String COLUMNS = "instance_flow_id,source_task_id,init_time,start_time,end_time,status,log,remarks,is_valid,create_user,update_user,create_time,update_time";
	static final private String SELECT_FROM = "select " + COLUMNS + " from instance_flow";

	@Override
	public Class<InstanceFlow> getEntityClass() {
		return InstanceFlow.class;
	}
	
	public RowMapper<InstanceFlow> getEntityRowMapper() {
		return entityRowMapper;
	}
	
	/**
	 * @return
	 */
	private String getInsertSql() {
		String sql = "insert into instance_flow " 
			 + " (instance_flow_id,source_task_id,init_time,start_time,end_time,status,log,remarks,is_valid,create_user,update_user,create_time,update_time) " 
			 + " values "
			 + " (:InstanceFlowId,:sourceTaskId,:initTime,:startTime,:endTime,:status,:log,:remarks,:isValid,:createUser,:updateUser,:createTime,:updateTime)";
		return sql;
	}
	
	@Override
	public int[] batchInsert(List<InstanceFlow> entityList) {
		SqlParameterSource[] batchArgs = SqlParameterSourceUtils.createBatch(entityList.toArray());   
		return getNamedParameterJdbcTemplate().batchUpdate(getInsertSql(), batchArgs);
	}

	/**
	 * @param minKeepTime
	 * @return
	 */
	@Override
	public int cleanHistory(Date minKeepTime) {
		String sql = "delete from instance_flow where create_time < ?";
		return getSimpleJdbcTemplate().update(sql, minKeepTime);
	}

	@Override
	public List<InstanceFlow> getByQuery(InstanceFlowQuery query) {
		StringBuilder sql = new StringBuilder(SELECT_FROM + " where is_valid=1 ");
		if(query != null) {
			if(query.getSourceTaskId() != null) {
				 sql.append(" and source_task_id = :sourceTaskId");
			}			
			if(StringUtils.isNotBlank(query.getStatus())) {
	            sql.append(" and status = :status");
	        }
			if(StringUtils.isNotBlank(query.getStartTime())) {
	            sql.append(" and start_time >= :startTime");
	        }
			if(StringUtils.isNotBlank(query.getEndTime())) {
	            sql.append(" and end_time <= :endTime");
	        }
			sql.append(" ORDER BY start_time DESC ");
			if(query.getFirstIndex()!=null && query.getPageCount()!=null) {
				sql.append(" limit :firstIndex,:pageCount");
			}
		}		
		return getSlaveNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), getEntityRowMapper());	
	}

	@Override
	public List<InstanceFlow> getRecentInstances(Date startCreateTime, String status) {
		if (status != null) {
			String sql = SELECT_FROM + " where create_time>=? and status=?";
			return getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), startCreateTime, status);
		} else {
			String sql = SELECT_FROM + " where create_time>=?";
			return getSlaveSimpleJdbcTemplate().query(sql, getEntityRowMapper(), startCreateTime);
		}
	}

	@Override
	public List<LoopRate> getLoopRates(Date date0, Date date1, Date date2) {
		StringBuilder sql = new StringBuilder();
		sql.append("select t1.source_task_id, COALESCE(round(t2.cnt/t1.cnt, 2), 0) loop_rate ");
		sql.append("from ( ");
		sql.append("  select source_task_id, count(1) cnt ");
		sql.append("  from instance_flow ");
		sql.append("  where create_time>=? and create_time<=? ");
		sql.append("  group by source_task_id ");
		sql.append(") t1 ");
		sql.append("left join ( ");
		sql.append("  select source_task_id, count(1) cnt ");
		sql.append("  from instance_flow ");
		sql.append("  where create_time>=? and create_time<=? ");
		sql.append("  group by source_task_id ");
		sql.append(") t2 on t2.source_task_id=t1.source_task_id ");
		return getSlaveSimpleJdbcTemplate().query(sql.toString(), loopRateRowMapper, date0, date1, date1, date2);
	}

}

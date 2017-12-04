package com.duowan.meteor.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.duowan.meteor.dao.DefDependDao;
import com.duowan.meteor.dao.common.BaseSpringJdbcDao;
import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.query.DefDependQuery;

/**
 * tableName: def_depend
 * [DefDepend] 的Dao操作 
 * @author chenwu
*/
@Repository("defDependDao")
public class DefDependDaoImpl extends BaseSpringJdbcDao implements DefDependDao {
	
	private RowMapper<DefDepend> entityRowMapper = new BeanPropertyRowMapper<DefDepend>(getEntityClass());
	
	static final private String COLUMNS = "file_id,pre_file_id,project_id,remarks,is_valid,create_time,update_time,create_user,update_user";
	static final private String SELECT_FROM = "select " + COLUMNS + " from def_depend";
	
	@Override
	public Class<DefDepend> getEntityClass() {
		return DefDepend.class;
	}
	
	@Override
	public String getIdentifierPropertyName() {
		return "fileId";
	}
	
	public RowMapper<DefDepend> getEntityRowMapper() {
		return entityRowMapper;
	}
	
	public int insert(DefDepend entity) {
		String sql = "insert into def_depend " 
			 + " (file_id,pre_file_id,project_id,remarks,is_valid,create_time,update_time,create_user,update_user) " 
			 + " values "
			 + " (:fileId,:preFileId,:projectId,:remarks,:isValid,:createTime,:updateTime,:createUser,:updateUser)";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
		// return insertWithGeneratedKey(entity,sql);
	}
	
	public int update(DefDepend entity) {
		String sql = "update def_depend set "
					+ " project_id=:projectId,remarks=:remarks,is_valid=:isValid,create_time=:createTime,update_time=:updateTime,create_user=:createUser,update_user=:updateUser "
					+ " where  file_id = :fileId and pre_file_id = :preFileId ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}
	
	public int deleteById(int fileId, int preFileId) {
		String sql = "delete from def_depend where  file_id = ? and pre_file_id = ? ";
		return  getSimpleJdbcTemplate().update(sql, fileId, preFileId);
	}

	public DefDepend getById(int fileId, int preFileId) {
		String sql = SELECT_FROM + " where  file_id = ? and pre_file_id = ? ";
		return (DefDepend)DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(),fileId,preFileId));
	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	public List<DefDepend> getByDefDependQuery(DefDependQuery query) {
		StringBuilder sql = new StringBuilder("select "+ COLUMNS + " from def_depend where 1=1 ");
		if(query != null) {
			if(query.getFileId() != null) {
	            sql.append(" and file_id = :fileId ");
	        }
			if(query.getPreFileId() != null) {
	            sql.append(" and pre_file_id = :preFileId ");
	        }
			if(query.getProjectId() != null) {
	            sql.append(" and project_id = :projectId ");
	        }
			if(query.getIsValid() != null) {
	            sql.append(" and is_valid = :isValid ");
	        }
			if(StringUtils.isNotBlank(query.getCreateUser())) {
	            sql.append(" and create_user = :createUser ");
	        }
			if(StringUtils.isNotBlank(query.getUpdateUser())) {
	            sql.append(" and update_user = :updateUser ");
	        }
		} else {
			query = new DefDependQuery();
		}
		return getNamedParameterJdbcTemplate().query(sql.toString(), new BeanPropertySqlParameterSource(query), getEntityRowMapper());		
	}

	@Override
	public int deleteByFileId(int fileId) {
		String sql = "delete from def_depend where  file_id = ?";
		return  getSimpleJdbcTemplate().update(sql, fileId);
	}
	
	@Override
	public int deleteByPreFileId(int prefileId) {
		String sql = "delete from def_depend where  pre_file_id = ?";
		return  getSimpleJdbcTemplate().update(sql, prefileId);
	}

	@Override
	public List<DefDepend> getAll() {
		String sql = "select "+ COLUMNS + " from def_depend";
		Map<String, Object> paramMap = null;
		return getNamedParameterJdbcTemplate().query(sql, paramMap, getEntityRowMapper());
	}
}

package com.duowan.meteor.task.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class DaoCommon {

	protected SimpleJdbcTemplate simpleJdbcTemplate;
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void init(DataSource ds) {
		simpleJdbcTemplate = new SimpleJdbcTemplate(ds);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(ds);
	}
	
	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}
	
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}
}

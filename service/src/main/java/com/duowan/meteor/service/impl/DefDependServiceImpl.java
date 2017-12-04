/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.DefDependDao;
import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.query.DefDependQuery;
import com.duowan.meteor.service.DefDependService;

/**
 * [DefDepend] 的业务操作实现类
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service("defDependService")
@Transactional
public class DefDependServiceImpl implements DefDependService {

	protected static final Logger log = LoggerFactory.getLogger(DefDependServiceImpl.class);

	@Autowired
	private DefDependDao defDependDao;

	/** 
	 * 创建DefDepend
	 **/
	public int create(DefDepend defDepend) {
		Assert.notNull(defDepend, "'defDepend' must be not null");
		return defDependDao.insert(defDepend);
	}

	/** 
	 * 更新DefDepend
	 **/
	public int update(DefDepend defDepend) {
		Assert.notNull(defDepend, "'defDepend' must be not null");
		return defDependDao.update(defDepend);
	}

	/** 
	 * 删除DefDepend
	 **/
	public int deleteById(int fileId, int preFileId) {
		return defDependDao.deleteById(fileId, preFileId);
	}

	/** 
	 * 根据ID得到DefDepend
	 **/
	public DefDepend getById(int fileId, int preFileId) {
		return defDependDao.getById(fileId, preFileId);
	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	@Override
	public List<DefDepend> getByDefDependQuery(DefDependQuery query) {
		return defDependDao.getByDefDependQuery(query);
	}

	@Override
	public List<DefDepend> getById(int fileId) {
		DefDependQuery defDependQuery = new DefDependQuery();
		defDependQuery.setFileId(fileId);

		return getByDefDependQuery(defDependQuery);
	}

}

package com.duowan.meteor.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duowan.meteor.dao.DefFileSysExtDao;
import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;
import com.duowan.meteor.service.DefFileSysExtService;

/**
 * [DefFileSysExt] 的业务操作实现类
 * 
 * @author taosheng
 */
@Service("defFileSysExtService")
@Transactional
public class DefFileSysExtServiceImpl implements DefFileSysExtService {

	protected static final Logger log = LoggerFactory.getLogger(DefFileSysExtServiceImpl.class);

	@Autowired
	private DefFileSysExtDao defFileSysExtDao;

	@Override
	public DefFileSysExt getById(int fileId) {
		return defFileSysExtDao.getById(fileId);
	}

	@Override
	public String getInfoOnViewById(int fileId) {
		DefFileSysExt defFileSysExt = getById(fileId);
		return defFileSysExt == null ? "[]" : defFileSysExt.toStringOnView();
	}

	@Override
	public List<DefFileSysExt> getPreDependById(int fileId) {
		return defFileSysExtDao.getPreDependById(fileId);
	}

	@Override
	public List<DefFileSysExt> getByDefFileSysExtQuery(DefFileSysExtQuery query) {
		String lineStatus = query.getLineStatus();
		if (query.getOfflineTimeBegin() == null && query.getOfflineTimeEnd() == null) {
			if (StringUtils.equals("online", lineStatus)) {
				query.setOfflineTimeBegin(new Date());
			}else if (StringUtils.equals("offline", lineStatus)) {
				query.setOfflineTimeEnd(new Date());
			}
			
		}
		return defFileSysExtDao.getByDefFileSysExtQuery(query);
	}

	@Override
	public int getMaxFileId() {
		return defFileSysExtDao.getMaxFileId();
	}

	@Override
	public List<DefFileSysExt> getDefFileNodeChainById(int fileId) {
		return defFileSysExtDao.getDefFileNodeChainById(fileId);
	}

}

package com.duowan.meteor.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.DefFileSysDao;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;
import com.duowan.meteor.model.query.DefFileSysQuery;
import com.duowan.meteor.service.DefDependService;
import com.duowan.meteor.service.DefFileSysService;


/**
 * [DefFileSys] 的业务操作实现类
 * 
 * @author chenwu
 */
@Service("defFileSysService")
@Transactional
public class DefFileSysServiceImpl implements DefFileSysService {

	protected static final Logger log = LoggerFactory.getLogger(DefFileSysServiceImpl.class);

	@Autowired
	private DefFileSysDao defFileSysDao;
	
	@Autowired
	private DefDependService defDependService;
	
	/** 
	 * 创建DefFileSys
	 **/
	public int create(DefFileSys defFileSys) {
	    Assert.notNull(defFileSys,"'defFileSys' must be not null");
	    if (defFileSys.getOfflineTime() == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, 1);
	    	defFileSys.setOfflineTime(calendar.getTime());
		}
	    return defFileSysDao.insert(defFileSys);
	}
	
	/** 
	 * 更新DefFileSys
	 **/	
    public int update(DefFileSys defFileSys) {
        Assert.notNull(defFileSys,"'defFileSys' must be not null");
        return defFileSysDao.update(defFileSys);
    }	
    
	/** 
	 * 删除DefFileSys
	 **/
    public int deleteById(int fileId) {
        return defFileSysDao.deleteById(fileId);
    }
    
	/** 
	 * 根据ID得到DefFileSys
	 **/    
    public DefFileSys getById(int fileId) {
        return defFileSysDao.getById(fileId);
    }

	/**
	 * @param isValid
	 * @return
	 */
	@Override
	public List<DefFileSys> getByDefFileSysQuery(DefFileSysQuery query) {
		return defFileSysDao.getByDefFileSysQuery(query);
	}
	
	/**
	 * 
	 * @param fileType
	 * @param fileTypeCategory
	 * @param isCanDepend
	 * @return
	 */
	public List<DefFileSys> getByDefFileType(String fileType, String fileTypeCategory, Integer isCanDepend) {
		return defFileSysDao.getByDefFileType(fileType, fileTypeCategory, isCanDepend);
	}

	@Override
	public DefFileSys createWithReturnObject(DefFileSys entity) {
		return defFileSysDao.insertWithReturnObject(entity);
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
		return defFileSysDao.getByDefFileSysExtQuery(query);
	}
}

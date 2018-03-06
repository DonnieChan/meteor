package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;

/**
 * [DefFileSysExt] 的业务操作
 * 
 * @author taosheng
 */
public interface DefFileSysExtService {

	/** 
	 * 根据ID得到DefFileSysExt
	 **/
	public DefFileSysExt getById(int fileId);

	/**
	 * 获取前缀依赖
	 */
	public List<DefFileSysExt> getPreDependById(int fileId);

	public int getMaxFileId();
	
	public String getInfoOnViewById(int fileId);
	
	public List<DefFileSysExt> getByDefFileSysExtQuery(DefFileSysExtQuery query) ;

	public List<DefFileSysExt> getDefFileNodeChainById(int fileId);
}

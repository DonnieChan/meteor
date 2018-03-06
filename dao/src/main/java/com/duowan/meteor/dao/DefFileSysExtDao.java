package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;

/**
 * tableName: def_file_sys
 * [DefFileSysExt] 的Dao操作
 * @author taosheng
*/
public interface DefFileSysExtDao {
	
	/**
	 * 查询fileId
	 */
	public DefFileSysExt getById(int fileId);

	/**
	 * 获取前缀依赖
	 */
	public List<DefFileSysExt> getPreDependById(int fileId);

	List<DefFileSysExt> getByDefFileSysExtQuery(DefFileSysExtQuery query);

	public int getMaxFileId();

	public List<DefFileSysExt> getDefFileNodeChainById(int fileId);
	
}

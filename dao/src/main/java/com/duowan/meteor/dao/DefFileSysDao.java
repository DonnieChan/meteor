package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;
import com.duowan.meteor.model.query.DefFileSysQuery;

/**
 * tableName: def_file_sys
 * [DefFileSys] 的Dao操作
 * @author chenwu
*/
public interface DefFileSysDao {
	
	public int insert(DefFileSys entity);
	
	public DefFileSys insertWithReturnObject(DefFileSys entity);
	
	public int update(DefFileSys entity);

	public int deleteById(int fileId);
	
	public DefFileSys getById(int fileId);
	
	public List<DefFileSys> getByDefFileSysQuery(DefFileSysQuery query);

	/**
	 * @param fileType
	 * @param fileTypeCategory
	 * @param isCanDepend
	 * @return 
	 */
	public List<DefFileSys> getByDefFileType(String fileType, String fileTypeCategory, Integer dependFlag);
	
	List<DefFileSysExt> getByDefFileSysExtQuery(DefFileSysExtQuery query);
}

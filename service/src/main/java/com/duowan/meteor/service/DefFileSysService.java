package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.db.DefFileSysExt;
import com.duowan.meteor.model.query.DefFileSysExtQuery;
import com.duowan.meteor.model.query.DefFileSysQuery;


/**
 * [DefFileSys] 的业务操作
 * 
 * @author chenwu
 */
public interface DefFileSysService {

	/** 
	 * 创建DefFileSys
	 **/
	public int create(DefFileSys defFileSys);
	
	public DefFileSys createWithReturnObject(DefFileSys entity);
	
	/** 
	 * 更新DefFileSys
	 **/	
    public int update(DefFileSys defFileSys);
    
	/** 
	 * 删除DefFileSys
	 **/
    public int deleteById(int fileId);
    
	/** 
	 * 根据ID得到DefFileSys
	 **/    
    public DefFileSys getById(int fileId);
    
    /**
     * 
     * @param query
     * @return
     */
    public List<DefFileSys> getByDefFileSysQuery(DefFileSysQuery query);
    
    /**
     * 
     * @param fileType
     * @param fileTypeCategory
     * @param isCanDepend
     * @return
     */
    public List<DefFileSys> getByDefFileType(String fileType, String fileTypeCategory, Integer isCanDepend);
    
    public List<DefFileSysExt> getByDefFileSysExtQuery(DefFileSysExtQuery query) ;
}

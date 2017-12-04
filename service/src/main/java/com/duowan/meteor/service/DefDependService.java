package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.query.DefDependQuery;


/**
 * [DefDepend] 的业务操作
 * 
 * @author chenwu
 */
public interface DefDependService {

	/** 
	 * 创建DefDepend
	 **/
	public int create(DefDepend defDepend);
	
	/** 
	 * 更新DefDepend
	 **/	
    public int update(DefDepend defDepend);
    
	/** 
	 * 删除DefDepend
	 **/
    public int deleteById(int fileId, int preFileId);
    
	/** 
	 * 根据ID得到DefDepend
	 **/    
    public DefDepend getById(int fileId, int preFileId);
    
    /**
     * 
     * @param query
     * @return
     */
    public List<DefDepend> getByDefDependQuery(DefDependQuery query);
    

    /**
     * 前置依赖 列表
     */
    public List<DefDepend> getById(int fileId);    
    
}

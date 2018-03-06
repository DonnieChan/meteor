package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.query.DefDependQuery;

/**
 * tableName: def_depend
 * [DefDepend] 的Dao操作
 * @author chenwu
*/
public interface DefDependDao {
	
	public int insert(DefDepend entity);
	
	public int update(DefDepend entity);

	public int deleteById(int fileId, int preFileId);
	
	public int deleteByFileId(int fileId);
	
	public int deleteByPreFileId(int prefileId);
	
	public DefDepend getById(int fileId, int preFileId);
	
	public List<DefDepend> getByDefDependQuery(DefDependQuery query);
	
	public List<DefDepend> getAll();
}

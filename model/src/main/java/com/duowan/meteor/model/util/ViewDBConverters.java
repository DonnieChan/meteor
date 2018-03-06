package com.duowan.meteor.model.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.duowan.meteor.model.db.DefDepend;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.model.view.AbstractTaskDepend;
import com.duowan.meteor.util.JsonUtils;

/**
 * 展示模型与数据库模型转换
 * @author chenwu
 */
public class ViewDBConverters {

	/**
	 * 展示模型 -> 数据库模型 ：任务定义
	 * @param viewObject
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static DefFileSys convertToDefFileSys(AbstractBase viewObject) throws Exception {
		if(viewObject == null) {
			return null;
		}
		DefFileSys defFileSys = new DefFileSys();
		defFileSys.setFileId(viewObject.getFileId());
		defFileSys.setParentFileId(viewObject.getParentFileId());
		defFileSys.setProjectId(viewObject.getProjectId());
		defFileSys.setFileName(viewObject.getFileName());
		defFileSys.setFileType(viewObject.getFileType());
		defFileSys.setRemarks(viewObject.getRemarks());
		defFileSys.setCreateUser(viewObject.getCreateUser());
		defFileSys.setUpdateUser(viewObject.getUpdateUser());
		defFileSys.setCreateTime(viewObject.getCreateTime());
		defFileSys.setUpdateTime(viewObject.getUpdateTime());
		defFileSys.setOfflineTime(viewObject.getOfflineTime());
		defFileSys.setContacts(viewObject.getContacts());
		defFileSys.setIsDir(viewObject.getIsDir());
		defFileSys.setIsValid(viewObject.getIsValid());
		Map classToMap = BeanUtils.describe(viewObject);
		filterProps(classToMap);
		String json = JsonUtils.encode(classToMap);
		defFileSys.setFileBody(json);
		return defFileSys;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void filterProps(Map classToMap) {
		String[] propArray = new String[]{
				"fileId", "parentFileId", "projectId", "fileName", "fileType", "isDir", "ext",
				"remarks", "isValid", "createTime", "updateTime", "offlineTime", "createUser", "updateUser",
				"contacts",
				"preDependSet",
				"postDependSet",
				};
		for(String prop : propArray) {
			classToMap.remove(prop);
		}
		//为了适应RPC接口，处理AbstractBase类的@JsonTypeInfo
		classToMap.put("CLASS", StringUtils.substringAfter((String) classToMap.remove("class"), "class "));
	}
	
	/**
	 * 展示模型 -> 数据库模型 ：任务依赖
	 * @param viewObject
	 * @return
	 */
	public static List<DefDepend> convertToDefDependList(AbstractBase viewObject) {
		if(viewObject == null) {
			return null;
		}
		if(viewObject instanceof AbstractTaskDepend) {
			AbstractTaskDepend abstractTaskDepend = (AbstractTaskDepend) viewObject;
			Set<Integer> preDependSet = abstractTaskDepend.getPreDependSet();
			if(preDependSet != null) {
				List<DefDepend> defDependList = new ArrayList<DefDepend>();
				for(Integer preFileId : preDependSet) {
					DefDepend defDepend = new DefDepend();
					defDepend.setFileId(viewObject.getFileId());
					defDepend.setPreFileId(preFileId);
					defDepend.setProjectId(viewObject.getProjectId());
					defDepend.setIsValid(viewObject.getIsValid());
					defDepend.setCreateTime(viewObject.getCreateTime());
					defDepend.setCreateUser(viewObject.getCreateUser());
					defDepend.setUpdateTime(viewObject.getUpdateTime());
					defDepend.setUpdateUser(viewObject.getUpdateUser());
					defDependList.add(defDepend);
				}
				return defDependList;
			}
			
		}
		return null;
	}
	
	/**
	 * 数据库模型 -> 展示模型
	 * @param defFileSys
	 * @param defDependList
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static AbstractBase convertToViewObject(DefFileSys defFileSys, List<DefDepend> defDependList) throws Exception {
		Set<Integer> preDependSet = null;
		if(defDependList != null && !defDependList.isEmpty()) {
			preDependSet = new HashSet<Integer>();
			for(DefDepend defDepend : defDependList) {
				if(defDepend.getFileId() == defFileSys.getFileId()) {
					preDependSet.add(defDepend.getPreFileId());
				}
			}
		}
		AbstractBase viewObject = convertToViewObjectWithSet(defFileSys, preDependSet);
		return viewObject;
	}
	
	/**
	 * 数据库模型 -> 展示模型
	 * @param defFileSys
	 * @param preDependSet
	 * @return
	 * @throws Exception
	 */
	public static AbstractBase convertToViewObjectWithSet(DefFileSys defFileSys, Set<Integer> preDependSet) throws Exception {
		AbstractBase viewObject = buildView(defFileSys);
		if(viewObject == null) {
			return null;
		}
		if(viewObject instanceof AbstractTaskDepend) {
			((AbstractTaskDepend) viewObject).setPreDependSet(preDependSet);
		}
		return viewObject;
	}

	/**
	 * 数据库模型 -> 展示模型
	 * @param defFileSys
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static AbstractBase buildView(DefFileSys defFileSys) throws JsonParseException, JsonMappingException, IOException {
		if(defFileSys == null) {
			return null;
		}
		FileType fileType = FileType.getFileTypeByName(defFileSys.getFileType());
		if(fileType == null || fileType.getRefClass() == null) {
			return null;
		}
		
		AbstractBase viewObject = (AbstractBase) JsonUtils.decode(defFileSys.getFileBody(), fileType.getRefClass());
		viewObject.setFileId(defFileSys.getFileId());
		viewObject.setParentFileId(defFileSys.getParentFileId());
		viewObject.setProjectId(defFileSys.getProjectId());
		viewObject.setFileName(defFileSys.getFileName());
		viewObject.setFileType(defFileSys.getFileType());
		viewObject.setIsDir(defFileSys.getIsDir());
		viewObject.setRemarks(defFileSys.getRemarks());
		viewObject.setIsValid(defFileSys.getIsValid());
		viewObject.setCreateTime(defFileSys.getCreateTime());
		viewObject.setUpdateTime(defFileSys.getUpdateTime());
		viewObject.setCreateUser(defFileSys.getCreateUser());
		viewObject.setUpdateUser(defFileSys.getUpdateUser());
		viewObject.setOfflineTime(defFileSys.getOfflineTime());
		viewObject.setContacts(defFileSys.getContacts());
		return viewObject;
	}
}

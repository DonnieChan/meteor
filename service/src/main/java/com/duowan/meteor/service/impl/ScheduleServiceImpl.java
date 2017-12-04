package com.duowan.meteor.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.db.DefProject;
import com.duowan.meteor.model.menutree.TreeNode;
import com.duowan.meteor.service.DefFileSysService;
import com.duowan.meteor.service.DefProjectService;
import com.duowan.meteor.service.ScheduleService;

@Service("scheduleService")
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

	protected static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

	@Autowired
	private DefProjectService defProjectService;

	@Autowired
	private DefFileSysService defFileSysService;

	@Override
	public List<TreeNode> getAll() throws Exception {
		List<TreeNode> subList;
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();

		List<DefProject> defProjectList = defProjectService.getByDefProjectQuery(null);
		subList = convertDefProject(defProjectList);
		if (subList != null) {
			treeNodes.addAll(subList);
		}

		List<DefFileSys> defFileSysList = defFileSysService.getByDefFileSysQuery(null);
		subList = convertDefFileSys(defFileSysList);
		if (subList != null) {
			treeNodes.addAll(subList);
		}

		return treeNodes;
	}
	
	
	public List<TreeNode> convertDefFileSys(List<DefFileSys> defFileSysList) throws Exception {
		Assert.isTrue(defFileSysList != null, "Wrong!! Must be defFileSysList!=null");

		long curTime = System.currentTimeMillis();
		List<TreeNode> pList = new ArrayList<TreeNode>();
		for (DefFileSys defFileSys : defFileSysList) {
			int parentId = defFileSys.getParentFileId();
			String parentUniqueName = parentId != 0 ? ("defFileSys" + "/" + parentId) : ("defProject" + "/" + defFileSys.getProjectId());
			
			TreeNode pNode = new TreeNode();
			pNode.setUniqueName("defFileSys" + "/" + defFileSys.getFileId());
			pNode.setParentUniqueName(parentUniqueName);
			pNode.setName(makeNodeIconStyle(defFileSys));
			pNode.setCaption(defFileSys.getFileName()); 

			Map<String, String> ext = new HashMap<String, String>();
			ext.put("idType", "defFileSys");
			ext.put("idValue", Integer.toString(defFileSys.getFileId()));
			ext.put("isProject", Integer.toString(0));
			ext.put("projectId", Integer.toString(defFileSys.getProjectId()));
			ext.put("fileId", Integer.toString(defFileSys.getFileId()));
			ext.put("fileType", defFileSys.getFileType());
			ext.put("isDir", Integer.toString(defFileSys.getIsDir()));
			Integer isValid = defFileSys.getIsValid();
			if(curTime - defFileSys.getOfflineTime().getTime() >= 0) {
				isValid = 0;
			}
			ext.put("isValid", Integer.toString(isValid));
			pNode.setExt(ext);
			pList.add(pNode);
		}
		return pList;
	}
	
	
	private String makeNodeIconStyle(DefFileSys defFileSys) {
		if (defFileSys == null) {
			return "";
		}
		String icon = "";
		if (1 == defFileSys.getIsDir()) {
			icon = "<i class='icon-book'></i>";
		} else {
			icon = "<i class='icon-pencil'></i>";
		}
		String result = icon + defFileSys.getFileName();
		return result;
	}


	public static List<TreeNode> convertDefProject(List<DefProject> defProjectList) throws Exception {
		Assert.isTrue(defProjectList != null, "Wrong!! Must be defList!=null");
		List<TreeNode> pList = new ArrayList<TreeNode>();
		String parentUniqueName = "defProject";
		String icon = "<i class='icon-home'></i>";
		for (DefProject defProject: defProjectList) {
			TreeNode pNode = new TreeNode();
			pNode.setUniqueName(parentUniqueName + "/" + defProject.getProjectId());
			pNode.setParentUniqueName(parentUniqueName);
			pNode.setName(icon + defProject.getProjectName());
			pNode.setCaption(defProject.getProjectName());

			Map<String, String> ext = new HashMap<String, String>();
			ext.put("idType", "defProject");
			ext.put("idValue", Integer.toString(defProject.getProjectId()));
			ext.put("isProject", Integer.toString(1));
			ext.put("projectId", Integer.toString(defProject.getProjectId()));
			ext.put("fileId", "0");
			ext.put("fileType", "defProject");
			ext.put("isDir", Integer.toString(0));
			ext.put("isValid", Integer.toString(defProject.getIsValid()));
			pNode.setExt(ext);
			pList.add(pNode);
		}
		return pList;
	}
}

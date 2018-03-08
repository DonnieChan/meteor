package com.duowan.meteor.mc.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duowan.meteor.mc.common.controller.UserBaseController;
import com.duowan.meteor.mc.utils.ControllerUtils;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.menutree.TreeNode;
import com.duowan.meteor.service.DefFileSysService;
import com.duowan.meteor.service.ScheduleService;

@Controller
@RequestMapping("/schedule")
public class ScheduleController extends UserBaseController {

	protected static Logger logger = LoggerFactory.getLogger(ScheduleController.class);

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private DefFileSysService defFileSysService;

	/**
	 * index
	 */
	@RequestMapping("/index.do")
	public String index(Integer projectId, Integer fileId, ModelMap model, HttpServletRequest request) throws Exception {
		if (projectId != null) {
			model.put("projectId", projectId);
		}
		model.put("fileId", fileId != null ? fileId : 0);
		return "pages/home/schedule/index";
	}

	/**
	 * getDefFileSysTree nPId
	 */
	@RequestMapping("/nPId/getDefFileSysTree.do")
	public void getDefFileSysTree(HttpServletResponse response, String searchName) throws Exception {
		List<TreeNode> pNodes = scheduleService.getAll();
		ControllerUtils.writeToResponse(pNodes, response);
	}

	/**
	 * project
	 */
	@RequestMapping("/project.do")
	public String project(Integer projectId, Integer fileId, ModelMap model, HttpServletRequest request) throws Exception {
		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		model.put("projectId", projectId);
		model.put("fileId", 0); // 无父节点的节点的父节点为0

		String projectDirFileType = "project";
		model.put("projectDirFileType", projectDirFileType);

		return "pages/home/schedule/project";
	}

	/**
	 * dir file
	 * http://127.0.0.1:8070/schedule/file.do?projectId=120&fileId=130416
	 */
	@RequestMapping({ "/dir.do", "/file.do" })
	public String dirAndFile(Integer projectId, Integer fileId, ModelMap model, HttpServletRequest request) throws Exception {
		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		Assert.notNull(fileId, "Wrong!! fileId must not be null!");
		DefFileSys defFileSys = defFileSysService.getById(fileId);
		Assert.notNull(defFileSys, "Wrong!! fileId[Id=" + fileId + "]'s file must not be null!");
		Assert.isTrue(projectId.intValue() == defFileSys.getProjectId(), "Wrong!! fileId[Id=" + fileId + "]'s projectId[Id=" + projectId + "] must not be the same to the input projectId from url!");

		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		model.put("projectId", projectId);
		model.put("fileId", fileId);
		model.put("defFileSys", defFileSys);
		model.put("idType", "defFileSys");
		model.put("idValue", fileId);

		String projectDirFileType = defFileSys.getIsDir() == 1 ? "dir" : "file";
		model.put("projectDirFileType", projectDirFileType);

		return "pages/home/schedule/" + projectDirFileType;
	}

	@RequestMapping({ "/displayDefFileTask.do" })
	public String displayDefFileTask(Integer projectId, Integer fileId, ModelMap model, HttpServletRequest request) throws Exception {
		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		Assert.notNull(fileId, "Wrong!! fileId must not be null!");
		DefFileSys defFileSys = defFileSysService.getById(fileId);
		Assert.notNull(defFileSys, "Wrong!! fileId[Id=" + fileId + "]'s file must not be null!");
		Assert.isTrue(projectId.intValue() == defFileSys.getProjectId(), "Wrong!! fileId[Id=" + fileId + "]'s projectId[Id=" + projectId + "] must not be the same to the input projectId from url!");

		return "redirect:/task/read.do" + "?projectId=" + projectId + "&fileId=" + fileId + "&fileType=" + defFileSys.getFileType();
	}

	@RequestMapping("/nPId/getFileById.do")
	public void getFileById(Integer fileId, HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		Assert.isTrue(fileId != null && fileId.intValue() != 0, "Wrong!! fileId[Id=" + fileId + "]'s file must not be null and not be 0 !");

		DefFileSys defFileSys = defFileSysService.getById(fileId);
		Assert.notNull(defFileSys, "Wrong!! fileId[Id=" + fileId + "]'s file must be valid!");

		ControllerUtils.writeToResponse(defFileSys, response);
	}
}

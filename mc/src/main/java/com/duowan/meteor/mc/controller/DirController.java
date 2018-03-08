package com.duowan.meteor.mc.controller;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duowan.meteor.mc.common.controller.UserBaseController;
import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.view.other.Dir;
import com.duowan.meteor.service.DefFileSysService;
import com.duowan.meteor.service.TaskService;

@Controller
@RequestMapping("/dir")
public class DirController extends UserBaseController {

	protected static Logger logger = LoggerFactory.getLogger(DirController.class);

	@Autowired
	private DefFileSysService defFileSysService;

	@Autowired
	private TaskService taskService;

	/**
	 * 创建目录
	 * Integer fileId 视为 parentFileId
	 */
	@RequestMapping("/createDir.do")
	public String createDir(Integer projectId, Integer fileId, String projectCreateDirIptName, ModelMap model) throws Exception {
		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		Assert.isTrue(!StringUtils.isBlank(projectCreateDirIptName), "Wrong!! 目录名称 must not be null!");

		Dir dir = new Dir();

		int parentFileId = fileId == null ? 0 : fileId;
		dir.setParentFileId(parentFileId);
		dir.setProjectId(projectId);
		dir.setFileName(projectCreateDirIptName);
		dir.setIsDir(1);
		dir.setOfflineTime(DateUtils.parseDate("2099-01-01 00:00:00", new String[] { "yyyy-MM-dd HH:mm:ss" }));
		dir.setRemarks("");
		dir.setIsValid(1);
		dir.setCreateTime(new Date());
		dir.setUpdateTime(new Date());
		dir.setCreateUser(getLoginPassport());
		dir.setUpdateUser(getLoginPassport());

		dir = (Dir) taskService.addTask(dir);

		return "redirect:/schedule/index.do?projectId=" + dir.getProjectId() + "&fileId=" + dir.getFileId();
	}

	/**
	 * 更改目录
	 */
	@RequestMapping("/renameDir.do")
	public String renameDir(DefFileSys defFileSys, ModelMap model) throws Exception {
		Assert.notNull(defFileSys, "Wrong!! projectId must not be null!");
		Integer projectId = defFileSys.getProjectId();
		Integer fileId = defFileSys.getFileId();
		String fileName = defFileSys.getFileName();
		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		Assert.notNull(fileId, "Wrong!! fileId must not be null!");
		Assert.notNull(fileName, "Wrong!! fileName must not be null!");

		defFileSys = defFileSysService.getById(fileId);
		defFileSys.setFileName(fileName);
		defFileSys.setUpdateTime(new Date());
		defFileSys.setUpdateUser(getLoginPassport());

		defFileSysService.update(defFileSys);

		return "redirect:/schedule/index.do?projectId=" + defFileSys.getProjectId() + "&fileId=" + defFileSys.getFileId();
	}

	/**
	 * 移动目录
	 */
	@RequestMapping("/moveDir.do")
	public String moveDir(DefFileSys defFileSys, ModelMap model) throws Exception {
		Assert.notNull(defFileSys, "Wrong!! projectId must not be null!");
		Integer projectId = defFileSys.getProjectId();
		Integer fileId = defFileSys.getFileId();
		Integer parentFileId = defFileSys.getParentFileId();
		Assert.notNull(projectId, "Wrong!! projectId must not be null!");
		Assert.notNull(fileId, "Wrong!! fileId must not be null!");
		Assert.notNull(parentFileId, "Wrong!! parentFileId must not be null!");

		// 假如回环检测
		Assert.isTrue(fileId.intValue() != parentFileId.intValue(), "Wrong!! fileId 等于 parentFileId ！");

		defFileSys = defFileSysService.getById(fileId);
		defFileSys.setParentFileId(parentFileId);
		defFileSys.setUpdateTime(new Date());
		defFileSys.setUpdateUser(getLoginPassport());

		defFileSysService.update(defFileSys);

		return "redirect:/schedule/index.do?projectId=" + defFileSys.getProjectId() + "&fileId=" + defFileSys.getFileId();
	}

}

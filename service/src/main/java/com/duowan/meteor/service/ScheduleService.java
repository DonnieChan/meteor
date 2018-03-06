package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.menutree.TreeNode;

/**
 * 业务任务操作逻辑
 * @author liuchaohong
 *
 */
public interface ScheduleService {

	public List<TreeNode> getAll() throws Exception;
}

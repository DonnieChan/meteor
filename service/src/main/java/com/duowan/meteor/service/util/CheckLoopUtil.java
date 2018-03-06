package com.duowan.meteor.service.util;

import java.util.LinkedList;
import java.util.List;

import com.duowan.meteor.model.db.DefDepend;

/**
 * 
 * @author chenwu
 */
public class CheckLoopUtil {

	/**
	 * 是否有回环
	 * 若存在回环，List<Integer>不为空
	 * @param defDepends
	 */
	public static List<Integer> isLoop(List<DefDepend> defDepends) {
		if(defDepends == null || defDepends.size() == 0) {
			return null;
		}
		List<Integer> beginFileIdList = new LinkedList<Integer>();
		List<Integer> endFileIdList = new LinkedList<Integer>();
		for(DefDepend defDepend : defDepends) {
			beginFileIdList.add(defDepend.getPreFileId());
			endFileIdList.add(defDepend.getFileId());
		}

		boolean flag = true;
		while(flag) {
			flag = false;
			for(int i=beginFileIdList.size() - 1; i>=0; i--) {
				if( !endFileIdList.contains(beginFileIdList.get(i)) ) {
					Integer beginFileId = beginFileIdList.remove(i);
					Integer endFileId = endFileIdList.remove(i);
					defDepends.remove(new DefDepend(endFileId, beginFileId));
					flag = true;
				}
			}
		}
		
		if(beginFileIdList.size() == 0 && endFileIdList.size() == 0) {
			return null;
		}else {
			return endFileIdList;
		}
	}
}

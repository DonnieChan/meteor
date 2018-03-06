package com.duowan.meteor.model.view.other;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractBase;

/**
 * 目录
 * @author chenwu
 */
public class Dir extends AbstractBase {
	
	private static final long serialVersionUID = 237626963891865073L;
	public final String CLASS = "com.duowan.meteor.model.view.other.Dir";

	public Dir() {
		setFileType(FileType.Dir.name());
		setIsDir(1);
	}
}

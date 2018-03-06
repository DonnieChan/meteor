package com.duowan.meteor.datasync.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * 遍历目录下所有文件
 * 
 * @author
 *
 */
public class FileHelper {

	public static List<File> listFiles(String dir) {
		Collection<File> files = null;
		if (new File(dir).isDirectory()) {
			files = FileUtils.listFiles(new File(dir), null, true);
		} else {
			files = Arrays.asList(new File(dir));
		}

		List<File> result = new ArrayList<File>();
		for (File f : files) {
			if (f.isDirectory()) {
				continue;
			}
			if (f.isHidden()) {
				continue;
			}
			if (f.getName().startsWith("_")) {
				continue;
			}
			if (f.getName().startsWith(".")) {
				continue;
			}
			if (f.getPath().contains(".svn")) {
				continue;
			}
			if (f.length() <= 0) {
				continue;
			}

			result.add(f);
		}
		return result;
	}

}

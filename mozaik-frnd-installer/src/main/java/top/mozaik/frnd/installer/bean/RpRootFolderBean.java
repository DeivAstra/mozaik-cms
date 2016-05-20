/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.bean;

import org.apache.commons.lang.SystemUtils;

public class RpRootFolderBean {
	
	private String path = SystemUtils.IS_OS_WINDOWS?"c:/mozaik":System.getProperty("user.home")+"/mozaik";
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}

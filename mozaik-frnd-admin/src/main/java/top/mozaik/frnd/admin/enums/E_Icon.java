/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.enums;

public enum E_Icon {
	
	FOLDER;
	
	private static final String ROOT = "/media/";
	private static final String EXT = ".svg";
	
	private final String path;
	
	private E_Icon() {
		this.path = ROOT + this.name().toLowerCase() + EXT;
	}
	
	public String getPath() {
		return path;
	}
}

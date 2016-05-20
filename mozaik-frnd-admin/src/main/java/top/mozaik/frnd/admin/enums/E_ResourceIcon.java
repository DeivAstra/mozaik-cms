/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.enums;

public enum E_ResourceIcon {
	
	FOLDER, 
	FOLDER_EMPTY, 
	RESOURCE;
	
	private static final String ROOT = "/media/tree/resource/";
	private static final String EXT = ".gif";
	
	private final String path;
	
	private E_ResourceIcon() {
		this.path = ROOT + this.name().toLowerCase() + EXT;
	}
	
	public String getPath() {
		return path;
	}
}

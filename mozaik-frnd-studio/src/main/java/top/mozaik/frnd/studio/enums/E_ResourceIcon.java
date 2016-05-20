/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.enums;

public enum E_ResourceIcon {
	
	FOLDER, 
	FOLDER_EMPTY, 
	RESOURCE,
	
	NEW_CLASS,
	NEW_FOLDER,
	NEW_PACKAGE,
	NEW_ZUL,
	
	RESOURCESET_FOLDER,
	
	ZUL_RESOURCE,
	
	JAVA_FOLDER,
	JAVA_RESOURCE,
	PACKAGE_FOLDER,
	PACKAGE_FOLDER_EMPTY,
	
	SCRIPT_RESOURCE
	;
	
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

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.enums;

public enum E_WcmIcon {
	
	LIBRARY, LIBRARY_SMALL,
	DOCUMENT, DOCUMENT_SMALL,
	COMPONENT, COMPONENT_SMALL,
	TEMPLATE, TEMPLATE_SMALL,
	RESOURCE, RESOURCE_SMALL;
	
	private static final String ROOT = "/media/wcm/";
	private static final String EXT = ".svg";
	
	private final String path;
	
	private E_WcmIcon() {
		this.path = ROOT + this.name().toLowerCase() + EXT;
	}
	
	public String getPath() {
		return path;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.converter.wcm;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import top.mozaik.frnd.admin.bean.wcm.resource.TreeLibraryResourceFolder;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

import static top.mozaik.frnd.admin.enums.E_Icon.*;
import static top.mozaik.frnd.admin.enums.E_WcmIcon.*;

public class ResourceTreeImageUrlConverter implements Converter<String, A_TreeElement, Component> {

	private static ResourceTreeImageUrlConverter instance;
	
	private ResourceTreeImageUrlConverter(){}
	
	public static ResourceTreeImageUrlConverter getInstance() {
		if(instance == null) {
			instance = new ResourceTreeImageUrlConverter();
		}
		return instance;
	}
	
	@Override
	public String coerceToUi(A_TreeElement el, Component component, BindContext ctx) {
		if(el instanceof TreeLibraryResourceFolder) {
			return LIBRARY.getPath();
		}
		if(el instanceof TreeResourceFolder) {
			return FOLDER.getPath();
		}
		if(el instanceof TreeResource) {
			return RESOURCE_SMALL.getPath();
		}
		return null;
	}

	@Override
	public A_TreeElement coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}

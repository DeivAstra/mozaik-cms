/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.converter;

import static top.mozaik.frnd.studio.enums.E_ResourceIcon.FOLDER;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.FOLDER_EMPTY;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.JAVA_FOLDER;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.JAVA_RESOURCE;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.PACKAGE_FOLDER;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.PACKAGE_FOLDER_EMPTY;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.RESOURCE;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.RESOURCESET_FOLDER;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.SCRIPT_RESOURCE;
import static top.mozaik.frnd.studio.enums.E_ResourceIcon.ZUL_RESOURCE;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.bean.tree.TreeResourceFolder;
import top.mozaik.frnd.studio.bean.tree.TreeResourceSetFolder;
import top.mozaik.frnd.studio.util.TreeResourceUtils;

public class TreeImageUrlConverter implements Converter<String, A_TreeElement, Component> {

	private static TreeImageUrlConverter instance;
	
	private TreeImageUrlConverter(){}
	
	public static TreeImageUrlConverter getInstance() {
		if(instance == null) {
			instance = new TreeImageUrlConverter();
		}
		return instance;
	}
	
	@Override
	public String coerceToUi(A_TreeElement el, Component component, BindContext ctx) {
		if(el instanceof TreeResourceSetFolder) {
			return RESOURCESET_FOLDER.getPath();
		}
		
		if(el instanceof TreeResourceFolder) {
			final TreeResourceFolder folder = (TreeResourceFolder) el;
			if(folder.isType(E_ResourceType.JAVA)) {
				return JAVA_FOLDER.getPath();
			} 

			if(E_ResourceType.JAVA.equals(TreeResourceUtils.getTreeResourceType(folder))) {
				if(folder.size() > 0) {
					return PACKAGE_FOLDER.getPath();
				}
				return PACKAGE_FOLDER_EMPTY.getPath();
			} else {
				if(folder.size() > 0) {
					return FOLDER.getPath();
				}
				return FOLDER_EMPTY.getPath();
			}
		}
		if(el instanceof TreeResource) {
			final _Resource resource = ((TreeResource)el).getValue();
			if(resource.getType() == E_ResourceType.JAVA) {
				return JAVA_RESOURCE.getPath();
			}
			if(resource.getType() == E_ResourceType.ZUL) {
				return ZUL_RESOURCE.getPath();
			}
			if(resource.getType() == E_ResourceType.SCRIPT) {
				return SCRIPT_RESOURCE.getPath();
			}
			return RESOURCE.getPath();
		}
		return null;
	}

	@Override
	public TreeResource coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.converter.wcm;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import top.mozaik.frnd.admin.bean.wcm.template.TreeLibraryTemplateFolder;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplate;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplateFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

import static top.mozaik.frnd.admin.enums.E_Icon.*;
import static top.mozaik.frnd.admin.enums.E_WcmIcon.*;

public class TemplateTreeImageUrlConverter implements Converter<String, A_TreeElement, Component> {

	private static TemplateTreeImageUrlConverter instance;
	
	private TemplateTreeImageUrlConverter(){}
	
	public static TemplateTreeImageUrlConverter getInstance() {
		if(instance == null) {
			instance = new TemplateTreeImageUrlConverter();
		}
		return instance;
	}
	
	@Override
	public String coerceToUi(A_TreeElement el, Component component, BindContext ctx) {
		if(el instanceof TreeLibraryTemplateFolder) {
			return LIBRARY.getPath();
		}
		if(el instanceof TreeTemplateFolder) {
			return FOLDER.getPath();
		}
		if(el instanceof TreeTemplate) {
			return TEMPLATE_SMALL.getPath();
		}
		return null;
	}

	@Override
	public A_TreeElement coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}

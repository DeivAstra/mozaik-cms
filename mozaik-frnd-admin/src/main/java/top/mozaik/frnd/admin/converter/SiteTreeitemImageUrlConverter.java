/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import top.mozaik.frnd.admin.bean.site.tree.A_TreeSiteElement;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.admin.bean.site.tree.TreeSitePage;

import static top.mozaik.frnd.admin.enums.E_SiteIcon.*;

public class SiteTreeitemImageUrlConverter implements Converter<String, A_TreeSiteElement, Component> {

	private static SiteTreeitemImageUrlConverter instance;
	
	private SiteTreeitemImageUrlConverter(){}
	
	public static SiteTreeitemImageUrlConverter getInstance() {
		if(instance == null) {
			instance = new SiteTreeitemImageUrlConverter();
		}
		return instance;
	}
	
	@Override
	public String coerceToUi(A_TreeSiteElement resource, Component component, BindContext ctx) {
		if(resource instanceof TreeSite) {
			return SITE.getPath();
		}
		
		if(resource instanceof TreeSitePage) {
			return PAGE.getPath();
		}
		
		return null;
	}

	@Override
	public A_TreeSiteElement coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}

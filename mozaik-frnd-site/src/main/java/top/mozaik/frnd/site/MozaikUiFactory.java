/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import static top.mozaik.frnd.site.PageAttrs.CLASS_LOADER;
import static top.mozaik.frnd.site.PageAttrs.RESOURCE_PACK;
import static top.mozaik.frnd.site.PageAttrs.RESOURCE_SET;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.http.SimpleUiFactory;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;

import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;

public class MozaikUiFactory extends SimpleUiFactory {
	
	public MozaikUiFactory() {}
	
	@Override
	public PageDefinition getPageDefinition(RequestInfo ri, String path) {
		
		if(path.startsWith("~") || path.startsWith("/@") || path.startsWith("/WEB-INF/"))
			return super.getPageDefinition(ri, path);
		
		if(path.startsWith("/"))
			path = path.substring(1);
		
		System.out.println(path);
		
		final ExecutionCtrl execCtrl = ((ExecutionCtrl)Executions.getCurrent());
		final Page page = execCtrl.getCurrentPage();
		
		final ResourcePack resPack = (ResourcePack) page.getAttribute(RESOURCE_PACK);
		final ResourcePackSet resSet = (ResourcePackSet) page.getAttribute(RESOURCE_SET);
		
		//Thread.currentThread().setContextClassLoader(ClassLoaderMap.get(resourcePack.getName()));
		Thread.currentThread().setContextClassLoader((ClassLoader)page.getAttribute(CLASS_LOADER));
		
		System.out.println(resPack.toString());
		System.out.println(resSet.toString());
		
		/*
		final ResourceSetUtils resourceSetUtils = new ResourceSetUtils(resourcePack, resourceSet.getResourceSetId());
		final _ResourceData resourceData = resourceSetUtils.findResourceDataByPath(path, E_ResourceType.ZUL);
		
		return  PageDefinitions.getPageDefinitionDirectly(Executions.getCurrent().getDesktop().getWebApp(), null,
						new String(resourceData.getSourceData()), "zul");
						*/
		
		return PageDefCache.$().getByName(resPack, resSet, path);
	}
}

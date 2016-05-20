/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site.component;

import static top.mozaik.frnd.site.PageAttrs.*;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;

import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;

public class Widget extends Div {

	public Widget() {
		
		final ExecutionCtrl execCtrl = ((ExecutionCtrl)Executions.getCurrent());
		final Page page = execCtrl.getCurrentPage();
		
		//final ResourcePack skinResPack = (ResourcePack) page.getAttribute(RESOURCE_PACK);
		//final ResourcePackSet skinResSet = (ResourcePackSet) page.getAttribute(RESOURCE_SET);
		
		final ResourcePack widgetResPack = (ResourcePack) page.getAttribute(INCLUDED_RESOURCE_PACK);
		final ResourcePackSet widgetResSet = (ResourcePackSet) page.getAttribute(INCLUDED_RESOURCE_SET);
		
		/*
		final ResourcePackServicesFacade rpsFacade = ResourcePackServicesFacade.get(resourcePack);
		
		final _ResourceData widgetZul = rpsFacade.getResourceService()
			.readWithData(new _Resource().setResourceSetId(resourceSetId).setName("index.zul").getFilter()).get(0);
		*/
		final Include include = new Include("/index.zul");
		include.setMode("defer");
		
		include.setDynamicProperty(RESOURCE_PACK, widgetResPack);
		include.setDynamicProperty(RESOURCE_SET, widgetResSet);
		
		appendChild(include);
	}
}

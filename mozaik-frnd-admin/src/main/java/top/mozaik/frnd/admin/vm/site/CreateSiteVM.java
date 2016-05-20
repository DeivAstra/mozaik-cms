/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.Site;
import top.mozaik.bknd.api.service.SiteService;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateSiteVM extends BaseVM {
	
	private final SiteService siteService = ServicesFacade.$().getSiteService();
	
	private I_CUDEventHandler eventHandler;
	
	private final Site bean = new Site();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("treeEventHandler") I_CUDEventHandler eventHandler
			) {
		this.eventHandler = eventHandler;
	}
	
	/// BINDING ///
	
	public Site getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		ZKUtils.validate(getView());
		try {
			final Integer id = siteService.create(bean);
			final Site bean = siteService.read1(new Site().setId(id));
			eventHandler.onCreate(new TreeSite(bean));
			Notification.showMessage("Site created succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Site", e);
		}
	}
}

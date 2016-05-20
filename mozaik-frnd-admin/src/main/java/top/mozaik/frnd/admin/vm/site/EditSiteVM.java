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

public class EditSiteVM extends BaseVM {
	
	private final SiteService siteService = ServicesFacade.$().getSiteService();
	
	private I_CUDEventHandler eventHandler;
	private TreeSite treeSiteFolder;
	private Site bean;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeSiteFolder") TreeSite treeSiteFolder) {
		this.eventHandler = eventHandler;
		this.treeSiteFolder = treeSiteFolder;
		this.bean = treeSiteFolder.getValue();
	}
	
	/// BINDING ///
	
	public Site getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void save() {
		ZKUtils.validate(getView());
		try {
			siteService.update1(bean);
			eventHandler.onUpdate(treeSiteFolder);
			Notification.showMessage("Site saved succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while save Site", e);
		}
	}
}

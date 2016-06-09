/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.Site;
import top.mozaik.bknd.api.model.SitePage;
import top.mozaik.bknd.api.service.SitePageService;
import top.mozaik.bknd.api.service.SiteService;
import top.mozaik.frnd.admin.Dialogs;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditSiteVM extends BaseVM {
	
	private final SiteService siteService = ServicesFacade.$().getSiteService();
	private final SitePageService sitePageService = ServicesFacade.$().getSitePageService();
	
	private I_CUDEventHandler eventHandler;
	private TreeSite treeSiteFolder;
	private Site bean;
	
	@Wire
	Label selectedIndexPageInfoLabel;
	@Wire
	Label selectedLoginPageInfoLabel;
	@Wire
	Label selected404PageInfoLabel;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeSiteFolder") TreeSite treeSiteFolder) {
		this.eventHandler = eventHandler;
		this.treeSiteFolder = treeSiteFolder;
		this.bean = treeSiteFolder.getValue().twin();
		
		if(bean.getIndexPageId() != null) {
			final SitePage page = sitePageService.read1(new SitePage().setId(bean.getIndexPageId()));
			selectedIndexPageInfoLabel.setValue(page.getTitle());
		}
		if(bean.getLoginPageId() != null) {
			final SitePage page = sitePageService.read1(new SitePage().setId(bean.getLoginPageId()));
			selectedLoginPageInfoLabel.setValue(page.getTitle());
		}
		if(bean.get404PageId() != null) {
			final SitePage page = sitePageService.read1(new SitePage().setId(bean.get404PageId()));
			selected404PageInfoLabel.setValue(page.getTitle());
		}
	}
	
	/// BINDING ///
	
	public Site getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	@NotifyChange("bean")
	public void selectIndexPage() {
		Dialogs.selectSitePage(new I_CallbackArg<SitePage>() {
			public void call(SitePage page) {
				bean.setIndexPageId(page.getId());
				selectedIndexPageInfoLabel.setValue(page.getTitle());
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			};
		});
	}
	
	@Command
	@NotifyChange("bean")
	public void deleteIndexPage() {
		bean.setIndexPageId(null);
		selectedIndexPageInfoLabel.setValue(null);
	}
	
	@Command
	@NotifyChange("bean")
	public void selectLoginPage() {
		Dialogs.selectSitePage(new I_CallbackArg<SitePage>() {
			public void call(SitePage page) {
				bean.setLoginPageId(page.getId());
				selectedLoginPageInfoLabel.setValue(page.getTitle());
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			};
		});
	}
	
	@Command
	@NotifyChange("bean")
	public void deleteLoginPage() {
		bean.setLoginPageId(null);
		selectedLoginPageInfoLabel.setValue(null);
	}
	
	@Command
	@NotifyChange("bean")
	public void select404Page() {
		Dialogs.selectSitePage(new I_CallbackArg<SitePage>() {
			public void call(SitePage page) {
				bean.set404PageId(page.getId());
				selected404PageInfoLabel.setValue(page.getTitle());
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			};
		});
	}
	
	@Command
	@NotifyChange("bean")
	public void delete404Page() {
		bean.set404PageId(null);
		selected404PageInfoLabel.setValue(null);
	}
	
	@Command
	public void save() {
		ZKUtils.validate(getView());
		try {
			siteService.update1(bean.commit());
			eventHandler.onUpdate(treeSiteFolder);
			Notification.showMessage("Site saved succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while save Site", e);
		}
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site.page;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model.SitePage;
import top.mozaik.bknd.api.service.SitePageService;
import top.mozaik.frnd.admin.bean.site.tree.A_TreeSiteElement;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.admin.bean.site.tree.TreeSitePage;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreatePageVM extends BaseVM {
	
	private final SitePageService sitePageService = ServicesFacade.$().getSitePageService();
	
	private I_CUDEventHandler eventHandler;
	private A_TreeSiteElement treeSiteElement;
	
	private final SitePage bean = new SitePage();
	
	@Wire
	Label selectedThemeInfoLabel;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeSiteElement") A_TreeSiteElement treeSiteElement) {
		this.eventHandler = eventHandler;
		this.treeSiteElement = treeSiteElement;
	}
	
	/// BINDING ///
	
	public SitePage getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	@NotifyChange("bean")
	public void selectTheme() {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", new I_CallbackArg<ResourcePackSet>() {
			public void call(ResourcePackSet theme) {
				bean.setThemeId(theme.getId());
				selectedThemeInfoLabel.setStyle(null);
				selectedThemeInfoLabel.setValue(theme.getTitle());
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			};
		});
		Executions.createComponents("/WEB-INF/zul/site/page/selectTheme.wnd.zul", null, args);
	}
	
	@Command
	public void create() {
		
		if(bean.getThemeId() == null) {
			throw new WrongValueException(selectedThemeInfoLabel, "Theme is not selected");
		}
		
		ZKUtils.validate(getView());
		try {
			if(treeSiteElement instanceof TreeSite) {
				bean.setSiteId(((TreeSite)treeSiteElement).getValue().getId());
			} else {
				final SitePage pageBean = ((TreeSitePage)treeSiteElement).getValue();
				bean.setSiteId(pageBean.getSiteId());
				bean.setParentId(pageBean.getId());
			}
			
			bean.setId(sitePageService.create(bean));
			
			final TreeSitePage page = new TreeSitePage(bean);
			page.setParent(treeSiteElement);
			eventHandler.onCreate(page);
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create new Page", e);
		}
	}
}

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
import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model.SitePage;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.bknd.api.service.SitePageService;
import top.mozaik.frnd.admin.bean.site.tree.TreeSitePage;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditPageVM extends BaseVM {
	
	private final SitePageService sitePageService = ServicesFacade.$().getSitePageService();
	private final ResourcePackSetService resourcePackSetService = ServicesFacade.$().getResourcePackSetService();
	
	private I_CUDEventHandler eventHandler;
	private TreeSitePage treeSitePage;
	private SitePage bean;
	
	private final CommandExecutionQueue commandQueue = new CommandExecutionQueue();
	public static final int COMMAND_SAVE = 0;
	public static final int COMMAND_VALIDATE = 1;
	
	@Wire
	Label selectedThemeInfoLabel;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeSitePage") TreeSitePage treeSitePage) {
		this.eventHandler = eventHandler;
		this.treeSitePage = treeSitePage;
		this.bean = treeSitePage.getValue();
		
		if(bean.getThemeId() == null) {
			selectedThemeInfoLabel.setStyle("color:red");
			selectedThemeInfoLabel.setValue("Theme is not defined ");
			return;
		}
		
		final ResourcePackSet theme = resourcePackSetService.read1(
				new ResourcePackSet().
					setId(bean.getThemeId()).
					setResourceSetType(E_ResourceSetType.THEME)
		);
		if(theme == null) {
			selectedThemeInfoLabel.setStyle("color:red");
			selectedThemeInfoLabel.setValue("Theme not found for ID = " + bean.getThemeId());
		} else {
			selectedThemeInfoLabel.setValue(theme.getTitle());
		}
	}
	
	public CommandExecutionQueue getCommandQueue() {
		return commandQueue;
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
	public void validateAndSave() {
		try {
			if(bean.getThemeId() == null) {
				throw new WrongValueException(selectedThemeInfoLabel, "Theme is not selected");
			}
		
			ZKUtils.validate(getView());
			commandQueue.execCommand(COMMAND_VALIDATE);
		
			getBinder().postCommand("save", null);
		} catch (WrongValueException e) {
			ZKUtils.openTabByConstraintError(e);
			throw e;
		}
	}
	
	@Command
	public void save() {
		try {
			sitePageService.startTransaction();
			
			commandQueue.execCommand(COMMAND_SAVE);
			
			sitePageService.update1(bean);
			eventHandler.onUpdate(treeSitePage);
			
			sitePageService.commit();
			
			Notification.showMessage("Page saved succesfully");
		} catch (Exception e) {
			sitePageService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while save Page", e);
		}
	}
}

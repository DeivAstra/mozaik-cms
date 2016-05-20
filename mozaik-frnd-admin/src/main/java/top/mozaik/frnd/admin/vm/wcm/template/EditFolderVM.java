/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmTemplateFolder;
import top.mozaik.bknd.api.service.WcmTemplateFolderService;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplateFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditFolderVM extends BaseVM {
	
	private final WcmTemplateFolderService folderService = ServicesFacade.$().getWcmTemplateFolderService();
	
	private I_CUDEventHandler<TreeTemplateFolder> eventHandler;
	private TreeTemplateFolder treeTemplateFolder;
	private WcmTemplateFolder bean;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeTemplateFolder> eventHandler,
			@ExecutionArgParam("treeTemplateFolder") TreeTemplateFolder treeTemplateFolder) {
		this.eventHandler = eventHandler;
		this.treeTemplateFolder = treeTemplateFolder;
		this.bean = treeTemplateFolder.getValue();
	}
	
	/// BINDING ///
	
	public WcmTemplateFolder getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void save(){
		ZKUtils.validate(getView());
		try {
			folderService.update1(bean);
			eventHandler.onUpdate(treeTemplateFolder);
			Notification.showMessage("Folder saved succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while save Folder", e);
		}
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.component;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmComponentFolder;
import top.mozaik.bknd.api.service.WcmComponentFolderService;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponentFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditFolderVM extends BaseVM {
	
	private final WcmComponentFolderService folderService = ServicesFacade.$().getWcmComponentFolderService();
	
	private I_CUDEventHandler<TreeComponentFolder> eventHandler;
	private TreeComponentFolder treeComponentFolder;
	private WcmComponentFolder bean;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeComponentFolder> eventHandler,
			@ExecutionArgParam("treeComponentFolder") TreeComponentFolder treeComponentFolder) {
		this.eventHandler = eventHandler;
		this.treeComponentFolder = treeComponentFolder;
		this.bean = treeComponentFolder.getValue();
	}
	
	/// BINDING ///
	
	public WcmComponentFolder getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void save(){
		ZKUtils.validate(getView());
		try {
			folderService.update1(bean);
			eventHandler.onUpdate(treeComponentFolder);
			Notification.showMessage("Folder saved succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while save Folder", e);
		}
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.resource;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmResourceFolder;
import top.mozaik.bknd.api.service.WcmResourceFolderService;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditFolderVM extends BaseVM {
	
	private final WcmResourceFolderService folderService = ServicesFacade.$().getWcmResourceFolderService();
	
	private I_CUDEventHandler<TreeResourceFolder> eventHandler;
	private TreeResourceFolder treeResourceFolder;
	private WcmResourceFolder bean;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeResourceFolder> eventHandler,
			@ExecutionArgParam("treeResourceFolder") TreeResourceFolder treeResourceFolder) {
		this.eventHandler = eventHandler;
		this.treeResourceFolder = treeResourceFolder;
		this.bean = treeResourceFolder.getValue();
	}
	
	/// BINDING ///
	
	public WcmResourceFolder getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void save(){
		ZKUtils.validate(getView());
		try {
			folderService.update1(bean);
			eventHandler.onUpdate(treeResourceFolder);
			Notification.showMessage("Folder saved succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while save Folder", e);
		}
	}
}

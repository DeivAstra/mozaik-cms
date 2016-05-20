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
import top.mozaik.frnd.admin.bean.wcm.resource.TreeLibraryResourceFolder;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateFolderVM extends BaseVM {
	
	private final WcmResourceFolderService folderService = ServicesFacade.$().getWcmResourceFolderService();
	
	private final WcmResourceFolder bean = new WcmResourceFolder();
	
	private I_CUDEventHandler<TreeResourceFolder> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeResourceFolder> eventHandler,
			@ExecutionArgParam("parentFolder") A_TreeNode parentFolder) {
		this.eventHandler = eventHandler;
		this.parentFolder = parentFolder;
		if(parentFolder instanceof TreeLibraryResourceFolder) {
			final TreeLibraryResourceFolder library = (TreeLibraryResourceFolder)parentFolder;
			bean.setFolderId(-library.getValue().getId());
		} else if(parentFolder instanceof TreeResourceFolder){
			final TreeResourceFolder folder = (TreeResourceFolder) parentFolder;
			//bean.setLibraryId(folder.getValue().getLibraryId());
			bean.setFolderId(folder.getValue().getId());
		}
	}
	
	/// BINDING ///
	
	public WcmResourceFolder getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			final Integer id = folderService.create(bean);
			final WcmResourceFolder bean = folderService.read1(new WcmResourceFolder().setId(id));
			final TreeResourceFolder treeFolder = new TreeResourceFolder(bean);
			treeFolder.setParent(parentFolder);
			eventHandler.onCreate(treeFolder);
			Notification.showMessage("Folder created succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Folder", e);
		}
	}
}

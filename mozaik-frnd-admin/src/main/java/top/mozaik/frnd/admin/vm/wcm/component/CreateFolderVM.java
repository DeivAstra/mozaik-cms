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
import top.mozaik.frnd.admin.bean.wcm.component.TreeLibraryComponentFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateFolderVM extends BaseVM {
	
	private final WcmComponentFolderService folderService = ServicesFacade.$().getWcmComponentFolderService();
	
	private final WcmComponentFolder bean = new WcmComponentFolder();
	
	private I_CUDEventHandler<TreeComponentFolder> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeComponentFolder> eventHandler,
			@ExecutionArgParam("parentFolder") A_TreeNode parentFolder) {
		this.eventHandler = eventHandler;
		this.parentFolder = parentFolder;
		if(parentFolder instanceof TreeLibraryComponentFolder) {
			final TreeLibraryComponentFolder library = (TreeLibraryComponentFolder)parentFolder;
			bean.setFolderId(-library.getValue().getId());
		} else if(parentFolder instanceof TreeComponentFolder){
			final TreeComponentFolder folder = (TreeComponentFolder) parentFolder;
			//bean.setLibraryId(folder.getValue().getLibraryId());
			bean.setFolderId(folder.getValue().getId());
		}
	}
	
	/// BINDING ///
	
	public WcmComponentFolder getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			final Integer id = folderService.create(bean);
			final WcmComponentFolder bean = folderService.read1(new WcmComponentFolder().setId(id));
			final TreeComponentFolder treeFolder = new TreeComponentFolder(bean);
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

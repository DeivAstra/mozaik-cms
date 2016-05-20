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
import top.mozaik.frnd.admin.bean.wcm.template.TreeLibraryTemplateFolder;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplateFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateFolderVM extends BaseVM {
	
	private final WcmTemplateFolderService folderService = ServicesFacade.$().getWcmTemplateFolderService();
	
	private final WcmTemplateFolder bean = new WcmTemplateFolder();
	
	private I_CUDEventHandler<TreeTemplateFolder> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeTemplateFolder> eventHandler,
			@ExecutionArgParam("parentFolder") A_TreeNode parentFolder) {
		this.eventHandler = eventHandler;
		this.parentFolder = parentFolder;
		if(parentFolder instanceof TreeLibraryTemplateFolder) {
			final TreeLibraryTemplateFolder library = (TreeLibraryTemplateFolder)parentFolder;
			bean.setFolderId(-library.getValue().getId());
		} else if(parentFolder instanceof TreeTemplateFolder){
			final TreeTemplateFolder folder = (TreeTemplateFolder) parentFolder;
			//bean.setLibraryId(folder.getValue().getLibraryId());
			bean.setFolderId(folder.getValue().getId());
		}
	}
	
	/// BINDING ///
	
	public WcmTemplateFolder getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			final Integer id = folderService.create(bean);
			final WcmTemplateFolder bean = folderService.read1(new WcmTemplateFolder().setId(id));
			final TreeTemplateFolder treeFolder = new TreeTemplateFolder(bean);
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

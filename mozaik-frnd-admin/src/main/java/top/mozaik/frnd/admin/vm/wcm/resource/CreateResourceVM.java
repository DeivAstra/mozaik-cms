/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.resource;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.service.WcmResourceService;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeLibraryResourceFolder;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateResourceVM extends BaseVM {
	
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	
	private final WcmResource bean = new WcmResource();
	
	private I_CUDEventHandler<TreeResource> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeResource> eventHandler,
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
	
	public WcmResource getBean() {
		return bean;
	}
		
	/// COMMANDS ///
	
	@Command
	public void create(){
		
		ZKUtils.validate(getView());
		try {
			final Integer id = resourceService.create(bean);
			final WcmResource bean = resourceService.read1(new WcmResource().setId(id));
			final TreeResource treeResource = new TreeResource(bean);
			treeResource.setParent(parentFolder);
			eventHandler.onCreate(treeResource);
			Notification.showMessage("Resource created succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Resource", e);
		}
	}
}

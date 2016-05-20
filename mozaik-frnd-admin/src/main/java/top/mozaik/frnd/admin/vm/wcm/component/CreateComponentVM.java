/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.component;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmComponent;
import top.mozaik.bknd.api.service.WcmComponentService;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponent;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponentFolder;
import top.mozaik.frnd.admin.bean.wcm.component.TreeLibraryComponentFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateComponentVM extends BaseVM {
	
	private final WcmComponentService componentService = ServicesFacade.$().getWcmComponentService();
	
	@Wire
	Label selectedTemplateInfoLabel;
	
	private final WcmComponent bean = new WcmComponent();
	
	private I_CUDEventHandler<TreeComponent> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeComponent> eventHandler,
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
	
	public WcmComponent getBean() {
		return bean;
	}
		
	/// COMMANDS ///
	
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			final Integer id = componentService.create(bean);
			final WcmComponent bean = componentService.read1(new WcmComponent().setId(id));
			final TreeComponent treeComponent = new TreeComponent(bean);
			treeComponent.setParent(parentFolder);
			eventHandler.onCreate(treeComponent);
			Notification.showMessage("Component created succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Component", e);
		}
	}
}

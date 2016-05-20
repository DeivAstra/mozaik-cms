/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmTemplate;
import top.mozaik.bknd.api.service.WcmTemplateService;
import top.mozaik.frnd.admin.bean.wcm.template.TreeLibraryTemplateFolder;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplate;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplateFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateTemplateVM extends BaseVM {
	
	private final WcmTemplateService templateService = ServicesFacade.$().getWcmTemplateService();
	
	private final WcmTemplate bean = new WcmTemplate();
	
	private I_CUDEventHandler<TreeTemplate> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeTemplate> eventHandler,
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
	
	public WcmTemplate getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			final Integer id = templateService.create(bean);
			final WcmTemplate bean = templateService.read1(new WcmTemplate().setId(id));
			final TreeTemplate treeTemplate = new TreeTemplate(bean);
			treeTemplate.setParent(parentFolder);
			eventHandler.onCreate(treeTemplate);
			Notification.showMessage("Template created succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Template", e);
		}
	}
}

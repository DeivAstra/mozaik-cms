/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.document;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmDocumentFolder;
import top.mozaik.bknd.api.model.WcmTemplate;
import top.mozaik.bknd.api.service.WcmDocumentFolderService;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocumentFolder;
import top.mozaik.frnd.admin.bean.wcm.document.TreeLibraryDocumentFolder;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateFolderVM extends BaseVM {
	
	private final WcmDocumentFolderService folderService = ServicesFacade.$().getWcmDocumentFolderService();
	
	private final WcmDocumentFolder bean = new WcmDocumentFolder();
	
	private I_CUDEventHandler<TreeDocumentFolder> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeDocumentFolder> eventHandler,
			@ExecutionArgParam("parentFolder") A_TreeNode parentFolder) {
		this.eventHandler = eventHandler;
		this.parentFolder = parentFolder;
		if(parentFolder instanceof TreeLibraryDocumentFolder) {
			final TreeLibraryDocumentFolder library = (TreeLibraryDocumentFolder)parentFolder;
			bean.setFolderId(-library.getValue().getId());
		} else if(parentFolder instanceof TreeDocumentFolder){
			final TreeDocumentFolder folder = (TreeDocumentFolder) parentFolder;
			//bean.setLibraryId(folder.getValue().getLibraryId());
			bean.setFolderId(folder.getValue().getId());
		}
	}
	
	/// BINDING ///
	
	public WcmDocumentFolder getBean() {
		return bean;
	}
		
	/// COMMANDS ///
	
	@Command
	public void selectTemplate() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", new I_CallbackArg<WcmTemplate>() {
			@Override
			public void call(WcmTemplate template) {
				bean.setTemplateId(template.getId());
				reloadComponent();
			}
		});
		Executions.createComponents("/WEB-INF/zul/wcm/common/selectTemplate.wnd.zul", null, args);
	}
	
	@Command
	@NotifyChange("bean")
	public void deleteTemplate() {
		bean.setTemplateId(null);
	}
	
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			final Integer id = folderService.create(bean);
			final WcmDocumentFolder bean = folderService.read1(new WcmDocumentFolder().setId(id));
			final TreeDocumentFolder treeFolder = new TreeDocumentFolder(bean);
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

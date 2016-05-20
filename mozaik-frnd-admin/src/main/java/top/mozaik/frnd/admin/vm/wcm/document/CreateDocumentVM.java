/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.document;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.bknd.api.service.WcmDocumentService;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocument;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocumentFolder;
import top.mozaik.frnd.admin.bean.wcm.document.TreeLibraryDocumentFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateDocumentVM extends BaseVM {
	
	private final WcmDocumentService documentService = ServicesFacade.$().getWcmDocumentService();
	
	private final WcmDocument bean = new WcmDocument();
	
	private I_CUDEventHandler<TreeDocument> eventHandler;
	
	private A_TreeNode parentFolder;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeDocument> eventHandler,
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
	
	public WcmDocument getBean() {
		return bean;
	}
		
	/// COMMANDS ///
	
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			final Integer id = documentService.create(bean);
			final WcmDocument bean = documentService.read1(new WcmDocument().setId(id));
			final TreeDocument treeDocument = new TreeDocument(bean);
			treeDocument.setParent(parentFolder);
			eventHandler.onCreate(treeDocument);
			Notification.showMessage("Document created succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Document", e);
		}
	}
}

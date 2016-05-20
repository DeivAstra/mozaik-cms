/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.component;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmComponent;
import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.bknd.api.model.WcmDocumentFolder;
import top.mozaik.bknd.api.service.WcmComponentService;
import top.mozaik.bknd.api.service.WcmDocumentFolderService;
import top.mozaik.bknd.api.service.WcmDocumentService;
import top.mozaik.frnd.admin.Dialogs;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponent;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditComponentVM extends BaseVM {
	
	private final WcmComponentService componentService = ServicesFacade.$().getWcmComponentService();
	private final WcmDocumentService documentService = ServicesFacade.$().getWcmDocumentService();
	private final WcmDocumentFolderService documentFolderService = ServicesFacade.$().getWcmDocumentFolderService();
	
	private I_CUDEventHandler<TreeComponent> eventHandler;
	private TreeComponent treeComponent;
	private WcmComponent bean;
	
	private final CommandExecutionQueue commandQueue = new CommandExecutionQueue();
	public static final int COMMAND_SAVE = 0;
	
	@Wire
	Label selectedElementInfoLabel;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeComponent> eventHandler,
			@ExecutionArgParam("treeComponent") TreeComponent treeComponent) {
		this.eventHandler = eventHandler;
		this.treeComponent = treeComponent;
		this.bean = treeComponent.getValue();
		
		if(bean.getElementId() == null) return;
			
		if(bean.getElementId() < 0) {
			final WcmDocumentFolder f = documentFolderService.read1(
					new WcmDocumentFolder().setId(-bean.getElementId()));
			selectedElementInfoLabel.setValue(f.getTitle());
		} else {
			final WcmDocument d = documentService.read1(
					new WcmDocument().setId(bean.getElementId()));
			selectedElementInfoLabel.setValue(d.getTitle());
		}
	}
	
	/// BINDING ///
	
	public WcmComponent getBean() {
		return bean;
	}
	
	public CommandExecutionQueue getCommandQueue() {
		return commandQueue;
	}
	
	/// COMMANDS ///
	
	@Command
	@NotifyChange("bean")
	public void deleteElement() {
		bean.setElementId(null);
		selectedElementInfoLabel.setValue(null);
	}
	
	@Command
	@NotifyChange("bean")
	public void selectDocumentOrFolder() {
		Dialogs.selectDocumentOrFolder(new I_CallbackArg<Object>() {
			public void call(Object element) {
				if(element instanceof WcmDocument) {
					final WcmDocument d = (WcmDocument) element;
					bean.setElementId(d.getId());
					selectedElementInfoLabel.setValue(d.getTitle());
				} else if(element instanceof WcmDocumentFolder) {
					final WcmDocumentFolder f = (WcmDocumentFolder) element;
					bean.setElementId(-f.getId());
					selectedElementInfoLabel.setValue(f.getTitle());
				}
			};
		});
	}

	@Command
	public void validateAndSave() {
		try {
			ZKUtils.validate(getView());
			getBinder().postCommand("save", null);
		} catch (WrongValueException e) {
			ZKUtils.openTabByConstraintError(e);
			throw e;
		}
	}
	
	@Command
	public void save(){
		try {
			componentService.startTransaction();
			
			commandQueue.execCommand(COMMAND_SAVE);
			
			componentService.update1(bean);
			
			componentService.commit();
			
			eventHandler.onUpdate(treeComponent);
			Notification.showMessage("Component saved succesfully");
		} catch (Exception e) {
			componentService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while save Component", e);
		}
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.library;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.service.WcmLibraryService;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditLibraryVM extends BaseVM {
	
	private final WcmLibraryService libraryService = ServicesFacade.$().getWcmLibraryService();
	
	private WcmLibrary bean;
	private I_CUDEventHandler<WcmLibrary> eventHandler;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("bean") WcmLibrary bean,
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<WcmLibrary> eventHandler) {
		this.bean = bean;
		this.eventHandler = eventHandler;
	}
	
	/// BINDING ///
	
	public WcmLibrary getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void save(){
		ZKUtils.validate(getView());
		try {
			libraryService.update1(bean);
			eventHandler.onUpdate(bean);
			Notification.showMessage("Library updated succesfully");
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while update Library", e);
		}
	}
}

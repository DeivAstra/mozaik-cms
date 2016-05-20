/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.settings.contenttype;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmContentType;
import top.mozaik.bknd.api.service.WcmContentTypeService;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditContentTypeVM extends BaseVM {
	
	private final WcmContentTypeService contentTypeService = ServicesFacade.$().getWcmContentTypeService();
	
	private WcmContentType bean;
	private I_CUDEventHandler<WcmContentType> eventHandler;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("bean") WcmContentType bean,
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<WcmContentType> eventHandler) {
		this.bean = bean;
		this.eventHandler = eventHandler;
	}
	
	/// BINDING ///
	
	public WcmContentType getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void save(){
		ZKUtils.validate(getView());
		try {
			contentTypeService.update1(bean);
			eventHandler.onUpdate(bean);
			Notification.showMessage("Content Type updated succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while update Content Type", e);
		}
	}
}

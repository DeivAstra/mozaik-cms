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

public class CreateContentTypeVM extends BaseVM {
	
	private final WcmContentTypeService contentTypeService = ServicesFacade.$().getWcmContentTypeService();
	
	private final WcmContentType bean = new WcmContentType();
	
	private I_CUDEventHandler<WcmContentType> eventHandler;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<WcmContentType> eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	/// BINDING ///
	
	public WcmContentType getBean() {
		return bean;
	}
		
	/// COMMANDS ///
		
	@Command
	public void create(){
		ZKUtils.validate(getView());
		try {
			contentTypeService.create(bean);
			eventHandler.onCreate(bean);
			Notification.showMessage("Content Type created succesfully");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Content Type", e);
		}
	}
}

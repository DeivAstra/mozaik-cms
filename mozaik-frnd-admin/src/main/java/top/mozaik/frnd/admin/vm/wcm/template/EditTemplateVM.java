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
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplate;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditTemplateVM extends BaseVM {
	
	private final WcmTemplateService templateService = ServicesFacade.$().getWcmTemplateService();
	
	private I_CUDEventHandler<TreeTemplate> eventHandler;
	private TreeTemplate treeTemplate;
	private WcmTemplate bean;
	
	private final CommandExecutionQueue commandQueue = new CommandExecutionQueue();
	public static final int COMMAND_SAVE = 0;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeTemplate> eventHandler,
			@ExecutionArgParam("treeTemplate") TreeTemplate treeTemplate) {
		this.eventHandler = eventHandler;
		this.treeTemplate = treeTemplate;
		this.bean = treeTemplate.getValue();
	}
	
	/// BINDING ///
	
	public WcmTemplate getBean() {
		return bean;
	}
	
	public CommandExecutionQueue getCommandQueue() {
		return commandQueue;
	}
	
	/// COMMANDS ///
		
	@Command
	public void save(){
		ZKUtils.validate(getView());
		try {
			templateService.startTransaction();
			
			commandQueue.execCommand(COMMAND_SAVE);
			
			templateService.update1(bean);
			
			templateService.commit();
			
			eventHandler.onUpdate(treeTemplate);
			Notification.showMessage("Template saved succesfully");
		} catch (Exception e) {
			templateService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while save Template", e);
		}
	}
}

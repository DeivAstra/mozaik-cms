/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.resource;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmContentType;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.service.WcmContentTypeService;
import top.mozaik.bknd.api.service.WcmResourceService;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditResourceVM extends BaseVM {
	
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	private final WcmContentTypeService contentTypeService = ServicesFacade.$().getWcmContentTypeService();
	
	private I_CUDEventHandler<TreeResource> eventHandler;
	private TreeResource treeResource;
	private WcmResource bean;
	
	private final CommandExecutionQueue commandQueue = new CommandExecutionQueue();
	public static final int COMMAND_SAVE = 0;
	
	private String contentType;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<TreeResource> eventHandler,
			@ExecutionArgParam("treeResource") TreeResource treeResource) {
		this.eventHandler = eventHandler;
		this.treeResource = treeResource;
		this.bean = treeResource.getValue();
		contentType = bean.getDataContentType();
	}
	
	/// BINDING ///
	
	public WcmResource getBean() {
		return bean;
	}
	
	public CommandExecutionQueue getCommandQueue() {
		return commandQueue;
	}
	
	public WcmContentType getContentType() {
		if(contentType == null) return null;
		return contentTypeService.read1(
				new WcmContentType().setMime(contentType));
	}
	
	/// COMMANDS ///
	
	@Command
	public void selectContentType() {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", new I_CallbackArg<WcmContentType>() {
			public void call(WcmContentType ct) {
				contentType = ct.getMime();
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			};
		});
		Executions.createComponents("/WEB-INF/zul/wcm/common/selectContentType.wnd.zul", null, args);
	}
	
	@Command
	@NotifyChange("contentType")
	public void deleteContentType() {
		contentType = null;
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
			
			bean.setDataContentType(contentType);
			
			resourceService.startTransaction();
			
			commandQueue.execCommand(COMMAND_SAVE);
			
			resourceService.update1(bean);
			
			resourceService.getJdbcTemplate().update(
					"delete from wcm_resource_data where id not in (select data_id from wcm_resources)");
			
			resourceService.commit();
			
			eventHandler.onUpdate(treeResource);
			Notification.showMessage("Resource saved succesfully");
		} catch (Exception e) {
			resourceService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while save Resource", e);
		}
	}
}

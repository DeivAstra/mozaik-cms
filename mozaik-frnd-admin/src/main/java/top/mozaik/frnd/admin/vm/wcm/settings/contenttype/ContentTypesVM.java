/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.settings.contenttype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmContentType;
import top.mozaik.bknd.api.service.WcmContentTypeService;
import top.mozaik.frnd.admin.vm.system.SettingsVM;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.command.I_CommandExecutor;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.ListboxCUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class ContentTypesVM extends BaseVM implements I_CommandExecutor {
	
	private final WcmContentTypeService contentTypeService = ServicesFacade.$().getWcmContentTypeService();
	
	@Wire
	Listbox contentTypeListbox;
	
	private I_CUDEventHandler<WcmContentType> eventHandler;
	
	
	@Init
	public void init(@BindingParam("commandQueue") CommandExecutionQueue commandQueue) {
		commandQueue.addListener(this);
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		eventHandler = new ListboxCUDEventHandler<WcmContentType>(contentTypeListbox){
			@Override
			public void onCreate(WcmContentType v) {
				super.onCreate(v);
			}
			@Override
			public void onUpdate(WcmContentType v) {
				super.onUpdate(v);
			}
		};
	}
	
	@Override
	public void execCommand(int cmdId) {
		if(SettingsVM.COMMAND_SAVE != cmdId) throw new IllegalArgumentException();
		
		
	}
	
	/// BINDING ///
	
	public List<WcmContentType> getContentTypeList() {
		return contentTypeService.read(new WcmContentType());
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/wcm/settings/contenttype/createContentType.wnd.zul", null, args);
	}
		
	@Command
	public void edit(@BindingParam("bean") WcmContentType bean) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("bean", bean);
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/wcm/settings/contenttype/editContentType.wnd.zul", null, args);
		}
		
	@Command
	public void remove() {
		final Listitem item = contentTypeListbox.getSelectedItem();
		if(item == null) return;
			
		final WcmContentType bean = item.getValue();
			
		final StringBuilder msg = new StringBuilder("Content Type '")
				.append(bean.getMime()).append("' will be removed. Continue?");
			
		Dialog.confirm("Remove Content Type", msg.toString(), new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					contentTypeService.delete1(bean);
					eventHandler.onDelete(bean);
					Notification.showMessage("Content Type removed succesfully");
				} catch (Exception e) {
					Dialog.error("Error occured while remove Content Type: " + bean, e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
		
	@Command
	@NotifyChange("contentTypeList")
	public void refresh() {
	}
}

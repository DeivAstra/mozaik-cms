/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.service.WcmLibraryService;
import top.mozaik.frnd.admin.enums.E_WcmIcon;
import top.mozaik.frnd.admin.vm.wcm.WcmVM;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.ListboxCUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class LibrariesVM extends BaseVM {
	
	private final WcmLibraryService libraryService = ServicesFacade.$().getWcmLibraryService();
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	private WcmVM wcmCtrl;
	
	private I_CUDEventHandler<WcmLibrary> eventHandler;
	
	@Wire
	Listbox libraryListbox;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("wcmCtrl")final WcmVM wcmCtrl) {
		this.wcmCtrl = wcmCtrl;
		
		eventHandler = new ListboxCUDEventHandler<WcmLibrary>(libraryListbox){
			@Override
			public void onCreate(WcmLibrary v) {
				//super.onCreate(v);
				LibrariesVM.this.reloadComponent();
			}
			@Override
			public void onUpdate(WcmLibrary v) {
				final Tab tab = wcmCtrl.getTab(v);
				tab.setLabel(v.getAlias());
				super.onUpdate(v);
			}
		};
	}
	
	/// BINDING ///
	
	public List<WcmLibrary> getLibraryList() {
		return libraryService.readAll();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/wcm/library/createLibrary.wnd.zul", null, args);
	}
	
	@Command
	public void edit(@BindingParam("bean") WcmLibrary bean) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("bean", bean);
		args.put("eventHandler", eventHandler);
		wcmCtrl.openTab(E_WcmIcon.LIBRARY_SMALL.getPath(), bean.getAlias(), 
				bean.getTitle(), bean, "/WEB-INF/zul/wcm/library/editLibrary.tab.zul", args);
	}
	
	@Command
	public void remove() {
		final Listitem item = libraryListbox.getSelectedItem();
		if(item == null) return;
		
		final WcmLibrary bean = item.getValue();
		
		final StringBuilder msg = new StringBuilder("Library '")
			.append(bean.getTitle()).append("' will be removed. Continue?");
		
		Dialog.confirm("Remove Library", msg.toString()
				 , new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					libraryService.delete1(bean);
					eventHandler.onDelete(bean);
					Notification.showMessage("Library removed succesfully");
				} catch (Exception e) {
					Dialog.error("Error occured while remove Library: " + bean, e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	@Command
	@NotifyChange("libraryList")
	public void refresh() {
	}
}

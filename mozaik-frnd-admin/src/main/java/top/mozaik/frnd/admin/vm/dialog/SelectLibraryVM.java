/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.dialog;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.service.WcmLibraryService;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectLibraryVM extends BaseVM {
	
	private final WcmLibraryService libraryService = ServicesFacade.$().getWcmLibraryService();
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	@Wire
	Listbox libraryListbox;
	
	I_CallbackArg callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") final I_CallbackArg<?> callback) {
		this.callback = callback;
	}
	
	/// BINDING ///
	
	public List<WcmLibrary> getLibraryList() {
		return libraryService.read(new WcmLibrary());
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("library") WcmLibrary library) {
		callback.call(library);
		getView().detach();
	}
}

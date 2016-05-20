/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.common;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmContentType;
import top.mozaik.bknd.api.service.WcmContentTypeService;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectContentTypeVM extends BaseVM {
	
	private final WcmContentTypeService contentTypeService = ServicesFacade.$().getWcmContentTypeService();
	
	@Wire
	Listbox contentTypeListbox;
	
	I_CallbackArg<WcmContentType> callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") final I_CallbackArg<WcmContentType> callback ) {
		this.callback = callback;
	}
	
	/// BINDING ///
	
	public List<WcmContentType> getContentTypeList() {
		return contentTypeService.read(new WcmContentType());
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("contentType") WcmContentType contentType) {
		callback.call(contentType);
		getView().detach();
	}
}

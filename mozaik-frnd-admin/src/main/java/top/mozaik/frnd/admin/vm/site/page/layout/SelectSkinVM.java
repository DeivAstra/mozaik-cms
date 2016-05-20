/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site.page.layout;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectSkinVM extends BaseVM {
	
	private final ResourcePackService resourcePackService = ServicesFacade.$().getResourcePackService();
	private final ResourcePackSetService resourcePackSetService = ServicesFacade.$().getResourcePackSetService();
	
	private I_CallbackArg<ResourcePackSet> callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") I_CallbackArg<ResourcePackSet> callback) throws Exception {
		this.callback = callback;
	}
	
	/// BINDINGS ///
	
	public List<ResourcePackSet> getResourcePackSetList() {
		return resourcePackSetService.read(
				new ResourcePackSet().setResourceSetType(E_ResourceSetType.SKIN));
	}
	
	public String getResourcePackTitle(Integer resourcePackId) {
		return resourcePackService.read1(
				new ResourcePack().setId(resourcePackId)).getTitle();
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("resourceSet") ResourcePackSet resourceSet) {
		getView().detach();
		callback.call(resourceSet);
	}
}

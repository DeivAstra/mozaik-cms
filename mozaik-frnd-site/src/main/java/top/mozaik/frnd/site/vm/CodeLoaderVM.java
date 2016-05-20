/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site.vm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.site.CodeLoader;

public class CodeLoaderVM extends BaseVM {
	
	private final ResourcePackService resPackService = ServicesFacade.$().getResourcePackService();
	
	@Wire
	Listbox resPackListbox;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
	}
	
	/// BINDING ///
	
	public List<ResourcePack> getResourcePackList() {
		return resPackService.read(new ResourcePack());
	}
	
	/// COMMANDS ///
	
	@Command
	public void buildAndLoad() {
		final Listitem item = resPackListbox.getSelectedItem();
		if(item == null) return;
		
		CodeLoader.getInstance().load(
				((ResourcePack)item.getValue()).getAlias());
	}
}

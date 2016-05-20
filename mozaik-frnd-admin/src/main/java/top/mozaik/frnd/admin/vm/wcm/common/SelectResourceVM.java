/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.common;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tree;

import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.admin.converter.wcm.ResourceTreeImageUrlConverter;
import top.mozaik.frnd.admin.model.wcm.ResourceTreeModel;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectResourceVM extends BaseVM {
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	@Wire
	Tree resourceTree;
	
	I_CallbackArg<WcmResource> callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") final I_CallbackArg<WcmResource> callback) {
		this.callback = callback;
	}
	
	/// BINDING ///
	
	public ResourceTreeModel getResourceTreeModel() throws Exception {
		return new ResourceTreeModel();
	}
	
	public ResourceTreeImageUrlConverter getTreeImageUrlConverter() {
		return ResourceTreeImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("el") A_TreeElement el) {
		if(!(el instanceof TreeResource)) return;
		
		callback.call(((TreeResource)el).getValue());
		getView().detach();
	}
}

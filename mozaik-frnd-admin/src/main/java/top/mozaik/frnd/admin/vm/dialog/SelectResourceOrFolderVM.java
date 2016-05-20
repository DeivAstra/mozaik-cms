/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.dialog;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tree;

import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.admin.converter.wcm.ResourceTreeImageUrlConverter;
import top.mozaik.frnd.admin.model.wcm.ResourceTreeModel;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectResourceOrFolderVM extends BaseVM {
	
	public static final String TARGET_RESOURCE = "r";
	public static final String TARGET_FOLDER = "rf";
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	@Wire
	Tree resourceTree;
	
	I_CallbackArg callback;
	
	private String target;
	
	@Init
	public void init(@ExecutionArgParam("target") String target) {
		this.target = target;
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") final I_CallbackArg<?> callback) {
		this.callback = callback;
	}
	
	/// BINDING ///
	
	public String getTitle(){
		if(TARGET_RESOURCE.equals(target)) {
			return "Select Resource";
		} else if(TARGET_FOLDER.equals(target)) {
			return "Select Resource Folder";
		} else {
			return "Select Resource or Folder";
		}
	}
	
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
		if(!(el instanceof TreeResource) & !(el instanceof TreeResourceFolder)) return;
		
		if(TARGET_RESOURCE.equals(target)) {
			if(!(el instanceof TreeResource)) return;
		} else if(TARGET_FOLDER.equals(target)) {
			if(!(el instanceof TreeResourceFolder)) return;
		}
		
		callback.call(el.getValue());
		getView().detach();
	}
}

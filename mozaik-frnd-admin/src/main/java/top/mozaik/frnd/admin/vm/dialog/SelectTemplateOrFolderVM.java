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

import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplate;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplateFolder;
import top.mozaik.frnd.admin.converter.wcm.TemplateTreeImageUrlConverter;
import top.mozaik.frnd.admin.model.wcm.TemplateTreeModel;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectTemplateOrFolderVM extends BaseVM {
	
	public static final String TARGET_TEMPLATE = "t";
	public static final String TARGET_FOLDER = "tf";
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	@Wire
	Tree templateTree;
	
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
		if(TARGET_TEMPLATE.equals(target)) {
			return "Select Template";
		} else if(TARGET_FOLDER.equals(target)) {
			return "Select Template Folder";
		} else {
			return "Select Template or Folder";
		}
	}
	
	public TemplateTreeModel getTemplateTreeModel() throws Exception {
		return new TemplateTreeModel();
	}
	
	public TemplateTreeImageUrlConverter getTreeImageUrlConverter() {
		return TemplateTreeImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("el") A_TreeElement el) {
		if(!(el instanceof TreeTemplate) & !(el instanceof TreeTemplateFolder)) return;
		
		if(TARGET_TEMPLATE.equals(target)) {
			if(!(el instanceof TreeTemplate)) return;
		} else if(TARGET_FOLDER.equals(target)) {
			if(!(el instanceof TreeTemplateFolder)) return;
		}
		
		callback.call(el.getValue());
		getView().detach();
	}
}

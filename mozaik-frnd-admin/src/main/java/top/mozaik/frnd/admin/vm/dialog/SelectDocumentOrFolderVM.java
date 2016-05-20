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

import top.mozaik.frnd.admin.bean.wcm.document.TreeDocument;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocumentFolder;
import top.mozaik.frnd.admin.converter.wcm.DocumentTreeImageUrlConverter;
import top.mozaik.frnd.admin.model.wcm.DocumentTreeModel;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectDocumentOrFolderVM extends BaseVM {
	
	public static final String TARGET_DOCUMENT = "d";
	public static final String TARGET_FOLDER = "df";
	
	private static final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	@Wire
	Tree documentTree;
	
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
		if(TARGET_DOCUMENT.equals(target)) {
			return "Select Document";
		} else if(TARGET_FOLDER.equals(target)) {
			return "Select Document Folder";
		} else {
			return "Select Document or Folder";
		}
	}
	
	public DocumentTreeModel getDocumentTreeModel() throws Exception {
		return new DocumentTreeModel();
	}
	
	public DocumentTreeImageUrlConverter getTreeImageUrlConverter() {
		return DocumentTreeImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("el") A_TreeElement el) {
		if(!(el instanceof TreeDocument) & !(el instanceof TreeDocumentFolder)) return;
		
		if(TARGET_DOCUMENT.equals(target)) {
			if(!(el instanceof TreeDocument)) return;
		} else if(TARGET_FOLDER.equals(target)) {
			if(!(el instanceof TreeDocumentFolder)) return;
		}
		
		callback.call(el.getValue());
		getView().detach();
	}
}

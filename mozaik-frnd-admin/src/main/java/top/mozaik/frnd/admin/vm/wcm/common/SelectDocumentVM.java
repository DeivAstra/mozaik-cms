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

import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocument;
import top.mozaik.frnd.admin.converter.wcm.DocumentTreeImageUrlConverter;
import top.mozaik.frnd.admin.model.wcm.DocumentTreeModel;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectDocumentVM extends BaseVM {
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	@Wire
	Tree documentTree;
	
	I_CallbackArg<WcmDocument> callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") final I_CallbackArg<WcmDocument> callback) {
		this.callback = callback;
	}
	
	/// BINDING ///
	
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
		if(!(el instanceof TreeDocument)) return;
		
		callback.call(((TreeDocument)el).getValue());
		getView().detach();
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.dialog;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tree;

import top.mozaik.frnd.admin.bean.wcm.component.TreeComponent;
import top.mozaik.frnd.admin.converter.wcm.ComponentTreeImageUrlConverter;
import top.mozaik.frnd.admin.model.wcm.ComponentTreeModel;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectComponentVM extends BaseVM {
	
	private static final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	@Wire
	Tree componentTree;
	
	I_CallbackArg callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") final I_CallbackArg<?> callback) {
		this.callback = callback;
	}
	
	/// BINDING ///
	
	public ComponentTreeModel getComponentTreeModel() throws Exception {
		return new ComponentTreeModel();
	}
	
	public ComponentTreeImageUrlConverter getTreeImageUrlConverter() {
		return ComponentTreeImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("el") A_TreeElement el) {
		if(!(el instanceof TreeComponent)) return;
		
		callback.call(el.getValue());
		getView().detach();
	}
}
/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.component;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.Wire;

import top.mozaik.bknd.api.model.WcmComponent;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.command.I_CommandExecutor;
import top.mozaik.frnd.plus.zk.component.CodeMirror;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditComponentDataVM extends BaseVM implements I_CommandExecutor {
	
	@Wire("#cmInclude #cm")
	CodeMirror cm;
	
	private WcmComponent bean;
	
	@Init
	public void init(
			@BindingParam("bean") WcmComponent bean,
			@BindingParam("commandQueue") CommandExecutionQueue commandQueue) {
		this.bean = bean;
		commandQueue.addListener(this);
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){
		cm.setValue(bean.getData());
	}
	
	
	@Override
	public void execCommand(int cmdId) {
		if(EditComponentVM.COMMAND_SAVE != cmdId) throw new IllegalArgumentException();
		
		bean.setData(cm.getValue());
	}
}

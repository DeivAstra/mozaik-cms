/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resourceeditor;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.select.annotation.Wire;

import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.component.CodeMirror;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class TextResourceEditorVM extends BaseVM {
	
	@Wire("#cmInclude #cm")
	CodeMirror cm;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("setCMCallback") I_CallbackArg<CodeMirror> setCMCallback,
			@ExecutionArgParam("resource") _Resource bean,
			@ExecutionArgParam("data") String data) {
		setCMCallback.call(cm);		
		
		cm.setValue(data);
	}
}

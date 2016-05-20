/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site.page.layout;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.frnd.admin.bean.site.SitePageLayoutBean;
import top.mozaik.frnd.plus.callback.I_Callback;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.callback.I_CallbackArgs;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SetStyleVM extends BaseVM {
	
	private SitePageLayoutBean bean;
	private I_Callback callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("sitePageLayoutBean") SitePageLayoutBean bean,
			@ExecutionArgParam("callback") I_Callback callback) throws Exception {
		this.bean = bean;
		this.callback = callback;
	}
	
	/// BINDINGS ///
	
	public SitePageLayoutBean getBean()  {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void set() {
		callback.call();
		getView().detach();
	}
}

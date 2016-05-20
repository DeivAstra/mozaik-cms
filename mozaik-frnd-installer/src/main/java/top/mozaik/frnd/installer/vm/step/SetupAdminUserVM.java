/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.vm.step;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import top.mozaik.frnd.installer.bean.AdminUserBean;
import top.mozaik.frnd.installer.bean.StepsBean;
import top.mozaik.frnd.installer.vm.I_Installer;
import top.mozaik.frnd.installer.vm.I_Step;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SetupAdminUserVM extends BaseVM implements I_Step {
	
	@Wire
	Textbox confirmPasswordTextbox;
	
	private AdminUserBean bean;
	
	private String confirmPassword;
	
	@Init
	public void init(@BindingParam("installer") I_Installer installer,
					 @BindingParam("bean") StepsBean bean) {
		installer.setStep(this);
		this.bean = bean.getAdminUserBean();
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	@Override
	public void setNextBtn(Button btn) {
	}
	
	@Override
	public boolean onBeforeNext() {
		ZKUtils.validate(getView());
			
		if(!bean.getPassword().equals(confirmPassword))
			throw new WrongValueException(confirmPasswordTextbox, 
							"Confirmed password not equals to password");
		
		return true;
	}
	
	/// BINDING ///
	
	public AdminUserBean getBean() {
		return bean;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.vm.step;

import org.springframework.jdbc.core.JdbcTemplate;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import top.mozaik.frnd.installer.bean.DbUserBean;
import top.mozaik.frnd.installer.bean.StepsBean;
import top.mozaik.frnd.installer.vm.I_Installer;
import top.mozaik.frnd.installer.vm.I_Step;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SetupDbUserVM extends BaseVM implements I_Step {
	
	@Wire
	Textbox loginTextbox;
	@Wire
	Textbox confirmPasswordTextbox;
	
	private DbUserBean bean;
	private JdbcTemplate jdbc;
	
	private String confirmPassword;
	
	@Init
	public void init(@BindingParam("installer") I_Installer installer,
					 @BindingParam("bean") StepsBean bean) {
		installer.setStep(this);
		this.bean = bean.getDbUserBean();
		this.jdbc = bean.getDbBean().getJdbc();
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	@Override
	public void setNextBtn(Button btn) {
	}
	
	@Override
	public boolean onBeforeNext() {
		ZKUtils.validate(getView());
			
		// check if user already exists
		boolean userExists = jdbc.queryForObject(
				"select count(*) from mysql.user where user='"+ bean.getLogin() +"'", Integer.class) > 0;
		if(userExists)
			throw new WrongValueException(loginTextbox, "User '" + bean.getLogin() + "' is already exists.");
		
		if(!bean.getPassword().equals(confirmPassword)) 
			throw new WrongValueException(confirmPasswordTextbox, "Confirmed password not equals to password");
	
		return true;
	}
	
	/// BINDING ///
	
	public DbUserBean getBean() {
		return bean;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}

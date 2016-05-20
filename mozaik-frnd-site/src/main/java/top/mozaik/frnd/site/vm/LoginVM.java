/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site.vm;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_UserRole;
import top.mozaik.bknd.api.model.User;
import top.mozaik.bknd.api.service.UserService;
import top.mozaik.bknd.api.utils.MDUtils;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class LoginVM extends BaseVM {
	
	private final UserService userService = ServicesFacade.$().getUserService();
	
	private String redirect;
	
	@Init
	public void init(@BindingParam("redirect") String redirect) {
		this.redirect = redirect;
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() throws Exception {
	}
	
	/// COMMANDS ///
	
	@Command
	public void login(
			@BindingParam("username") String username, 
			@BindingParam("password") String password) throws Exception {
		final Execution ex = Executions.getCurrent();
		final User user = userService.read1(
				new User()
					.setLogin(username)
					.setPassword(MDUtils.toMD5(password))
					.setRole(E_UserRole.ADMIN)
					.setActive(true)
		);
		if(user == null) {
			if(redirect != null) {
				redirect = redirect.replaceAll("&wrong=1", "");
				ex.sendRedirect("/@login.zul?r="+redirect +"&wrong=1");
			} else {
				ex.sendRedirect("/@login.zul?wrong=1");
			}
		} else {
			ex.getSession().setAttribute("user", user);
			if(redirect != null) {
				ex.sendRedirect(redirect);
			} else {
				ex.sendRedirect("/");
			}
		}
	}
}

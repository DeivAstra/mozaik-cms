/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm;

import org.springframework.jdbc.core.JdbcTemplate;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_UserRole;
import top.mozaik.bknd.api.model.User;
import top.mozaik.bknd.api.service.UserService;
import top.mozaik.bknd.api.utils.MDUtils;
import top.mozaik.frnd.plus.callback.I_Callback;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SetupLoginVM extends BaseVM {
	
	private final UserService userService = ServicesFacade.$().getUserService();
	
	private final User bean = new User();
	
	private I_Callback callback;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("jdbc") JdbcTemplate jdbc,
			@ExecutionArgParam("callback") I_Callback callback) {
		userService.setJdbcTemplate(jdbc);
		this.callback = callback;
	}
	
	/// BINDING ///
	
	public User getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void save() throws Exception {
		bean.setPassword(MDUtils.toMD5(bean.getPassword()));
		bean.setRole(E_UserRole.ADMIN);
		bean.setActive(true);
		final User user = userService.read1(bean);
		if(user == null) {
			Notification.showError("User not found with such login and password");
		} else {
			getView().detach();
			callback.call();
		}
	}
}

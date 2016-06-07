/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.access.user;

import java.util.Objects;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_UserRole;
import top.mozaik.bknd.api.model.User;
import top.mozaik.bknd.api.service.UserService;
import top.mozaik.frnd.admin.constraint.UserLoginConstraint;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateUserVM extends BaseVM {
	
	private final UserService userService = ServicesFacade.$().getUserService();
	
	private final UserLoginConstraint userLoginConstraint = new UserLoginConstraint();
	
	@Wire
    Textbox confirmPasswordTextbox;
	
	private final User bean = new User();
	
	private I_CUDEventHandler<User> eventHandler;
	
	private String confirmPassword;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<User> eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	/// BINDING ///
	
	public User getBean() {
		return bean;
	}
	
	public E_UserRole [] getRoleList() {
		return E_UserRole.values();
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	/// COMMANDS ///
	
	@Command
	public void create(){
		ZKUtils.validate(getView());
		
		
		if(!Objects.equals(bean.getPassword(), confirmPassword)) {
            throw new WrongValueException(confirmPasswordTextbox, "Passwords don't match.");
        }
		
		try {
			bean.setId(userService.create(bean));
			bean.setPassword(null);
			eventHandler.onCreate(bean);
			Notification.showMessage("User created succesfully");
			detachView();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create User", e);
		}
	}
}
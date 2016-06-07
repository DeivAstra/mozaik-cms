/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.access.user;

import java.util.ArrayList;
import java.util.List;
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

public class EditUserVM extends BaseVM {
	
	private final UserService userService = ServicesFacade.$().getUserService();
	
	private final UserLoginConstraint userLoginConstraint = new UserLoginConstraint();
	
	@Wire
    Textbox confirmPasswordTextbox;
	
	private User bean;
	private I_CUDEventHandler<User> eventHandler;
	
	private String confirmPassword;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("bean") User bean,
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<User> eventHandler) {
		this.bean = bean.twin();
		this.bean.setPassword(null);
		this.eventHandler = eventHandler;
		
		this.userLoginConstraint.setLogin(bean.getLogin());
	}
	
	/// BINDING ///
	
	public User getBean() {
		return bean;
	}
	
	public E_UserRole [] getRoleList() {
		return E_UserRole.values();
	}
	
	public UserLoginConstraint getUserLoginConstraint() {
		return userLoginConstraint;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	/// COMMANDS ///
	
	private static final List<String> skipToWriteFields = new ArrayList<String>();
	static {
		skipToWriteFields.add("password");
	}
	
	@Command
	public void save(){
		ZKUtils.validate(getView());
		
		if(!Objects.equals(bean.getPassword(), confirmPassword)) {
            throw new WrongValueException(confirmPasswordTextbox, "Passwords don't match.");
        }
		
		final User user = userService.read1(new User().setId(bean.getId()));
		if(user.getRole() == E_UserRole.ADMIN) {
			final boolean isLastAdmin = userService.readAll().size() == 1;
			// CHECK IF CHANGE ROLE TO LAST ADMIN
			if(isLastAdmin && bean.getRole() != E_UserRole.ADMIN) {
				Notification.showError("You can't change ROLE to last AMDIN user");
				return;
			}
			// CHECK IF CHANGE ACTIVE TO LAST ADMIN
			if(isLastAdmin && !bean.isActive()) {
				Notification.showError("You can't disable ACTIVE to last AMDIN user");
				return;
			}
		}
		
		if(bean.getPassword() == null) {
			bean.getFilter().setSkipToWriteFields(skipToWriteFields);
		}
		
		try {
			userService.update1(bean);
			bean.setPassword(null);
			
			eventHandler.onUpdate(bean.commit());
			Notification.showMessage("User updated succesfully");
			detachView();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while update User", e);
		}
	}
}
/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.settings;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmSettings;
import top.mozaik.bknd.api.service.WcmSettingsService;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SettingsVM extends BaseVM {
	
	private final WcmSettingsService settingsService = ServicesFacade.$().getWcmSettingsService();
	
	private final CommandExecutionQueue commandQueue = new CommandExecutionQueue();
	public static final int COMMAND_SAVE = 0;
	
	private WcmSettings bean;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		//final List<WcmSettings> list = settingsService.read(new WcmSettings().getFilter());
		// TODO: get last by id
		if(/*list.size() > 0*/false) {
			//this.bean = list.get(0);
		} else {
			this.bean = new WcmSettings();
		}
	}
	
	public CommandExecutionQueue getCommandQueue() {
		return commandQueue;
	}
	
	/// BINDING ///
	
	
	/// COMMANDS ///
	
	@Command
	public void save() {
		ZKUtils.validate(getView());
		try {
			settingsService.startTransaction();
			
			commandQueue.execCommand(COMMAND_SAVE);
			
			//settingsService.update(bean);
			
			settingsService.commit();
			
			Notification.showMessage("Settings saved succesfully");
		} catch (Exception e) {
			settingsService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while save Settings", e);
		}
	}
}

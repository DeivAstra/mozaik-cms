/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.log;

import org.apache.commons.logging.impl.Log4JLogger;

import top.mozaik.frnd.plus.zk.component.Dialog;

public class Logger extends Log4JLogger {
	
	private final boolean showDialog;
	
	public Logger(Class clazz) {
		super(clazz.getName());
		this.showDialog = false;
	}
	
	public Logger(Class clazz, boolean showDialog) {
		super(clazz.getName());
		this.showDialog = showDialog;
	}
	
	@Override
	public void error(Object message, Throwable t) {
		super.error(message, t);
		
		if(!showDialog) return;
		
		if(message == null)
			Dialog.error(null, t);
		else {
			Dialog.error(message.toString(), t);
		}
	}
}

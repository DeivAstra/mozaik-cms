/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Executions;

public class Notification {
	
	private static final String COMPONENT_PATH = "/WEB-INF/zul/_shared_/notification.zul";
	
	private static final String TYPE_MESSAGE = "message";
	private static final String TYPE_ERROR = "error";
	
	public static void showMessage(final String message) {
		showNotification(message, TYPE_MESSAGE);
	}
	
	public static void showError(String message) {
		showNotification(message, TYPE_ERROR);
	}
	
	public static void showError(Exception e) {
		e.printStackTrace();
		showNotification(e.getMessage(), TYPE_ERROR);	
	}
	
	private static void showNotification(String message, String type){
		final Map<String, String> args = new HashMap<String, String>();
		args.put("type", type);
		args.put("message", message);
		Executions.createComponents(COMPONENT_PATH, null, args);
	}
}

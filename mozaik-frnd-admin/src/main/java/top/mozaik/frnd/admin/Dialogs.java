/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Executions;

import top.mozaik.bknd.api.model.WcmComponent;
import top.mozaik.frnd.admin.vm.dialog.SelectDocumentOrFolderVM;
import top.mozaik.frnd.admin.vm.dialog.SelectResourceOrFolderVM;
import top.mozaik.frnd.admin.vm.dialog.SelectSiteOrPageVM;
import top.mozaik.frnd.admin.vm.dialog.SelectTemplateOrFolderVM;
import top.mozaik.frnd.plus.callback.I_CallbackArg;

public class Dialogs {
	
	public static void selectSite(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectSiteOrPageVM.TARGET_SITE);
		Executions.createComponents("/WEB-INF/zul/dialog/selectSiteOrPage.wnd.zul", null, args);
	}
	
	public static void selectSitePage(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectSiteOrPageVM.TARGET_PAGE);
		Executions.createComponents("/WEB-INF/zul/dialog/selectSiteOrPage.wnd.zul", null, args);
	}
	
	public static void selectDocument(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectDocumentOrFolderVM.TARGET_DOCUMENT);
		Executions.createComponents("/WEB-INF/zul/dialog/selectDocumentOrFolder.wnd.zul", null, args);
	}
	
	public static void selectDocumentFolder(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectDocumentOrFolderVM.TARGET_FOLDER);
		Executions.createComponents("/WEB-INF/zul/dialog/selectDocumentOrFolder.wnd.zul", null, args);
	}
	
	public static void selectDocumentOrFolder(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/dialog/selectDocumentOrFolder.wnd.zul", null, args);
	}
	
	public static void selectTemplate(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectTemplateOrFolderVM.TARGET_TEMPLATE);
		Executions.createComponents("/WEB-INF/zul/dialog/selectTemplateOrFolder.wnd.zul", null, args);
	}
	
	public static void selectTemplateFolder(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectTemplateOrFolderVM.TARGET_FOLDER);
		Executions.createComponents("/WEB-INF/zul/dialog/selectTemplateOrFolder.wnd.zul", null, args);
	}
	
	public static void selectTemplateOrFolder(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/dialog/selectTemplateOrFolder.wnd.zul", null, args);
	}
	
	public static void selectComponent(I_CallbackArg<WcmComponent> callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/dialog/selectComponent.wnd.zul", null, args);
	}
	
	public static void selectResource(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectResourceOrFolderVM.TARGET_RESOURCE);
		Executions.createComponents("/WEB-INF/zul/dialog/selectResourceOrFolder.wnd.zul", null, args);
	}
	
	public static void selectResourceFolder(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		args.put("target", SelectResourceOrFolderVM.TARGET_FOLDER);
		Executions.createComponents("/WEB-INF/zul/dialog/selectResourceOrFolder.wnd.zul", null, args);
	}
	
	public static void selectResourceOrFolder(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/dialog/selectResourceOrFolder.wnd.zul", null, args);
	}
	
	public static void selectLibrary(I_CallbackArg callback) {
		final Map<String, Object> args = new HashMap<>();
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/dialog/selectLibrary.wnd.zul", null, args);
	}
}
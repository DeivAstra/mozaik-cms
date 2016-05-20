/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package com.ktrsys.base.widget.wcmwidget;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.sys.*;
import org.zkoss.zul.*;
import java.util.*;

import top.mozaik.bknd.api.*;
import top.mozaik.bknd.api.enums.*;
import top.mozaik.bknd.api.service.*;
import top.mozaik.bknd.api.model.*;
import top.mozaik.frnd.common.vm.*;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class ComponentVM extends BaseVM {

	private static final String 
					LAYOUT_WIDGET_PARAMS_ATTR = "LAYOUT_WIDGET_PARAMS",
					COMPONENT_ID_ATTR = "COMPONENT_ID",
					ELEMENT_ID_ATTR = "ELEMENT_ID",
					ELEMENT_ATTR = "ELEMENT",
					API_ATTR = "API",
	
					DOC_TYPE = "doc",   // Document
					DOCF_TYPE = "docf", 	// Document folder
					TEMPL_TYPE = "templ",   // Template
					TEMPLF_TYPE = "templf", // Template folder
					RES_TYPE = "res",   // Resource
					RESF_TYPE = "resf", // Resource folder
					LIB_TYPE = "lib", // Library
					INT_TYPE = "int", // Integer
					DBL_TYPE = "dbl", // Double
					TXT_TYPE = "txt", // Text
					MTXT_TYPE = "mtxt", // Multiline text
					DATE_TYPE = "date", // Date
					TIME_TYPE = "time", // Time
					URL_TYPE = "url"; // URL
	
	private final WcmComponentService componentService = ServicesFacade.$().getWcmComponentService();
	private final WcmDocumentService documentService = ServicesFacade.$().getWcmDocumentService();
	private final WcmDocumentFolderService documentFolderService = ServicesFacade.$().getWcmDocumentFolderService();
	private final WcmTemplateService templateService = ServicesFacade.$().getWcmTemplateService();
	private final WcmTemplateFolderService templateFolderService = ServicesFacade.$().getWcmTemplateFolderService();
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	private final WcmResourceFolderService resourceFolderService = ServicesFacade.$().getWcmResourceFolderService();
	private final WcmLibraryService libraryService = ServicesFacade.$().getWcmLibraryService();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		final Map<String,Object> widgetParams = (Map) Executions.getCurrent().getAttribute(LAYOUT_WIDGET_PARAMS_ATTR);
		try	{
			
			if(widgetParams == null)
				throw new Exception(LAYOUT_WIDGET_PARAMS_ATTR + " not defined");
			
			if(widgetParams.get(COMPONENT_ID_ATTR) == null)
				throw new Exception(COMPONENT_ID_ATTR + " not defined");
		
			final int componentId = Integer.parseInt((String)widgetParams.get(COMPONENT_ID_ATTR));
			final WcmComponent wcmComponent = componentService.read1(new WcmComponent().setId(componentId));
			if(wcmComponent == null)
				throw new Exception("Component with ID = " + componentId  + " not found");
			
			// I can set as Page attributes but not sure that is good.
			// Later I will try to implement including component to component.
			//final ExecutionCtrl execCtrl = ((ExecutionCtrl)Executions.getCurrent());
			//final Page page = execCtrl.getCurrentPage();
			
			final StringBuilder sb = new StringBuilder("<zk>");
			sb.append("<zscript>Object args=arg;</zscript>");
			sb.append(wcmComponent.getData()).append("</zk>");
			
			final Map<String, Object> args = parseParams(widgetParams);
			args.put(API_ATTR, API.$());
			
			logAttrs(args);
			
			getView().appendChild(Executions.createComponentsDirectly(sb.toString(), "zul", null, args));
		} catch(NumberFormatException e) {
			showError("Error occured while parse " + COMPONENT_ID_ATTR);
		} catch(Throwable t) {
			t.printStackTrace();
			showError(t.getMessage()==null?t.getClass().getSimpleName():t.getMessage());
		}
	}
	
	private Map<String, Object> parseParams(Map<String,Object> paramMap) {
		final Map<String, Object> resMap = new HashMap<>();
		for(String key : paramMap.keySet()){
			if(key.indexOf(':') == -1) {
				continue;
			}
			final String [] typeName = key.split(":");
			final String value = (String)paramMap.get(key);
			switch(typeName[0]) {
				case DOC_TYPE:
					resMap.put(typeName[1],
						documentService.read1(
							new WcmDocument().setId(Integer.parseInt(value))));
				break;
				case DOCF_TYPE:
					resMap.put(typeName[1],
						documentFolderService.read1(
							new WcmDocumentFolder().setId(Integer.parseInt(value))));
				break;
				case TEMPL_TYPE:
					resMap.put(typeName[1],
						templateService.read1(
							new WcmTemplate().setId(Integer.parseInt(value)))
					);
				break;
				case TEMPLF_TYPE:
					resMap.put(typeName[1],
						templateFolderService.read1(
							new WcmTemplateFolder().setId(Integer.parseInt(value)))
					);
				break;
				case RES_TYPE:
					resMap.put(typeName[1],
						resourceService.read1(
							new WcmResource().setId(Integer.parseInt(value)))
					);
				break;
				case RESF_TYPE:
					resMap.put(typeName[1],
						resourceFolderService.read1(
							new WcmResourceFolder().setId(Integer.parseInt(value)))
					);
				break;
				case LIB_TYPE:
					resMap.put(typeName[1],
						libraryService.read1(
							new WcmLibrary().setId(Integer.parseInt(value)))
					);
				break;
				case INT_TYPE:
					resMap.put(typeName[1], Integer.parseInt(value));
				break;
				case DBL_TYPE:
					resMap.put(typeName[1], Double.parseDouble(value));
				break;
				case TXT_TYPE:
				case MTXT_TYPE:
				case URL_TYPE:
					resMap.put(typeName[1], value);
				break;
				case DATE_TYPE:
				case TIME_TYPE:
					resMap.put(typeName[1], new Date(Long.parseLong(value)));
				break;
			}
		}
		return resMap;
	}
	
	private String getSiteUrl() {
		return ServicesFacade.$().getSettingsService().read1(
				new Settings().setKey(E_SettingsKey.SITE_URL)).getValue();
	}
	
	private void appendZscriptVars(StringBuilder sb, Map<String,String> params) {
		sb.append("<zscript>");
		for(String key : params.keySet()){
			if(key.indexOf(':') == -1) {
				continue;
			}
			key = key.split(":")[1];
			sb.append("Object ").append(key).append('=').append(key).append(';');
		}
		sb.append("</zscript>");
		System.out.println(sb.toString());
	}
	
	private void logAttrs(Map<String, Object> attrMap) {
		System.out.println("WCM Widgets attrs:");
		for(String key : attrMap.keySet()){
			System.out.println(key+"="+attrMap.get(key).toString());
		}
	}
	
	private void showError(String message) {
		final Label label = new Label(message);
		label.setStyle("font-weight:bold;color:red");
		getView().appendChild(label);
	}
}

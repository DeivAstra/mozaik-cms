/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApp;

import top.mozaik.bknd.api.enums.E_DbSettings;
import top.mozaik.bknd.api.utils.SpringPropertiesUtil;
import top.mozaik.frnd.common.init.DbInit;
import top.mozaik.frnd.plus.zk.constraint.SwitchConstraint;

public final class App {
	private static final String CACHE_PARAM_NAME = "cache";
	private static final String CFG_PARAM_NAME = "cfg";
    
	public static void init(WebApp app) {
    	if(app.getAttribute(CACHE_PARAM_NAME) == null) {
    		final Map<String, Object> map = new HashMap<>();
    		
    			final Map<String, Object> constraintMap = new HashMap<>();
    			constraintMap.put("noempty", new SwitchConstraint(false, "no empty"));
    		
    		map.put("constraint", constraintMap);
    		
    		app.setAttribute(CACHE_PARAM_NAME, map);
    	}
    	/// Access to spring app-context properties
    	/// Examples:
    	/// ${application.attributes.cfg['jdbc.url']}
    	/// ${application.attributes.cfg.someProp}
    	if(app.getAttribute(CFG_PARAM_NAME) == null) {
    		//final String someProp = SpringPropertiesUtil.getProperty("someProp");
    		app.setAttribute(CFG_PARAM_NAME, SpringPropertiesUtil.getMap());
    	}
    	
    	E_DbSettings.DATABASE.setValue(
    			(String)Sessions.getCurrent().getAttribute(DbInit.DB_ATTR_NAME));
    }
}

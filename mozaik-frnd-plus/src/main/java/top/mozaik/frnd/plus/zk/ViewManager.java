/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

public class ViewManager {
	
	private final static String VIEW_URI_PARAMNAME = "view";
	private final static String INDEX_VIEWNAME = "main";
	
	private static volatile ViewManager instance;
	
	private ViewManager(){}
	
	public static ViewManager getInstance() {
		if(instance == null){
			instance = new ViewManager();
		}
		return instance;
	}
	
	public void showView(Component parent){
		final Execution ex = Executions.getCurrent();
		String view = ex.getParameter(VIEW_URI_PARAMNAME);
		if(view == null) {
			view = INDEX_VIEWNAME;
		}
		Executions.createComponents("/zul/"+ view + ".zul", parent, ex.getParameterMap());
	}
	
	public void openIndexView(){
		Executions.sendRedirect("/");
	}
	
	public void openView(String viewName){
		final StringBuffer buf = new StringBuffer("?");
		buf.append(VIEW_URI_PARAMNAME).append("=").append(viewName);
		Executions.sendRedirect(buf.toString());
	}
	
	public void openView(String viewName, String paramName, Object paramValue){
		final StringBuffer buf = new StringBuffer("?");
		buf.append(VIEW_URI_PARAMNAME).append("=").append(viewName);
		buf.append("&").append(paramName).append("=").append(paramValue);
		Executions.sendRedirect(buf.toString());
	}
	
	public void openView(String viewName, Map<String, String> params){
		final StringBuffer buf = new StringBuffer("?");
		buf.append(VIEW_URI_PARAMNAME).append("=").append(viewName);
		for(String key: params.keySet()){
			buf.append("&").append(key).append("=").append(params.get(key));
		}
		Executions.sendRedirect(buf.toString());
	}
}

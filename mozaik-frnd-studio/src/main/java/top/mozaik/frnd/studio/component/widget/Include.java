/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.component.widget;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

class Include extends org.zkoss.zul.Include {
	
	public static final String ZUL_DEFINITION = "<?component name=\"include\" class=\"" + Include.class.getName()  +"\"?>";
	
	private Map<String, Object> dynamicProps;
	
	private String src;
	
	public Include() {
		setMode("instant");
	}
		
	@Override
	public void setSrc(String src) {
		this.src = src;
	}
	/*
	@Override
	public void afterCompose() {
		final Integer widgetId = (Integer) Executions.getCurrent().getAttribute("widgetId");
		setDynamicProperty("WIDGET_ID", widgetId);
		final ResourceSetUtils resourceSetUtils = new ResourceSetUtils(widgetId);
		final _ResourceData resourceData = resourceSetUtils.findResourceDataByPath(src, E_ResourceType.ZUL);
		
		final StringBuilder zulData = new StringBuilder(E_ResourcePackSettings.ZUL_COMPONENT_DEFINITIONS.getValue().toString());
		zulData.append(new String(resourceData.getSourceData()));
		//final Map<String, Object> oldProps = setupDynams(getExecution()); /// FOR WHAT???
		try {
			this.getChildren().clear();
			Executions.createComponentsDirectly(zulData.toString(), "zul", this, dynamicProps);
		} finally {
			//restoreDynams(getExecution(), oldProps); /// FOR WHAT???
		}
	}*/
	
	private Execution getExecution() {
		return (getPage()==null)?Executions.getCurrent():getPage().getDesktop().getExecution();
	}
	
	public boolean hasDynamicProperty(String name) {
		return dynamicProps != null && dynamicProps.containsKey(name);
	}
	
	public Object getDynamicProperty(String name) {
		return dynamicProps != null ? dynamicProps.get(name): null;
	}
	
	public void setDynamicProperty(String name, Object value) {
		if (dynamicProps == null)
			dynamicProps = new HashMap<String, Object>();
		dynamicProps.put(name, value);
	}
	
	public void clearDynamicProperties() {
		dynamicProps = null;
	}
	/*
	private Map<String, Object> setupDynams(Execution exec) {
		if (dynamicProps == null || dynamicProps.isEmpty())
			return null;

		final Map<String, Object> old = new HashMap<String, Object>();
		for (Map.Entry<String, Object> me: dynamicProps.entrySet()) {
			final String nm = me.getKey();
			final Object val = me.getValue();

			old.put(nm, exec.getAttribute(nm));

			if (val != null) exec.setAttribute(nm, val);
			else exec.removeAttribute(nm);
		}
		return old;
	}
	
	private static void restoreDynams(Execution exec, Map<String, Object> old) {
		if (old == null) return;
		
		for (Map.Entry<String, Object> me: old.entrySet()) {
			final String nm = me.getKey();
			final Object val = me.getValue();

			if (val != null) exec.setAttribute(nm, val);
			else exec.removeAttribute(nm);
		}
	}
	*/
}

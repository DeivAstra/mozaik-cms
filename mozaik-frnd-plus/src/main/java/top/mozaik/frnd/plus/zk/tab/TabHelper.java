/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.tab;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

public class TabHelper {
	
	private final Tabbox tabbox;
	private final String valueAttrName;
	
	private EventListener<Event> onCloseListener;
	
	public TabHelper(Tabbox tabbox) {
		this(tabbox, "value");
	}
	
	public TabHelper(Tabbox tabbox, String valueAttrName) {
		if(tabbox == null) throw new IllegalArgumentException("null");
		this.tabbox = tabbox;
		this.valueAttrName = valueAttrName;
	}
	
	public Tab getTabByValue(Object value) {
		Tab tab = null;
		for(Component comp: tabbox.getTabs().getChildren()) {
			//if(comp.getAttribute(valueAttrName).equals(value)) {
			if(value.equals(comp.getAttribute(valueAttrName))) {
				tab = (Tab)comp;
			}
		}
		return tab;
	}
	
	public void setOnCloseListener(EventListener<Event> onCloseListener) {
		this.onCloseListener = onCloseListener;
	}
	
	public void openTab(String title, String tooltip, Object value, String zulPath, Map<String, Object> args) {
		openTab(null, title, tooltip, value, zulPath, args);
	}
	
	public void openTab(String image, String title, String tooltip, Object value, String zulPath, Map<String, Object> args) {
		Tab tab = getTabByValue(value);
		
		if(tab != null) {
			tab.setSelected(true);
			return;
		}

		tab = new Tab(title);
		tab.setSclass("image14px");
		tab.setImage(image);
		tab.setTooltiptext(tooltip);
		tab.setAttribute(valueAttrName, value);
		tab.setClosable(true);
		tab.setSelected(true);
		tabbox.getTabs().appendChild(tab);
		
		if(onCloseListener != null){
			tab.addEventListener(Events.ON_CLOSE , onCloseListener);
		}
		
		final Tabpanel tabPanel = new Tabpanel();
		tabPanel.setStyle("padding:0");
		tabbox.getTabpanels().appendChild(tabPanel);
		
		if(args == null) {
			args = new HashMap<String, Object>();
		}		
		args.put("tab", tab);
		args.put(valueAttrName, value);
		Executions.createComponents(zulPath, tabPanel, args);
	}
}

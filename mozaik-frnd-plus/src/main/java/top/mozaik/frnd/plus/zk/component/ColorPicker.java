/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;

import top.mozaik.frnd.plus.zk.ZKUtils;

public class ColorPicker extends Div {
	
	private static final String ONCHANGE_COLOR_EVENT_NAME = "onChange";
	
	private String color = "#000000";

	public ColorPicker() {
		renderComp();
	}
	
	private void renderComp() {
		ZKUtils.detachAll(this);
		
		final Map<String, String> args = new HashMap<String, String>();
		args.put("color", color);
		args.put("eventName", ONCHANGE_COLOR_EVENT_NAME);
		final Div div = (Div)Executions.createComponents("/zul/_shared_/ColorPicker.zul", this, args);
		div.addEventListener(ONCHANGE_COLOR_EVENT_NAME, new EventListener<Event>() {
			
			public void onEvent(Event event) throws Exception {
				//System.out.println(event.getData());
				ColorPicker.this.setColor(event.getData().toString());
				renderComp();
				Events.postEvent(ONCHANGE_COLOR_EVENT_NAME, ColorPicker.this, event.getData());
			}
		});
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
		renderComp();
	}
}

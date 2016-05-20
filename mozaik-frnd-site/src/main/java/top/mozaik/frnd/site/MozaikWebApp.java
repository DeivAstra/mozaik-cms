/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import java.io.InputStream;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.http.SimpleWebApp;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class MozaikWebApp extends SimpleWebApp {
	
	private final MozaikUiFactory uiFactory = new MozaikUiFactory();
	
	public MozaikWebApp() {}
	
	@Override
	public void init(Object context, Configuration config) {
		// TODO Auto-generated method stub
		super.init(context, config);
		
		config.addRichlet("test-richlet", new GenericRichlet() {
			
			@Override
			public void service(final Page page) throws Exception {
				final Window wnd = new Window();
				
				final Label label = new Label("Welcome to Richlet!");
				label.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						Clients.log("Click in the Richlet");
						throw new RuntimeException();
					};
				});
				
				wnd.appendChild(label);
				wnd.setPage(page);
			}
		});
		
		config.addRichletMapping("test-richlet", "/@/test-richlet");
	}
	
	@Override
	public InputStream getResourceAsStream(String path) {
		return super.getResourceAsStream(path);
	}
	
	@Override
	public UiFactory getUiFactory() {
		return uiFactory;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm;

import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.East;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

import top.mozaik.frnd.plus.zk.tab.TabHelper;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class WcmVM extends BaseVM {
	
	@Wire
	Tabbox wcmCenterTabbox;
	
	private TabHelper tabHelper;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		final Tab closePanelTab = (Tab)wcmCenterTabbox.getTabs().getChildren().get(0);
		final EventListener<Event> hideEditPanelListener = new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				event.stopPropagation();
				hideEditPanel();
			}
		};
		closePanelTab.addEventListener(Events.ON_CLOSE, hideEditPanelListener);
		closePanelTab.addEventListener(Events.ON_CLICK, hideEditPanelListener);		
		
		tabHelper = new TabHelper(wcmCenterTabbox);
		tabHelper.setOnCloseListener(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(wcmCenterTabbox.getTabs().getChildren().size() > 2) return;
				hideEditPanel();
			}
		});
	}
	
	private void showEditPanel() {
		final East east = ((Borderlayout)getView()).getEast();
		east.setSplittable(true);
		east.setStyle("visibility:visible");
	}
	
	private void hideEditPanel() {
		final East east = ((Borderlayout)getView()).getEast();
		east.setStyle("visibility:hidden");
		east.setSplittable(false);
	}
	
	public void openTab(String image, String title, String tooltip, Object value, String zulPath, Map<String, Object> args) {
		showEditPanel();
		tabHelper.openTab(image, title, tooltip, value, zulPath, args);
	}
	
	public Tab getTab(Object v) {
		return tabHelper.getTabByValue(v);
	}
}
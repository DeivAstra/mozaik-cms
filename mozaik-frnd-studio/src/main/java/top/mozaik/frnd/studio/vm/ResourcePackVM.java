/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.frnd.common.ResourcePackServicesFacade;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.tab.TabHelper;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class ResourcePackVM extends BaseVM {
	
	public static final String RESOURCE_EDITOR_ATTR = "RESOURCE_EDITOR";
	private static final String RESPACK_FACADE_ATTR = "RESPACK_FACADE";
	
	private final ResourcePackService resourcePackService = ServicesFacade.$().getResourcePackService();
	
	@Wire
	Tree widgetTree;
	@Wire
	Tabbox centerTabbox;
	
	private TabHelper tabHelper;
	
	private ResourcePack resourcePack;
	
	@Init
	public void init() {
		final String resourcePackAlias = (String)Executions.getCurrent().getParameter("rpa");
		if(resourcePackAlias == null) {
			throw new IllegalArgumentException("Parameter 'rpa' is not defined");
		}
		
		resourcePack = resourcePackService.read1(
				new ResourcePack().setAlias(resourcePackAlias));
		if(resourcePack == null) {
			throw new IllegalArgumentException("Resource Pack not found with alias = " + resourcePackAlias);
		}
		Executions.getCurrent().getDesktop()
				.setAttribute(RESPACK_FACADE_ATTR, ResourcePackServicesFacade.get(resourcePack));
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
	}
	
	public static ResourcePackServicesFacade getRespackFacade() {
		return (ResourcePackServicesFacade) Executions.getCurrent().getDesktop().getAttribute(RESPACK_FACADE_ATTR);
	}
	
	public TabHelper getTabHelper() {
		if(tabHelper == null)
			tabHelper = new TabHelper(centerTabbox, "resource");
		return tabHelper;
	}
	
	/// BINDING ///
	
	public ResourcePack getResourcePack() {
		return resourcePack;
	}
	
	/// COMMANDS ///
	/*
	public Tab getTabByResource(Object value) {
		Tab tab = null;
		for(Component comp: centerTabbox.getTabs().getChildren()) {
			if(comp.getAttribute("resource").equals(value)) {
				tab = (Tab)comp;
			}
		}
		return tab;
	}
	
	
	public void openTab(String title, String tooltip, Object resource, String zulPath) {
		Tab tab = getTabByResource(resource);
		
		if(tab != null) {
			tab.setSelected(true);
			return;
		}

		tab = new Tab(title);
		tab.setTooltiptext(tooltip);
		tab.setAttribute("resource", resource);
		tab.setClosable(true);
		tab.setSelected(true);
		centerTabbox.getTabs().appendChild(tab);
		
		final Tabpanel tabPanel = new Tabpanel();
		centerTabbox.getTabpanels().appendChild(tabPanel);
		
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("tab", tab);
		args.put("resourcePack", ResourcePackServicesFacade.getResourcePack().getName());
		args.put("resource", resource);
		Executions.createComponents(zulPath, tabPanel, args);
	}*/
	
	@Command
	public void saveResource() {
		final Tab tab = centerTabbox.getSelectedTab();
		if(tab == null) return;
		((I_ResourceEditor)tab.getAttribute(RESOURCE_EDITOR_ATTR)).save();
		Notification.showMessage("File saved successfully");
	}
	
	@Command
	public void saveAllResources() {
		final Tabs tabs = centerTabbox.getTabs();
		if(tabs.getChildren().isEmpty()) return;
		
		for(Component tab: tabs.getChildren()) {
			((I_ResourceEditor)tab.getAttribute(RESOURCE_EDITOR_ATTR)).save();
		}
		Notification.showMessage("All Files saved successfully");
	}
	
	@Command
	public void closeResource() {
		final Tab tab = centerTabbox.getSelectedTab();
		if(tab == null) return;
		((I_ResourceEditor)tab.getAttribute(RESOURCE_EDITOR_ATTR)).beforeClose();
		tab.close();
	}
	
	@Command
	public void closeAllResources() {
		final Tabs tabs = centerTabbox.getTabs();
		while(tabs.getChildren().size() > 0) {
			final Tab tab = (Tab)tabs.getChildren().get(0);
			((I_ResourceEditor)tab.getAttribute(RESOURCE_EDITOR_ATTR)).beforeClose();
			tab.close();
		}
	}
	
	@Command
	public void build() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("resourcepack", resourcePack);
		Executions.createComponents("/WEB-INF/zul/build/view.wnd.zul", null, args);
	}
}

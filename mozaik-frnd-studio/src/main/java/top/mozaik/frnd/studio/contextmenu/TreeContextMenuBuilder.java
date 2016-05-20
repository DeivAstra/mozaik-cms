/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.contextmenu;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.bean.tree.TreeResourceFolder;
import top.mozaik.frnd.studio.bean.tree.TreeResourceSetFolder;
import top.mozaik.frnd.studio.util.TreeResourceUtils;

public class TreeContextMenuBuilder {
	
	private I_CUDEventHandler eventHandler;
	
	public TreeContextMenuBuilder(I_CUDEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	public void build(Menupopup menu, Object value) {
		if(value instanceof TreeResourceSetFolder) {
			buildMenu(menu, (TreeResourceSetFolder)value);
		} else if (value instanceof TreeResourceFolder) {
			buildMenu(menu, (TreeResourceFolder)value);
		} else if (value instanceof TreeResource) {
			buildMenu(menu, (TreeResource)value);
		}
	}
	
	private void buildMenu(Menupopup menu, final TreeResourceSetFolder folder) {
		Menuitem item = new Menuitem("Edit");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				editResourceSet(folder);
			};
		});
		menu.appendChild(item);
		menu.appendChild(new Menuseparator());
		item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				deleteResourceSet(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildMenu(Menupopup menu, final TreeResourceFolder folder) {
		if(folder.hasType()) {
			buildCreateItems(menu, folder, folder.getType());
			return;
		}
		
		buildCreateItems(menu, folder, TreeResourceUtils.getTreeResourceType(folder));
		menu.appendChild(new Menuseparator());
		buildMenu(menu, (A_TreeElement)folder);
	}
	
	private void buildMenu(Menupopup menu, final A_TreeElement<?,_Resource> el) {
		Menuitem item = new Menuitem("Rename");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				renameResource(el);
			};
		});
		menu.appendChild(item);
		menu.appendChild(new Menuseparator());
		item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				deleteResource(el);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildCreateItems(Menupopup menu, final TreeResourceFolder folder, final E_ResourceType resourceType) {
		Menuitem item = null;
		switch (resourceType) {
		case JAVA:
			item = new Menuitem("New Class");
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					createResource(folder, resourceType);
				};
			});
			menu.appendChild(item);
			item = new Menuitem("New Package");
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					createResourceFolder(folder, resourceType);
				};
			});
			menu.appendChild(item);
			break;
		default:
			item = new Menuitem("New " + resourceType.uiname());
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					createResource(folder, resourceType);
				};
			});
			menu.appendChild(item);
			item = new Menuitem("New Folder");
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					createResourceFolder(folder, E_ResourceType.FOLDER);
				};
			});
			menu.appendChild(item);
		}
	}
	
	private void editResourceSet(TreeResourceSetFolder folder) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("treeResourceSetFolder", folder);
		Executions.createComponents("/WEB-INF/zul/resourceset/editResourceSet.wnd.zul", null, args);
	}
	
	private void deleteResourceSet(TreeResourceSetFolder folder) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("treeResourceSetFolder", folder);
		Executions.createComponents("/WEB-INF/zul/resourceset/deleteResourceSet.wnd.zul", null, args);
	}
	
	private void createResource(TreeResourceFolder folder, E_ResourceType resourceType) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("treeResourceFolder", folder);
		args.put("resourceType", resourceType);
		Executions.createComponents("/WEB-INF/zul/resource/createResource.wnd.zul", null, args);
	}
	
	private void createResourceFolder(TreeResourceFolder folder, E_ResourceType resourceType) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("treeResourceFolder", folder);
		args.put("resourceType", resourceType);
		Executions.createComponents("/WEB-INF/zul/resource/createResourceFolder.wnd.zul", null, args);
	}
	
	private void renameResource(A_TreeElement<?, _Resource> el) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("treeElement", el);
		Executions.createComponents("/WEB-INF/zul/resource/renameResource.wnd.zul", null, args);
	}
	
	private void deleteResource(A_TreeElement<?, _Resource> el) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("treeElement", el);
		Executions.createComponents("/WEB-INF/zul/resource/deleteResource.wnd.zul", null, args);
	}
}

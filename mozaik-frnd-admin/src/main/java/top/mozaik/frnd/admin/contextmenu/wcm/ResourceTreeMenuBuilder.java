/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.contextmenu.wcm;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

import top.mozaik.frnd.admin.bean.wcm.resource.TreeLibraryResourceFolder;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.admin.vm.wcm.resource.ResourcesVM;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

public class ResourceTreeMenuBuilder {
	
	private final ResourcesVM ctrl;
	
	public ResourceTreeMenuBuilder(ResourcesVM ctrl) {
		this.ctrl = ctrl;
	}
	
	public void build(Menupopup menu, A_TreeElement value) {
		if(value instanceof TreeLibraryResourceFolder) {
			buildLibraryMenu(menu, (TreeLibraryResourceFolder)value);
		} else if (value instanceof TreeResourceFolder) {
			buildFolderMenu(menu, (TreeResourceFolder)value);
		} else if (value instanceof TreeResource) {
			buildTemplateMenu(menu, (TreeResource)value);
		}
	}
	
	private void buildLibraryMenu(Menupopup menu, final TreeLibraryResourceFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Resource");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createResource(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildFolderMenu(Menupopup menu, final TreeResourceFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Resource");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createResource(folder);
			};
		});
		menu.appendChild(item);
		menu.appendChild(new Menuseparator());
		item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteFolder(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildTemplateMenu(Menupopup menu, final TreeResource resource) {
		final Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteResource(resource);
			};
		});
		menu.appendChild(item);
	}
}

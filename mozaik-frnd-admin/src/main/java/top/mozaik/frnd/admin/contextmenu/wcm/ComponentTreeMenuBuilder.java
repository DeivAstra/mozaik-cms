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

import top.mozaik.frnd.admin.bean.wcm.component.TreeComponent;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponentFolder;
import top.mozaik.frnd.admin.bean.wcm.component.TreeLibraryComponentFolder;
import top.mozaik.frnd.admin.vm.wcm.component.ComponentsVM;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

public class ComponentTreeMenuBuilder {
	
	private final ComponentsVM ctrl;
	
	public ComponentTreeMenuBuilder(ComponentsVM ctrl) {
		this.ctrl = ctrl;
	}
	
	public void build(Menupopup menu, A_TreeElement value) {
		if(value instanceof TreeLibraryComponentFolder) {
			buildLibraryMenu(menu, (TreeLibraryComponentFolder)value);
		} else if (value instanceof TreeComponentFolder) {
			buildFolderMenu(menu, (TreeComponentFolder)value);
		} else if (value instanceof TreeComponent) {
			buildTemplateMenu(menu, (TreeComponent)value);
		}
	}
	
	private void buildLibraryMenu(Menupopup menu, final TreeLibraryComponentFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Component");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createComponent(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildFolderMenu(Menupopup menu, final TreeComponentFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Component");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createComponent(folder);
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
	
	private void buildTemplateMenu(Menupopup menu, final TreeComponent component) {
		final Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteComponent(component);
			};
		});
		menu.appendChild(item);
	}
}

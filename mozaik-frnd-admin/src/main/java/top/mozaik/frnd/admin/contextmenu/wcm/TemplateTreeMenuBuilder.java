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

import top.mozaik.frnd.admin.bean.wcm.template.TreeLibraryTemplateFolder;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplate;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplateFolder;
import top.mozaik.frnd.admin.vm.wcm.template.TemplatesVM;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

public class TemplateTreeMenuBuilder {
	
	private final TemplatesVM ctrl;
	
	public TemplateTreeMenuBuilder(TemplatesVM ctrl) {
		this.ctrl = ctrl;
	}
	
	public void build(Menupopup menu, A_TreeElement value) {
		if(value instanceof TreeLibraryTemplateFolder) {
			buildLibraryMenu(menu, (TreeLibraryTemplateFolder)value);
		} else if (value instanceof TreeTemplateFolder) {
			buildFolderMenu(menu, (TreeTemplateFolder)value);
		} else if (value instanceof TreeTemplate) {
			buildTemplateMenu(menu, (TreeTemplate)value);
		}
	}
	
	private void buildLibraryMenu(Menupopup menu, final TreeLibraryTemplateFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Template");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createTemplate(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildFolderMenu(Menupopup menu, final TreeTemplateFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Template");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createTemplate(folder);
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
	
	private void buildTemplateMenu(Menupopup menu, final TreeTemplate template) {
		final Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteTemplate(template);
			};
		});
		menu.appendChild(item);
	}
}

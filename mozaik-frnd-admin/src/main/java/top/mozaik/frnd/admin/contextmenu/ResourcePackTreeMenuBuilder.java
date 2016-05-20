/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.contextmenu;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourcePackFolder;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourceSet;
import top.mozaik.frnd.admin.vm.resourcepack.ResourcePacksVM;

public class ResourcePackTreeMenuBuilder {
	
	private final ResourcePacksVM ctrl;
	
	public ResourcePackTreeMenuBuilder(ResourcePacksVM ctrl) {
		this.ctrl = ctrl;
	}
	
	public void build(Menupopup menu, Object value) {
		menu.getChildren().clear();
		if(value instanceof TreeResourcePackFolder) {
			buildMenu(menu, (TreeResourcePackFolder) value);
		} else if (value instanceof TreeResourceSet) {
			buildMenu(menu, (TreeResourceSet) value);
		} else {
			menu.close();
		}
	}
	
	private void buildMenu(Menupopup menu, final TreeResourcePackFolder folder) {
		Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteResourcePack(folder);
			};
		});
		menu.appendChild(item);
	}
	
	
	private void buildMenu(Menupopup menu, final TreeResourceSet treeResourceSet) {
		
		final Integer resourcePackId = treeResourceSet.getParent().getParent().getValue().getId();
		if(ctrl.isResourceSetRegistered(
				resourcePackId,
				treeResourceSet.getValue())) {
			final Menuitem item = new Menuitem("Unregister");
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					ctrl.unregisterResourceSet(resourcePackId, treeResourceSet);
				};
			});
			menu.appendChild(item);
		} else {
			final Menuitem item = new Menuitem("Register");
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					ctrl.registerResourceSet(resourcePackId, treeResourceSet);
				};
			});
			menu.appendChild(item);
		}
	}
}

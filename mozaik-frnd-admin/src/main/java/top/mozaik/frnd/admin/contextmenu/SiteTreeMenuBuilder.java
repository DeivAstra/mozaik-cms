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
import org.zkoss.zul.Menuseparator;

import top.mozaik.frnd.admin.bean.site.tree.A_TreeSiteElement;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.admin.bean.site.tree.TreeSitePage;
import top.mozaik.frnd.admin.vm.site.SitesVM;

public class SiteTreeMenuBuilder {
	
	private final SitesVM ctrl;
	
	public SiteTreeMenuBuilder(SitesVM ctrl) {
		this.ctrl = ctrl;
	}
	
	public void build(Menupopup menu, Object value) {
		if(value instanceof TreeSite) {
			buildMenu(menu, (TreeSite)value);
		} else if (value instanceof TreeSitePage) {
			buildMenu(menu, (TreeSitePage)value);
		}
	}
	
	private void buildMenu(Menupopup menu, final TreeSite folder) {
		buildCreateItems(menu, folder);
		menu.appendChild(new Menuseparator());
		final Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteSite(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildMenu(Menupopup menu, final TreeSitePage page) {
		buildCreateItems(menu, page);
		menu.appendChild(new Menuseparator());
		final Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deletePage(page);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildCreateItems(Menupopup menu, final A_TreeSiteElement folder) {
		final Menuitem item = new Menuitem("New Page");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createPage(folder);
			};
		});
		menu.appendChild(item);
	}
}
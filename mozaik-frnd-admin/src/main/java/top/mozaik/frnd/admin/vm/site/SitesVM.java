/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.East;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.Site;
import top.mozaik.bknd.api.model.SitePage;
import top.mozaik.bknd.api.service.SitePageService;
import top.mozaik.bknd.api.service.SiteService;
import top.mozaik.frnd.admin.bean.site.tree.A_TreeSiteElement;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.admin.bean.site.tree.TreeSitePage;
import top.mozaik.frnd.admin.contextmenu.SiteTreeMenuBuilder;
import top.mozaik.frnd.admin.converter.SiteTreeitemImageUrlConverter;
import top.mozaik.frnd.admin.enums.E_SiteIcon;
import top.mozaik.frnd.admin.model.SiteTreeModel;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.TreeCUDEventHandler;
import top.mozaik.frnd.plus.zk.tab.TabHelper;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SitesVM extends BaseVM {
	
	private static final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	private final SiteService siteService = ServicesFacade.$().getSiteService();
	private final SitePageService sitePageService = ServicesFacade.$().getSitePageService();
	
	private I_CUDEventHandler<A_TreeSiteElement> eventHandler;
	private SiteTreeMenuBuilder treeItemContextMenuBuilder;
	
	@Wire
	Tree siteTree;
	@Wire
	Tabbox siteCenterTabbox;
	
	private TabHelper tabHelper;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		final Tab closePanelTab = (Tab)siteCenterTabbox.getTabs().getChildren().get(0);
		final EventListener<Event> hideEditPanelListener = new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				event.stopPropagation();
				hideEditPanel();
			}
		};
		closePanelTab.addEventListener(Events.ON_CLOSE, hideEditPanelListener);
		closePanelTab.addEventListener(Events.ON_CLICK, hideEditPanelListener);		
		
		tabHelper = new TabHelper(siteCenterTabbox);
		tabHelper.setOnCloseListener(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(siteCenterTabbox.getTabs().getChildren().size() > 2) return;
				hideEditPanel();
			}
		});
		eventHandler = new TreeCUDEventHandler<A_TreeSiteElement>(siteTree){
			@Override
			public void onCreate(A_TreeSiteElement e) {
				editElement(e);
				super.onCreate(e);
			}
			
			@Override
			public void onUpdate(A_TreeSiteElement e) {
				// change tab label
				final Tab tab = tabHelper.getTabByValue(e);
				if(tab != null) {
					final String title = e.toString();
					tab.setLabel(truncate(title));
					tab.setTooltiptext(title);
				}
				super.onUpdate(e);
			}
			
		};
		treeItemContextMenuBuilder = new SiteTreeMenuBuilder(this);
	}
	
	private String truncate(String v) {
		if(v.length() <= 15) return v;

		return v.substring(0, 14) +"..";
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
	
	public void deleteSite(final TreeSite treeSite) {
		Dialog.confirm("Delete", "Site '" + treeSite.getValue().getTitle() + "' will be deleted. Continue?",
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					siteService.startTransaction();
					
					siteService.delete1(treeSite.getValue());
					
					// TODO: delete childs recursively
					
					siteService.commit();
					eventHandler.onDelete(treeSite);
					Notification.showMessage("Document deleted succesfully");
				} catch (Exception e) {
					siteService.rollback();
					Dialog.error("Error occured while delete: " + treeSite.getValue(), e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	public void createPage(A_TreeSiteElement folder) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("treeSiteElement", folder);
		Executions.createComponents("/WEB-INF/zul/site/page/createPage.wnd.zul", null, args);
	}
	
	public void deletePage(final TreeSitePage treeSitePage) {
		Dialog.confirm("Delete", "Page '" + treeSitePage.getValue().getTitle() + "' will be deleted. Continue?",
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					sitePageService.startTransaction();
					
					sitePageService.delete1(treeSitePage.getValue());
					
					// TODO: delete childs recursively
					
					sitePageService.commit();
					
					eventHandler.onDelete(treeSitePage);
					Notification.showMessage("Document deleted succesfully");
				} catch (Exception e) {
					sitePageService.rollback();
					Dialog.error("Error occured while delete: " + treeSitePage.getValue(), e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	/// BINDING ///
	
	public SiteTreeModel getSiteTreeModel() throws Exception {
		return new SiteTreeModel();
	}
	
	public SiteTreeitemImageUrlConverter getTreeitemImageUrlConverter() {
		return SiteTreeitemImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void createSite() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("treeEventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/site/createSite.wnd.zul", null, args);
	}
	
	@Command
	public void editElement(@BindingParam("el") A_TreeSiteElement el) {
		//if(siteCenterTabbox.getTabs().getChildren().size() == 1) {
			showEditPanel();
		//}
		
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		if(el instanceof TreeSite) {
			final Site site = ((TreeSite)el).getValue();
			args.put("treeSiteFolder", el);
			tabHelper.openTab(E_SiteIcon.SITE.getPath(), 
					site.getTitle(), site.getDescr(), el, "/WEB-INF/zul/site/editSite.tab.zul", args);
		} else if(el instanceof TreeSitePage) {
			final SitePage page = ((TreeSitePage)el).getValue();
			args.put("treeSitePage", el);
			tabHelper.openTab(E_SiteIcon.PAGE.getPath(),
					truncate(page.getTitle()), page.getTitle(), el, "/WEB-INF/zul/site/page/editPage.tab.zul", args);
		}
	}
	
	@Command
	public void showTreeItemContextMenu(@BindingParam("event") OpenEvent event) {
		final Menupopup menu = (Menupopup)event.getTarget();
		final Component ref = event.getReference();
		
		if(ref == null) {
			menu.getChildren().clear();
			return;
		}
		
		final Treeitem treeitem = (Treeitem)ref;
		treeItemContextMenuBuilder.build(menu, treeitem.getValue());
	}
}
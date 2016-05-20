/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.resourcepack;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
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
import top.mozaik.bknd.api.enums.E_DbSettings;
import top.mozaik.bknd.api.enums.E_SettingsKey;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model.SitePage;
import top.mozaik.bknd.api.model.SitePageLayoutWidget;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.orm.annotation.Table;
import top.mozaik.bknd.api.service.QueryService;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.bknd.api.service.SettingsService;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourcePackFolder;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourceSet;
import top.mozaik.frnd.admin.contextmenu.ResourcePackTreeMenuBuilder;
import top.mozaik.frnd.admin.converter.ResourcePackTreeitemImageUrlConverter;
import top.mozaik.frnd.admin.model.ResourcePackTreeModel;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.TreeCUDEventHandler;
import top.mozaik.frnd.plus.zk.tab.TabHelper;
import top.mozaik.frnd.plus.zk.tree.I_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class ResourcePacksVM extends BaseVM {
	
	private final QueryService queryService = ServicesFacade.$().getQueryService();
	private final ResourcePackService resourcePackService = ServicesFacade.$().getResourcePackService();
	private final ResourcePackSetService resourcePackSetService = ServicesFacade.$().getResourcePackSetService();
	private final SettingsService settingsService = ServicesFacade.$().getSettingsService();
	
	private I_CUDEventHandler<I_TreeElement> eventHandler;
	private ResourcePackTreeMenuBuilder treeContextMenuBuilder;
	
	@Wire
	Tree resourcePackTree;
	@Wire
	Tabbox resourcePackCenterTabbox;
	
	private TabHelper tabHelper;

	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		final Tab closePanelTab = (Tab)resourcePackCenterTabbox.getTabs().getChildren().get(0);
		final EventListener<Event> hideEditPanelListener = new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				event.stopPropagation();
				hideEditPanel();
			}
		};
		closePanelTab.addEventListener(Events.ON_CLOSE, hideEditPanelListener);
		closePanelTab.addEventListener(Events.ON_CLICK, hideEditPanelListener);		
		
		tabHelper = new TabHelper(resourcePackCenterTabbox);
		tabHelper.setOnCloseListener(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(resourcePackCenterTabbox.getTabs().getChildren().size() > 2) return;
				hideEditPanel();
			}
		});
		eventHandler = new  TreeCUDEventHandler<I_TreeElement>(resourcePackTree){
			@Override
			public void onCreate(I_TreeElement e) {
				//editElement(e);
				super.onCreate(e);
			}
			
			@Override
			public void onUpdate(I_TreeElement e) {
				// change tab label
				final Tab tab = tabHelper.getTabByValue(e);
				if(tab != null) {
					tab.setLabel(e.toString());
				}
				super.onUpdate(e);
			}
			
		};
		treeContextMenuBuilder = new ResourcePackTreeMenuBuilder(this);
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
	
	public boolean isResourceSetRegistered(Integer resourcePackId, _ResourceSet resourceSet) {
		return resourcePackSetService.read1(
				new ResourcePackSet()
					.setResourcePackId(resourcePackId)
					.setResourceSetId(resourceSet.getId())
		) != null;
	}
	
	public void showRegisterSettings(Integer resourcePackId, _ResourceSet resourceSet) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("treeEventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/resourcepack/editRegisterSettings.wnd.zul", null, args);
	}
	
	public void registerResourceSet(Integer resourcePackId, TreeResourceSet treeResourceSet) {
		final _ResourceSet resourceSet = treeResourceSet.getValue();
		resourcePackSetService.create(
				new ResourcePackSet()
					.setResourcePackId(resourcePackId)
					.setResourceSetId(resourceSet.getId())
					.setResourceSetType(resourceSet.getType())
					.setTitle(resourceSet.getTitle())
		);
		eventHandler.onUpdate(treeResourceSet);
	}
	
	public void unregisterResourceSet(final Integer resourcePackId, final TreeResourceSet treeResourceSet) {
		final _ResourceSet resourceSet = treeResourceSet.getValue();
		Dialog.confirm("Unregister", resourceSet.getType().uiname() + " '" +resourceSet.getTitle() +
				"' will be unregistered. All binded data of the resource set will be deleted also. Continue?",
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					
					final ResourcePackSet resourcePackSet = resourcePackSetService.read1(
							new ResourcePackSet()
								.setResourcePackId(resourcePackId)
								.setResourceSetId(resourceSet.getId())
					);
					
					if(resourcePackSet == null) throw new NullPointerException();
					
					resourcePackSetService.delete1(resourcePackSet);
					
					switch (resourceSet.getType()) {
					// delete widget from layouts
					case WIDGET:
						ServicesFacade.$().getSitePageLayoutWidgetService().delete(
								new SitePageLayoutWidget()
									.setWidgetId(resourcePackSet.getId())
								, true);
						break;
					// delete theme from pages
					case THEME:
						deleteThemeFromPages(resourcePackSet.getId());
						break;
					// delete skin from layout_widget
					case SKIN:
						deleteSkinFromLayoutWidget(resourcePackSet.getId());
						break;
					}
					
					eventHandler.onUpdate(treeResourceSet);
					Notification.showMessage(
							StringUtils.capitalize(resourceSet.getType().name())  + " unregistered succesfully");
				} catch (Exception e) {
					//siteService.rollback();
					Dialog.error("Error occured while unregister: " + resourceSet, e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	
	private void deleteThemeFromPages(final int themeId) {
		ServicesFacade.$().getQueryService().getJdbcTemplate().update(
			new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					final StringBuilder sql = new StringBuilder("update ")
						.append(SitePage.class.getAnnotation(Table.class).name())
						.append(" set theme_id=NULL where theme_id=").append(themeId);
					
					return con.prepareStatement(
	            			sql.toString(), Statement.RETURN_GENERATED_KEYS);
				}
			}
		);
	}
	
	private void deleteSkinFromLayoutWidget(final int skinId) {
		ServicesFacade.$().getQueryService().getJdbcTemplate().update(
			new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					final StringBuilder sql = new StringBuilder("update ")
						.append(SitePageLayoutWidget.class.getAnnotation(Table.class).name())
						.append(" set skin_id=NULL,skin_params=NULL where skin_id=").append(skinId);
					
					return con.prepareStatement(
	            			sql.toString(), Statement.RETURN_GENERATED_KEYS);
				}
			}
		);
	}
	
	/// BINDING ///
	
	public ResourcePackTreeModel getResourcePackTreeModel() throws Exception {
		return new ResourcePackTreeModel();
	}
	
	public ResourcePackTreeitemImageUrlConverter getTreeitemImageUrlConverter() {
		return ResourcePackTreeitemImageUrlConverter.getInstance();
	}
	
	/// COMMANDS ///
	
	@Command
	@NotifyChange("resourcePackTreeModel")
	public void refresh() {
	}
	
	@Command
	public void createResourcePack() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/resourcepack/createResourcePack.wnd.zul", null, args);
	}
	
	@Command
	public void deleteResourcePack(final TreeResourcePackFolder treeResourcePack) {
		final ResourcePack bean = treeResourcePack.getValue();
		Dialog.confirm("Delete", "Resource Pack  '" + bean.getTitle() 
				+ "' will be deleted. Continue?",
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					/// DROP SCHEMA
					queryService.execute(
							"drop schema `" + E_DbSettings.DATABASE + E_DbSettings.SCHEMA_RESOURCE_PACK_PREFIX +
							bean.getAlias() +"`");
					
					/// UNREGISTER IN CMS
					resourcePackService.delete1(bean);
					
					/// DELETE RESOURCE PACK FOLDER FOR JARS
					final String rpRootFolderPath = settingsService.readValue(E_SettingsKey.ROOT_FOLDER);
					//FileUtils.deleteDirectory(new File(rpRootFolderPath+ File.separator +bean.getAlias()));
					Files.deleteIfExists(FileSystems.getDefault()
							.getPath(rpRootFolderPath+ File.separator +bean.getAlias()));
					
					eventHandler.onDelete(treeResourcePack);
					Notification.showMessage("Resource Pack deleted succesfully");
				} catch (Exception e) {
					Dialog.error("Error occured while delete: " + bean, e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	@Command
	public void importResourcePack() {
		Dialog.info("Import from archive", "Not implemented yet");
	}
	
	@Command
	public void exportResourcePack() {
		Dialog.info("Export to archive", "Not implemented yet");
	}
	
	@Command
	public void showTreeContextMenu(@BindingParam("event") OpenEvent event) {
		final Menupopup menu = (Menupopup)event.getTarget();
		final Component ref = event.getReference();
		
		if(ref == null) {
			menu.getChildren().clear();
			return;
		}
		
		final Treeitem treeitem = (Treeitem)ref;
		treeContextMenuBuilder.build(menu, treeitem.getValue());
	}
}

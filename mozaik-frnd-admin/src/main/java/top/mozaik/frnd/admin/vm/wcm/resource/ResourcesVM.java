/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.model.WcmResourceFolder;
import top.mozaik.bknd.api.service.WcmResourceFolderService;
import top.mozaik.bknd.api.service.WcmResourceService;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeLibraryResourceFolder;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.admin.contextmenu.wcm.ResourceTreeMenuBuilder;
import top.mozaik.frnd.admin.converter.wcm.ResourceTreeImageUrlConverter;
import top.mozaik.frnd.admin.enums.E_Icon;
import top.mozaik.frnd.admin.enums.E_WcmIcon;
import top.mozaik.frnd.admin.model.wcm.ResourceTreeModel;
import top.mozaik.frnd.admin.vm.wcm.WcmVM;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.TreeCUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class ResourcesVM extends BaseVM {
	
	private final WcmResourceFolderService folderService = ServicesFacade.$().getWcmResourceFolderService();
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	private I_CUDEventHandler<A_TreeElement> eventHandler;
	private ResourceTreeMenuBuilder treeItemContextMenuBuilder;
	
	@Wire
	Tree resourceTree;
	
	private WcmVM wcmCtrl;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("wcmCtrl") final WcmVM wcmCtrl) {
		this.wcmCtrl = wcmCtrl;
		eventHandler = new TreeCUDEventHandler<A_TreeElement>(resourceTree){
			@Override
			public void onCreate(A_TreeElement e) {
				if(e instanceof TreeResource) {
					editElement(e);
				}
				super.onCreate(e);
			}
			@Override
			public void onUpdate(A_TreeElement e) {
				// change tab label
				final Tab tab = wcmCtrl.getTab(e);
				if(tab != null) {
					tab.setLabel(e.toString());
				}
				super.onUpdate(e);
			}
		};
		treeItemContextMenuBuilder = new ResourceTreeMenuBuilder(this);
	}
	
	public void createFolder(A_TreeNode parentFolder) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("parentFolder", parentFolder);
		Executions.createComponents("/WEB-INF/zul/wcm/resource/createFolder.wnd.zul", null, args);
	}
	
	public void deleteFolder(final TreeResourceFolder folder) {
		Dialog.confirm("Delete", "Folder '" + folder.getValue().getTitle() + "' will be deleted. Continue?",
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					folderService.startTransaction();
					
					deleteFolderDeep(folder.getValue().getId());
					
					folderService.commit();
					eventHandler.onDelete(folder);
					Notification.showMessage("Folder deleted succesfully");
				} catch (Exception e) {
					folderService.rollback();
					Dialog.error("Error occured while delete: " + folder.getValue(), e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	private final WcmResourceFolder _deleteFolderFilter = new WcmResourceFolder();
	private final WcmResource _deleteResourceFilter = new WcmResource();
	private void deleteFolderDeep(Integer folderId) {
		folderService.delete1(new WcmResourceFolder().setId(folderId));
		
		/// TODO: CHECK DEPENDENCIES !!!
		/// WE CANT REMOVE TEMPLATES WHICH HAS DEPENDENTS (DOCUMENTS)
		resourceService.delete(_deleteResourceFilter.setFolderId(folderId), true);
		
		_deleteFolderFilter.setFolderId(folderId);
		final List<WcmResourceFolder> folders = folderService.read(_deleteFolderFilter);
		for(WcmResourceFolder f:folders) {
			deleteFolderDeep(f.getId());
		}
	}
	
	public void createResource(A_TreeNode parentFolder) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("parentFolder", parentFolder);
		Executions.createComponents("/WEB-INF/zul/wcm/resource/createResource.wnd.zul", null, args);
	}
	
	public void deleteResource(final TreeResource treeResource) {
		Dialog.confirm("Delete", "Resource '" + treeResource.getValue().getTitle() + "' will be deleted. Continue?",
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					resourceService.delete1(treeResource.getValue());
					
					eventHandler.onDelete(treeResource);
					Notification.showMessage("Resource deleted succesfully");
				} catch (Exception e) {
					Dialog.error("Error occured while delete: " + treeResource.getValue(), e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	/// BINDING ///
	
	public ResourceTreeModel getResourceTreeModel() throws Exception {
		return new ResourceTreeModel();
	}
	
	public ResourceTreeImageUrlConverter getTreeImageUrlConverter() {
		return ResourceTreeImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void drop(@BindingParam("event") DropEvent event) {
		final A_TreeElement draggedEl = ((Treeitem)event.getDragged()).getValue();
		final A_TreeElement toEl = ((Treeitem)event.getTarget()).getValue();
		if(draggedEl instanceof TreeResourceFolder){
			final WcmResourceFolder draggedFolder = ((TreeResourceFolder) draggedEl).getValue();
			if(toEl instanceof TreeLibraryResourceFolder) {
				final WcmLibrary toLibrary = ((TreeLibraryResourceFolder)toEl).getValue();
				folderService.update1(
						draggedFolder
						//	.setLibraryId()
							.setFolderId(-toLibrary.getId())
				);
			} else if(toEl instanceof TreeResourceFolder) {
				final WcmResourceFolder toFolder = ((TreeResourceFolder)toEl).getValue();
				folderService.update1(
						draggedFolder
							//.setLibraryId(toFolder.getLibraryId())
							.setFolderId(toFolder.getId())
				);
			}
		} else if(draggedEl instanceof TreeResource){
			final WcmResource draggedTemplate = ((TreeResource) draggedEl).getValue();
			if(toEl instanceof TreeLibraryResourceFolder) {
				final WcmLibrary toLibrary = ((TreeLibraryResourceFolder)toEl).getValue();
				resourceService.update1(
						draggedTemplate
							//.setLibraryId(toLibrary.getId())
							.setFolderId(-toLibrary.getId())
				);
			} else if(toEl instanceof TreeResourceFolder) {
				final WcmResourceFolder toFolder = ((TreeResourceFolder)toEl).getValue();
				resourceService.update1(
						draggedTemplate
							//.setLibraryId(toFolder.getLibraryId())
							.setFolderId(toFolder.getId())
				);
			}
		}
		
		AbstractTreeModel model = (AbstractTreeModel<?>) resourceTree.getModel();
		final int[][] openedPaths = model.getOpenPaths();
		reloadComponent();
		model = (AbstractTreeModel<?>) resourceTree.getModel();
		model.addOpenPaths(openedPaths);
	}
	
	@Command
	public void createResource() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/wcm/resource/createResource.wnd.zul", null, args);
	}
	
	@Command
	public void editElement(@BindingParam("el") A_TreeElement el) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		
		if(el instanceof TreeResourceFolder) {
			args.put("treeResourceFolder", el);
			final TreeResourceFolder folder = (TreeResourceFolder) el;
			wcmCtrl.openTab(E_Icon.FOLDER.getPath(), folder.getValue().getTitle(),
					folder.getValue().getDescr(), el, "/WEB-INF/zul/wcm/resource/editFolder.tab.zul", args);
		} else if(el instanceof TreeResource) {
			args.put("treeResource", el);
			final TreeResource resource = (TreeResource) el;
			wcmCtrl.openTab(E_WcmIcon.RESOURCE_SMALL.getPath(), resource.getValue().getTitle(),
					resource.getValue().getDescr(), el, "/WEB-INF/zul/wcm/resource/editResource.tab.zul", args);
		}
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
		treeItemContextMenuBuilder.build(menu, (A_TreeElement) treeitem.getValue());
	}
	
	@Command
	@NotifyChange("resourceTreeModel")
	public void refresh() {
	}
}

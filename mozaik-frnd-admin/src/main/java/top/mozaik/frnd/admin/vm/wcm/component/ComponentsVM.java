/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.component;

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
import top.mozaik.bknd.api.model.WcmComponent;
import top.mozaik.bknd.api.model.WcmComponentFolder;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.service.WcmComponentFolderService;
import top.mozaik.bknd.api.service.WcmComponentService;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponent;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponentFolder;
import top.mozaik.frnd.admin.bean.wcm.component.TreeLibraryComponentFolder;
import top.mozaik.frnd.admin.contextmenu.wcm.ComponentTreeMenuBuilder;
import top.mozaik.frnd.admin.converter.wcm.ComponentTreeImageUrlConverter;
import top.mozaik.frnd.admin.enums.E_Icon;
import top.mozaik.frnd.admin.enums.E_WcmIcon;
import top.mozaik.frnd.admin.model.wcm.ComponentTreeModel;
import top.mozaik.frnd.admin.vm.wcm.WcmVM;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.TreeCUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class ComponentsVM extends BaseVM {
	
	private final WcmComponentFolderService folderService = ServicesFacade.$().getWcmComponentFolderService();
	private final WcmComponentService componentService = ServicesFacade.$().getWcmComponentService();
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	private I_CUDEventHandler<A_TreeElement> eventHandler;
	private ComponentTreeMenuBuilder treeItemContextMenuBuilder;
	
	@Wire
	Tree componentTree;
	
	private WcmVM wcmCtrl;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("wcmCtrl") final WcmVM wcmCtrl) {
		this.wcmCtrl = wcmCtrl;
		eventHandler = new TreeCUDEventHandler<A_TreeElement>(componentTree){
			@Override
			public void onCreate(A_TreeElement e) {
				if(e instanceof TreeComponent) {
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
		treeItemContextMenuBuilder = new ComponentTreeMenuBuilder(this);
	}
	
	public void createFolder(A_TreeNode parentFolder) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("parentFolder", parentFolder);
		Executions.createComponents("/WEB-INF/zul/wcm/component/createFolder.wnd.zul", null, args);
	}
	
	public void deleteFolder(final TreeComponentFolder folder) {
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
	
	private final WcmComponentFolder _deleteFolderFilter = new WcmComponentFolder();
	private final WcmComponent _deleteComponentFilter = new WcmComponent();
	private void deleteFolderDeep(Integer folderId) {
		folderService.delete1(new WcmComponentFolder().setId(folderId));
		
		/// TODO: CHECK DEPENDENCIES !!!
		/// WE CANT REMOVE TEMPLATES WHICH HAS DEPENDENTS (DOCUMENTS)
		componentService.delete(_deleteComponentFilter.setFolderId(folderId), true);
		
		_deleteFolderFilter.setFolderId(folderId);
		final List<WcmComponentFolder> folders = folderService.read(_deleteFolderFilter);
		for(WcmComponentFolder f:folders) {
			deleteFolderDeep(f.getId());
		}
	}
	
	public void createComponent(A_TreeNode parentFolder) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		args.put("parentFolder", parentFolder);
		Executions.createComponents("/WEB-INF/zul/wcm/component/createComponent.wnd.zul", null, args);
	}
	
	public void deleteComponent(final TreeComponent treeComponent) {
		Dialog.confirm("Delete", "Component '" + treeComponent.getValue().getTitle() + "' will be deleted. Continue?",
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					componentService.delete1(treeComponent.getValue());
					
					eventHandler.onDelete(treeComponent);
					Notification.showMessage("Component deleted succesfully");
				} catch (Exception e) {
					Dialog.error("Error occured while delete: " + treeComponent.getValue(), e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	/// BINDING ///
	
	public ComponentTreeModel getComponentTreeModel() throws Exception {
		return new ComponentTreeModel();
	}
	
	public ComponentTreeImageUrlConverter getTreeImageUrlConverter() {
		return ComponentTreeImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void drop(@BindingParam("event") DropEvent event) {
		final A_TreeElement draggedEl = ((Treeitem)event.getDragged()).getValue();
		final A_TreeElement toEl = ((Treeitem)event.getTarget()).getValue();
		if(draggedEl instanceof TreeComponentFolder){
			final WcmComponentFolder draggedFolder = ((TreeComponentFolder) draggedEl).getValue();
			if(toEl instanceof TreeLibraryComponentFolder) {
				final WcmLibrary toLibrary = ((TreeLibraryComponentFolder)toEl).getValue();
				folderService.update1(
						draggedFolder
						//	.setLibraryId()
							.setFolderId(-toLibrary.getId())
				);
			} else if(toEl instanceof TreeComponentFolder) {
				final WcmComponentFolder toFolder = ((TreeComponentFolder)toEl).getValue();
				folderService.update1(
						draggedFolder
							//.setLibraryId(toFolder.getLibraryId())
							.setFolderId(toFolder.getId())
				);
			}
		} else if(draggedEl instanceof TreeComponent){
			final WcmComponent draggedTemplate = ((TreeComponent) draggedEl).getValue();
			if(toEl instanceof TreeLibraryComponentFolder) {
				final WcmLibrary toLibrary = ((TreeLibraryComponentFolder)toEl).getValue();
				componentService.update1(
						draggedTemplate
							//.setLibraryId(toLibrary.getId())
							.setFolderId(-toLibrary.getId())
				);
			} else if(toEl instanceof TreeComponentFolder) {
				final WcmComponentFolder toFolder = ((TreeComponentFolder)toEl).getValue();
				componentService.update1(
						draggedTemplate
							//.setLibraryId(toFolder.getLibraryId())
							.setFolderId(toFolder.getId())
				);
			}
		}
		
		AbstractTreeModel model = (AbstractTreeModel<?>) componentTree.getModel();
		final int[][] openedPaths = model.getOpenPaths();
		reloadComponent();
		model = (AbstractTreeModel<?>) componentTree.getModel();
		model.addOpenPaths(openedPaths);
	}
	
	@Command
	public void createComponent() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/wcm/component/createComponent.wnd.zul", null, args);
	}
	
	@Command
	public void editElement(@BindingParam("el") A_TreeElement el) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		
		if(el instanceof TreeComponentFolder) {
			args.put("treeComponentFolder", el);
			final TreeComponentFolder folder = (TreeComponentFolder) el;
			wcmCtrl.openTab(E_Icon.FOLDER.getPath(), folder.getValue().getTitle(),
					folder.getValue().getDescr(), el, "/WEB-INF/zul/wcm/component/editFolder.tab.zul", args);
		} else if(el instanceof TreeComponent) {
			args.put("treeComponent", el);
			final TreeComponent component = (TreeComponent) el;
			wcmCtrl.openTab(E_WcmIcon.COMPONENT_SMALL.getPath(), component.getValue().getTitle(),
					component.getValue().getDescr(), el, "/WEB-INF/zul/wcm/component/editComponent.tab.zul", args);
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
	@NotifyChange("componentTreeModel")
	public void refresh() {
	}
}

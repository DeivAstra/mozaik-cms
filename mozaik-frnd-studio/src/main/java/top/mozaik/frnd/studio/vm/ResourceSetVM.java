/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.TreeCUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.contextmenu.TreeContextMenuBuilder;
import top.mozaik.frnd.studio.converter.TreeImageUrlConverter;
import top.mozaik.frnd.studio.model.ResourceSetTreeModel;
import top.mozaik.frnd.studio.util.TreeResourceUtils;

public class ResourceSetVM extends BaseVM {
	
	private I_CUDEventHandler<?> treeEventHandler;
	private TreeContextMenuBuilder treeContextMenuBuilder;
	
	private ResourcePackVM resourcePackVM;
	private E_ResourceSetType resourceSetType;
	
	@Init
	public void init(
			@BindingParam("resourcePackController") ResourcePackVM resourcePackVM,
			@BindingParam("resourceSetType") E_ResourceSetType resourceSetType){
		 this.resourceSetType = resourceSetType; //E_ResourceSetType.getTypeByString(resourceSetType);
		 this.resourcePackVM = resourcePackVM;
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		//final Tree resourceSetTree = (Tree) getView().getFellow(resourceSetType.lname()+"Tree");
		final Tree resourceSetTree = (Tree) getView().query("tree");
		treeEventHandler = new TreeCUDEventHandler<A_TreeElement<?, _Resource>>(resourceSetTree){
			@Override
			public void onCreate(A_TreeElement<?, _Resource> el) {
				// open tab
				editResource(el);
				super.onCreate(el);
			}
			@Override
			public void onUpdate(A_TreeElement<?, _Resource> el) {
				// change tab label
				final Tab tab = resourcePackVM.getTabHelper().getTabByValue(el);
				if(tab != null) {
					tab.setLabel(el.toString());
					((I_ResourceEditor)tab.getAttribute(ResourcePackVM.RESOURCE_EDITOR_ATTR)).syncWithTree();
				}
				super.onUpdate(el);
			}
			@Override
			public void onDelete(A_TreeElement<?, _Resource> el) {
				// close tab
				final Tab tab = resourcePackVM.getTabHelper().getTabByValue(el);
				if(tab != null)tab.close();
				super.onDelete(el);
			}
		};
		treeContextMenuBuilder = new TreeContextMenuBuilder(treeEventHandler);
	}
	
	/// BINDING ///
	
	public ResourceSetTreeModel getResourceSetTreeModel() throws Exception {
		return new ResourceSetTreeModel(
				ResourcePackVM.getRespackFacade().getResourcePack().getAlias(), resourceSetType);
	}
		
	public TreeImageUrlConverter getTreeitemImageUrlConverter() {
		return TreeImageUrlConverter.getInstance();
	}
		
	/// COMMANDS ///
	
	@Command
	public void createResourceSet() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", treeEventHandler);
		args.put("resourcePack", ResourcePackVM.getRespackFacade().getResourcePack());
		args.put("resourceSetType", resourceSetType);
		Executions.createComponents("/WEB-INF/zul/resourceset/createResourceSet.wnd.zul", null, args);
	}
	
	@Command
	public void editResource(@BindingParam("resource") A_TreeElement<?, _Resource> treeElement) {
		if(!(treeElement instanceof TreeResource)) return;
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("resourcePack", ResourcePackVM.getRespackFacade().getResourcePack().getAlias());
		resourcePackVM.getTabHelper().openTab(
				treeElement.toString(), 
				TreeResourceUtils.buildPath(treeElement.getParent()),
				treeElement, "/WEB-INF/zul/resourceeditor/view.tab.zul", args);
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

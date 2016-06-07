/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.model.wcm;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmComponent;
import top.mozaik.bknd.api.model.WcmComponentFolder;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.service.WcmComponentFolderService;
import top.mozaik.bknd.api.service.WcmComponentService;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponent;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponentFolder;
import top.mozaik.frnd.admin.bean.wcm.component.TreeLibraryComponentFolder;
import top.mozaik.frnd.admin.bean.wcm.component.TreeRootComponentFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class ComponentTreeModel extends AbstractTreeModel<A_TreeElement<A_TreeNode<?,?,?>, Object>> {
	
	private final WcmComponentFolderService folderService = ServicesFacade.$().getWcmComponentFolderService();
	private final WcmComponentService componentService = ServicesFacade.$().getWcmComponentService();
	
	public ComponentTreeModel() throws Exception {
		super(buildRootElement());
	}
	
	private static A_TreeElement  buildRootElement() throws Exception {
		final TreeRootComponentFolder rootFolder = new TreeRootComponentFolder();
		
		/// LOAD LIBRARY LIST
		final List<WcmLibrary> libraries = 
				ServicesFacade.$().getWcmLibraryService().readAll();
		
		for(final WcmLibrary bean : libraries) {			
			rootFolder.addChild(new TreeLibraryComponentFolder(bean));
		}
		//Collections.sort(regions, nameFieldComparator);
		return rootFolder;
	}
	
	//private static final List<String> nullFields = new ArrayList<>();
	//static {
	//	nullFields.add("folderId");
	//}
	private void loadChildrens(final A_TreeNode treeFolder){
		if(treeFolder instanceof TreeRootComponentFolder || !treeFolder.childsIsNull()) return;
		
		final WcmComponentFolder folderFilter = new WcmComponentFolder();
		final WcmComponent componentFilter = new WcmComponent();
		if(treeFolder instanceof TreeLibraryComponentFolder) {
			final WcmLibrary library = ((TreeLibraryComponentFolder)treeFolder).getValue();
			
			//folderFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			//templateFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			folderFilter.setFolderId(-library.getId());
			componentFilter.setFolderId(-library.getId());
			
		} else if(treeFolder instanceof TreeComponentFolder) {
			final WcmComponentFolder folder = ((TreeComponentFolder) treeFolder).getValue();
			
			//folderFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			
			//templateFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			folderFilter.setFolderId(folder.getId());
			componentFilter.setFolderId(folder.getId());
		}
		folderFilter.getFilter().setSorting("title", "asc");
		componentFilter.getFilter().setSorting("title", "asc");
		/// APPEND FOLDERS
		final List<WcmComponentFolder> folders = folderService.read(folderFilter);
		for(WcmComponentFolder f:folders) {
			treeFolder.addChild(new TreeComponentFolder(f));
		}
		/// APPEND TEMPLATES
		final List<WcmComponent> docs = componentService.read(componentFilter);
		for(WcmComponent d:docs) {
			treeFolder.addChild(new TreeComponent(d));
		}
	}

	@Override
	public int getChildCount(A_TreeElement<A_TreeNode<?,?,?>, Object> parent) {
		final A_TreeNode folder = (A_TreeNode) parent;
		loadChildrens(folder);
		return folder.size();
	}

	@Override
	public A_TreeElement<A_TreeNode<?,?,?>, Object> getChild(
			A_TreeElement<A_TreeNode<?,?,?>, Object> parent, int index) {
		final A_TreeNode folder = (A_TreeNode) parent;
		return folder.get(index);
	}
	
	@Override
	public boolean isLeaf(A_TreeElement el) {
		if (el == null/*NullPointerEx while drag-n-drop*/ || el instanceof TreeComponent) return true;
		
		final A_TreeNode folder = (A_TreeNode) el;
		loadChildrens(folder);
		return folder.size() == 0;
	}
}

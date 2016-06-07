/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.model.wcm;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.model.WcmResourceFolder;
import top.mozaik.bknd.api.service.WcmResourceFolderService;
import top.mozaik.bknd.api.service.WcmResourceService;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeLibraryResourceFolder;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResource;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeResourceFolder;
import top.mozaik.frnd.admin.bean.wcm.resource.TreeRootResourceFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class ResourceTreeModel extends AbstractTreeModel<A_TreeElement<A_TreeNode<?,?,?>, Object>> {
	
	private final WcmResourceFolderService folderService = ServicesFacade.$().getWcmResourceFolderService();
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	
	public ResourceTreeModel() throws Exception {
		super(buildRootElement());
	}
	
	private static A_TreeElement  buildRootElement() throws Exception {
		final TreeRootResourceFolder rootFolder = new TreeRootResourceFolder();
		
		/// LOAD LIBRARY LIST
		final List<WcmLibrary> libraries = 
				ServicesFacade.$().getWcmLibraryService().readAll();
		
		for(final WcmLibrary bean : libraries) {			
			rootFolder.addChild(new TreeLibraryResourceFolder(bean));
		}
		//Collections.sort(regions, nameFieldComparator);
		return rootFolder;
	}
	
	//private static final List<String> nullFields = new ArrayList<>();
	//static {
	//	nullFields.add("folderId");
	//}
	private void loadChildrens(final A_TreeNode treeFolder){
		if(treeFolder instanceof TreeRootResourceFolder || !treeFolder.childsIsNull()) return;
		
		final WcmResourceFolder folderFilter = new WcmResourceFolder();
		final WcmResource resourceFilter = new WcmResource();
		if(treeFolder instanceof TreeLibraryResourceFolder) {
			final WcmLibrary library = ((TreeLibraryResourceFolder)treeFolder).getValue();
			
			//folderFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			//templateFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			folderFilter.setFolderId(-library.getId());
			resourceFilter.setFolderId(-library.getId());
			
		} else if(treeFolder instanceof TreeResourceFolder) {
			final WcmResourceFolder folder = ((TreeResourceFolder) treeFolder).getValue();
			
			//folderFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			
			//templateFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			folderFilter.setFolderId(folder.getId());
			resourceFilter.setFolderId(folder.getId());
		}
		folderFilter.getFilter().setSorting("title", "asc");
		resourceFilter.getFilter().setSorting("title", "asc");
		/// APPEND FOLDERS
		final List<WcmResourceFolder> folders = folderService.read(folderFilter);
		for(WcmResourceFolder f:folders) {
			treeFolder.addChild(new TreeResourceFolder(f));
		}
		/// APPEND TEMPLATES
		final List<WcmResource> docs = resourceService.read(resourceFilter);
		for(WcmResource d:docs) {
			treeFolder.addChild(new TreeResource(d));
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
		if (el == null/*NullPointerEx while drag-n-drop*/ || el instanceof TreeResource) return true;
		
		final A_TreeNode folder = (A_TreeNode) el;
		loadChildrens(folder);
		return folder.size() == 0;
	}
}

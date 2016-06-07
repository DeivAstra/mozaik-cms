/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.model.wcm;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.bknd.api.model.WcmDocumentFolder;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.service.WcmDocumentFolderService;
import top.mozaik.bknd.api.service.WcmDocumentService;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocument;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocumentFolder;
import top.mozaik.frnd.admin.bean.wcm.document.TreeLibraryDocumentFolder;
import top.mozaik.frnd.admin.bean.wcm.document.TreeRootDocumentFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class DocumentTreeModel extends AbstractTreeModel<A_TreeElement<A_TreeNode<?,?,?>, Object>> {
	
	private final WcmDocumentFolderService folderService = ServicesFacade.$().getWcmDocumentFolderService();
	private final WcmDocumentService documentService = ServicesFacade.$().getWcmDocumentService();
	
	public DocumentTreeModel() throws Exception {
		super(buildRootElement());
	}
	
	private static A_TreeElement  buildRootElement() throws Exception {
		final TreeRootDocumentFolder rootFolder = new TreeRootDocumentFolder();
		
		/// LOAD LIBRARY LIST
		final List<WcmLibrary> libraries = 
				ServicesFacade.$().getWcmLibraryService().readAll();
		
		for(final WcmLibrary bean : libraries) {			
			rootFolder.addChild(new TreeLibraryDocumentFolder(bean));
		}
		//Collections.sort(regions, nameFieldComparator);
		return rootFolder;
	}
	
	//private static final List<String> nullFields = new ArrayList<>();
	//static {
	//	nullFields.add("folderId");
	//}
	private void loadChildrens(final A_TreeNode treeFolder){
		if(treeFolder instanceof TreeRootDocumentFolder || !treeFolder.childsIsNull()) return;
		
		final WcmDocumentFolder folderFilter = new WcmDocumentFolder();
		final WcmDocument documentFilter = new WcmDocument();
		if(treeFolder instanceof TreeLibraryDocumentFolder) {
			final WcmLibrary library = ((TreeLibraryDocumentFolder)treeFolder).getValue();
			
			//folderFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			//templateFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			folderFilter.setFolderId(-library.getId());
			documentFilter.setFolderId(-library.getId());
			
		} else if(treeFolder instanceof TreeDocumentFolder) {
			final WcmDocumentFolder folder = ((TreeDocumentFolder) treeFolder).getValue();
			
			//folderFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			
			//templateFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			folderFilter.setFolderId(folder.getId());
			documentFilter.setFolderId(folder.getId());
		}
		folderFilter.getFilter().setSorting("title", "asc");
		documentFilter.getFilter().setSorting("title", "asc");
		/// APPEND FOLDERS
		final List<WcmDocumentFolder> folders = folderService.read(folderFilter);
		for(WcmDocumentFolder f:folders) {
			treeFolder.addChild(new TreeDocumentFolder(f));
		}
		/// APPEND TEMPLATES
		final List<WcmDocument> docs = documentService.read(documentFilter);
		for(WcmDocument d:docs) {
			treeFolder.addChild(new TreeDocument(d));
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
		if (el == null/*NullPointerEx while drag-n-drop*/ || el instanceof TreeDocument) return true;
		
		final A_TreeNode folder = (A_TreeNode) el;
		loadChildrens(folder);
		return folder.size() == 0;
	}
}

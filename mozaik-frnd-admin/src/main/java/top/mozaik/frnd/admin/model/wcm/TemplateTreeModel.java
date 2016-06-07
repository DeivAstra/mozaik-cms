/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.model.wcm;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmLibrary;
import top.mozaik.bknd.api.model.WcmTemplate;
import top.mozaik.bknd.api.model.WcmTemplateFolder;
import top.mozaik.bknd.api.service.WcmTemplateFolderService;
import top.mozaik.bknd.api.service.WcmTemplateService;
import top.mozaik.frnd.admin.bean.wcm.template.TreeLibraryTemplateFolder;
import top.mozaik.frnd.admin.bean.wcm.template.TreeRootTemplateFolder;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplate;
import top.mozaik.frnd.admin.bean.wcm.template.TreeTemplateFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;

public class TemplateTreeModel extends AbstractTreeModel<A_TreeElement<A_TreeNode<?,?,?>, Object>> {
	
	private final WcmTemplateFolderService folderService = ServicesFacade.$().getWcmTemplateFolderService();
	private final WcmTemplateService templateService = ServicesFacade.$().getWcmTemplateService();
	
	public TemplateTreeModel() throws Exception {
		super(buildRootElement());
	}
	
	private static A_TreeElement  buildRootElement() throws Exception {
		final TreeRootTemplateFolder rootFolder = new TreeRootTemplateFolder();
		
		/// LOAD LIBRARY LIST
		final List<WcmLibrary> libraries = 
				ServicesFacade.$().getWcmLibraryService().readAll();
		
		for(final WcmLibrary bean : libraries) {			
			rootFolder.addChild(new TreeLibraryTemplateFolder(bean));
		}
		//Collections.sort(regions, nameFieldComparator);
		return rootFolder;
	}
	
	//private static final List<String> nullFields = new ArrayList<>();
	//static {
	//	nullFields.add("folderId");
	//}
	private void loadChildrens(final A_TreeNode treeFolder){
		if(treeFolder instanceof TreeRootTemplateFolder || !treeFolder.childsIsNull()) return;
		
		final WcmTemplateFolder folderFilter = new WcmTemplateFolder();
		final WcmTemplate templateFilter = new WcmTemplate();
		if(treeFolder instanceof TreeLibraryTemplateFolder) {
			final WcmLibrary library = ((TreeLibraryTemplateFolder)treeFolder).getValue();
			
			//folderFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			//templateFilter.setLibraryId(library.getId())
			//	.getFilter().setNullFields(nullFields);
			
			folderFilter.setFolderId(-library.getId());
			templateFilter.setFolderId(-library.getId());
			
		} else if(treeFolder instanceof TreeTemplateFolder) {
			final WcmTemplateFolder folder = ((TreeTemplateFolder) treeFolder).getValue();
			
			//folderFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			
			//templateFilter.setLibraryId(folder.getLibraryId())
			//	.setFolderId(folder.getId());
			folderFilter.setFolderId(folder.getId());
			templateFilter.setFolderId(folder.getId());
		}
		folderFilter.getFilter().setSorting("title", "asc");
		templateFilter.getFilter().setSorting("title", "asc");
		/// APPEND FOLDERS
		final List<WcmTemplateFolder> folders = folderService.read(folderFilter);
		for(WcmTemplateFolder f:folders) {
			treeFolder.addChild(new TreeTemplateFolder(f));
		}
		/// APPEND TEMPLATES
		final List<WcmTemplate> templates = templateService.read(templateFilter);
		for(WcmTemplate t:templates) {
			treeFolder.addChild(new TreeTemplate(t));
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
		if (el == null/*NullPointerEx while drag-n-drop*/ || el instanceof TreeTemplate) return true;
		
		final A_TreeNode folder = (A_TreeNode) el;
		loadChildrens(folder);
		return folder.size() == 0;
	}
}

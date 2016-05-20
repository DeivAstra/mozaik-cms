/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.model;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.bean.tree.TreeResourceFolder;
import top.mozaik.frnd.studio.bean.tree.TreeResourceRootFolder;
import top.mozaik.frnd.studio.bean.tree.TreeResourceSetFolder;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class ResourceSetTreeModel extends AbstractTreeModel<A_TreeElement<A_TreeNode, Object>> {
	
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	public ResourceSetTreeModel(String resourcePackAlias, E_ResourceSetType type) throws Exception {
		super(buildRootElement(type));
	}
	
	private static TreeResourceRootFolder buildRootElement(E_ResourceSetType type) throws Exception {
		final TreeResourceRootFolder rootFolder = new TreeResourceRootFolder();
				
		/// LOAD RESOURCE SET LIST
		final List<_ResourceSet> resourceSets = 
				ResourcePackVM.getRespackFacade().getResourceSetService().read(
						new _ResourceSet().setType(type));
		
		final String resourcePackAlias = ResourcePackVM.getRespackFacade().getResourcePack().getAlias();
		for(final _ResourceSet wgt : resourceSets) {
			final TreeResourceSetFolder resourceSetFolder = new TreeResourceSetFolder(resourcePackAlias, wgt);
			loadResourceSetFolders(resourceSetFolder);
			rootFolder.addChild(resourceSetFolder);
		}
		//Collections.sort(regions, nameFieldComparator);
		return rootFolder;
	}
	
	private static void loadResourceSetFolders(TreeResourceSetFolder folder) throws Exception {
		final _Resource filter = new _Resource()
			.setResourceSetId(folder.getValue().getId()).setType(E_ResourceType.FOLDER);
		final List<_Resource> resources = 
				ResourcePackVM.getRespackFacade().getResourceService().read(filter);
		for(final _Resource bean : resources) {
			for(E_ResourceType type :E_ResourceType.values()) {
				if(bean.getName().equals(type.lname())) {
					folder.addChild(new TreeResourceFolder(bean));
				}
			}
		}
	}
	
	private final _Resource _childsFilter = new _Resource(); /// NOT BEAUTYFUL BUT FAST
	private void loadChildrens(final A_TreeNode treeNode){
		try {
			if(treeNode.getValue() == null || !treeNode.childsIsNull()) return;
			//Clients.log("loadChildrens: " + node.toString());
			
			if(treeNode instanceof TreeResourceSetFolder) {
				loadResourceSetFolders((TreeResourceSetFolder)treeNode);
				return;
			}
			
			_childsFilter.setParentId(((TreeResourceFolder)treeNode).getValue().getId());
			
			final List<_Resource> resources = resourceService.read(_childsFilter);
			for(final _Resource res : resources) {
				if(res.getType() == E_ResourceType.FOLDER) {
					treeNode.addChild(new TreeResourceFolder(res));
				} else {
					treeNode.addChild(new TreeResource(res));
				}
			}
			//System.out.println("loadChilds: " + node.getName() + ", count:" +((childs==null)?0:childs.size()));
		} catch (Exception e) {
			//Dialog.error("Ошибка при загрузке элементов узла", e);
			e.printStackTrace();
		}
	}
	
	@Override
	public int getChildCount(A_TreeElement<A_TreeNode, Object> parent) {
		final A_TreeNode folder = (A_TreeNode) parent;
		loadChildrens(folder);
		return folder.size();
	}

	@Override
	public A_TreeElement<A_TreeNode, Object> getChild(
			A_TreeElement<A_TreeNode, Object> parent, int index) {
		final A_TreeNode folder = (A_TreeNode) parent;
		return folder.get(index);
	}
	
	public boolean isLeaf(A_TreeElement el) {
		//Dialog.info(null, "> isLeaf:node=" + node);
		if(!(el instanceof TreeResource)) {
			final A_TreeNode node = (A_TreeNode)el;
			loadChildrens(node);
			return node.childsIsNull()?true:node.size() == 0;
		}
		return true;
	}
}

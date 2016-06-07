/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_DbSettings;
import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service._ResourceSetService;
import top.mozaik.frnd.admin.bean.resourcepack.tree.A_TreeResourcePackFolder;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourcePackFolder;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourcePackRootFolder;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourceSet;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourceSetTypeFolder;
import top.mozaik.frnd.plus.zk.tree.I_TreeElement;

public class ResourcePackTreeModel extends AbstractTreeModel<I_TreeElement> {
	
	private final Map<String, _ResourceSetService> resourceSetServiceMap = new HashMap<>(); 
	
	public ResourcePackTreeModel() throws Exception {
		super(buildRootElement());
	}
	
	private static A_TreeResourcePackFolder  buildRootElement() throws Exception {
		final TreeResourcePackRootFolder rootFolder = new TreeResourcePackRootFolder();

		/// LOAD RESOURCE PACK LIST
		final List<ResourcePack> resourcePacks = 
				ServicesFacade.$().getResourcePackService().readAll();

		for(final ResourcePack bean : resourcePacks) {
			rootFolder.addChild(new TreeResourcePackFolder(bean));
		}
		//Collections.sort(regions, nameFieldComparator);
		return rootFolder;
	}

	private _ResourceSetService getResourceSetService(String resourcePackAlias) {
		_ResourceSetService service = resourceSetServiceMap.get(resourcePackAlias);
		if(service == null) {
			service = new _ResourceSetService(
					E_DbSettings.DATABASE.toString() + E_DbSettings.SCHEMA_RESOURCE_PACK_PREFIX + resourcePackAlias);
			resourceSetServiceMap.put(resourcePackAlias, service);
			service.setJdbcTemplate(ServicesFacade.$().getJdbc());
		}
		return service;
	}
	
	private void loadChildrens(final A_TreeResourcePackFolder folder){
		if(folder instanceof TreeResourcePackRootFolder || !folder.childsIsNull()) return;
		try {
			//Clients.log("loadChildrens:" + folder);
			if(folder instanceof TreeResourcePackFolder) {
				final ResourcePack bean = ((TreeResourcePackFolder) folder).getValue();
				final _ResourceSetService service = getResourceSetService(bean.getAlias());
				
				final List<_ResourceSet> widgets = service.read(new _ResourceSet().setType(E_ResourceSetType.WIDGET));
				if(widgets.size() > 0) {
					final TreeResourceSetTypeFolder typeFolder = new TreeResourceSetTypeFolder(E_ResourceSetType.WIDGET);
					folder.addChild(typeFolder);
					for(_ResourceSet rset : widgets) {
						typeFolder.addChild(new TreeResourceSet(rset));
					}
				}
				
				final List<_ResourceSet> themes = service.read(new _ResourceSet().setType(E_ResourceSetType.THEME));
				if(themes.size() > 0) {
					final TreeResourceSetTypeFolder typeFolder = new TreeResourceSetTypeFolder(E_ResourceSetType.THEME);
					folder.addChild(typeFolder);
					for(_ResourceSet rset : themes) {
						typeFolder.addChild(new TreeResourceSet(rset));
					}
				}
				
				final List<_ResourceSet> skins = service.read(new _ResourceSet().setType(E_ResourceSetType.SKIN));
				if(skins.size() > 0) {
					final TreeResourceSetTypeFolder typeFolder = new TreeResourceSetTypeFolder(E_ResourceSetType.SKIN);
					folder.addChild(typeFolder);
					for(_ResourceSet rset : skins) {
						typeFolder.addChild(new TreeResourceSet(rset));
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getChildCount(I_TreeElement node) {
		//Clients.log("getChildCount:" + node);
		final A_TreeResourcePackFolder folder = (A_TreeResourcePackFolder)node;
		loadChildrens(folder);
		return folder.size();
	}
	
	@Override
	public I_TreeElement getChild(I_TreeElement node, int index) {
		//Clients.log("getChild:" + node + ", idx:" + index);
		final A_TreeResourcePackFolder folder = (A_TreeResourcePackFolder)node;
		return (I_TreeElement) folder.get(index);
	}

	@Override
	public boolean isLeaf(I_TreeElement node) {
		//Clients.log("isLeaf:" + node);
		if(node instanceof TreeResourceSet) return true;
		final A_TreeResourcePackFolder folder = (A_TreeResourcePackFolder)node;
		loadChildrens(folder);
		return folder.childsIsNull()?true:folder.size() == 0;
	}
}

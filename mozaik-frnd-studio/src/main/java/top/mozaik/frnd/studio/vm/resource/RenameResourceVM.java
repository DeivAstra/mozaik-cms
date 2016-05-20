/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resource;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.frnd.common.ResourceUtils;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.bean.tree.TreeResourceFolder;
import top.mozaik.frnd.studio.util.TreeResourceUtils;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class RenameResourceVM extends BaseVM {
	
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	private I_CUDEventHandler eventHandler;
	private A_TreeElement<?, _Resource> treeResource;
	private _Resource bean;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeElement") A_TreeElement<?, _Resource> treeResource) {
		this.eventHandler = eventHandler;
		this.treeResource = treeResource;
		bean = treeResource.getValue();
	}
	
	/// BINDING ///
	
	public _Resource getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void rename() {
		ZKUtils.validate(getView());
		try {
			resourceService.startTransaction();
			
			// is package renaming?
			if(treeResource instanceof TreeResourceFolder) {
				final TreeResourceFolder treeFolder = (TreeResourceFolder) treeResource;
				if(TreeResourceUtils.getTreeResourceType(treeResource) == E_ResourceType.JAVA) {
					fixPackages(treeFolder);
				}
			} else {
				setResourceName(bean, TreeResourceUtils.getTreeResourceType(treeResource));
			}
			
			resourceService.update1(bean);
			
			resourceService.commit();
			
			eventHandler.onUpdate(treeResource);
			getView().detach();
		} catch (Exception e) {
			resourceService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while rename Resource", e);
		}
	}
	
	private static void setResourceName(_Resource bean, E_ResourceType resourceFolderType){
		bean.setName(ResourceUtils.appendExtension(bean.getName(), resourceFolderType));
		switch (resourceFolderType) {
		case JAVA:
			// TODO: fix class name after rename
			
		}
	}
	
	private void fixPackages(TreeResourceFolder treeFolder) {
		
		final List<TreeResource> treeResources = TreeResourceUtils.
				findAllTreeResources(treeFolder);
		
		for(TreeResource treeResource: treeResources) {
			final _Resource bean = treeResource.getValue();
			final _ResourceData resourceData = resourceService.readWithData1(new _Resource().setId(bean.getId()));
			resourceData.setSourceData(
					ResourceUtils.fixPackageDef(
							TreeResourceUtils.buildPackagePath(treeResource.getParent()),
							new String(resourceData.getSourceData())).getBytes()
					);
			resourceService.update1(resourceData);
			eventHandler.onUpdate(treeResource); /// need call to syncWithTree in editor
		}
	}
}

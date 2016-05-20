/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resourceset;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.bknd.api.service._ResourceSetService;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.bean.tree.TreeResourceSetFolder;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class DeleteResourceSetVM extends BaseVM {
	
	private final _ResourceSetService resourceSetService = ResourcePackVM.getRespackFacade().getResourceSetService();
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	private final ResourcePackService resourcePackService = ServicesFacade.$().getResourcePackService();
	private final ResourcePackSetService resourcePackSetService = ServicesFacade.$().getResourcePackSetService();
	
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") final I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeResourceSetFolder") final TreeResourceSetFolder treeResourceSetFolder) {
		final _ResourceSet bean = treeResourceSetFolder.getValue();
		
		// check if resource set registered in admin
		
		final ResourcePack resourcePack = resourcePackService.read1(
				new ResourcePack()
					.setAlias(treeResourceSetFolder.getResourcePackAlias())
		);
		
		final ResourcePackSet resourcePackSet = resourcePackSetService.read1(
				new ResourcePackSet()
					.setResourcePackId(resourcePack.getId())
					.setResourceSetId(bean.getId())
		);
		
		final String resourceSetType = bean.getType().uiname();
		if(resourcePackSet != null) {
			Dialog.error("Can't delete registered "+ resourceSetType,
					resourceSetType +" is registered in CMS. Please contact the administrator.");
			return;
		}
		
		Dialog.confirm("Delete", resourceSetType + " '" + bean.getTitle() + 
				"' will be deleted. All resources of Resource Set also will be deleted permanently. Continue?", 
				new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					
					resourceSetService.startTransaction();
					
					/// delete ResourceSet
					resourceSetService.delete1(bean);
					
					/// delete all ResourceSet resources
					resourceService.delete(
							new _Resource().setResourceSetId(bean.getId()), true);
						
					resourceSetService.commit();
					
					eventHandler.onDelete(treeResourceSetFolder);
					Notification.showMessage("ResourceSet deleted succesfully");
				} catch (Exception e) {
					resourceSetService.rollback();
					Dialog.error("Error occured while delete ResourceSet: " + bean, e);
					e.printStackTrace();
				}
				getView().detach();
					
			}
			@Override
			public void onCancel() {
				getView().detach();
			}
		});
	}
	
	private void deleteResourceFolder(_Resource resource) {
		resourceService.delete1(resource);
		/// REMOVE CHILDRENS
		final List<_Resource> childrens = resourceService.read(
				new _Resource().setParentId(resource.getId()));
		for(_Resource child: childrens) {
			if(child.getType() == E_ResourceType.FOLDER) {
				deleteResourceFolder(child);
			} else {
				resourceService.delete1(child);
			}
		}
	}
}

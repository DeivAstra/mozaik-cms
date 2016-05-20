/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resource;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class DeleteResourceVM extends BaseVM {
	
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") final I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeElement") final A_TreeElement<?, _Resource> treeResource) {
		final _Resource bean = treeResource.getValue();
		
		
		Dialog.confirm("Delete", "'" + bean.getName() + "' will be deleted. Continue?" , new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					resourceService.startTransaction();
						
					if(bean.getType() == E_ResourceType.FOLDER) {
						deleteResourceFolder(bean);
					} else {
						resourceService.delete1(bean);
					}
						
					resourceService.commit();
						
					eventHandler.onDelete(treeResource);
					Notification.showMessage("Deleted succesfully");
				} catch (Exception e) {
					resourceService.rollback();
					Dialog.error("Error occured while delete: " + bean, e);
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

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.resourcepack;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class PublishResourceSetVM extends BaseVM {
	
	private final ResourcePackSetService resourcePackSetService = ServicesFacade.$().getResourcePackSetService();
	
	private Integer resourcePackId;
	private _ResourceSet resourceSet;
	
	private String title;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("resourcePackId") Integer resourcePackId,
			@ExecutionArgParam("resourceSet") _ResourceSet resourceSet) {
		this.resourcePackId = resourcePackId;
		this.resourceSet = resourceSet;
		this.title = resourceSet.getTitle();
	}
	
	/// BINDING ///
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	/// COMMANDS ///
	
	@Command
	public void publish() {
		ZKUtils.validate(getView());
		try {
			resourcePackSetService.create(
					new ResourcePackSet()
						.setResourcePackId(resourcePackId)
						.setResourceSetId(resourceSet.getId())
						.setResourceSetType(resourceSet.getType())
						.setTitle(title)
			);
			
			/// CREATE SCHEME
			
			/// CREATE TABLES
			
			//eventHandler.onCreate(new TreeSiteFolder(bean));
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create new Resource Pack", e);
		}
	}
}

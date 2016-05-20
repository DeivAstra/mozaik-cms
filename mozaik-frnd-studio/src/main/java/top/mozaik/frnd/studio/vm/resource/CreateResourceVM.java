/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resource;

import org.apache.commons.lang3.StringUtils;
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
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.bean.tree.TreeResourceFolder;
import top.mozaik.frnd.studio.util.TreeResourceUtils;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class CreateResourceVM extends BaseVM {
	
	protected final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	protected I_CUDEventHandler eventHandler;
	protected TreeResourceFolder treeResourceFolder;
	protected E_ResourceType resourceType;
	
	protected final _ResourceData bean = new _ResourceData();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeResourceFolder") TreeResourceFolder treeResourceFolder,
			@ExecutionArgParam("resourceType") E_ResourceType resourceType) {
		this.eventHandler = eventHandler;
		this.treeResourceFolder = treeResourceFolder;
		this.resourceType = resourceType;
	}
	
	/// BINDING ///
	
	public String getTitle() {
		if(resourceType == E_ResourceType.JAVA) {
			return "Create new Class";
		}
		return "Create new " + resourceType.uiname();
	}
	
	public String getPath() {
		return TreeResourceUtils.buildPath(treeResourceFolder);
	}
	
	public _ResourceData getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		ZKUtils.validate(getView());
		
		final StringBuilder defaultData = new StringBuilder();
		switch (resourceType) {
		case STYLE:
			bean.setName(ResourceUtils.appendExtension(bean.getName(), resourceType));
			defaultData.append("/* LESS */\n");
			break;
		case SCRIPT:
			bean.setName(ResourceUtils.appendExtension(bean.getName(), resourceType));
			defaultData.append("/* JAVASCRIPT */\n");
			break;
		case JAVA:
			bean.setName(StringUtils.capitalize(ResourceUtils.appendExtension(bean.getName(), resourceType)));
			defaultData.append("package ").append(TreeResourceUtils.buildPackagePath(treeResourceFolder)).append(";\n\n")
				.append("public class ")
				.append(ResourceUtils.removeExtension(bean.getName()))
				.append(" {\n\n\t\n\n}");
			break;
		case ZUL:
			bean.setName(ResourceUtils.appendExtension(bean.getName(), resourceType));
			defaultData.append("<zk>\n\n\t\n\n</zk>");
			break;
		case QUERY:
			bean.setName(ResourceUtils.appendExtension(bean.getName(), resourceType));
			defaultData.append("/* SQL */\n");
		}
		
		bean.setResourceSetId(treeResourceFolder.getValue().getResourceSetId());
		bean.setParentId(treeResourceFolder.getValue().getId());
		bean.setType(resourceType);
		bean.setSourceData(defaultData.toString().getBytes());
		
		/// SET PARENT FOLDER IF TARGET FOLDER IS NOT TYPE FOLDER
		if(!(treeResourceFolder.hasType())){
			final _Resource parentBean = (_Resource)treeResourceFolder.getValue();
			bean.setParentId(parentBean.getId());
		}
		
		try {
			resourceService.startTransaction();
			
			final Integer resourceId = resourceService.create(bean);
			bean.setId(resourceId);
			
			resourceService.commit();
			
			eventHandler.onCreate(new TreeResource(treeResourceFolder, bean));
			getView().detach();
		} catch (Exception e) {
			resourceService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while create Resource", e);
		}
	}
}

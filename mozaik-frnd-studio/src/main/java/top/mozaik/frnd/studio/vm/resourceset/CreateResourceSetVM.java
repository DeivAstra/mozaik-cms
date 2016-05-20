/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resourceset;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.bknd.api.service._ResourceSetService;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.bean.tree.TreeResourceSetFolder;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class CreateResourceSetVM extends BaseVM {
	
	private final _ResourceSetService resourceSetService = ResourcePackVM.getRespackFacade().getResourceSetService();
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	private I_CUDEventHandler eventHandler;
	private ResourcePack resourcePack;
	private E_ResourceSetType resourceSetType;
	
	private final _ResourceSet bean = new _ResourceSet();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("resourcePack") ResourcePack resourcePack,
			@ExecutionArgParam("resourceSetType") E_ResourceSetType resourceSetType) {
		this.eventHandler = eventHandler;
		this.resourcePack = resourcePack;
		this.resourceSetType = resourceSetType;
	}
	
	/// BINDING ///
	
	public _ResourceSet getBean() {
		return bean;
	}
	
	public String getTitle() {
		return "New " + resourceSetType.uiname();
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		ZKUtils.validate(getView());
		try {
			resourceSetService.startTransaction();
			
			bean.setType(resourceSetType);
			final Integer resourceSetId = resourceSetService.create(bean);
			
			switch (resourceSetType) {
			case WIDGET:
			{
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.STYLE.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.SCRIPT.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.MEDIA.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.JAVA.lname()));
				
				final Integer zulFolderId = resourceService.create(new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.ZUL.lname()));
				
				resourceService.create(new _ResourceData(null, resourceSetId, zulFolderId, E_ResourceType.ZUL, "index.zul", EMPTY_ZUL));
			}
				break;
			case THEME:
			{
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.STYLE.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.SCRIPT.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.MEDIA.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.JAVA.lname()));
				
				final Integer zulFolderId = resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.ZUL.lname()));
				
				resourceService.create(
						new _ResourceData(null, resourceSetId, zulFolderId, E_ResourceType.ZUL, "index.zul", THEME_INDEX_ZUL));
				resourceService.create(
						new _ResourceData(null, resourceSetId, zulFolderId, E_ResourceType.ZUL, "header.zul", EMPTY_ZUL));
				resourceService.create(
						new _ResourceData(null, resourceSetId, zulFolderId, E_ResourceType.ZUL, "footer.zul", EMPTY_ZUL));
			}
				break;
			case SKIN:
			{
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.STYLE.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.SCRIPT.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.MEDIA.lname()));
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.JAVA.lname()));
				
				final Integer zulFolderId = resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.ZUL.lname()));
				
				resourceService.create(
						new _ResourceData(null, resourceSetId, zulFolderId, E_ResourceType.ZUL, "index.zul", SKIN_INDEX_ZUL));
			}
				break;
			case LIBRARY:
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.JAVA.lname()));
			case QUERY_FOLDER:
				resourceService.create(
						new _Resource(null, resourceSetId, null, E_ResourceType.FOLDER, E_ResourceType.QUERY.lname()));
			}
			
			resourceSetService.commit();
			
			bean.setId(resourceSetId);
			eventHandler.onCreate(new TreeResourceSetFolder(resourcePack.getAlias(), bean));
			getView().detach();
		} catch (Exception e) {
			resourceSetService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while create new ResourceSet", e);
		}
	}
	
	private static final byte [] EMPTY_ZUL = "<zk>\n\n\t\n\n</zk>".getBytes();

	private static final byte [] THEME_INDEX_ZUL =
			("<?page title=\"${SITE.title} - ${PAGE.title}\"?>" +
			"\n<html xmlns=\"native\">" +
			"\n\t<body>" +
			"\n\t\t<div class=\"theme-body\" xmlns=\"zul\">" +
			"\n\t\t\t<include src=\"header.zul\"/>" + 
			"\n\t\t\t<page-layout/>" +
			"\n\t\t\t<include src=\"footer.zul\"/>" +
			"\n\t\t</div>" +
			"\n\t</body>" +
			"\n</html>").getBytes();
	
	private static final byte [] SKIN_INDEX_ZUL = 
			("<div class=\"default-skin\" xmlns=\"native\">" +
			 "\n\n\t<header class=\"title-area\">" +
			 "\n\t\t<div class=\"left\"><span>${RESOURCE_SET.name}</span></div>" +
			 "\n\t\t<div class=\"right\"></div>" +
			 "\n\t</header>" +
			 "\n\t<div class=\"widget-body\" xmlns=\"zul\">" +
			 "\n\t\t<widget/>" +
			 "\n\t</div>" +
			 "\n</div>").getBytes();
}

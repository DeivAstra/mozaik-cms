/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resource;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.studio.bean.tree.TreeResourceFolder;

public class CreateResourceFolderVM extends CreateResourceVM {
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
	}
	
	/// BINDING ///
	
	public String getTitle() {
		if(resourceType.equals(E_ResourceType.JAVA)) {
			return "Create new Package";
		}
		return super.getTitle();
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		ZKUtils.validate(getView());
		
		bean.setResourceSetId(treeResourceFolder.getValue().getResourceSetId());
		bean.setParentId(treeResourceFolder.getValue().getId());
		bean.setType(E_ResourceType.FOLDER);
		
		/// SET PARENT FOLDER IF TARGET FOLDER IS NOT TYPE FOLDER
		if(!(treeResourceFolder.hasType())){
			final _Resource parentBean = (_Resource)treeResourceFolder.getValue();
			bean.setParentId(parentBean.getId());
		}
		
		try {
			resourceService.startTransaction();
			
			final Integer resourceId = resourceService.create(bean);
			
			resourceService.commit();
			
			bean.setId(resourceId);
			eventHandler.onCreate(new TreeResourceFolder(treeResourceFolder, bean));
			getView().detach();
		} catch (Exception e) {
			resourceService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while create Resource", e);
		}
	}
}

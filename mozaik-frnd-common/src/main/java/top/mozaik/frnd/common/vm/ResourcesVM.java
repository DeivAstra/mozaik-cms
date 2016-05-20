/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common.vm;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.service.WcmResourceFolderService;
import top.mozaik.bknd.api.service.WcmResourceService;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class ResourcesVM extends BaseVM {
	
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	private final WcmResourceFolderService resourceFolderService = ServicesFacade.$().getWcmResourceFolderService();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	public List<WcmResource> getResources(Integer folderId) {
		if(folderId == null) return null;
		try {
			return resourceService.read(new WcmResource().setFolderId(folderId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

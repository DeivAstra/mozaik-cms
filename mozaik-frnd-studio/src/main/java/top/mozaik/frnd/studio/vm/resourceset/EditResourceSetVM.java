/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resourceset;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.bknd.api.service._ResourceSetService;
import top.mozaik.frnd.common.ResourceSetUtils;
import top.mozaik.frnd.common.ResourceUtils;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.bean.tree.TreeResourceSetFolder;
import top.mozaik.frnd.studio.util.TreeResourceUtils;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class EditResourceSetVM extends BaseVM {
	
	private final _ResourceSetService resourceSetService = ResourcePackVM.getRespackFacade().getResourceSetService();
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	private  I_CUDEventHandler eventHandler;
	private TreeResourceSetFolder treeResourceSetFolder;
	private _ResourceSet bean; 
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler,
			@ExecutionArgParam("treeResourceSetFolder") TreeResourceSetFolder treeResourceSetFolder ) {
		this.eventHandler = eventHandler;
		this.treeResourceSetFolder = treeResourceSetFolder;
		bean = cloneBean(treeResourceSetFolder.getValue());  // need clone because is unmodificable from cache
		treeResourceSetFolder.setValue(bean);
	}
	
	public _ResourceSet cloneBean(_ResourceSet bean) {
		return new _ResourceSet()
				.setId(bean.getId())
				.setType(bean.getType())
				.setTitle(bean.getTitle())
				.setAlias(bean.getAlias())
				.setDescr(bean.getDescr());
	}
	
	/// BINDING ///
	
	public _ResourceSet getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void save() {
		ZKUtils.validate(getView());

		try {
			resourceSetService.startTransaction();
			
			final ResourceSetUtils utils = new ResourceSetUtils(ResourcePackVM.getRespackFacade().getResourcePack(), bean.getId());
			for(_ResourceData res: utils.findAllResourceDatas(E_ResourceType.JAVA)) {
				if(res.getSourceData() == null) res.setSourceData(new byte[0]);
				final String _package = utils.buildPackagePath(bean, res);
				res.setSourceData(
						ResourceUtils.fixPackageDef(
								_package, new String(res.getSourceData())).getBytes()
						);
				resourceService.update1(res);
			}
			
			final List<TreeResource> treeResources = TreeResourceUtils.findAllTreeResources(
					treeResourceSetFolder, E_ResourceType.JAVA);
			for(TreeResource treeResource: treeResources) {
				eventHandler.onUpdate(treeResource); /// need call to syncWithTree in editor
			}
			
			resourceSetService.update1(bean);
			
			resourceSetService.commit();
			
			eventHandler.onUpdate(treeResourceSetFolder);
			getView().detach();
		} catch (Exception e) {
			resourceSetService.rollback();
			e.printStackTrace();
			Dialog.error("Error occured while rename ResourceSet", e);
		}
	}
}

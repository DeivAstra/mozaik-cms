/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourceSet;
import top.mozaik.frnd.plus.zk.tree.I_TreeElement;

import static top.mozaik.frnd.admin.enums.E_ResourceIcon.*;

public class ResourcePackTreeitemImageUrlConverter implements Converter<String, I_TreeElement, Component> {
	
	private static ResourcePackTreeitemImageUrlConverter instance;
	
	private ResourcePackTreeitemImageUrlConverter(){}
	
	private final ResourcePackSetService resourceSetService = ServicesFacade.$().getResourcePackSetService();
	
	public static ResourcePackTreeitemImageUrlConverter getInstance() {
		if(instance == null) {
			instance = new ResourcePackTreeitemImageUrlConverter();
		}
		return instance;
	}
	
	@Override
	public String coerceToUi(I_TreeElement el, Component component, BindContext ctx) {
		
		if(el instanceof TreeResourceSet) {
			final TreeResourceSet treeResourceSet = (TreeResourceSet) el;
			final Integer resourcePackId = treeResourceSet.getParent().getParent().getValue().getId();
			final _ResourceSet resourceSet = ((TreeResourceSet)el).getValue();
			final ResourcePackSet resourcePackSet = resourceSetService.read1(
					new ResourcePackSet()
						.setResourcePackId(resourcePackId)
						.setResourceSetId(resourceSet.getId())
			);
			if(resourcePackSet != null)
				return "/media/resourcepack/register.svg";
		}
		
		return null;
	}

	@Override
	public I_TreeElement coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}

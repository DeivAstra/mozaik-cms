/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common;

import java.util.HashMap;
import java.util.Map;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_DbSettings;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.bknd.api.service._ResourceSetService;

public class ResourcePackServicesFacade {
	
	//private static final String ATTR_NAME = "resourcePackServices";
	
	private final static Map<String, ResourcePackServicesFacade> facadeMap = new HashMap<>();
	
	private final ResourcePack resourcePack;
	
	private final _ResourceSetService resourceSetService;
	private final _ResourceService resourceService;
	
	/*
	public static ResourcePackServicesFacade $() {
		return (ResourcePackServicesFacade) Sessions.getCurrent().getAttribute(ATTR_NAME);
	}*/
	
	private ResourcePackServicesFacade(ResourcePack resourcePack) {
		this.resourcePack = resourcePack;
		
		final String schema = E_DbSettings.DATABASE.toString()
				+ E_DbSettings.SCHEMA_RESOURCE_PACK_PREFIX + resourcePack.getAlias();
		resourceSetService = new _ResourceSetService(schema);
		resourceSetService.setJdbcTemplate(ServicesFacade.$().getJdbc());
		resourceService = new _ResourceService(schema);
		resourceService.setJdbcTemplate(ServicesFacade.$().getJdbc());
		// fix session timeout. another schema throws exception anyway
		try {
			resourceSetService.read(new _ResourceSet());
		} catch (Exception e) {}
	}

	public static ResourcePackServicesFacade get(ResourcePack resourcePack){
		ResourcePackServicesFacade facade = facadeMap.get(resourcePack.getAlias());
		if(facade == null) {
			facade = new ResourcePackServicesFacade(resourcePack);
			facadeMap.put(resourcePack.getAlias(), facade);
		}
		return facade;
	}
	
	/*
	public static void init() {
		if(Sessions.getCurrent().getAttribute(ATTR_NAME) != null) return;
		final List<ResourcePack> resourcePacks = ServicesFacade.$().getResourcePackService().readCachedList();
		Sessions.getCurrent().setAttribute(ATTR_NAME, new ResourcePackServicesFacade(resourcePacks.get(0)));
	}
	
	public static ResourcePack getResourcePack() {
		return ((ResourcePackServicesFacade)Sessions.getCurrent().getAttribute(ATTR_NAME)).resourcePack;
	}*/

	public ResourcePack getResourcePack() {
		return resourcePack;
	}
	
	public _ResourceSetService getResourceSetService() {
		return resourceSetService;
	}
	
	public _ResourceService getResourceService() {
		return resourceService;
	}
}

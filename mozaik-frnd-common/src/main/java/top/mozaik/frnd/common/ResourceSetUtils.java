/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common;

import static top.mozaik.bknd.api.enums.E_ResourceType.FOLDER;
import static top.mozaik.bknd.api.enums.E_ResourceType.JAVA;
import static top.mozaik.frnd.common.ResourceUtils.isType;

import java.util.ArrayList;
import java.util.List;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service._ResourceService;

public class ResourceSetUtils {
	
	private final ResourcePack resourcePack;
	private final Integer resourceSetId;
	private final _ResourceService resourceService;
	
	public ResourceSetUtils(ResourcePack resourcePack, Integer resourceSetId) {
		this.resourcePack = resourcePack;
		this.resourceSetId = resourceSetId;
		
		resourceService = ResourcePackServicesFacade.get(resourcePack).getResourceService();
	}
	
	public List<_Resource> findAllResources(E_ResourceType resourceType) {
		return resourceService.read(
				new _Resource()
					.setResourceSetId(resourceSetId)
					.setType(resourceType)
		);
	}
	
	public List<_ResourceData> findAllResourceDatas(E_ResourceType resourceType) {
		return resourceService.readWithData(
				new _Resource()
				.setResourceSetId(resourceSetId)
				.setType(resourceType)
		);
	}
	
	public List<_ResourceData> findResourceDatas(int folderId) {
		return findResourceDatas(new ArrayList<_ResourceData>(), folderId);
	}
	
	private final _Resource findResourceDatas_filter = new _Resource();
	private List<_ResourceData> findResourceDatas(List<_ResourceData> list, int folderId) {
		findResourceDatas_filter.setResourceSetId(resourceSetId).setParentId(folderId);
		final List<_ResourceData> datas = resourceService.readWithData(findResourceDatas_filter);
		for(_ResourceData res : datas) {
			if(res.getType() == E_ResourceType.FOLDER) {
				findResourceDatas(list, res.getId());
			}
			list.add(res);
		}
		return list;
	}
	
	private final _Resource buildPackagePath_filter = new _Resource();
	public String buildPackagePath(_ResourceSet resourceSet, _Resource resource) {
		final StringBuilder sb = new StringBuilder();
		
		if(resource.getType() == FOLDER) {
			sb.append(resource.getName());
		}
		
		buildPackagePath_filter.setId(resource.getParentId());
		
		_Resource folder;
		while((folder = resourceService.read1(buildPackagePath_filter)) != null
				& !(folder.getType() == FOLDER) & isType(folder.getName(), JAVA)) {
			if(sb.length() > 0) { sb.insert(0, '.'); }
			sb.insert(0, folder.getName());
			buildPackagePath_filter.setId(folder.getParentId());
			
		}
		if(sb.length() > 0) { 
			sb.insert(0, '.');
		}
		return sb.insert(0, resourceSet.getAlias())
			.insert(0, '.')
			.insert(0, resourceSet.getType())
			.insert(0, '.')
			.insert(0, resourcePack.getAlias())
			.toString().toLowerCase();
	}
	
	public _Resource findResourceByPath(String path, E_ResourceType type) {
		return findResourceByPath(path, type, false);
	}
	
	public _ResourceData findResourceDataByPath(String path, E_ResourceType type) {
		return (_ResourceData)findResourceByPath(path, type, true);
	}
	
	private _Resource findResourceByPath(String path, E_ResourceType type, boolean includeData) {
		final _Resource rootFolder = resourceService.read1(
				new _Resource()
					.setResourceSetId(resourceSetId)
					.setName(type.toString())
					.setType(E_ResourceType.FOLDER)
		);
		
		final String [] names = path.split("/");
		_Resource resource  = rootFolder;
		for(String name : names) {
			resource = getResourceByName(resource.getId(), name, includeData);
		}
		return resource;
	}
	
	
	private final _Resource getResourceByName_filter = new _Resource();
	private _Resource getResourceByName(Integer folderId, String name, boolean includeData) {
		getResourceByName_filter.setResourceSetId(resourceSetId)
			.setParentId(folderId)
			.setName(name);
		final _Resource resource = (includeData)?
				resourceService.readWithData1(getResourceByName_filter)
				:resourceService.read1(getResourceByName_filter);
		if(resource == null) {
			throw new RuntimeException("Resource not found: " + getResourceByName_filter);
		}
		return resource;
	}
}
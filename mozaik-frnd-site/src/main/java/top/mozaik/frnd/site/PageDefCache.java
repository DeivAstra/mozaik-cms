/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.frnd.common.ResourcePackServicesFacade;
import top.mozaik.frnd.common.ResourceSetUtils;

public class PageDefCache {
	
	private class CacheEntity {
		String resPackName;
		Integer resSetId;
		String resName;
		String zul;
		PageDefinition pageDef;
		boolean compare(String resPackName, Integer resSetId, String resName) {
			return this.resPackName.equals(resPackName) & this.resSetId.equals(resSetId) & this.resName.equals(resName);
		}
	}
	
	public static PageDefCache instance;
	
	private final Map<String, List<CacheEntity>> map = new HashMap<>();
	
	private PageDefCache() {
	}
	
	public static PageDefCache $() {
		if(instance == null) {
			instance = new PageDefCache();
		}
		return instance;
	}
	
	public synchronized PageDefinition getByName(ResourcePack resPack, ResourcePackSet resPackSet, String resName) {
		List<CacheEntity> respackEntities = map.get(resPack.getAlias());
		if(respackEntities == null) {
			respackEntities = new ArrayList<>();
			map.put(resPack.getAlias(), respackEntities);
		} else {
			for(CacheEntity entity : respackEntities) {
				if(entity.compare(resPack.getAlias(), resPackSet.getResourceSetId(), resName)){
					System.out.println("Found cached: "+ resPack.getAlias() + "/" + resPackSet.getTitle() + "/" +resName);
					return entity.pageDef;
				}
			}
		}
		
		final CacheEntity entity = loadByName(resPack, resPackSet.getResourceSetId(), resName);
		respackEntities.add(entity);
		return entity.pageDef;
	}
	
	public synchronized PageDefinition getByPath(ResourcePack resPack, ResourcePackSet resPackSet, String resPath) {
		List<CacheEntity> respackEntities = map.get(resPack.getAlias());
		if(respackEntities == null) {
			respackEntities = new ArrayList<>();
			map.put(resPack.getAlias(), respackEntities);
		} else {
			for(CacheEntity entity : respackEntities) {
				if(entity.compare(resPack.getAlias(), resPackSet.getResourceSetId(), resPath)){
					System.out.println("Found cached: "+ resPack.getAlias() + "/" + resPackSet.getTitle() + "/" +resPath);
					return entity.pageDef;
				}
			}
		}
		
		final CacheEntity entity = loadByPath(resPack, resPackSet.getResourceSetId(), resPath);
		respackEntities.add(entity);
		return entity.pageDef;
	}
	
	private CacheEntity loadByName(ResourcePack resPack, Integer resSetId, String resName) {
		final CacheEntity entity = new CacheEntity();
		entity.resPackName = resPack.getAlias();
		entity.resSetId = resSetId;
		entity.resName = resName;
		
		final ResourcePackServicesFacade rpsFacade = ResourcePackServicesFacade.get(resPack);
		final _ResourceData resData = rpsFacade.getResourceService()
				.readWithData(new _Resource().setResourceSetId(resSetId).setName(resName)).get(0);
		
		entity.zul = new String(resData.getSourceData());
		entity.pageDef = PageDefinitions.getPageDefinitionDirectly(Executions.getCurrent().getDesktop().getWebApp(),
				null, new String(resData.getSourceData()), "zul");
		return entity;
	}
	
	private CacheEntity loadByPath(ResourcePack resPack, Integer resSetId, String resPath) {
		final CacheEntity entity = new CacheEntity();
		entity.resPackName = resPack.getAlias();
		entity.resSetId = resSetId;
		entity.resName = resPath;
		
		final ResourceSetUtils resSetUtils = new ResourceSetUtils(resPack, resSetId);
		final _ResourceData resData = resSetUtils.findResourceDataByPath(resPath, E_ResourceType.ZUL);
		
		entity.zul = new String(resData.getSourceData()); 
		entity.pageDef = PageDefinitions.getPageDefinitionDirectly(Executions.getCurrent().getDesktop().getWebApp(), null,
						new String(resData.getSourceData()), "zul");
		return entity;
	}
}

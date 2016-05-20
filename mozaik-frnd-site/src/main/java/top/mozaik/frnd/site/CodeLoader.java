/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.compiler.Compiler;
import top.mozaik.bknd.api.compiler.I_ResourcePackClassLoader;
import top.mozaik.bknd.api.compiler.ResourcePackClassLoader;
import top.mozaik.bknd.api.compiler.SourceJavaFileObject;
import top.mozaik.bknd.api.enums.E_LogComponent;
import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.enums.E_SettingsKey;
import top.mozaik.bknd.api.log.DbLogger;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.bknd.api.service._ResourceSetService;
import top.mozaik.frnd.common.ResourcePackServicesFacade;
import top.mozaik.frnd.common.ResourceUtils;

public class CodeLoader {
	
	private final DbLogger log = new DbLogger(E_LogComponent.SITE, "CodeLoader");
	
	private static CodeLoader instance;
	
	private final Map<String, ClassLoader> clMap = new HashMap<>();
	
	private synchronized void put(String resourcePackAlias, ClassLoader cl) {
		clMap.put(resourcePackAlias, cl);
	}
	
	public synchronized ClassLoader get(String resourcePackAlias) {
		return clMap.get(resourcePackAlias);
	}
	
	private final ResourcePackService resPackService = ServicesFacade.$().getResourcePackService();
	private final ResourcePackSetService resPackSetService = ServicesFacade.$().getResourcePackSetService();
	
	private String webappPath;
	
	private CodeLoader() {}
	
	public static CodeLoader getInstance() {
		if(instance == null) {
			instance = new CodeLoader();
		}
		return instance;
	}
	
	public void setWebAppPath(String webappPath) {
		this.webappPath = webappPath;
	}
	
	public synchronized void loadAll(){
		load(null);
	}
	
	public synchronized void load(String resourcePackAlias) {
		final I_ResourcePackClassLoader cl = (I_ResourcePackClassLoader) clMap.remove(resourcePackAlias);
		if(cl != null) cl.kill();
		
		final Map<ResourcePack, List<ResourcePackSet>> resPackMap = new HashMap<>();
		final List<ResourcePack> resPackList = resPackService.read(
				new ResourcePack().setAlias(resourcePackAlias));
		
		for(ResourcePack resPack : resPackList) {
			final List<ResourcePackSet> resPackSetList = resPackSetService.read(
					new ResourcePackSet().setResourcePackId(resPack.getId()));
			resPackMap.put(resPack, resPackSetList);
		}

		for(Entry<ResourcePack, List<ResourcePackSet>> entry : resPackMap.entrySet()) {
			final ResourcePackClassLoader classLoader = buildClassLoader(entry.getKey(), entry.getValue(), webappPath);
			put(entry.getKey().getAlias(), classLoader);
		}
	}
	
	private ResourcePackClassLoader buildClassLoader(ResourcePack resPack, List<ResourcePackSet> rpSets, String webappPath) {
		final List<SourceJavaFileObject> javaSourcesList = new ArrayList<>();
		
		final  ResourcePackServicesFacade rpsFacade = ResourcePackServicesFacade.get(resPack);
		final _ResourceSetService resourceSetService = rpsFacade.getResourceSetService();
		final _ResourceService resourceService = rpsFacade.getResourceService();
		
		/// JAVA SOURCES OF LIBRARIES

		final List<_ResourceSet> libraries = resourceSetService.read(
				new _ResourceSet().setType(E_ResourceSetType.LIBRARY));
		for(_ResourceSet lib : libraries) {
			javaSourcesList.addAll(loadJavaSources(resourceService, lib.getId()));
		}
		
		/// JAVA SOURCES OF WIDGETS, THEMES, SKINS
		for(ResourcePackSet rpSet : rpSets) {
			javaSourcesList.addAll(loadJavaSources(resourceService, rpSet.getResourceSetId()));
		}
		
		if(javaSourcesList.size() == 0) // return empty
			return new ResourcePackClassLoader(resPack.getAlias(), Thread.currentThread().getContextClassLoader());
		
		final String zkVersion = "8.0.1"; //to do as maven generated template
		final List<String> compilerJarPaths = new ArrayList<>();
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zk-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zkplus-"+ zkVersion +".zip");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zkbind-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zul-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zcommon-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/mozaik-bknd-api.jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/mozaik-frnd-plus.jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/mozaik-frnd-common.jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/classes");
		
		/// APPEND JARS FROM RESOURCE PACK FOLDER IF EXISTS
		final String rootFolderPath = ServicesFacade.$()
				.getSettingsService().readValue(E_SettingsKey.ROOT_FOLDER);
				
		final String resourcePackRootPath = rootFolderPath+ File.separator +resPack.getAlias();
		final List<String> clJarPaths = new ArrayList<>();
		try {
			log.info("Start search jars in "+ resourcePackRootPath  + "..");
			final File rootFolder = new File(resourcePackRootPath);
			if(rootFolder.listFiles() != null) {
				for(File file : rootFolder.listFiles()) {
					if(file.getName().endsWith(".jar")) {
						log.info("Found: " + file.getName());
						clJarPaths.add(resourcePackRootPath+ File.separator +file.getName());
					}
				}
			}
			if(rootFolder.listFiles() == null || clJarPaths.isEmpty())
				log.info("Nothing found.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Compiler.run(resPack.getAlias(), 
				Thread.currentThread().getContextClassLoader(), javaSourcesList, compilerJarPaths, clJarPaths, log);
	}
	
	private List<SourceJavaFileObject> loadJavaSources(_ResourceService resourceService, int resourceSetId) {
		final _Resource javaFolder = resourceService.read1(
				new _Resource()
					.setResourceSetId(resourceSetId)
					.setType(E_ResourceType.FOLDER)
					.setName(E_ResourceType.JAVA.lname())
		);
		final List<_ResourceData> sourceList = new ArrayList<_ResourceData>();
		loadAllResourcesWithData(resourceService, javaFolder.getId(), sourceList);
		return buildSourceJavaFiles(sourceList);
	}
	
	private final _Resource _resourceFilter = new _Resource(); 	/// NOT BEAUTIFUL BUT FAST
	private void loadAllResourcesWithData(_ResourceService resourceService, Integer folderId, List<_ResourceData> list) {
		_resourceFilter.setParentId(folderId);
		final List<_ResourceData> resList = resourceService.readWithData(_resourceFilter);
		for (_ResourceData res : resList) {
			//_resourceDataFilter.setWidgetResourceId(res.getId());
			if(res.getType() == E_ResourceType.FOLDER) {
				loadAllResourcesWithData(resourceService, res.getId(), list);
			} else {
				list.add(res);
			}
		}
	}
	
	private List<SourceJavaFileObject> buildSourceJavaFiles(List<_ResourceData> list) {
		final List<SourceJavaFileObject> sourceList = new ArrayList<>();
		for(_ResourceData resource : list) {
			sourceList.add(
					new SourceJavaFileObject(
							ResourceUtils.removeExtension(resource.getName()),
							new String(resource.getSourceData())));
		}
		return sourceList;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.widget.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Executions;

import top.mozaik.bknd.api.compiler.ClassLoaderUtils;
import top.mozaik.bknd.api.compiler.SourceJavaFileObject;
import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.bknd.api.service._ResourceSetService;
import top.mozaik.frnd.common.ResourceUtils;
import top.mozaik.frnd.common.enums.E_ResourcePackSettings;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class PreviewWidgetVM extends BaseVM {
	
	private final _ResourceSetService resourceSetService = ResourcePackVM.getRespackFacade().getResourceSetService();
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	private final Integer widgetId = Integer.valueOf(Executions.getCurrent().getParameter("wgt"));
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){
		
		// CHECK IF WIDGET EXISTS
		if(resourceSetService.read(new _ResourceSet().setId(widgetId)).size() == 0) {
			throw new IllegalArgumentException("Widget with id = " + widgetId + " not found");
		}
		
		final List<SourceJavaFileObject> javaSourcesList = new ArrayList<>();
		
		/// PREPARE JAVA SOURCES OF LIBRARIES
		final List<_ResourceSet> libraries = resourceSetService.read(
				new _ResourceSet().setType(E_ResourceSetType.LIBRARY));
		for(_ResourceSet lib : libraries) {
			javaSourcesList.addAll(loadJavaSourcesFromDB(lib.getId()));
		}
		
		/// PREPARE JAVA SOURCES OF WIDGET
		javaSourcesList.addAll(loadJavaSourcesFromDB(widgetId));
		
		final List<String> options = new ArrayList<>();
		options.add("-cp");
		final StringBuilder pathToLibs = new StringBuilder();
		pathToLibs.append("/opt/data/dev/lib/bwp-editor-compilation/zk.jar")
		.append(":/opt/data/dev/lib/bwp-editor-compilation/zkplus.zip")
		.append(":/opt/data/dev/lib/bwp-editor-compilation/zkbind.jar")
		.append(":/opt/data/dev/maven-repos/mozaik-bknd-api/mozaik-bknd-api/0.0.1-SNAPSHOT/mozaik-bknd-api-0.0.1-SNAPSHOT.jar")
		.append(":/opt/data/dev/maven-repos/mozaik-frnd-api/mozaik-frnd-api/0.0.1-SNAPSHOT/mozaik-frnd-api-0.0.1-SNAPSHOT.jar");
		options.add(pathToLibs.toString());
		
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(classLoader));
    	ClassLoaderUtils.printMemoryStatus();
    	
    	//classLoader = Compiler.run(classLoader, javaSourcesList, options);
    	Thread.currentThread().setContextClassLoader(classLoader);
		
		/// PREPARE index.zul AND CREATE COMPONENT
		
		final _Resource zulFolderFilter = new _Resource();
		zulFolderFilter.setResourceSetId(widgetId);
		zulFolderFilter.setType(E_ResourceType.FOLDER);
		zulFolderFilter.setName(E_ResourceType.ZUL.name());
		
		final _Resource zulFolder = resourceService.read1(zulFolderFilter);
		
		final List<_ResourceData> indexZuls = resourceService.readWithData(
				new _Resource()
					.setResourceSetId(widgetId)
					.setType(E_ResourceType.ZUL)
					.setName("index.zul")
					.setParentId(zulFolder.getId())
		);
		if(indexZuls.size() == 0) {
			throw new IllegalArgumentException("index.zul not found");
		}
		final _ResourceData indexZulResource = (_ResourceData) indexZuls.get(0);
		
		/*
		final WidgetResourceDataFilter indexZulDataFilter = new WidgetResourceFilter(true);
		indexZulDataFilter.setWidgetResourceId(indexZulResource.getId());
		final WidgetResourceData indexZulResourceData = widgetResourceDataService.read(indexZulDataFilter).get(0);
		*/
		
		final Map<String, Object> args = new HashMap<String, Object>();
		//args.put("widgetId", widgetId);
		Executions.getCurrent().setAttribute("widgetId", widgetId);
		final StringBuilder zulData = new StringBuilder(
				E_ResourcePackSettings.ZUL_COMPONENT_DEFINITIONS.getValue().toString());
		zulData.append(new String(indexZulResource.getSourceData()));
		Executions.createComponentsDirectly(zulData.toString(), "zul", getView(), args);
	}
	
	private List<SourceJavaFileObject> loadJavaSourcesFromDB(int resourceSetId) {
		final _Resource javaFolder = resourceService.read1(
				new _Resource()
					.setResourceSetId(resourceSetId)
					.setType(E_ResourceType.FOLDER)
					.setName(E_ResourceType.JAVA.lname())
		);
		final List<_ResourceData> sourceList = new ArrayList<_ResourceData>();
		loadAllResourcesWithData(javaFolder.getId(), sourceList);
		return buildSourceJavaFiles(sourceList);
	}
	
	/*
	private void loadAllResources(Integer folderId, List<WidgetResource> list) {
		_filter.setFolderId(folderId);
		final List<WidgetResource> resList = widgetResourceService.read(_filter);
		for (WidgetResource res : resList) {
			if(res.getType() == EWidgetResourceType.FOLDER) {
				loadAllResources(res.getId(), list);
			} else {
				list.add(res);
			}
		} 
	}*/
	
	private List<SourceJavaFileObject> buildSourceJavaFiles(List<_ResourceData> list) {
		final List<SourceJavaFileObject> sourceList = new ArrayList<>();
		for(_ResourceData resource : list) {
			sourceList.add(
					new SourceJavaFileObject(
							ResourceUtils.removeExtension(resource.getName()), new String(resource.getSourceData())));
		}
		return sourceList;
	}
	
	private final _Resource _resourceFilter = new _Resource(); 	/// NOT BEAUTIFUL BUT FAST
	//private final WidgetResourceDataFilter _resourceDataFilter = new WidgetResourceDataFilter(); 	/// NOT BEAUTIFUL BUT FAST
	private void loadAllResourcesWithData(Integer folderId, List<_ResourceData> list) {
		_resourceFilter.setParentId(folderId);
		final List<_ResourceData> resList = resourceService.readWithData(_resourceFilter);
		for (_ResourceData res : resList) {
			//_resourceDataFilter.setWidgetResourceId(res.getId());
			if(res.getType() == E_ResourceType.FOLDER) {
				loadAllResourcesWithData(res.getId(), list);
			} else {
				list.add(res);
			}
		} 
	}
}

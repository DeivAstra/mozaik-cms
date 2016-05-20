/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.build;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.impl.PollingServerPush;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.compiler.Compiler;
import top.mozaik.bknd.api.compiler.SourceJavaFileObject;
import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.enums.E_SettingsKey;
import top.mozaik.bknd.api.log.I_Logger;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.model._ResourceSet;
import top.mozaik.bknd.api.service.SettingsService;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.bknd.api.service._ResourceSetService;
import top.mozaik.frnd.common.ResourcePackServicesFacade;
import top.mozaik.frnd.common.ResourceSetUtils;
import top.mozaik.frnd.common.ResourceUtils;
import top.mozaik.frnd.plus.zk.component.AppendTextbox;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class BuildVM extends BaseVM {
	
	private final SettingsService settingsService = ServicesFacade.$().getSettingsService();
	
	private class TextboxLogger implements I_Logger {
	    
		private final StringBuilder sb = new StringBuilder();
		
		public TextboxLogger(final AppendTextbox textbox){
	        new Thread(new Runnable() {
				@Override
				public void run() {
					while(printThreadActive) {
						synchronized (sb) {
							if(sb.length() > 0) {
								try {
						    		Executions.activate(desktop);
						    		textbox.appendText(sb.toString());
						    		sb.setLength(0);
						    		sb.trimToSize();
						    		Clients.evalJavaScript("scrollDown('"+ textbox.getUuid() +"')");
						    	} catch (Exception e) {
									//e.printStackTrace();
									throw new RuntimeException(e);
								} finally {
									Executions.deactivate(desktop);
								}
							}
							try {
								sb.wait(500);
							} catch (Exception e) {
							}
						}
					}
				}
			}).start();
	    }
	    @Override
	    public void info(String msg) {
	    	synchronized (sb) {
				sb.append(msg+"\n");
			}
	    }
	    @Override
	    public void error(String msg) {
	    	info(msg);
	    }
	    @Override
	    public void error(String msg, Throwable t) {
	    	final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			pw.write(msg);
			t.printStackTrace(pw);
	    	info(sw.toString());
	    }
	}
	
	@Wire
	AppendTextbox logTextbox;
	@Wire
	Button closeBtn;
	
	ResourcePack resourcePack;
	
	private Desktop desktop;
	
	private boolean printThreadActive = true;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(@ExecutionArgParam("resourcepack") ResourcePack resourcePack) {
		this.resourcePack = resourcePack;
		
		this.desktop = Executions.getCurrent().getDesktop();
		//desktop.enableServerPush(true);
		
		((DesktopCtrl)desktop).enableServerPush(
			    new PollingServerPush(1, 1, 0));
		
		build(resourcePack, 
				Executions.getCurrent().getSession().getWebApp().getRealPath(""), 
					new TextboxLogger(logTextbox));
	}
	
	public void build(final ResourcePack resourcePack, final String webappPath, final I_Logger log) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					doBuild(resourcePack, webappPath, log);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						Executions.activate(desktop);
						closeBtn.setDisabled(false);
						Executions.deactivate(desktop);
					} catch (Exception e) {}
					desktop.enableServerPush(false);
					printThreadActive = false;
				}
			}
		}).start();
	}
	
	public void doBuild(ResourcePack resourcePack, String webappPath, I_Logger log) {
		final List<SourceJavaFileObject> javaSourcesList = new ArrayList<>();
		
		final  ResourcePackServicesFacade rpsFacade = ResourcePackServicesFacade.get(resourcePack);
		final _ResourceSetService _resourceSetService = rpsFacade.getResourceSetService();
		final _ResourceService _resourceService = rpsFacade.getResourceService();
		
		/// JAVA SOURCES OF WIDGETS, THEMES, SKINS, LIBRARIES
		{
			final List<_ResourceSet> rsets = _resourceSetService.read(
				new _ResourceSet().setType(E_ResourceSetType.WIDGET));
			for(_ResourceSet rs : rsets) {
				javaSourcesList.addAll(loadJavaSources(_resourceSetService, _resourceService, rs.getId()));
			}
		}
		
		{
			final List<_ResourceSet> rsets = _resourceSetService.read(
				new _ResourceSet().setType(E_ResourceSetType.THEME));
			for(_ResourceSet rs : rsets) {
				javaSourcesList.addAll(loadJavaSources(_resourceSetService, _resourceService, rs.getId()));
			}
		}
		
		{
			final List<_ResourceSet> rsets = _resourceSetService.read(
				new _ResourceSet().setType(E_ResourceSetType.SKIN));
			for(_ResourceSet rs : rsets) {
				javaSourcesList.addAll(loadJavaSources(_resourceSetService, _resourceService, rs.getId()));
			}
		}
		
		{
			final List<_ResourceSet> rsets = _resourceSetService.read(
				new _ResourceSet().setType(E_ResourceSetType.LIBRARY));
			for(_ResourceSet rs : rsets) {
				javaSourcesList.addAll(loadJavaSources(_resourceSetService, _resourceService, rs.getId()));
			}
		}
		
		if(javaSourcesList.size() == 0) {// nothing to compile
			log.info("Nothing to compile.");
			return;
		}
		
		final String zkVersion = "8.0.1"; //to do as maven generated template
		final String mzkVersion = "0.0.1-SNAPSHOT";
		final List<String> compilerJarPaths = new ArrayList<>();
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zk-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zkplus-"+ zkVersion +".zip");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zkbind-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zul-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/zcommon-"+ zkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/mozaik-bknd-api-local-"+ mzkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/mozaik-bknd-api-"+ mzkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/mozaik-frnd-plus-"+ mzkVersion +".jar");
		compilerJarPaths.add(webappPath+"/WEB-INF/lib/mozaik-frnd-common-"+ mzkVersion +".jar");
		//compilerJarPaths.add(webappPath+"/WEB-INF/classes");
		
		log.info("Attached jars and classes ..");
		for(String path: compilerJarPaths) {
			log.info(path);
		}
		
		/// APPEND JARS FROM RESOURCE PACK FOLDER IF EXISTS
		final String rootFolderPath = settingsService.readValue(E_SettingsKey.ROOT_FOLDER);
		
		final String resourcePackRootPath = rootFolderPath + File.separator +resourcePack.getAlias();
		final List<String> clJarPaths = new ArrayList<>();
		try {
			log.info("Search jars in '"+ resourcePackRootPath  + "' ..");
			final File rootFolder = new File(resourcePackRootPath);
			if(rootFolder.listFiles() != null) {
				for(File file : rootFolder.listFiles()) {
					if(file.getName().endsWith(".jar")){
						log.info("Found: " + file.getName());
						clJarPaths.add(resourcePackRootPath+ File.separator +file.getName());
					}
				}
			}
			if(rootFolder.listFiles() == null || clJarPaths.isEmpty())
				log.info("Nothing found.");
		} catch (Exception e) {
			log.error("Error occured while get jars from resource pack folder.",e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		final String errorMesage = Compiler.run(javaSourcesList, compilerJarPaths, clJarPaths, log);
		if(errorMesage == null) {
			log.info("Done.");
		} else {
			log.error(errorMesage);
		}
	}
	
	private List<SourceJavaFileObject> loadJavaSources(
			_ResourceSetService _resourceSetService, _ResourceService _resourceService, int resourceSetId) {
		final _Resource javaFolder = _resourceService.read1(
				new _Resource()
					.setResourceSetId(resourceSetId)
					.setType(E_ResourceType.FOLDER)
					.setName(E_ResourceType.JAVA.lname())
		);
		final List<_ResourceData> sourceList = new ArrayList<_ResourceData>();
		loadAllResourcesWithData(_resourceService, javaFolder.getId(), sourceList);
		return buildSourceJavaFiles(_resourceSetService, sourceList);
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
	
	private List<SourceJavaFileObject> buildSourceJavaFiles(_ResourceSetService _resourceSetService, List<_ResourceData> list) {
		final List<SourceJavaFileObject> sourceList = new ArrayList<>();
		for(_ResourceData _resource : list) {
			final ResourceSetUtils rsu = new ResourceSetUtils(resourcePack, _resource.getResourceSetId());
			final _ResourceSet _resourceSet = _resourceSetService.read1(new _ResourceSet().setId(_resource.getResourceSetId()));
			sourceList.add(
					new SourceJavaFileObject(
							rsu.buildPackagePath(_resourceSet, _resource) + "."+ResourceUtils.removeExtension(_resource.getName()),
							new String(_resource.getSourceData())));
		}
		return sourceList;
	}
}

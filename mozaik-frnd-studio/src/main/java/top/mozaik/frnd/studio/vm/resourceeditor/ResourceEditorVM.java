/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resourceeditor;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Tab;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.service._ResourceService;
import top.mozaik.frnd.common.ResourceUtils;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.component.CodeMirror;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.util.TreeResourceUtils;
import top.mozaik.frnd.studio.vm.I_ResourceEditor;
import top.mozaik.frnd.studio.vm.ResourcePackVM;

public class ResourceEditorVM extends BaseVM implements I_ResourceEditor {
	
	private final _ResourceService resourceService = ResourcePackVM.getRespackFacade().getResourceService();
	
	private Tab tab;
	private TreeResource treeResource;
	private _ResourceData resource;
	private E_ResourceType resourceType;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("tab") Tab tab,
			@ExecutionArgParam("resourcePack") String resourcePack,
			@ExecutionArgParam("resource") TreeResource treeResource) {
		this.tab = tab;
		tab.setAttribute(ResourcePackVM.RESOURCE_EDITOR_ATTR, this);
		this.treeResource = treeResource;
		
		final _Resource bean = treeResource.getValue();
		
		this.resource = resourceService.readWithData(new _Resource().setId(bean.getId())).get(0);
		final String data = resource.getSourceData() == null?"":new String(resource.getSourceData());
		
		resourceType = bean.getType();
		
		switch (bean.getType()) {
		case STYLE:
			open("/WEB-INF/zul/resourceeditor/styleEditor.zul", bean, data);
			return;
		case SCRIPT:
			open("/WEB-INF/zul/resourceeditor/scriptEditor.zul", bean, data);
			return;
		case MEDIA:
			open("/WEB-INF/zul/resourceeditor/mediaEditor.zul", bean, data);
			return;
		case JAVA:
			open("/WEB-INF/zul/resourceeditor/javaEditor.zul", bean, data);
			return;
		case ZUL:
			open("/WEB-INF/zul/resourceeditor/zulEditor.zul", bean, data);
			return;
		case QUERY:
			open("/WEB-INF/zul/resourceeditor/queryEditor.zul", bean, data);
			return;
		}
	}
	
	private CodeMirror cm;
	
	private void open(String zulPath, _Resource bean, String data) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("resource", bean);
		args.put("setCMCallback", new I_CallbackArg<CodeMirror>() {
			public void call(CodeMirror cm) {
				ResourceEditorVM.this.cm = cm;
			};
		});
		args.put("data", data);
		Executions.createComponents(zulPath, getView(), args);
	}
	
	/// COMMANDS ///
	
	@Command
	public void save() {
		syncWithTree();

		resource.setSourceData(cm.getValue().getBytes());
		resourceService.update1(resource);
	}
	
	public void syncWithTree() {
		switch (resourceType) {
		case JAVA:
			/// fix package path. developer can modify it manually
			final String source = ResourceUtils.fixPackageDef(
					TreeResourceUtils.buildPackagePath(treeResource.getParent()), cm.getValue());
			
			if(tab.isSelected()) {
				cm.setValue(source);
				return;
			}
			// fix: value not changes when tab is not selected
			tab.addEventListener(Events.ON_SELECT, new EventListener<SelectEvent>() {  
				@Override
				public void onEvent(SelectEvent event) throws Exception {
					cm.setValue(source);
					tab.removeEventListener(Events.ON_SELECT, this);
				}
			});
		}
	}
	
	@Override
	public void beforeClose() {
	}
}

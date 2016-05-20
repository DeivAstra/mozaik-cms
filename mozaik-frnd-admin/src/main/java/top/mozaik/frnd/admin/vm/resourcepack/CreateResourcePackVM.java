/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.resourcepack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;

import top.mozaik.bknd.api.FreeMarker;
import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_DbSettings;
import top.mozaik.bknd.api.enums.E_SettingsKey;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.service.QueryService;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.SettingsService;
import top.mozaik.frnd.admin.App;
import top.mozaik.frnd.admin.bean.resourcepack.tree.TreeResourcePackFolder;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import freemarker.template.Template;

public class CreateResourcePackVM extends BaseVM {
	
	private final QueryService queryService = ServicesFacade.$().getQueryService();
	private final ResourcePackService resourcePackService = ServicesFacade.$().getResourcePackService();
	private final SettingsService settingsService = ServicesFacade.$().getSettingsService();
	
	private I_CUDEventHandler eventHandler;
	
	private final ResourcePack bean = new ResourcePack();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	/// BINDING ///
	
	public ResourcePack getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void validateAndCreate() {
		ZKUtils.validate(getView());
		getBinder().postCommand("create", null);
	}
	
	@Command
	public void create() {
		
		try {
			/// CHECK IF SCHEMA IS NOT EXISTS
			final StringBuilder query = new StringBuilder(
					"select count(*) from information_schema.schemata where schema_name='")
				.append(E_DbSettings.DATABASE)
				.append(E_DbSettings.SCHEMA_RESOURCE_PACK_PREFIX)
				.append(bean.getAlias()).append('\'');
			final boolean schemaExists = queryService.getJdbcTemplate().queryForObject(query.toString(), Integer.class) > 0;
			if(schemaExists) {
				Dialog.error("Error create Resource Pack", 
						"Resource Pack with alias = '"+bean.getAlias() +"' already exists.");
				return;
			}
			
			/// CREATE SCHEMA
			final String schema = E_DbSettings.DATABASE.toString() +
					E_DbSettings.SCHEMA_RESOURCE_PACK_PREFIX + bean.getAlias();
			queryService.getJdbcTemplate().execute("create schema `"+ schema +"`");
			
			/// CREATE TABLES
			final Map params = new HashMap<>();
			params.put("schema", schema);
			createTable("sql/install/resourcepack/$_resource_sets.ftl", params);
			createTable("sql/install/resourcepack/$_resources.ftl", params);
			
			/// REGISTER IN CMS
			bean.setId(resourcePackService.create(bean));
			
			/// CREATE RESOURCE PACK FOLDER FOR JARS
			final String rpRootFolderPath = settingsService.readValue(E_SettingsKey.ROOT_FOLDER);
			new File(rpRootFolderPath+ File.separator +bean.getAlias()).mkdirs();
			
			eventHandler.onCreate(new TreeResourcePackFolder(bean));
			Notification.showMessage("Resource Pack succesfully created");
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create new Resource Pack", e);
		}
	}
	
	private void createTable(String templateName, Map<String,String> params) throws Exception {
		final Template template = FreeMarker.getConfiguration().getTemplate(templateName);
        final StringWriter stringWriter = new StringWriter();

        final BufferedWriter writer = new BufferedWriter(stringWriter);
        template.process(params, writer);
        writer.flush();
        
        final String sql = stringWriter.getBuffer().toString();
        queryService.getJdbcTemplate().execute(sql);
	}
}

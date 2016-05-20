/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.vm.step;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.xml.bind.DatatypeConverter;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.impl.PollingServerPush;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_DbSettings;
import top.mozaik.bknd.api.enums.E_SettingsKey;
import top.mozaik.bknd.api.enums.E_UserRole;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.Settings;
import top.mozaik.bknd.api.utils.MDUtils;
import top.mozaik.bknd.api.utils.SqlUtils;
import top.mozaik.frnd.installer.bean.AdminUserBean;
import top.mozaik.frnd.installer.bean.StepsBean;
import top.mozaik.frnd.installer.vm.I_Installer;
import top.mozaik.frnd.installer.vm.I_Step;
import top.mozaik.frnd.plus.zk.log.Logger;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class InstallVM extends BaseVM implements I_Step {
	
	private static final Logger log = new Logger(InstallVM.class, true);
	
	@Wire
	Vlayout infoVlayout;
	
	private StepsBean bean;
	private JdbcTemplate jdbc;
	
	private Desktop desktop;
	private Button nextBtn;
	
	final ServletContext ctx = Sessions.getCurrent().getWebApp().getServletContext();
	final File templatesFolder = new File(
			Sessions.getCurrent().getWebApp().getRealPath("") + "/WEB-INF/classes/templates");
	
	@Init
	public void init(@BindingParam("installer") I_Installer installer,
					 @BindingParam("bean") StepsBean bean) {
		installer.setStep(this);
		this.bean = bean;
		this.jdbc = bean.getDbBean().getJdbc();
		this.desktop = Executions.getCurrent().getDesktop();
		//desktop.enableServerPush(true);
		
		((DesktopCtrl)desktop).enableServerPush(
			    new PollingServerPush(1, 1, 0));
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
	}
	
	private String toHex(String val) {
		return DatatypeConverter.printHexBinary(val.getBytes());
	}
	
	
	private final Configuration config = new Configuration();
	{
		try {
			config.setDirectoryForTemplateLoading(templatesFolder);
		} catch(Throwable t) { log.error("Error while config.setDirectoryForTemplateLoading", t); }
	}
	private void execute(String templatePath, Map<String,String> params) throws Exception {
		
		final Template template = config.getTemplate(templatePath);
        
		final StringWriter stringWriter = new StringWriter();

        final BufferedWriter writer = new BufferedWriter(stringWriter);
        template.process(params, writer);
        writer.flush();
        
        final String sql = stringWriter.getBuffer().toString();
        //jdbc.execute(sql);
        //final ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        //rdp.addScript(new ClassPathResource(templatePath));
        //rdp.populate(jdbc.getDataSource().getConnection());
        execute(sql);
	}
	
	@Override
	public void setNextBtn(Button btn) {
		btn.setDisabled(true);
		nextBtn = btn;
	}
	
	@Override
	public boolean onBeforeNext() {
		return true;
	}
	
	private void log(String msg, int progressPercent) {
		appendInfoLabel(new Label(msg), progressPercent);
	}
	
	private void logError(String msg) {
		final Label label = new Label(msg);
		label.setStyle("color:red");
		appendInfoLabel(new Label(msg), null);
	}
	
	private void appendInfoLabel(Label label, Integer progressPercent) {
		try {
			Executions.activate(desktop);
			if(progressPercent != null) {
				Clients.evalJavaScript("setLogoWidth('" +progressPercent+ "%')");
			}
			infoVlayout.appendChild(label);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Executions.deactivate(desktop);
		}
	}
	
	private void execute(String sql) throws Exception {
		log.debug(sql);
		SqlUtils.executeScript(jdbc, sql);
	}
	private void execute2(String query) throws Exception {
		log.debug(query);
		
		query = query.trim();
		if(query.charAt(query.length()-1) != ';') {
			query = query + ";";
		}
		
		final BufferedReader br = new BufferedReader(new StringReader(query));
		String delimiter = ";";
		String line;
		final StringBuilder sb = new StringBuilder();
		while ((line = br.readLine().trim()) != null) {
			//Skip comments and empty lines
			if(line.length() == 0) continue;
			
			if(line.charAt(0) == '-' || line.charAt(0) == '#') 
				continue;
			
			if(line.toLowerCase().startsWith("delimiter")) {
				final String[] a = line.split(" ");
				if(a.length == 2) {
					delimiter = a[1].trim();
					continue;
				}
			}
			
			sb.append(" ").append(line);
			
			if(line.endsWith(delimiter)) {
				sb.delete(sb.length()-1 - delimiter.length(), sb.length()-1);
				final String sql = sb.toString();
				if(sql.toLowerCase().startsWith("create procedure") 
						|| sql.toLowerCase().startsWith("create function")) {
					jdbc.execute(new StatementCallback() {
						@Override
						public Object doInStatement(Statement stmt) throws SQLException, DataAccessException {
							stmt.executeUpdate(sql);
							return null;
						}
					});
				} else {
					jdbc.execute(sql);
				}
				sb.setLength(0);
				sb.trimToSize();
			}
			/*
			if(sb.charAt(sb.length()-1) == ';'){
				sb.deleteCharAt(sb.length()-1);
				jdbc.execute(sb.toString());
				sb.setLength(0);
				sb.trimToSize();
			}
			*/
		}
	}
	
	/// COMMANDS ///
	
	@Command
	public void install() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					doInstall();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					desktop.enableServerPush(false);
				}
			}
		}).start();
	}
	
	private void doInstall() {
		try {
			/// create schema
			final String schema = bean.getDbBean().getDbName();
			execute("create schema `"+ schema +"`");
			
			log("Database '" + schema + "' was created.", 15);
		
			/// create tables
			final Map<String,String> params = new HashMap<>();
			params.put("schema", schema);
			
			execute("USE `" + schema+"`");
			
			execute("cms/settings.ftl", params);
			
			execute("cms/users.ftl", params);
		
			execute("cms/e_resource_set_types.ftl", params);
			execute("cms/e_resource_types.ftl", params);
		
			execute("cms/resource_packs.ftl", params);
			execute("cms/resource_pack_sets.ftl", params);
		
			execute("cms/sites.ftl", params);
			execute("cms/site_pages.ftl", params);
			execute("cms/site_page_layout_widgets.ftl", params);
			execute("cms/site_page_layouts.ftl", params);
		
			execute("cms/wcm_document_folders.ftl", params);
			execute("cms/wcm_documents.ftl", params);
			execute("cms/wcm_document_fields.ftl", params);
			execute("cms/wcm_document_refs.ftl", params);
		
			execute("cms/wcm_template_folders.ftl", params);
			execute("cms/wcm_templates.ftl", params);
			execute("cms/wcm_template_fields.ftl", params);
		
			execute("cms/wcm_component_folders.ftl", params);
			execute("cms/wcm_components.ftl", params);
		
			execute("cms/wcm_resource_folders.ftl", params);
			execute("cms/wcm_resources.ftl", params);
			execute("cms/wcm_resource_data.ftl", params);
		
			execute("cms/wcm_content_types.ftl", params);
		
			execute("cms/wcm_libraries.ftl", params);
			
			execute("cms/logs.ftl", params);
			
			log("Tables were created.", 40);
		
			/// create db user
			final String dbLogin = bean.getDbUserBean().getLogin() ;
			execute("CREATE USER '"+ dbLogin
					+"'@'localhost' IDENTIFIED BY '"+ bean.getDbUserBean().getPassword() +"'");
			
			
			// grants db user to create resource packs
			execute("GRANT CREATE ON *.* TO '"+  dbLogin +"'@'localhost'");
			execute("GRANT DROP ON *.* TO '"+  dbLogin +"'@'localhost'");
		
			execute("GRANT SELECT, INSERT, UPDATE, DELETE ON "+ schema  +".* TO '"+  dbLogin +"'@'localhost'");
			execute("GRANT SELECT, INSERT, UPDATE, DELETE ON `"+ schema  +"$rp$%`.* TO '"+  dbLogin +"'@'localhost'");
		
			log("Database user '"+ dbLogin  +"' was created.", 55);
			
			// create admin user
			final AdminUserBean pub = bean.getAdminUserBean();
			final StringBuilder createUserSql = new StringBuilder("INSERT INTO `users` (")
				.append("`login`,")
				.append("`password`,")
				.append("`role`,")
				.append("`active`")
				.append(") VALUES (")
				.append("unhex('").append(toHex(pub.getLogin())).append("')").append(',')
				.append("'").append(MDUtils.toMD5(pub.getPassword())).append("'").append(',')
				.append("'").append(E_UserRole.ADMIN).append("'").append(',')
				.append(1)
				.append(");");
			execute(createUserSql.toString());
			
			log("Admin user '"+ pub.getLogin()  +"' was created.", 60);
			
			// create resource pack root folder
			new File(bean.getRpRootFolderBean().getPath()).mkdirs();
						
			log("Resource pack root folder was created.", 65);
			
			final String baseRpAlias = "top.mozaik.base";
			// create base resource pack folder
			new File(bean.getRpRootFolderBean().getPath()+ File.separatorChar + baseRpAlias).mkdirs();
			
			// install base resource pack
			params.clear();
			params.put("schema", schema + E_DbSettings.SCHEMA_RESOURCE_PACK_PREFIX + baseRpAlias);
			params.put("base_path", templatesFolder.getAbsolutePath() +"/resourcepack/base");
			execute("resourcepack/base/install.ftl", params);
			
			log("Base resource pack installed.", 70);
			
			// register base resource pack in cms
			execute("USE `" +schema+ "`");
			ServicesFacade.$().setJdbc(jdbc);
			ServicesFacade.$().getResourcePackService().create(
					new ResourcePack()
						.setAlias(baseRpAlias)
						.setTitle("Mozaik Base")
						.setIsPublished(true)
					);
			
			log("Base resource pack registered.", 80);
			
			// create cms settings
			for(E_SettingsKey sk : E_SettingsKey.values()) {
				String value;
				if(sk == E_SettingsKey.ROOT_FOLDER) {
					value = bean.getRpRootFolderBean().getPath();
				} else {
					value = sk.getDefaultValue();
				}
				ServicesFacade.$().getSettingsService().create(
						new Settings()
							.setKey(sk)
							.setValue(value)
							.setDescr(sk.getDescr())
				);
			}
			
			log("CMS settings were added.", 90);
			
			if(bean.getHideInstallerAfterFinish()) {
				final String webappPath = ctx.getRealPath("/");
				boolean res = new File(webappPath + "index.zul")
					.renameTo(new File(webappPath + UUID.randomUUID() +"-index.zul"));
				//boolean res2 = new File(webappPath + "/zul")
				//.renameTo(new File(webappPath + UUID.randomUUID() +"-zul"));
				if(res) {
					log("Installer was hide.", 95);
				} else {
					logError("Error while hide installer. Try it do manually.");
				}
			}
			
			log("Done.", 100);
			
			Executions.activate(desktop);
			nextBtn.setDisabled(false);
			Executions.deactivate(desktop);
		} catch (Exception e) {
			try {
				Executions.activate(desktop); // NEED FOR SHOW DIALOG
				log.error("Error occured while installing CMS", e);
				Executions.deactivate(desktop);
			} catch(Throwable t) {}
			//Dialog.error("Error occured while installing CMS", e);
		}
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import top.mozaik.frnd.plus.callback.I_Callback;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.init.DbInit;

public class SetupVM extends BaseVM {
	
	private static final String PROPS_FILE_NAME = "mozaik.properties";
	
	private final Execution ex = Executions.getCurrent();
	private final ServletContext ctx = ex.getSession().getWebApp().getServletContext();
	
	private SettingsBean bean = new SettingsBean();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() throws Exception {
		final InputStream is = ctx.getResourceAsStream("WEB-INF/" + PROPS_FILE_NAME);
		if(is == null) return;
		final Properties props = new Properties();
		props.load(is);
		bean.setHost(props.getProperty("jdbc.host"));
		bean.setPort(Integer.parseInt(props.getProperty("jdbc.port")));
		bean.setDbName(props.getProperty("jdbc.database"));
		bean.setUsername(props.getProperty("jdbc.username"));
	}
	
	private JdbcTemplate connect() {
		final BasicDataSource ds = new BasicDataSource();
		
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://" + bean.getHost() 
				+":"+bean.getPort()+"/"+bean.getDbName()
				+"?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8");
		ds.setUsername(bean.getUsername());
		ds.setPassword(bean.getPassword());
		ds.setValidationQuery("select 1");
		
		final JdbcTemplate jdbc = new JdbcTemplate(ds);
		jdbc.queryForObject("select 1", Integer.class);
		return jdbc;
	}
	
	/// BINDING ///
	
	public SettingsBean getBean() {
		return bean;
	}
	
	/// COMMANDS ///
	
	@Command
	public void validateAndTestConnection() {
		ZKUtils.validate(getView());
		bean.setJdbc(connect());
		Notification.showMessage("Connection parameters are correct");
		getBinder().notifyChange(bean, "jdbc");
	}
	
	@Command
	public void save() {
		final Map<String, Object> args = new HashMap<>();
		args.put("jdbc", bean.getJdbc());
		args.put("callback", new I_Callback() {
			@Override
			public void call() {
				final String propsFilePath = ctx.getRealPath("/")+ "WEB-INF/" + PROPS_FILE_NAME;
				final File file = new File(propsFilePath);
				BufferedWriter writer = null;
				try {
					file.delete();
					file.createNewFile();
					
					writer = new BufferedWriter(new FileWriter(file));
					writer.write("jdbc.host="+bean.getHost());
					writer.newLine();
					writer.write("jdbc.port="+bean.getPort());
					writer.newLine();
					writer.write("jdbc.database="+bean.getDbName());
					writer.newLine();
					writer.write("jdbc.username="+bean.getUsername());
					writer.newLine();
					writer.write("jdbc.password="+bean.getPassword());
					writer.newLine();
					writer.close();
					Notification.showMessage("Properties file writen successfully");
					
					Sessions.getCurrent().removeAttribute(DbInit.DB_ATTR_NAME);
				} catch (Exception e) {
					Dialog.error("Error occured while creating properties file", e);
				} finally {
					try { if(writer != null) writer.close();
					} catch (Exception e) {}
				}
			}
		});
		Executions.createComponents("/WEB-INF/zul/setuplogin.zul", null, args);
	}
	
	public static class SettingsBean {
		private String host = "127.0.0.1";
		private Integer port = 3306;
		private String username = "mozaik";
		private String password;
		private String dbName = "mozaik";
		private JdbcTemplate jdbc;
		
		public SettingsBean() {
		}
		
		public String getHost() {
			return host;
		}
		
		public void setHost(String host) {
			this.host = host;
		}
		
		public Integer getPort() {
			return port;
		}
		
		public void setPort(Integer port) {
			this.port = port;
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
		
		public String getDbName() {
			return dbName;
		}
		
		public void setDbName(String dbName) {
			this.dbName = dbName;
		}
		
		public JdbcTemplate getJdbc() {
			return jdbc;
		}
		
		public void setJdbc(JdbcTemplate jdbc) {
			this.jdbc = jdbc;
		}
	}
}
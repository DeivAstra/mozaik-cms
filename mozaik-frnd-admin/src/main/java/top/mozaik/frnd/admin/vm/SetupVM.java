/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm;

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

import top.mozaik.frnd.common.bean.SetupBean;
import top.mozaik.frnd.common.init.DbInit;
import top.mozaik.frnd.plus.callback.I_Callback;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SetupVM extends BaseVM {
	
	private final Execution ex = Executions.getCurrent();
	private final ServletContext ctx = ex.getSession().getWebApp().getServletContext();
	
	private final SetupBean bean = new SetupBean();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() throws Exception {
		final InputStream is = ctx.getResourceAsStream("WEB-INF/" + SetupBean.SETUP_FILE_NAME);
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
	
	public SetupBean getBean() {
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
				final String propsFilePath = ctx.getRealPath("/")+ "WEB-INF/" + SetupBean.SETUP_FILE_NAME;
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
					Notification.showMessage("Setup properties were saved successfully");
					
					Sessions.getCurrent().removeAttribute(DbInit.DB_ATTR_NAME);
				} catch (Exception e) {
					Dialog.error("Error occured while creating properties file", e);
				} finally {
					try { if(writer != null) writer.close();
					} catch (Exception e) {}
				}
			}
		});
		Executions.createComponents("/WEB-INF/zul/setuplogin.wnd.zul", null, args);
	}
}
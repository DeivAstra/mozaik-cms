/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site.init;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_DbSettings;
import top.mozaik.bknd.api.orm.service.A_CRUDService;
import top.mozaik.bknd.api.orm.service.A_CRUDService.DataAccessExceptionListener;

public class DbInit implements Initiator {
	
	public static final String DB_ATTR_NAME = "db";
	
	private static final DataAccessExceptionListener daeListener = new DataAccessExceptionListener(){
		@Override
		public void onError() {
			Sessions.getCurrent().removeAttribute(DB_ATTR_NAME);
			Executions.sendRedirect("/");
		}
	};
	
	static {
		A_CRUDService.setDaeListener(daeListener);
	}
	
	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		init(Sessions.getCurrent(), page.getDesktop().getWebApp().getServletContext());
	}
	
	public static boolean init(HttpSession session) throws Exception {
		if(session.getAttribute(DB_ATTR_NAME) != null){
			return true;
		}
		boolean res = init(session.getServletContext());
		if(res) {
			session.setAttribute(DB_ATTR_NAME, E_DbSettings.DATABASE.getValue());
		}
		return res;
	}
	
	public static boolean init(Session session, ServletContext ctx) throws Exception {
		if(session.getAttribute(DB_ATTR_NAME) != null){
			return true;
		}
		boolean res = init(ctx);
		if(res) {
			session.setAttribute(DB_ATTR_NAME, E_DbSettings.DATABASE.getValue());
		}
		return res;
	}
	
	private static boolean init(ServletContext ctx) throws Exception {
		final InputStream is = ctx.getResourceAsStream("WEB-INF/mozaik.properties");
		if(is == null) {
			//Executions.sendRedirect("/setup.zul");
			return false;
		}
		final Properties props = new Properties();
		props.load(is);
		final DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://"
				+ props.getProperty("jdbc.host") 
				+":"+ props.getProperty("jdbc.port")
				+"/"+ props.getProperty("jdbc.database")
				+"?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&autoReconnect=true");
		ds.setUsername(props.getProperty("jdbc.username"));
		ds.setPassword(props.getProperty("jdbc.password"));
		ds.setValidationQuery("select 1");
				
		final JdbcTemplate jdbc = new JdbcTemplate(ds);
		jdbc.queryForObject("select 1", Integer.class);
		
		ServicesFacade.$().setJdbc(jdbc);
		
		//session.setAttribute(DB_ATTR_NAME, jdbc.queryForObject("select database()", String.class));
		E_DbSettings.DATABASE.setValue(jdbc.getDataSource().getConnection().getCatalog());
		
		return true;
	}
}

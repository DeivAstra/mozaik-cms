/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DbDriverDestroyListener implements ServletContextListener {
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		final String prefix = getClass().getSimpleName() +" destroy() ";
	    final ServletContext ctx = sce.getServletContext();
	    try {
	        final Enumeration<Driver> drivers = DriverManager.getDrivers();
	        while(drivers.hasMoreElements()) {
	            DriverManager.deregisterDriver(drivers.nextElement());
	        }
	    } catch(Exception e) {
	        ctx.log(prefix + "Exception caught while deregistering JDBC drivers", e);
	    }
	    ctx.log(prefix + "complete");
	}
}

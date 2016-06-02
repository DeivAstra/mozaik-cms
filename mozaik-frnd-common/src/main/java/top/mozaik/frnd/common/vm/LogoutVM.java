/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common.vm;

import java.io.File;

import javax.servlet.ServletContext;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import top.mozaik.frnd.common.bean.SetupBean;
import top.mozaik.frnd.common.init.DbInit;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class LogoutVM extends BaseVM {
	
	private final Execution ex = Executions.getCurrent();
	private final Session session = Sessions.getCurrent();
	private final ServletContext ctx = session.getWebApp().getServletContext();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	/// COMMANDS ///
	
	@Command
	public void logout() {
		session.removeAttribute("user");
		ex.sendRedirect("/");
	}
	
	@Command
	public void logoutAndDeleteSetup() {
		final String propsFilePath = ctx.getRealPath("/")+ "WEB-INF/" + SetupBean.SETUP_FILE_NAME;
		final File file = new File(propsFilePath);
		file.delete();
		session.removeAttribute(DbInit.DB_ATTR_NAME);
		logout();
	}
}
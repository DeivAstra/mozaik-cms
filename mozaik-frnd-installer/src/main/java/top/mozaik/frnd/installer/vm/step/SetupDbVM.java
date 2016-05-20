/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.vm.step;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import top.mozaik.frnd.installer.bean.DbBean;
import top.mozaik.frnd.installer.vm.I_Installer;
import top.mozaik.frnd.installer.vm.I_Step;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SetupDbVM extends BaseVM implements I_Step {
	
	@Wire
	Textbox dbNameTextbox;
	
	private DbBean bean;
	private Button nextBtn;
	
	private boolean disableDbFields;
	
	@Init
	public void init(@BindingParam("installer") I_Installer installer,
					 @BindingParam("bean") DbBean bean) {
		installer.setStep(this);
		this.bean = bean;
	}
	
	private JdbcTemplate jdbc;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	@Override
	public void setNextBtn(Button btn) {
		nextBtn = btn;
		nextBtn.setDisabled(true);
	}
	
	@Override
	public boolean onBeforeNext() {
		ZKUtils.validate(getView());
			
		// check if schema is already exists
		final StringBuilder query = new StringBuilder(
				"select count(*) from information_schema.schemata where schema_name='" 
				+ bean.getDbName() +"'");
		final boolean schemaExists = jdbc.queryForObject(query.toString(), Integer.class) > 0;
			
		if(schemaExists)
			throw new WrongValueException(dbNameTextbox, 
					"Database with name = '"+bean.getDbName() +"' is already exists.");
			
		bean.setJdbc(jdbc);
		
		return true;
	}
	
	/// BINDING ///
	
	public DbBean getBean() {
		return bean;
	}
	
	public boolean getDisableDbFields() {
		return disableDbFields;
	}
	
	public boolean getShowDbNameTextbox() {
		return jdbc != null;
	}
	
	/// COMMANDS ///
	
	@Command
	public void validateAndTestConnection() {
		ZKUtils.validate(getView());
		
		jdbc = null;
		jdbc = connect();
		
		final StringBuilder query = new StringBuilder("select count(*) from information_schema.USER_PRIVILEGES")
			.append(" where GRANTEE = \"'").append(bean.getUsername()).append("'@'localhost'\"")
			.append(" and PRIVILEGE_TYPE = 'CREATE'");
		final boolean createGrantExists = jdbc.queryForObject(query.toString(), Integer.class) > 0;
		if(!createGrantExists) {
			Dialog.error("Error", "User is not has the 'CREATE' privilege.");
			return;
		}
		
		disableDbFields = true;
		getBinder().notifyChange(this, "disableDbFields");
		getBinder().notifyChange(this, "showDbNameTextbox");
		
		nextBtn.setDisabled(false);
	}
	
	private JdbcTemplate connect() {
		final DataSource ds = new DataSource();
		
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://" + bean.getHost() 
				+":"+bean.getPort()+"/?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8");
		ds.setUsername(bean.getUsername());
		ds.setPassword(bean.getPassword());
		ds.setValidationQuery("select 1");
		
		final JdbcTemplate jdbc = new JdbcTemplate(ds);
		final Integer result = jdbc.queryForObject("select 1", Integer.class);
		return jdbc;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.bean;

public class StepsBean {
	
	private final DbBean dbBean = new DbBean();
	private final DbUserBean dbUserBean = new DbUserBean();
	private final AdminUserBean adminUserBean = new AdminUserBean();
	private final RpRootFolderBean rpRootFolderBean = new RpRootFolderBean();
	
	private boolean hideInstallerAfterFinish = true;
	private boolean installDemoData = true;
	
	public DbBean getDbBean() {
		return dbBean;
	}
	
	public DbUserBean getDbUserBean() {
		return dbUserBean;
	}
	
	public AdminUserBean getAdminUserBean() {
		return adminUserBean;
	}
	
	public RpRootFolderBean getRpRootFolderBean() {
		return rpRootFolderBean;
	}

	public boolean getHideInstallerAfterFinish() {
		return hideInstallerAfterFinish;
	}

	public void setHideInstallerAfterFinish(boolean hideInstallerAfterFinish) {
		this.hideInstallerAfterFinish = hideInstallerAfterFinish;
	}

	public boolean isInstallDemoData() {
		return installDemoData;
	}

	public void setInstallDemoData(boolean installDemoData) {
		this.installDemoData = installDemoData;
	}
}

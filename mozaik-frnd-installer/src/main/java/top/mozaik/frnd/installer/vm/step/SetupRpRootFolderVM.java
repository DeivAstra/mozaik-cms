/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.vm.step;

import java.io.File;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import top.mozaik.frnd.installer.bean.RpRootFolderBean;
import top.mozaik.frnd.installer.bean.StepsBean;
import top.mozaik.frnd.installer.vm.I_Installer;
import top.mozaik.frnd.installer.vm.I_Step;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SetupRpRootFolderVM extends BaseVM implements I_Step {
	
	@Wire
	Textbox pathTextbox;
	
	private RpRootFolderBean bean;
	
	@Init
	public void init(@BindingParam("installer") I_Installer installer,
					 @BindingParam("bean") StepsBean bean) {
		installer.setStep(this);
		this.bean = bean.getRpRootFolderBean();
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	@Override
	public void setNextBtn(Button btn) {
	}
	
	@Override
	public boolean onBeforeNext() {
		ZKUtils.validate(getView());
		
		File file = new File(bean.getPath());
		if(file.isDirectory()) {
			throw new WrongValueException(pathTextbox, "Folder '"+  bean.getPath()
					+"' already exist. Please set another name or remove exists folder manually");
		}
		
		file = file.getParentFile();
		if(file == null) {
			throw new WrongValueException(pathTextbox, "Folder should has at least one parent folder");
		}
		try {
			checkPermissions(bean.getPath());
		} catch (IllegalArgumentException e) {
			throw new WrongValueException(pathTextbox, e.getMessage());
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		final String path = "/home/danykey/123/345";
		checkPermissions(path);
		
		//System.out.println(parentFile + ":" + parentFile.canRead() + "," + parentFile.canWrite());
	}
	
	private static void checkPermissions(String path) {
		final File file = new File(path);
		final File parentFile = file.getParentFile();
		if(parentFile == null) return;
		if(parentFile.exists()) {
			if(!parentFile.isDirectory()) {
				throw new IllegalArgumentException("File '" + parentFile + "' is not a folder");
			}
			if(!parentFile.canRead()) {
				throw new IllegalArgumentException("'Read' access forbidden for '" + parentFile+ "'");
			}
			
			if(!parentFile.canWrite()) {
				throw new IllegalArgumentException("'Write' access forbidden for '" + parentFile+ "'");
			}
			return;
		}
		checkPermissions(parentFile.getPath());
	}
	
	/// BINDING ///
	
	public RpRootFolderBean getBean() {
		return bean;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.vm;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;

import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class InstallerVM extends BaseVM implements I_Installer {
	
	private static final String [] stepZuls = {
		//"welcome.inc.zul",
		"setup-db.inc.zul",
		"setup-dbuser.inc.zul",
		"setup-adminuser.inc.zul",
		"setup-rprootfolder.inc.zul",
		"summary.inc.zul",
		"install.inc.zul",
		"finish.inc.zul"
	};
	
	private I_Step step;
	
	private int stepNum = 0;
	
	@Wire
	Include inc;
	@Wire
	Button nextBtn;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	public void setStep(I_Step step){
		this.step = step;
		step.setNextBtn(nextBtn);
	}
	
	/// BINDING ///
	
	public boolean hasNextStep(){
		return stepNum < stepZuls.length;
	}
	
	public Integer getStepNum() {
		return stepNum+1;
	}
	
	public Integer getStepCount() {
		return stepZuls.length+1;
	}
	
	/// COMMANDS ///
	
	@Command
	public void next() {
		if(step != null){
			if(step.onBeforeNext()) {
				step = null;
				loadNextStep();
			}
		} else {
			loadNextStep();
		}
	}
	
	private void loadNextStep() {
		inc.setSrc("/WEB-INF/zul/step/" + stepZuls[stepNum]);
		stepNum++;
		reloadComponent();
	}
}

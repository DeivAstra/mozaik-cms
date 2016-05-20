/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;

public class CodeMirror extends Div {
	
	private static final String ON_BLUR_EVENT = "onBlur";
	
	private String mode;
	
	private String value;
	private String selectedValue;
	
	public CodeMirror() {
		//Executions.createComponents("/zul/_shared_/CodeMirror.zul", this, null);
		Selectors.wireComponents(this, this, false);
        Selectors.wireEventListeners(this, this);
	}
	
	
	@Override
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		final Map<String, Object> data = request.getData();
		
		if(cmd.equals(ON_BLUR_EVENT)){
			//Notification.showInfo((String)data.get("value"));
			value = (String)data.get("value");
			selectedValue = (String)data.get("selectedValue");
		} else {
			super.service(request, everError);
		}
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public String getMode() {
		return mode;
	}
	
	public void setValue(String value) {
		this.value = value;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("value", value);
		Clients.response(new AuInvoke(this, "setValue", map));
	}
	
	public String getValue() {
		return value;
	}
	
	public String getSelectedValue() {
		return selectedValue;
	}
}

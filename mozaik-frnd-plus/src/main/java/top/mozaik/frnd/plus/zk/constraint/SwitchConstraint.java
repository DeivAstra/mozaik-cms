/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.constraint;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.SimpleConstraint;

public class SwitchConstraint implements Constraint {
	
	private boolean enabled;
	private final String constraint;
	
	public SwitchConstraint(boolean enabled, String constraint) {
		this.enabled = enabled;
		this.constraint = constraint;
	}
	
	public SwitchConstraint(String constraint) {
		this.constraint = constraint;
		this.enabled = true;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void validate(Component comp, Object value)
			throws WrongValueException {
		if(!this.enabled)
			return;
			
		SimpleConstraint.
				getInstance(this.constraint).validate(comp, value);
	}
	
	public void forceValidate(Component comp, Object value){
		boolean wasDisabled = false;
		if(!this.enabled) {
			this.enabled = true;
			wasDisabled = true;
		}
		
		try {
			validate(comp, value);
		} finally {
			if(wasDisabled) this.enabled = false;
		}
	}
}

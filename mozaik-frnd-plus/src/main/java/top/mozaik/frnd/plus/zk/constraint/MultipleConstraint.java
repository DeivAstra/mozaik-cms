/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.constraint;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.SimpleConstraint;

public class MultipleConstraint extends SwitchConstraint {
	
	private final Constraint [] constraints;
	
	public MultipleConstraint(boolean enabled, String constraint) {
		super(enabled, constraint);
		constraints = initConstraint(constraint);
	}
	
	public MultipleConstraint(String constraint) {
		super(constraint);
		constraints = initConstraint(constraint);
	}
	
	private Constraint [] initConstraint(String constraint) {
		final String [] constrValues = constraint.split("\\|\\|");
		final Constraint [] constraints = new Constraint[constrValues.length];
		for(int i=0; i< constrValues.length; i++) {
			this.constraints[i] = new SimpleConstraint(constrValues[i]);
		}
		return constraints;
	}
	
	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		if(!isEnabled() || constraints == null) {
			return;
		}
		for(int i=0; i<constraints.length; i++){
			constraints[i].validate(comp, value);
		}
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.constraint;

import java.util.regex.Pattern;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;

public class RegexConstraint extends SwitchConstraint {
	
	public RegexConstraint() {
		super(false, null);
	}
	
	@Override
	public void validate(Component comp, Object value)
			throws WrongValueException {
		if(!isEnabled())
			return;
		
		final String regexp = (String) value;
		if(regexp == null || regexp.length() == 0) return;
		try {
			Pattern.compile(regexp);
		} catch (Exception e) {
			throw new WrongValueException(comp, e.getMessage());
		}
		
		if(!regexp.matches("^\\/.*\\/$")) {
			throw new WrongValueException(comp, "Pattern must be wrapped by '/'");
		}
	}
}

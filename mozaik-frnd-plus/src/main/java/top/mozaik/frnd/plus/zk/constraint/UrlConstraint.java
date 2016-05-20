/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.constraint;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;

public class UrlConstraint extends SwitchConstraint {
	
	private final boolean allowEmpty;
	
	public UrlConstraint() {
		this(true);
	}
	
	public UrlConstraint(boolean allowEmpty) {
		super(false, null);
		this.allowEmpty = allowEmpty;
	}
	
	@Override
	public void validate(Component comp, Object value)
			throws WrongValueException {
		if(!isEnabled())
			return;
		
		final String url = (String) value;
		if(allowEmpty) {
			if(url == null || url.length() == 0) return;
		} else {
			if(url == null || url.trim().length() == 0)
				throw new WrongValueException(comp, "This field may not be empty");
		}
		if(!(url).matches("^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")){
			throw new WrongValueException(comp, "Not valid URL");
		}
	}
}

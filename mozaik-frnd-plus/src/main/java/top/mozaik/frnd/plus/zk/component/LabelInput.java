/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.InputElement;

public class LabelInput extends InputElement {
	
	private String label = "";
	
	public LabelInput() {
	}
	
	@Override
	protected Object coerceFromString(String value) throws WrongValueException {
		return value;
	}

	@Override
	protected String coerceToString(Object value) {
		if(value == null) return null;
		return value.toString();
	}
	
	public void setValue(Object value) {
		//validate(value);
		setRawValue(value);
	}
	
	public Object getValue() {
		return getTargetValue();
	}
	
	public void setLabel(String label) {
		if(label == null) {
			 smartUpdate("label", null);
			 return;
		}
		if (!this.label.equals(label)) {
			this.label = label;
            smartUpdate("label", label);
        }
	}
	
	public String getLabel() {
		return label;
	}
	
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
            throws java.io.IOException {
        super.renderProperties(renderer);
        render(renderer, "label", label);
    }
}

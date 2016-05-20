/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import top.mozaik.frnd.plus.zk.I_StringUI;

@Deprecated
public class ComboboxObjectConverter implements TypeConverter{
	
	public Object coerceToUi(Object val, Component comp) {
		if(val == null) return val;
		if(val instanceof I_StringUI) {
			return ((I_StringUI)val).toStringUI();
		} else {
			return val.toString();
		}
	}
	
	public Object coerceToBean(Object val, Component comp) {
		final Comboitem item = ((Combobox)comp).getSelectedItem();
		if(item == null) return null;
		return item.getValue();
	}
}

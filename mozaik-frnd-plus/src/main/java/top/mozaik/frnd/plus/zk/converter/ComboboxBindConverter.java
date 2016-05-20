/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.converter;

import java.lang.reflect.Method;

import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

public class ComboboxBindConverter implements Converter<Object, Object, Component> {
	
	public Object coerceToUi(Object val, Component comp, org.zkoss.bind.BindContext ctx) {
		if(val == null) return null;
		final String toUi = (String)ctx.getConverterArg("toUi");
		if(toUi == null) return val;
		
		try {
			final Method method = val.getClass().getMethod(
					"get" + toUi.substring(0, 1).toUpperCase() + toUi.substring(1),  null);
			return method.invoke(val, null);
		} catch (Exception e) {
			//throw new RuntimeException(e);
		}
		
		try {
			final Method method = val.getClass().getMethod(toUi, null);
			return method.invoke(val, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object coerceToBean(Object val, Component comp, org.zkoss.bind.BindContext ctx) {
		
		final Comboitem item = ((Combobox)comp).getSelectedItem();
		if(item == null) return null;
		
		val = item.getValue();
		
		final String toBean = (String)ctx.getConverterArg("toBean");
		if(toBean != null) {
			try {
				final Method method = val.getClass().getMethod(
						"get" + toBean.substring(0, 1).toUpperCase() + toBean.substring(1),  null);
				return method.invoke(val, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return val;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zul.Doublebox;

public class TemplateDoubleFieldConverter implements Converter<Double, String, Doublebox> {
	@Override
	public Double coerceToUi(String val, Doublebox component, BindContext ctx) {
		if(val == null) return null;
		try {
			return Double.parseDouble(val);
		} catch (Exception e) {}
		return null;
	}
	@Override
	public String coerceToBean(Double val, Doublebox component, BindContext ctx) {
		if(val == null) return null;
		return val.toString();
	}
}

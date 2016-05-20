/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zul.Longbox;

public class TemplateIntegerFieldConverter implements Converter<Long, String, Longbox> {
	@Override
	public Long coerceToUi(String val, Longbox longbox, BindContext ctx) {
		if(val == null) return null;
		try {
			return Long.parseLong(val);
		} catch (Exception e) {}
		return null;
	}
	@Override
	public String coerceToBean(Long val, Longbox longbox, BindContext ctx) {
		if(val == null) return null;
		return val.toString();
	}
}

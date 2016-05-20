/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zul.Timebox;

public class TemplateTimeFieldConverter implements Converter<Date, String, Timebox> {
	
	private final DateFormat dateFormat = new SimpleDateFormat("kk:mm");
	
	@Override
	public Date coerceToUi(String time, Timebox timebox, BindContext ctx) {
		if(time == null) return null;
		try {
			return dateFormat.parse(time);
		} catch (Exception e) {
			//e.printStackTrace();
			return timebox.getValue();
		}
	}
	@Override
	public String coerceToBean(Date date, Timebox timebox, BindContext ctx) {
		if(date == null) return null;
		return dateFormat.format(date);
	}
}

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
import org.zkoss.zul.Datebox;

public class TemplateDateFieldConverter implements Converter<Date, String, Datebox> {
	
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	@Override
	public Date coerceToUi(String date, Datebox datebox, BindContext ctx) {
		if(date == null) return null;
		try {
			return dateFormat.parse(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return datebox.getValue();
		}
	}
	@Override
	public String coerceToBean(Date date, Datebox datebox, BindContext ctx) {
		if(date == null) return null;
		return dateFormat.format(date);
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

public class DateToStringConverter implements Converter<String, Object, Component> {
	
	private DateFormat dateFormat;
	
	public DateToStringConverter() {
	}
	
	public DateToStringConverter(String format) {
		this.dateFormat = new SimpleDateFormat(format);
	}
	
    public String coerceToUi(Object val/* Date, Long */, Component comp, BindContext ctx) {
    	if(val == null) return null;
    	
    	if(dateFormat == null) {
    		final String format = (String) ctx.getConverterArg("format");
    		if(format==null) {
    			dateFormat = new SimpleDateFormat();
    		} else {
    			dateFormat = new SimpleDateFormat(format);
    		}
    	}
    	if(val instanceof Long) return dateFormat.format(new Date((Long)val));
    	if(val instanceof Date) return dateFormat.format((Date)val);
    	
        throw new IllegalArgumentException("Value must be Date or Long");
    }
     
    public Object coerceToBean(String val, Component comp, BindContext ctx) {
    	if(dateFormat == null) {
    		final String format = (String) ctx.getConverterArg("format");
    		if(format==null) {
    			dateFormat = new SimpleDateFormat();
    		} else {
    			dateFormat = new SimpleDateFormat(format);
    		}
    	}
        try {
            return val == null ? null : dateFormat.parse((String)val);
        } catch (ParseException e) {
            throw UiException.Aide.wrap(e);
        }
    }
}

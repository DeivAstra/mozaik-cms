/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template.converter;

public interface I_TemplareConverter {
	
	public static final TemplateDateFieldConverter templateDateFieldConverter = new TemplateDateFieldConverter();
	public static final TemplateTimeFieldConverter templateTimeFieldConverter = new TemplateTimeFieldConverter();
	public static final TemplateIntegerFieldConverter templateIntegerFieldConverter = new TemplateIntegerFieldConverter();
	public static final TemplateDoubleFieldConverter templateDoubleFieldConverter = new TemplateDoubleFieldConverter();
	
}

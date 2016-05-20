/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.component.widget;

class Style extends org.zkoss.zul.Style {
	
	public static final String ZUL_DEFINITION = "<?component name=\"style\" class=\"" + Style.class.getName()  +"\"?>";
	
	public Style() {
	}
/*
	@Override
	public void setSrc(String src) {
		final Integer widgetId = (Integer) Executions.getCurrent().getAttribute("widgetId");
		final ResourceSetUtils resourceSetUtils = new ResourceSetUtils(widgetId);
		final _ResourceData resourceData = resourceSetUtils.findResourceDataByPath(src, E_ResourceType.STYLE);
		setContent(new String(resourceData.getSourceData()));
	}
*/
	/// TODO: WHILE SAVING STYLE DATA 
	///	1. wrap by the resourcePack#widget selector and compile by the LESS after that
	/// 2. save to compiled_data field
	@Override
	public void setContent(String content) {
		super.setContent(content);
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.component.widget;

class Script extends org.zkoss.zul.Script {
	
	public static final String ZUL_DEFINITION = "<?component name=\"script\" class=\"" + Script.class.getName()  +"\"?>";
	
	public Script() {
	}
	
	public Script(String content) {
		super(content);
	}
/*	
	@Override
	public void setSrc(String src) {
		final Integer widgetId = (Integer) Executions.getCurrent().getAttribute("widgetId");
		final ResourceSetUtils resourceSetUtils = new ResourceSetUtils(widgetId);
		final _ResourceData resourceData = resourceSetUtils.findResourceDataByPath(src, E_ResourceType.SCRIPT);
		setContent(new String(resourceData.getSourceData()));
	}
	*/
}

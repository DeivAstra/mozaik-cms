/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.component.widget;

@Deprecated
public class Include2 extends org.zkoss.zul.Include {
	
	public static final String ZUL_DEFINITION = "<?component name=\"include\" class=\"" + Include2.class.getName()  +"\"?>";
	
	public static final String RESOURCE_PATH_PROPERTY = "pathToResource";
	private static final String INCLUDE_RESOLVER_PATH = "/zul/include/includeResolver.zul";
	
	public Include2() {
	}
	
	public Include2(String src) {
		super(INCLUDE_RESOLVER_PATH);
		setDynamicProperty(RESOURCE_PATH_PROPERTY, src);
	}
	
	@Override
	public void setSrc(String src) {
		setDynamicProperty(RESOURCE_PATH_PROPERTY, src);
		super.setSrc(INCLUDE_RESOLVER_PATH);
	}
	/*
	private static String getParams(String src) {
		final Integer paramsIndex = src.indexOf('?');
		if(paramsIndex > 0) {
			return src.substring(paramsIndex);
		}
		return "";
	}
	
	private static String cutParams(String src) {
		final Integer paramsIndex = src.indexOf('?');
		if(paramsIndex > 0) {
			return src.substring(0, paramsIndex);
		}
		return src;
	}
	*/
}

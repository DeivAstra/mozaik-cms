/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common.enums;

import top.mozaik.frnd.common.component.Include;
import top.mozaik.frnd.common.component.Script;
import top.mozaik.frnd.common.component.Style;

public enum E_ResourcePackSettings {
	
	ZUL_COMPONENT_DEFINITIONS(Include.ZUL_DEFINITION + Script.ZUL_DEFINITION + Style.ZUL_DEFINITION);
	
	private Object value;
	
	private E_ResourcePackSettings(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}

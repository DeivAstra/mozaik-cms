/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Menuitem;

public class ColorMenuitem extends Menuitem {
	
	private String color = "black";
		
	public ColorMenuitem(String color) {		
		setColor(color);
	}
	
	public void setColor(String color) {
		this.color = color;
		//setClass("inherit-color");
		//setStyle("color:"+color);
		
		setLabel(StringUtils.capitalize(color.toLowerCase()));
	}
	
	public String getColor() {
		return color;
	}
}

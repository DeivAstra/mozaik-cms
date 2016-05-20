/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import org.zkoss.zul.Textbox;

import top.mozaik.frnd.plus.zk.constraint.MultipleConstraint;

public class ZKTextbox extends Textbox {
		
	@Override
	public void setConstraint(String constr) {
		super.setConstraint(new MultipleConstraint(constr));
	}
}

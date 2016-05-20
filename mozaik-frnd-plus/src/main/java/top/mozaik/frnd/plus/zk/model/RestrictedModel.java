/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.model;

import org.zkoss.zul.ListModelList;

public abstract class RestrictedModel<T> extends ListModelList<T> {
	
	private final I_ModelRestriction<T> restriction;
	
	public RestrictedModel(I_ModelRestriction<T> restriction) throws Exception {
		this.restriction = restriction;
		this.setMultiple(true);
		reload();
	}
	
	public void reload() throws Exception {
		restriction.execute(this); 
	}
}

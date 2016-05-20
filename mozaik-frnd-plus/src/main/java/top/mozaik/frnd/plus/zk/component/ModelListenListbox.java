/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

public class ModelListenListbox extends Listbox {
	
	public static interface Listener {
		void onSetModel(ListModel<?> model);
	}
	
	private Listener modelListener;
	
	@Override
	public void setModel(ListModel<?> model) {
		if(modelListener != null)
			modelListener.onSetModel(model);
		super.setModel(model);
	}
	
	public void setModelListener(Listener modelListener) {
		this.modelListener = modelListener;
	}
}

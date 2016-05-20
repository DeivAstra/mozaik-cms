/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.vm;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

public class BaseVM {
	
	private static final String ATTR_VM = "BaseVM";
	
	private Component view;
	private Binder binder;
	
	@AfterCompose
	public void doAfterComposer(
			@ContextParam(ContextType.VIEW) Component view,
			@ContextParam(ContextType.BINDER) Binder binder
		) {
		this.view = view;
		this.binder = binder;
		Selectors.wireComponents(view, this, false);
		view.setAttribute(ATTR_VM, this);
	}
		
	protected Component getView() {
		return view;
	}
	
	public BaseVM getParentVM() {
		Component parent = view.getParent();
		if(parent == null) return null;
		else
		do {
			if(parent.hasAttribute(ATTR_VM)) {
				return (BaseVM) parent.getAttribute(ATTR_VM);
			}
			parent = parent.getParent();
		} while(parent != null);
		return null;
	}
	
	protected Binder getBinder() {
		return binder;
	}
	
	public void reloadComponent() {
		binder.loadComponent(view, false);
	}
	
	@Command
	public void detachView() {
		view.detach();
	}
}

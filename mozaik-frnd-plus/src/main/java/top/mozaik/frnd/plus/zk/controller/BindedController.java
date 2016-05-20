/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.controller;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Window;

@Deprecated
public class BindedController extends GenericForwardComposer {
	
	public static final String PARENT_CONTROLLER_ARG_NAME = "parentController";
	
	private AnnotateDataBinder dataBinder;
	private Window view;
	private Map args;
	private BindedController parentController;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		comp.setAttribute("controller", this);
		//parentController = (BindedController)getExecArg(PARENT_CONTROLLER_ARG_NAME);
		//comp.setAttribute("parentController", parentController);
		
		args = comp.getDesktop().getExecution().getArg();
		view = (Window) comp;
		
		dataBinder = new AnnotateDataBinder(comp);
		bindBean("controller", this);
	}
	
	protected Object getExecArg(String name) {
		return args.get(name);
	}
	
	protected void bindBean(String beanId, Object bean) {
		dataBinder.bindBean(beanId, bean);
	}
	
	protected void loadDataBinderAll() {
		dataBinder.loadAll();
	}
	
	protected void loadDataBinderComponent(Component c) {
		dataBinder.loadComponent(c);
	}
	
	public AnnotateDataBinder getDataBinder() {
		return dataBinder;
	}
	
	public void update() {}
	
	public Window getView() {
		return view;
	}
	
	public BindedController getParentController() {
		if(parentController == null) {
			return this;
		}
		return parentController;
	}
}


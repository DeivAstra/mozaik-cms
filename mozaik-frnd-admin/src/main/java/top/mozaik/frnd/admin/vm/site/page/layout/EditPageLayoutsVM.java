/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site.page.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.SitePageLayout;
import top.mozaik.bknd.api.model.SitePageLayoutWidget;
import top.mozaik.bknd.api.service.SitePageLayoutService;
import top.mozaik.bknd.api.service.SitePageLayoutWidgetService;
import top.mozaik.frnd.admin.bean.site.SitePageLayoutBean;
import top.mozaik.frnd.admin.vm.site.page.EditPageVM;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.command.I_CommandExecutor;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditPageLayoutsVM extends BaseVM implements I_CommandExecutor {
	
	private final SitePageLayoutService sitePageLayoutService = ServicesFacade.$().getSitePageLayoutService();
	private final SitePageLayoutWidgetService sitePageLayoutWidgetService = ServicesFacade.$().getSitePageLayoutWidgetService();
	
	private Integer pageId;
	private SitePageLayoutBean bean;
	
	@Init
	public void init(
			@BindingParam("pageId") Integer pageId,
			@BindingParam("commandQueue") CommandExecutionQueue commandQueue) {
		this.pageId = pageId;
		commandQueue.addListener(this);
	}
	
	private static final List<String> isNullFields = new ArrayList<String>();
	static {
		isNullFields.add("parentLayoutId");
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){
		
		final SitePageLayout filter = new SitePageLayout().setPageId(pageId);
		filter.getFilter().setIsNullFields(isNullFields);
		final SitePageLayout rootLayout = sitePageLayoutService.read1(filter);
		
		/// APPEND ROOT LAYOUT
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("pageId", pageId);
		/*
		args.put("structureChangeCallback", new ICallback<Object>() {
			public void call(Object ... args ) {
				
			};
		});*/
		
		if(rootLayout == null) {
			bean = new SitePageLayoutBean(new SitePageLayout().setPageId(pageId));
		} else {
			bean = new SitePageLayoutBean(rootLayout);
		}
		args.put("sitePageLayoutBean", bean);
		Executions.createComponents("/WEB-INF/zul/site/page/layout/layout.zul", getView(), args);
		
		/// LAYOUTS CREATED - CLEAR ID IN ALL LAYOUTS
		clearIds(bean);
	}
	
	private void clearIds(SitePageLayoutBean bean) {
		bean.setId(null);
		if(bean.childsIsNull()) return;

		for(int i=0; i<bean.size();i++) {
			clearIds(bean.get(i));
		}
	}
	
	@Override
	public void execCommand(int cmdId) {
		if(EditPageVM.COMMAND_SAVE != cmdId &
				EditPageVM.COMMAND_VALIDATE != cmdId) throw new IllegalArgumentException();
		
		if(EditPageVM.COMMAND_VALIDATE == cmdId) {
			validatePageLayouts(bean);
			return;
		}
		
		try {
			
			sitePageLayoutService.startTransaction();
		
			/// DELETE ALL CURRENT PAGE LAYOUTS
			sitePageLayoutService.delete(new SitePageLayout().setPageId(pageId), true);
		
			/// DELETE ALL CURRENT PAGE LAYOUT WIDGETS
			sitePageLayoutWidgetService.delete(new SitePageLayoutWidget().setPageId(pageId), true);
		
			/// ADD NEW PAGE LAYOUTS
			final Integer id = sitePageLayoutService.create(bean.getDelegate());
			createChildPageLayouts(bean, id);
			
			sitePageLayoutService.commit();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while save Layouts", e);
			sitePageLayoutService.rollback();
		}
	}
	
	private void validatePageLayouts(SitePageLayoutBean bean){
		bean.getCommandExecutor().execCommand(0);
		for(int i = 0; i < bean.size(); i++) {
			final SitePageLayoutBean childBean = bean.get(i);
			validatePageLayouts(childBean);
		}
	}
	
	private void createChildPageLayouts(SitePageLayoutBean bean, Integer parentId) {
		/// CHECK IF LAYOUT CONTAINS WIDGET
		if(bean.size() == 0) {
			if(bean.getWidget() != null) {
				//System.out.println("createChildPageLayouts - "+bean);
				final SitePageLayoutWidget sitePageLayoutWidget = new SitePageLayoutWidget()
					.setPageId(pageId).setLayoutId(parentId)
					.setWidgetId(bean.getWidget().getId())
					.setSkinParams(bean.getLayoutSkinParams())
					.setWidgetParams(bean.getLayoutWidgetParams());
				if(bean.getSkin() != null) {
					sitePageLayoutWidget.setSkinId(bean.getSkin().getId());
				}
				sitePageLayoutWidgetService.create(sitePageLayoutWidget);
			}
			return;
		}
		
		for(int i = 0; i < bean.size(); i++) {
			final SitePageLayoutBean childBean = bean.get(i);
			childBean.setPageId(pageId);
			childBean.setParentLayoutId(parentId);
			final Integer id = sitePageLayoutService.create(childBean.getDelegate());
			createChildPageLayouts(childBean, id);
		}
	}
}

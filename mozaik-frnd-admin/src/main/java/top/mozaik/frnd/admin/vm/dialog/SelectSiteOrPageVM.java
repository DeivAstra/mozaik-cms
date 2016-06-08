/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.dialog;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tree;

import top.mozaik.frnd.admin.bean.site.tree.A_TreeSiteElement;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.admin.bean.site.tree.TreeSitePage;
import top.mozaik.frnd.admin.bean.wcm.component.TreeComponent;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocument;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocumentFolder;
import top.mozaik.frnd.admin.converter.SiteTreeitemImageUrlConverter;
import top.mozaik.frnd.admin.converter.wcm.ComponentTreeImageUrlConverter;
import top.mozaik.frnd.admin.model.SiteTreeModel;
import top.mozaik.frnd.admin.model.wcm.ComponentTreeModel;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class SelectSiteOrPageVM extends BaseVM {
	
	private static final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	public static final String TARGET_SITE = "s";
	public static final String TARGET_PAGE = "p";
	
	@Wire
	Tree siteTree;
	
	I_CallbackArg callback;
	
	private String target;
	
	@Init
	public void init(@ExecutionArgParam("target") String target) {
		this.target = target;
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("callback") final I_CallbackArg<?> callback) {
		this.callback = callback;
	}
	
	/// BINDING ///
	
	public String getTitle(){
		if(TARGET_SITE.equals(target)) {
			return "Select Site";
		} else if(TARGET_PAGE.equals(target)) {
			return "Select Page";
		} else {
			return "Select Site or Page";
		}
	}
	
	public SiteTreeModel getSiteTreeModel() throws Exception {
		return new SiteTreeModel();
	}
	
	public SiteTreeitemImageUrlConverter getTreeImageUrlConverter() {
		return SiteTreeitemImageUrlConverter.getInstance();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void select(@BindingParam("el") A_TreeSiteElement el) {
		if(!(el instanceof TreeSite) & !(el instanceof TreeSitePage)) return;
		
		if(TARGET_SITE.equals(target)) {
			if(!(el instanceof TreeSite)) return;
		} else if(TARGET_PAGE.equals(target)) {
			if(!(el instanceof TreeSitePage)) return;
		}
		
		callback.call(el.getValue());
		getView().detach();
	}
}
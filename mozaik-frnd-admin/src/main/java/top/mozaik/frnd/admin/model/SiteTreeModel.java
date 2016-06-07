/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.model;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.Site;
import top.mozaik.bknd.api.model.SitePage;
import top.mozaik.bknd.api.service.SitePageService;
import top.mozaik.frnd.admin.bean.site.tree.A_TreeSiteElement;
import top.mozaik.frnd.admin.bean.site.tree.TreeSite;
import top.mozaik.frnd.admin.bean.site.tree.TreeSitePage;
import top.mozaik.frnd.admin.bean.site.tree.TreeSiteRootFolder;

public class SiteTreeModel extends AbstractTreeModel<A_TreeSiteElement> {
	
	private final SitePageService sitePageService = ServicesFacade.$().getSitePageService();
	
	public SiteTreeModel() throws Exception {
		super(buildRootElement());
	}
	
	private static A_TreeSiteElement  buildRootElement() throws Exception {
		final TreeSiteRootFolder rootFolder = new TreeSiteRootFolder();
				
		/// LOAD SITES LIST
		final List<Site> sites = 
				ServicesFacade.$().getSiteService().readAll();
		
		for(final Site bean : sites) {			
			rootFolder.addChild(new TreeSite(bean));
		}
		return rootFolder;
	}
	
	private final SitePage _childsFilter = new SitePage(); /// NOT BEAUTYFUL BUT FAST
	private static final List<String> isNullFields = new ArrayList<String>();
	static {
		isNullFields.add("parentId");
	}
	private void loadChildrens(final A_TreeSiteElement folder){
		if(folder instanceof TreeSiteRootFolder || !folder.childsIsNull()) return;
		try {
			List<SitePage> pages = null;
			if(folder instanceof TreeSite) {
				final TreeSite siteFolder = (TreeSite) folder;
				final SitePage page = new SitePage().setSiteId(siteFolder.getValue().getId());
				page.getFilter().setIsNullFields(isNullFields);
				pages = sitePageService.read(page);
			} else {
				final TreeSitePage treeSitePage = ((TreeSitePage) folder);
				pages = sitePageService.read(
						new SitePage().setParentId(treeSitePage.getValue().getId()));
			}
			for(final SitePage page : pages) {
				folder.addChild(new TreeSitePage(page));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getChildCount(A_TreeSiteElement node) {
		loadChildrens(node);
		return node.size();
	}
	
	@Override
	public A_TreeSiteElement getChild(A_TreeSiteElement node, int index) {
		return (A_TreeSiteElement) node.get(index);
	}

	@Override
	public boolean isLeaf(A_TreeSiteElement node) {
		loadChildrens(node);
		return node.childsIsNull()?true:node.size() == 0;
	}
}

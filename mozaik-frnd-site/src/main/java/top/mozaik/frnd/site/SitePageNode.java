/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import top.mozaik.bknd.api.model.SitePage;

public class SitePageNode {
	
	private final SiteNode siteNode;
	private final SitePageNode parent;
	private final SitePage delegate;
	private final List<SitePageNode> pages;
	private final Date createDate;
	private final Date publishDate;
	private final String path;
	
	public SitePageNode(SiteNode siteNode, SitePageNode parent, SitePage delegate, List<SitePageNode> pages) {
		this.siteNode = siteNode;
		this.parent = parent;
		this.delegate = delegate;
		this.pages = pages;
		this.createDate = new Date(delegate.getCreateDate());
		this.publishDate = null;//new Date(delegate.getPublishDate());
		
		if(parent == null)
			this.path = "/" + delegate.getAlias();
		else
			this.path = parent.getPath() + "/" + delegate.getAlias();
	}
	
	public String getPath() {
		return path;
	}
	
	public SiteNode getSite() {
		return siteNode;
	}
	
	public SitePageNode getParent() {
		return parent;
	}
	
	public Integer getId(){
		return delegate.getId();
	}
	
	public String getTitle() {
		return delegate.getTitle();
	}

	public String getDescription() {
		return delegate.getDescr();
	}

	public String getAlias() {
		return delegate.getAlias();
	}

	public Date getCreateDate() {
		return createDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public Iterator<SitePageNode> getPages() {
		return pages.iterator();
	}
	
	SitePage getDelegate() {
		return delegate;
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import java.util.Iterator;
import java.util.List;

import top.mozaik.bknd.api.model.Site;

public class SiteNode {
	
	private final Site delegate;
	private final List<SitePageNode> pages;
	
	public SiteNode(Site delegate, List<SitePageNode> pages) {
		this.delegate = delegate;
		this.pages = pages;
	}
	
	public Integer getId() {
		return delegate.getId();
	}

	public String getTitle() {
		return delegate.getTitle();
	}

	public String getDomains() {
		return delegate.getDomains();
	}

	public String getDescr() {
		return delegate.getDescr();
	}
	
	public Iterator<SitePageNode> getPages() {
		return pages.iterator();
	}
}

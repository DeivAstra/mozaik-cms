/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.Site;
import top.mozaik.bknd.api.model.SitePage;
import top.mozaik.bknd.api.service.SitePageService;
import top.mozaik.bknd.api.service.SiteService;

public class SitePageCache {
	
	private final SiteService siteService = ServicesFacade.$().getSiteService();
	private final SitePageService sitePageService = ServicesFacade.$().getSitePageService();
	
	private final Map<String/*domain*/, Map<String/*path*/, SitePageNode>> map = new HashMap<>();
	
	public SitePageCache() {
		for(Site site: siteService.read(new Site())) {
			final List<SitePageNode> pageNodes = new ArrayList<>();
			final SiteNode siteNode = new SiteNode(site, pageNodes);
			map.put(site.getDomains(), loadPages(siteNode, pageNodes));
		}
	}
	
	private Map<String, SitePageNode> loadPages(SiteNode site, List<SitePageNode> pageNodes) {
		final Map<String, SitePageNode> pageNodeMap = new HashMap<>();
		final SitePage pageFilter = new SitePage().setSiteId(site.getId());
		pageFilter.getFilter().putNullField("parentId");
		for(SitePage page: sitePageService.read(pageFilter)) {
			final List<SitePageNode> subPageNodes = new ArrayList<>();
			final SitePageNode pageNode = new SitePageNode(site, null, page, subPageNodes);
			pageNodes.add(pageNode);
			pageNodeMap.put(pageNode.getPath(), pageNode);
			pageNodeMap.put(pageNode.getPath()+"/", pageNode);
			loadSubPages(pageNodeMap, pageNode, subPageNodes);
		}
		return pageNodeMap;
	}
	
	private void loadSubPages(Map<String, SitePageNode> pageNodeMap, SitePageNode parentPageNode, List<SitePageNode> parentPageNodes) {
		for(SitePage page: sitePageService.read(new SitePage().setParentId(parentPageNode.getId()))){
			final List<SitePageNode> subPageNodes = new ArrayList<>();
			final SitePageNode pageNode = new SitePageNode(parentPageNode.getSite(), parentPageNode, page, subPageNodes);
			parentPageNodes.add(pageNode);
			pageNodeMap.put(pageNode.getPath(), pageNode);
			pageNodeMap.put(pageNode.getPath()+"/", pageNode);
			loadSubPages(pageNodeMap, pageNode, subPageNodes);
		}
	}
	
	public SitePageNode get(String domain, String path) {
		final Map<String, SitePageNode> pageNodeMap = map.get(domain);
		if(pageNodeMap  == null){
			throw new RuntimeException("Site not found for: " + domain);
		}
		return pageNodeMap.get(path);
	}
}

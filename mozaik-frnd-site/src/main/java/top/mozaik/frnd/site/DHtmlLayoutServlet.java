/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import static top.mozaik.frnd.site.PageAttrs.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.resource.Locator;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.DesktopRecycles;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.DesktopRecycle;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_ResourceSetType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.frnd.common.ResourcePackServicesFacade;
import top.mozaik.frnd.site.init.DbInit;

public class DHtmlLayoutServlet extends org.zkoss.zk.ui.http.DHtmlLayoutServlet  {

	private final ResourcePackService resPackService = ServicesFacade.$().getResourcePackService();
	private final ResourcePackSetService resPackSetService = ServicesFacade.$().getResourcePackSetService();
	
	public static final String DB_ATTR_NAME = "db";
	
	private WebManager webman;
	private boolean compress = true;
	
	private SitePageCache sitePageCache;
	private volatile boolean codeLoaded = false;
	
	@Override
	public void init() throws ServletException {
		
		System.out.println("Start init..");
		
		String realPath = getServletContext().getRealPath("");
		System.out.println(realPath);
		
		try {
			super.init();
			
			CodeLoader.getInstance().setWebAppPath(realPath);
		
			final Field compressField = org.zkoss.zk.ui.http.DHtmlLayoutServlet.class.getDeclaredField("_compress");
			compressField.setAccessible(true);
			compress = compressField.getBoolean(this);
			
			final Field webmanField = org.zkoss.zk.ui.http.DHtmlLayoutServlet.class.getDeclaredField("_webman");
			webmanField.setAccessible(true); 
			webman = (WebManager)webmanField.get(this);
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	protected boolean process(Session sess, HttpServletRequest request,
			HttpServletResponse response, String path, boolean bRichlet)
			throws ServletException, IOException {

		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final Configuration config = wapp.getConfiguration();

		final boolean isIncluded = Servlets.isIncluded(request);
		final boolean willBeCompress = this.compress && !isIncluded;
		final Writer out = willBeCompress ? (Writer)new StringWriter(): response.getWriter();
		final DesktopRecycle dtrc = isIncluded ? null: config.getDesktopRecycle();
		final ServletContext ctx = getServletContext();
		Desktop desktop = dtrc != null ?
			DesktopRecycles.beforeService(dtrc, ctx, sess, request, response, path): null;

		try {
			if (desktop != null) { //recycle
				final Page page = getMainPage(desktop);
				if (page != null) {
					final Execution exec = new ExecutionImpl(
						ctx, request, response, desktop, page);
					WebManager.setDesktop(request, desktop);
					wappc.getUiEngine().recycleDesktop(exec, page, out);
				} else
					desktop = null;
			}
			
			boolean voided = false;
			if (desktop == null) {
				desktop = webman.getDesktop(sess, request, response, path, true);
				if (desktop == null) //forward or redirect
					return true;
				
				//final Locator locator = PageDefinitions.getLocator(wapp, path);
				final Locator locator = null;//new SitePageLocator(path);
				final RequestInfo ri = new RequestInfoImpl(
					wapp, sess, desktop, request, locator);
				((SessionCtrl)sess).notifyClientRequest(true);
				
				final UiFactory uf = wappc.getUiFactory();
				
				if (path.startsWith("/@")) {
					final PageDefinition pagedef = uf.getPageDefinition(ri, path);
					if (pagedef == null)
						return false; //not found

					final Page page = WebManager.newPage(uf, ri, pagedef, response, path);
					final Execution exec = new ExecutionImpl(
						ctx, request, response, desktop, page);
					wappc.getUiEngine().execNewPage(exec, pagedef, page, out);
					voided = exec.isVoided();
				} else if(init(ctx, out)){
					
					final RequestResult res = handleRequest(wapp, request, isIncluded);
					//final PageDefinition pagedef = uf.getPageDefinition(ri, path);
					if (res.pageDefinition == null)
						return false; //not found
					
					final ClassLoader cl = CodeLoader.getInstance().get(res.resourcePack.getAlias());
					Thread.currentThread().setContextClassLoader(cl);
						
					final Page page = WebManager.newPage(uf, ri, res.pageDefinition, response, path);
					page.setAttribute(CONTEXT_PATH, getServletContext().getContextPath());
					page.setAttribute(CLASS_LOADER, cl);
					page.setAttribute(SITE, res.site);
					page.setAttribute(PAGE, res.page);
					page.setAttribute(RESOURCE_PACK, res.resourcePack);
					page.setAttribute(RESOURCE_SET, res.resourceSet);
					
					if(res.layoutSkinParams != null) {
						page.setAttribute(LAYOUT_SKIN_PARAMS, res.layoutSkinParams);
					}
					if(res.layoutWidgetParams != null) {
						page.setAttribute(LAYOUT_WIDGET_PARAMS, res.layoutWidgetParams);
					}
					if(res.includedResourcePack != null) {
						page.setAttribute(INCLUDED_RESOURCE_PACK, res.includedResourcePack);
					}
					if(res.includedResourceSet != null) {
						page.setAttribute(INCLUDED_RESOURCE_SET, res.includedResourceSet);
					}
						
					final Execution exec = new ExecutionImpl(ctx, request, response, desktop, page);
					wappc.getUiEngine().execNewPage(exec, res.pageDefinition, page, out);
					voided = exec.isVoided();
				}
			}

			// check voided to ignore the IOExecuption that caused by Executions.forward()
			if (willBeCompress && !voided) {
				final String result = ((StringWriter)out).toString();
				try {
					final OutputStream os = response.getOutputStream();
						//Call it first to ensure getWrite() is not called yet

					byte[] data = result.getBytes(config.getResponseCharset());
					if (data.length > 200) {
						final byte[] bs = Https.gzip(request, response, null, data);
						if (bs != null) data = bs; //yes, browser support compress
					}

					response.setContentLength(data.length);
					os.write(data);
					response.flushBuffer();
				} catch (IllegalStateException ex) { //getWriter is called
					response.getWriter().write(result);
				}		
			}
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			if (dtrc != null)
				DesktopRecycles.afterService(dtrc, desktop);
		}
		return true; //success
	}

	private boolean init(ServletContext ctx, Writer out) {
		try {
			final boolean dbOk = DbInit.init(Sessions.getCurrent(), ctx);
			if(dbOk) {
				if(sitePageCache == null)
					sitePageCache = new SitePageCache();
				if(!codeLoaded) {
					CodeLoader.getInstance().loadAll();
					codeLoaded = true;
				}
			} else {
				out.write("Database is not configured");
				return false;
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Page getMainPage(Desktop desktop) {
		for (Iterator<Page> it = desktop.getPages().iterator(); it.hasNext();) {
			final Page page = it.next();
			if (((PageCtrl)page).getOwner() == null)
				return page;
		}
		return null;
	}

	private static final class RequestResult {
	
		SiteNode site;
		SitePageNode page;
		
		ResourcePack resourcePack;
		ResourcePackSet resourceSet;
		
		ResourcePack includedResourcePack;
		ResourcePackSet includedResourceSet;
		
		PageDefinition pageDefinition;
		
		Map layoutSkinParams;
		Map layoutWidgetParams;
	}

	private RequestResult handleRequest(WebApp wapp, HttpServletRequest request, boolean include) throws Exception {
		final RequestResult res = new RequestResult();
		
		/*
		/// FIND SITE BY HOSTNAME
		final Site site = siteService.read1(new Site().setDomains(request.getServerName()).getFilter());
		if(site == null) {
			throw new RuntimeException("Site not found for: " + request.getServerName());
		}

		/// FIND PAGE BY PATH
		final String [] pages = request.getPathInfo().split("/");
		SitePage page = null;
		for(int i=0; i < pages.length; i++) {
			if(pages[i].length() == 0) continue;
			if(page == null)
				page = sitePageService.read1(
					new SitePage().setSiteId(site.getId()).setAlias(pages[i]).getFilter());
			else
				page = sitePageService.read1(
					new SitePage().setSiteId(site.getId()).setParentId(page.getId()).setAlias(pages[i]).getFilter());
		}
		if(page == null) {
			throw new RuntimeException("Page not found for: " + request.getPathInfo());
		}*/
		
		final SitePageNode sitePageNode = sitePageCache.get(request.getServerName(), request.getPathInfo());
		if(sitePageNode == null) {
			throw new RuntimeException("Page not found for: " + request.getPathInfo());
		}
		
		res.site = sitePageNode.getSite();
		res.page = sitePageNode;

		if(include) { /// INCLUDED SKIN OR WIDGET
			//System.out.println("include");			
			//for (Enumeration<String> e = request.getAttributeNames(); e.hasMoreElements();)
			//       System.out.println("-"+e.nextElement());
			
			final ResourcePack resPack = (ResourcePack) request.getAttribute(RESOURCE_PACK);
			final ResourcePackSet resSet = (ResourcePackSet) request.getAttribute(RESOURCE_SET);
			
			if(request.getParameter("debug") != null) {
				final ResourcePackServicesFacade rpsFacade = ResourcePackServicesFacade.get(resPack);
				final _ResourceData indexData =	 rpsFacade.getResourceService()
						.readWithData1(new _ResourceData().setResourceSetId(
								resSet.getResourceSetId()).setName("index.zul"));
			
				res.pageDefinition = PageDefinitions.getPageDefinitionDirectly(
											wapp, null, new String(indexData.getSourceData()), "zul");
			} else {
				res.pageDefinition = PageDefCache.$().getByName(resPack, resSet, "index.zul");
			}
			
			res.pageDefinition.setStyle(resPack.getAlias().replace('.', '-')+":"+resSet.getResourceSetId());
			res.resourcePack = resPack;
			res.resourceSet = resSet;
			res.includedResourcePack = (ResourcePack) request.getAttribute(INCLUDED_RESOURCE_PACK);
			res.includedResourceSet = (ResourcePackSet) request.getAttribute(INCLUDED_RESOURCE_SET);
			res.layoutSkinParams = (Map) request.getAttribute(LAYOUT_SKIN_PARAMS);
			res.layoutWidgetParams = (Map) request.getAttribute(LAYOUT_WIDGET_PARAMS);
			return res;
		}
		
		/// FIND THEME BY PAGE
		final ResourcePackSet themeResSet = resPackSetService.read1(
				new ResourcePackSet().setId(res.page.getDelegate().getThemeId())
				.setResourceSetType(E_ResourceSetType.THEME));
		
		if(themeResSet == null){
			throw new Exception("Theme not found: id="+res.page.getDelegate().getThemeId());
		}
		
		final ResourcePack themeResPack = resPackService.read1(
				new ResourcePack().setId(themeResSet.getResourcePackId()));
		
		final ResourcePackServicesFacade rpsFacade = ResourcePackServicesFacade.get(themeResPack);
		
		final _ResourceData _themeZul = rpsFacade.getResourceService()
			.readWithData1(new _ResourceData().setResourceSetId(
					themeResSet.getResourceSetId()).setName("index.zul"));
		
		res.resourcePack = themeResPack;
		res.resourceSet = themeResSet;
		res.pageDefinition = PageDefinitions.
				getPageDefinitionDirectly(wapp, null, new String(_themeZul.getSourceData()), "zul");
		res.pageDefinition.setStyle(themeResPack.getAlias().replace('.', '-')+":"+themeResSet.getResourceSetId());
		return res; 
	}

	@Override
	public void destroy() {
		super.destroy();
		
		final String prefix = getClass().getSimpleName() +" destroy() ";
		final ServletContext ctx = getServletContext();
		try {
			final Enumeration<Driver> drivers = DriverManager.getDrivers();
			while(drivers.hasMoreElements()) {
				DriverManager.deregisterDriver(drivers.nextElement());
			}
		} catch(Exception e) {
			ctx.log(prefix + "Exception caught while deregistering JDBC drivers", e);
		}
		ctx.log(prefix + "complete");
	}
}

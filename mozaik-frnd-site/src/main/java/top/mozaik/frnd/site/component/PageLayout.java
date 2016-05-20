/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site.component;

import static top.mozaik.frnd.site.PageAttrs.INCLUDED_RESOURCE_PACK;
import static top.mozaik.frnd.site.PageAttrs.INCLUDED_RESOURCE_SET;
import static top.mozaik.frnd.site.PageAttrs.LAYOUT_SKIN_PARAMS;
import static top.mozaik.frnd.site.PageAttrs.LAYOUT_WIDGET_PARAMS;
import static top.mozaik.frnd.site.PageAttrs.PAGE;
import static top.mozaik.frnd.site.PageAttrs.RESOURCE_PACK;
import static top.mozaik.frnd.site.PageAttrs.RESOURCE_SET;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model.SitePageLayout;
import top.mozaik.bknd.api.model.SitePageLayoutWidget;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.bknd.api.service.SitePageLayoutService;
import top.mozaik.bknd.api.service.SitePageLayoutWidgetService;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.site.SitePageNode;

public class PageLayout extends Div {
	
	private final ResourcePackService resourcePackService = ServicesFacade.$().getResourcePackService();
	private final ResourcePackSetService resourcePackSetService = ServicesFacade.$().getResourcePackSetService();
	private final SitePageLayoutService sitePageLayoutService = ServicesFacade.$().getSitePageLayoutService();
	private final SitePageLayoutWidgetService sitePageLayoutWidgetService =ServicesFacade.$().getSitePageLayoutWidgetService();
	
	public PageLayout() {
		
		setSclass("page-layout");
		
		final ExecutionCtrl execCtrl = ((ExecutionCtrl)Executions.getCurrent());
		final Page page = execCtrl.getCurrentPage();
		
		final SitePageNode sitePage = (SitePageNode) page.getAttribute(PAGE);
		
		final SitePageLayout rootLayout = sitePageLayoutService.read1(
				new SitePageLayout()
					.setPageId(sitePage.getId())
					.getFilter().putNullField("parentLayoutId")
					.getDelegate()
		);
		
		if(rootLayout == null) {
			getChildren().add(new Label("Layout not found for " + page.getTitle()));
			return;
		}
		try {
			buildLayout(rootLayout, this, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void buildLayout(final SitePageLayout layout, Component parent, String style, String clazz) {
		try {
		// FIND WIDGET
		final SitePageLayoutWidget layoutWidget = sitePageLayoutWidgetService.read1(
				new SitePageLayoutWidget().setLayoutId(layout.getId()));
		if(layoutWidget != null){
			
			final Include include = new Include("/index.zul");
			include.setMode("defer");
			
			final ResourcePackSet widgetResSet = resourcePackSetService.read1(
					new ResourcePackSet().setId(layoutWidget.getWidgetId()));
			
			final ResourcePack widgetResPack = resourcePackService.read1(
					new ResourcePack().setId(widgetResSet.getResourcePackId()));
			
			// CHECK IF WIDGET WRAPPED BY SKIN
			if(layoutWidget.getSkinId() != null) {
				final ResourcePackSet skinResSet = resourcePackSetService.read1(
						new ResourcePackSet().setId(layoutWidget.getSkinId()));
				final ResourcePack skinResPack = resourcePackService.read1(
						new ResourcePack().setId(skinResSet.getResourcePackId()));
				include.setDynamicProperty(RESOURCE_PACK, skinResPack);
				include.setDynamicProperty(RESOURCE_SET, skinResSet);
				include.setDynamicProperty(INCLUDED_RESOURCE_PACK, widgetResPack);
				include.setDynamicProperty(INCLUDED_RESOURCE_SET, widgetResSet);
			} else {
				include.setDynamicProperty(RESOURCE_PACK, widgetResPack);
				include.setDynamicProperty(RESOURCE_SET, widgetResSet);
			}
			if(layoutWidget.getSkinParams() != null) {
				include.setDynamicProperty(LAYOUT_SKIN_PARAMS, parseParams(layoutWidget.getSkinParams()));
			}
			if(layoutWidget.getWidgetParams() != null) {
				include.setDynamicProperty(LAYOUT_WIDGET_PARAMS, parseParams(layoutWidget.getWidgetParams()));
			}
			
			//include.setDynamicProperty(LAYOUT_WIDGET_TITLE, widgetRef.getTitle());
			
			final HtmlNativeComponent table = new HtmlNativeComponent("table");
			if(style == null) {
				initTable(table, getStyle(layout), layout.getClazz());
			} else {
				initTable(table, style + getStyle(layout), layout.getClazz());
			}
			final HtmlNativeComponent tr = new HtmlNativeComponent("tr");
			final HtmlNativeComponent td = new HtmlNativeComponent("td");
			tr.appendChild(td);
			td.appendChild(include);
			table.appendChild(tr);
			parent.appendChild(table);
			return;
		}
		
		// FIND CHILD LAYOUTS
		final SitePageLayout filter = new SitePageLayout();
		filter.setParentLayoutId(layout.getId());
		filter.getFilter().setSort("id");
		final List<SitePageLayout> childs = sitePageLayoutService.read(filter);
		
		if(childs.size() == 0) return;
		
		final HtmlNativeComponent table = new HtmlNativeComponent("table");
		initTable(table, getStyle(layout), layout.getClazz());
		
		parent.appendChild(table);
		
		if(layout.getOrient() == 0) { // hor
			if(childs.size() > 1) {
				final HtmlNativeComponent colgroup = new HtmlNativeComponent("colgroup");
				for(final SitePageLayout child : childs) {
					final HtmlNativeComponent col = new HtmlNativeComponent("col");
					if(child.getWidth() != null){
						col.setDynamicProperty("width", child.getWidth()+"px");
					}
					colgroup.appendChild(col);
				}
				table.appendChild(colgroup);
			}
			final HtmlNativeComponent tr = new HtmlNativeComponent("tr");
			table.appendChild(tr);
			for(final SitePageLayout child : childs) {
				final HtmlNativeComponent td = new HtmlNativeComponent("td");
				tr.appendChild(td);
				buildLayout(child, td, getStyle(child), child.getClazz());
			}
		} else { // ver
			for(final SitePageLayout child : childs) {
				final HtmlNativeComponent tr = new HtmlNativeComponent("tr");
				final HtmlNativeComponent td = new HtmlNativeComponent("td");
				tr.appendChild(td);
				table.appendChild(tr);
				buildLayout(child, td, getStyle(child), child.getClazz());
			}
		}
		
		} catch (final Exception e) {
			final Hlayout hlayout = new Hlayout();
			final Label label = new Label("Error occured. ");
			final Label label2 = new Label("Show details");
			label2.setStyle("cursor:pointer;text-decoration:underline");
			label2.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					Dialog.error("Error occured", e);
				};
			});
			
			hlayout.appendChild(label);
			hlayout.appendChild(label2);
			parent.appendChild(hlayout);
		}
	}
	
	private Map<String, String> parseParams(String params) {
		final String[] keyValues = params.split(";");
		final Map<String,String> map = new HashMap<>();
		for(String keyValuePair : keyValues) {
			final String [] keyValue = keyValuePair.split("=");
			map.put(keyValue[0], new String(DatatypeConverter.parseHexBinary(keyValue[1])));
		}
		return map;
	}
	
	private String getStyle(SitePageLayout layout) {
		final StringBuilder sb = new StringBuilder();
		
		if(layout.getMinWidth() != null) {
			sb.append("min-width:").append(layout.getMinWidth()).append("px;");
		}
		if(layout.getWidth() != null) {
			sb.append("width:").append(layout.getWidth()).append("px;");
		}
		if(layout.getMaxWidth() != null) {
			sb.append("max-width:").append(layout.getMaxWidth()).append("px;");
		}
		if(layout.getMinHeight() != null) {
			sb.append("min-height:").append(layout.getMinHeight()).append("px;");
		}
		if(layout.getHeight() != null) {
			sb.append("height:").append(layout.getHeight()).append("px;");
		}
		if(layout.getMaxHeight() != null) {
			sb.append("max-height:").append(layout.getMaxHeight()).append("px;");
		}
		
		if(layout.getIndentTop() != null) {
			sb.append("padding-top:").append(layout.getIndentTop()).append("px;");
		}
		if(layout.getIndentRight() != null) {
			sb.append("padding-right:").append(layout.getIndentRight()).append("px;");
		}
		if(layout.getIndentBottom() != null) {
			sb.append("padding-bottom:").append(layout.getIndentBottom()).append("px;");
		}
		if(layout.getIndentLeft() != null) {
			sb.append("padding-left:").append(layout.getIndentLeft()).append("px;");
		}
		if(layout.getStyle() != null) {
			sb.append(layout.getStyle());
		}
		return sb.toString();
	}
	
	private void initTable(HtmlNativeComponent table, String style, String clazz){
		if(style != null) {
			table.setDynamicProperty("style", style);
		}
		if(clazz == null || clazz.length() == 0) {
			table.setDynamicProperty("class", "layout");
		} else {
			table.setDynamicProperty("class", "layout " + clazz);
		}
		/*table.setDynamicProperty("width", "100%");
		table.setDynamicProperty("cellspacing", "0");
		table.setDynamicProperty("cellpadding", "0");*/
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.site.page.layout;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Layout;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model.SitePageLayout;
import top.mozaik.bknd.api.model.SitePageLayoutWidget;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.bknd.api.service.ResourcePackService;
import top.mozaik.bknd.api.service.ResourcePackSetService;
import top.mozaik.bknd.api.service.SitePageLayoutService;
import top.mozaik.bknd.api.service.SitePageLayoutWidgetService;
import top.mozaik.frnd.admin.bean.site.SitePageLayoutBean;
import top.mozaik.frnd.common.ResourcePackServicesFacade;
import top.mozaik.frnd.plus.callback.I_Callback;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.command.I_CommandExecutor;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class PageLayoutVM extends BaseVM implements I_CommandExecutor {
	
	private final SitePageLayoutService sitePageLayoutService = ServicesFacade.$().getSitePageLayoutService();
	private final SitePageLayoutWidgetService sitePageLayoutWidgetService = ServicesFacade.$().getSitePageLayoutWidgetService();
	private final ResourcePackSetService resPackSetService = ServicesFacade.$().getResourcePackSetService();
	private final ResourcePackService resPackService = ServicesFacade.$().getResourcePackService();
	
	private Integer pageId;
	private SitePageLayoutBean bean;
	private Layout childsLayout;
	
	private SitePageLayoutWidget layoutWidget;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(@ExecutionArgParam("pageId") Integer pageId,
			@ExecutionArgParam("sitePageLayoutBean") SitePageLayoutBean bean){
		this.pageId = pageId;
		this.bean = bean;
		this.bean.setCommandExecutor(this);
		
		initIndent();
		// APPEND CONTAINER FOR CHILDS
		initChildsLayout();

		// CHECK IF LAYOUT CONTAIN WIDGET
		if (bean.getId() != null) {
			layoutWidget = sitePageLayoutWidgetService.read1(
					new SitePageLayoutWidget().setLayoutId(bean.getId()));

			if (layoutWidget != null) {
				
				if(layoutWidget.getSkinId() != null) {
					final ResourcePackSet skin = resPackSetService.read1(
							new ResourcePackSet().setId(layoutWidget.getSkinId()));
					bean.setSkin(skin);
				}
				
				final ResourcePackSet widget = resPackSetService.read1(
						new ResourcePackSet().setId(layoutWidget.getWidgetId()));
				bean.setWidget(widget);
				
				renderWidgetByValue();
				return;
			}
		} else {
			if (bean.getWidget() != null) {
				renderWidgetByValue();
				return;
			}
		}
		
		// LOAD CHILD'S LAYOUTS
		// CHECK IF CHILDRENS ALREADY LOADED THEN JUST APPEND TO RENDER
		if(bean.getId() == null) {
			if(bean.childsIsNull()) return;
			for(int i=0; i<bean.size();i++) {
				final SitePageLayoutBean childBean = bean.get(i);
				this.childsLayout.appendChild(createLayoutComponent(childBean));
			}
			return;
		}
		
		final List<SitePageLayout> layouts = getChildLayouts(bean.getId());
		for(SitePageLayout layout : layouts) {
			final SitePageLayoutBean childBean = new SitePageLayoutBean(layout);
			bean.addChild(childBean);
			this.childsLayout.appendChild(createLayoutComponent(childBean));
		}
	}
	
	@Override
	public void execCommand(int cmdId) {
		if(bean.getSkin() != null && bean.getWidget() == null) {
			throw new WrongValueException(
				getView().query("menubar"), "Skin was set but widget did not");
		}
	}
	
	private void initIndent() {
		final StringBuilder sb = new StringBuilder();
		if(bean.getIndentTop() != null) {
			//margin.append("margin-top:").append(bean.getMarginTop()).append("px;");
			sb.append("border-top:").append(bean.getIndentTop()).append("px solid gold;");
		}
		if(bean.getIndentRight() != null) {
			//margin.append("margin-right:").append(bean.getMarginRight()).append("px;");
			sb.append("border-right:").append(bean.getIndentRight()).append("px solid gold;");
		}
		if(bean.getIndentBottom() != null) {
			//margin.append("margin-bottom:").append(bean.getMarginBottom()).append("px;");
			sb.append("border-bottom:").append(bean.getIndentBottom()).append("px solid gold;");
		}
		if(bean.getIndentLeft() != null) {
			//margin.append("margin-left:").append(bean.getMarginLeft()).append("px;");
			sb.append("border-left:").append(bean.getIndentLeft()).append("px solid gold;");
		}
		final Vlayout view = (Vlayout) getView();
		if(sb.length() == 0) {
			view.setStyle(null);
		} else {
			view.setStyle(sb.toString());
		}
	}
	
	private void renderWidgetByValue() {
		bean.setOrientVer();
		initChildsLayout();
		
		renderSkinToolbar(bean.getSkin());
		renderWidgetToolbar(bean.getWidget());
	}
	
	private void renderSkinToolbar(final ResourcePackSet skin) {
		if(skin == null) return;
		
		final ResourcePack resPack = resPackService.read1(
				new ResourcePack().setId(skin.getResourcePackId()));
		
		final Hlayout layout = new Hlayout();
		childsLayout.appendChild(layout);
		
		final Label label = new Label("Skin: "+skin.getTitle());
		label.setTooltiptext(resPack.getTitle());
		layout.appendChild(label);
		
		// check if pair of the layout and widget has settings (layout_settings.zul)
		final ResourcePackServicesFacade facade = ResourcePackServicesFacade.get(resPack);
		final _Resource layoutParamsRes = facade.getResourceService().read1(
				new _Resource()
					.setResourceSetId(skin.getResourceSetId())
					.setName("layout_params.form.zul")
		);
		
		if(layoutParamsRes == null) return;
		
		final Image paramsImage = new Image("/media/settings16.svg");
		paramsImage.setStyle("cursor:pointer");
		paramsImage.setTooltiptext("Parameters");
		
		layout.appendChild(paramsImage);
		
		if(layoutWidget != null) {	
			bean.setLayoutSkinParams(layoutWidget.getSkinParams());
		}
		
		paramsImage.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				final Window wnd = new Window("Skin parameters: "+ skin.getTitle(), null, true);
				wnd.setParent(getView());
				wnd.addEventListener(Events.ON_CLOSE, new EventListener<Event>(){
					@Override
					public void onEvent(Event event) throws Exception {
						ZKUtils.detachAll(wnd);
					}
				});
				
				final String content = new String(
						facade.getResourceService().readWithData1(layoutParamsRes).getSourceData());
				
				//final Map<String,Object> args = new HashMap<>();
				
				if(bean.getLayoutSkinParams() == null){
					Executions.getCurrent().setAttribute("LAYOUT_SKIN_PARAMS", new HashMap<>());
				} else {
					//args.put("LAYOUT_WIDGET_PARAMS", parseLayoutWidgetParams(bean.getLayoutWidgetParams()));
					Executions.getCurrent().setAttribute("LAYOUT_SKIN_PARAMS", parseParams(bean.getLayoutSkinParams()));
				}
				
				Executions.getCurrent().setAttribute("FORM_CALLBACK", new I_CallbackArg<Map<String, Object>>() {
				//args.put("CALLBACK", new I_CallbackMap<String, Object>() {
					@Override
					public void call(Map<String, Object> map) {
						//System.out.println(map);
						if(map == null || map.size() == 0){
							bean.setLayoutSkinParams(null);
						} else {
							final StringBuilder sb = new StringBuilder();
							for(Map.Entry<String,Object> entry : map.entrySet()) {
								if(sb.length() > 0) {
									sb.append(";");
								}
								final Object value = entry.getValue();
								if(value == null) {
									throw new IllegalArgumentException("Value for key '"+ entry.getKey() + "' is null");
								}
								sb.append(validateParamName(entry.getKey()))
									.append("=")
									.append(DatatypeConverter.printHexBinary(value.toString().getBytes()));
							}
							bean.setLayoutSkinParams(sb.toString());
						}
						wnd.detach();
					}
				});
				final Component form = Executions.createComponentsDirectly(content, "zul", null, null/*args*/);
				if(form != null) {
					wnd.appendChild(form);
				}
				wnd.doModal();
			};
		});
	}
	
	private void renderWidgetToolbar(final ResourcePackSet widget) {
		if(widget == null) return;
		
		final ResourcePack resPack = resPackService.read1(
				new ResourcePack().setId(widget.getResourcePackId()));
		
		final Hlayout layout = new Hlayout();
		childsLayout.appendChild(layout);
		
		final Label label = new Label("Widget: "+widget.getTitle());
		label.setTooltiptext(resPack.getTitle());
		layout.appendChild(label);
		
		// check if pair of the layout and widget has settings (layout_settings.zul)
		final ResourcePackServicesFacade facade = ResourcePackServicesFacade.get(resPack);
		final _Resource layoutParamsRes = facade.getResourceService().read1(
				new _Resource()
					.setResourceSetId(widget.getResourceSetId())
					.setName("layout_params.form.zul")
		);
		
		if(layoutParamsRes == null) return;
		
		final Image paramsImage = new Image("/media/settings16.svg");
		paramsImage.setStyle("cursor:pointer");
		paramsImage.setTooltiptext("Parameters");
		
		layout.appendChild(paramsImage);
		
		if(bean.getLayoutWidgetParams() == null && layoutWidget != null) {
			bean.setLayoutWidgetParams(layoutWidget.getWidgetParams());
		}
		
		paramsImage.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				final Window wnd = new Window("Widget parameters: "+ widget.getTitle(), null, true);
				wnd.setParent(getView());
				wnd.addEventListener(Events.ON_CLOSE, new EventListener<Event>(){
					@Override
					public void onEvent(Event event) throws Exception {
						ZKUtils.detachAll(wnd);
					}
				});
				
				final String content = new String(
						facade.getResourceService().readWithData1(layoutParamsRes).getSourceData());
				
				//final Map<String,Object> args = new HashMap<>();
				
				if(bean.getLayoutWidgetParams() == null){
					Executions.getCurrent().setAttribute("LAYOUT_WIDGET_PARAMS", new HashMap<>());
				} else {
					//args.put("LAYOUT_WIDGET_PARAMS", parseLayoutWidgetParams(bean.getLayoutWidgetParams()));
					Executions.getCurrent().setAttribute("LAYOUT_WIDGET_PARAMS", parseParams(bean.getLayoutWidgetParams()));
				}
				
				Executions.getCurrent().setAttribute("FORM_CALLBACK", new I_CallbackArg<Map<String, Object>>() {
				//args.put("CALLBACK", new I_CallbackMap<String, Object>() {
					@Override
					public void call(Map<String, Object> map) {
						//System.out.println(map);
						if(map == null || map.size() == 0){
							bean.setLayoutWidgetParams(null);
						} else {
							final StringBuilder sb = new StringBuilder();
							for(Map.Entry<String,Object> entry : map.entrySet()) {
								if(sb.length() > 0) {
									sb.append(";");
								}
								final Object value = entry.getValue();
								if(value == null) {
									throw new IllegalArgumentException("Value for key '"+ entry.getKey() + "' is null");
								}
								sb.append(validateParamName(entry.getKey()))
									.append("=")
									.append(DatatypeConverter.printHexBinary(value.toString().getBytes()));
							}
							bean.setLayoutWidgetParams(sb.toString());
						}
						wnd.detach();
					}
				});
				final Component form = Executions.createComponentsDirectly(content, "zul", null, null/*args*/);
				if(form != null) {
					wnd.appendChild(form);
				}
				wnd.doModal();
			};
		});
	}
	
	private String validateParamName(String name) {
		if(!name.matches("[A-Za-z0-9_:$]+")) {
			throw new IllegalArgumentException("Name '" + name + 
					"' is not valid. Only A-Z a-z 0-9 _:$ are allowed.");
		}
		return name;
	}
	
	private Map<String, String> parseParams(String params) {
		final String[] keyValues = params.split(";");
		final Map<String,String> map = new LinkedHashMap<>(keyValues.length);
		for(String keyValuePair : keyValues) {
			final String [] keyValue = keyValuePair.split("=");
			map.put(keyValue[0], new String(DatatypeConverter.parseHexBinary(keyValue[1])));
		}
		return map;
	}
	
	private void initChildsLayout() {
		if(bean.isOrientHor()) {
			setChildsLayout(new Hlayout());
		} else {
			setChildsLayout(new Vlayout());
		}
	}
	
	private void removeChildsLayout() {
		if(childsLayout != null) {
			getView().removeChild(childsLayout);
		}
	}
	
	private void setChildsLayout(Layout layout) {
		removeChildsLayout();
		
		childsLayout = layout;
		setupLayout(childsLayout);
		getView().appendChild(childsLayout);
	}
	
	private void setupLayout(Layout layout) {
		layout.setHflex("true");
		layout.setVflex("true");
		layout.setSclass("mozaik-page-layout-childs");
	}
	
	private List<SitePageLayout> getChildLayouts(Integer parentId) {		
		final SitePageLayout filter = new SitePageLayout()
			.setParentLayoutId(parentId);
		filter.getFilter().setSort("id");
		return sitePageLayoutService.read(filter);
	}
	
	private Component createLayoutComponent(SitePageLayoutBean bean) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("pageId", pageId);
		args.put("sitePageLayoutBean", bean);
		return Executions.createComponents("/WEB-INF/zul/site/page/layout/layout.zul", getView(), args);
	}
	
	/// BINDINGS ///
	
	public SitePageLayoutBean getBean() {
		return bean;
	}

	/// COMMANDS ///
	
	@Command
	public void setStyle() {
		final Map<String, Object> args = new HashMap<String, Object>();
		final I_Callback callback = new I_Callback() {
			@Override
			public void call() {
				initIndent();
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			}
		};
		args.put("sitePageLayoutBean", this.bean);
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/site/page/layout/setStyle.wnd.zul", getView(), args);
	}
	
	@Command
	public void setWidget() {
		final Map<String, Object> args = new HashMap<String, Object>();
		final I_CallbackArg<ResourcePackSet> callback = new I_CallbackArg<ResourcePackSet>() {
			@Override
			public void call(ResourcePackSet widget) {
				bean.setWidget(widget);
				renderWidgetByValue();
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			}
		};
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/site/page/layout/selectWidget.wnd.zul", getView(), args);
	}
	
	@Command
	public void setSkin() {
		final Map<String, Object> args = new HashMap<String, Object>();
		final I_CallbackArg<ResourcePackSet> callback = new I_CallbackArg<ResourcePackSet>() {
			@Override
			public void call(ResourcePackSet skin) {
				bean.setSkin(skin);
				renderWidgetByValue();
				reloadComponent(); /// ASYNC, SO NEED RELOAD BINDER MANUALLY
			}
		};
		args.put("callback", callback);
		Executions.createComponents("/WEB-INF/zul/site/page/layout/selectSkin.wnd.zul", getView(), args);
	}

	@Command
	@NotifyChange("bean")
	public void deleteWidget() {
		bean.setLayoutWidgetParams(null);
		layoutWidget.setWidgetParams(null);
		bean.setWidget(null);
		bean.setOrientHor(); // TO DEFAULT
		renderWidgetByValue();
	}
	
	@Command
	@NotifyChange("bean")
	public void deleteSkin() {
		bean.setLayoutSkinParams(null);
		layoutWidget.setSkinParams(null);
		bean.setSkin(null);
		renderWidgetByValue();
	}
	
	@Command
	@NotifyChange("bean")
	public void addLayoutRight() {
		if(bean.isOrientVer()) {
			if(bean.size() > 0) {
				/// wrap all child layouts by the new one and create other new at the right
				setChildsLayout(new Hlayout());
				bean.setOrientHor();
			
				final SitePageLayoutBean wrapBean = new SitePageLayoutBean();
				wrapBean.setOrientVer();
				for(int i=0; i<bean.size();i++) {
					wrapBean.addChild(bean.get(i));
				}
				bean.clear();
				bean.addChild(wrapBean);
			
				childsLayout.appendChild(createLayoutComponent(wrapBean));
				
				final SitePageLayoutBean newBean = new SitePageLayoutBean();
				bean.addChild(newBean);
				childsLayout.appendChild(createLayoutComponent(newBean));
				return;
			} else {
				bean.setOrientHor();
				setChildsLayout(new Hlayout());
			}
		}
		final SitePageLayoutBean newBean = new SitePageLayoutBean();
		bean.addChild(newBean);
		childsLayout.appendChild(createLayoutComponent(newBean));
		getView().getParent().invalidate();
	}
	
	@Command
	@NotifyChange("bean")
	public void addLayoutLeft() {
		if(bean.isOrientVer()) {
			if(bean.size() > 0) {
				setChildsLayout(new Hlayout());
				bean.setOrientHor();
			
				final SitePageLayoutBean newBean = new SitePageLayoutBean();
				newBean.setOrientVer();
				childsLayout.appendChild(createLayoutComponent(newBean));
			
				final SitePageLayoutBean wrapBean = new SitePageLayoutBean();
				for(int i=0; i<bean.size();i++) {
					wrapBean.addChild(bean.get(i));
				}
				childsLayout.appendChild(createLayoutComponent(wrapBean));
			
				bean.clear();
				bean.addChild(newBean);
				bean.addChild(wrapBean);
				return;
			} else {
				bean.setOrientHor();
				setChildsLayout(new Hlayout());
			}
		}
		
		final SitePageLayoutBean newBean = new SitePageLayoutBean();
		bean.addChild(0, newBean);
		childsLayout.insertBefore(createLayoutComponent(newBean), childsLayout.getFirstChild());
		getView().getParent().invalidate();
	}
	
	@Command
	@NotifyChange("bean")
	public void addLayoutTop() {
		if(bean.isOrientHor()) {
			if(bean.size() > 0) {
				setChildsLayout(new Vlayout());
				bean.setOrientVer();
			
				final SitePageLayoutBean newBean = new SitePageLayoutBean();
				childsLayout.appendChild(createLayoutComponent(newBean));
			
				final SitePageLayoutBean wrapBean = new SitePageLayoutBean();
				for(int i=0; i<bean.size();i++) {
					wrapBean.addChild(bean.get(i));
				}
				childsLayout.appendChild(createLayoutComponent(wrapBean));
			
				bean.clear();
				bean.addChild(newBean);
				bean.addChild(wrapBean);
				return;
			} else {
				bean.setOrientVer();
				setChildsLayout(new Vlayout());
			}
		}
		
		final SitePageLayoutBean newBean = new SitePageLayoutBean();
		bean.addChild(0, newBean);
		childsLayout.insertBefore(createLayoutComponent(newBean), childsLayout.getFirstChild());
		getView().getParent().invalidate();
	}
	
	@Command
	@NotifyChange("bean")
	public void addLayoutBottom() {
		if(bean.isOrientHor()) {
			if(bean.size() > 0) {
				/// wrap all child layouts by the new one and create other new at the right
				setChildsLayout(new Vlayout());
				bean.setOrientVer();
			
				final SitePageLayoutBean wrapBean = new SitePageLayoutBean();
				for(int i=0; i<bean.size();i++) {
					wrapBean.addChild(bean.get(i));
				}
				bean.clear();
				bean.addChild(wrapBean);
				
				childsLayout.appendChild(createLayoutComponent(wrapBean));
				
				final SitePageLayoutBean newBean = new SitePageLayoutBean();
				bean.addChild(newBean);
				childsLayout.appendChild(createLayoutComponent(newBean));
				return;
			} else {
				bean.setOrientVer();
				setChildsLayout(new Vlayout());
			}
		}
		final SitePageLayoutBean newBean = new SitePageLayoutBean();
		bean.addChild(newBean);
		childsLayout.appendChild(createLayoutComponent(newBean));
		getView().getParent().invalidate();
	}
	
	
	@Command
	public void setOrientHor() {
		bean.setOrientHor();
		final List<Component> childs = this.childsLayout.getChildren();
		getView().removeChild(childsLayout);
		
		this.childsLayout = new Hlayout();
		setupLayout(childsLayout);
		
		while(childs.size() > 0 ) {
			this.childsLayout.appendChild(childs.get(0));
		}
		
		getView().appendChild(childsLayout);
	}
	
	@Command
	public void setOrientVer() {
		bean.setOrientVer();
		final List<Component> childs = this.childsLayout.getChildren();
		getView().removeChild(childsLayout);
		
		this.childsLayout = new Vlayout();
		setupLayout(childsLayout);
		
		while(childs.size() > 0 ) {
			this.childsLayout.appendChild(childs.get(0));
		}
		getView().appendChild(childsLayout);
	}
	
	@Command
	public void delete() {
		if(bean.getParent() == null) return;
		
		bean.getParent().removeChild(bean);
		
		// NOTIFY PARENT AFTER DELETE CHILD TO RE-RENDER HIS MENU
		final BaseVM parentController = getParentVM();
		if(parentController != null) {
			parentController.reloadComponent();
		}
		
		getView().getParent().removeChild(getView());
	}
}

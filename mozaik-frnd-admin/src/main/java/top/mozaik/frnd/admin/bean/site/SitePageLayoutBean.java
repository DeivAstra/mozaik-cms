/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.site;

import java.util.ArrayList;
import java.util.List;

import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model.SitePageLayout;
import top.mozaik.frnd.common.bean.A_Bean;
import top.mozaik.frnd.plus.command.I_CommandExecutor;
import top.mozaik.frnd.plus.zk.tree.I_TreeNode;

public class SitePageLayoutBean extends A_Bean<SitePageLayout> 
	implements I_TreeNode<SitePageLayoutBean, SitePageLayoutBean, ResourcePackSet>{
	
	private SitePageLayoutBean parent;
	private List<SitePageLayoutBean> childs;
	
	private ResourcePackSet widget;
	private ResourcePackSet skin;
	private String layoutSkinParams;
	private String layoutWidgetParams;
	private I_CommandExecutor commandExecutor;
	
	public SitePageLayoutBean() {
		this(new SitePageLayout());
	}

	public SitePageLayoutBean(SitePageLayout delegate) {
		super(delegate);
		if(delegate.getOrient() == null)
			setOrientHor();
	}
	@Override
	public void setParent(SitePageLayoutBean parent) {
		this.parent = parent;
		if(parent == null) return;
		delegate.setPageId(parent.getPageId());
		delegate.setParentLayoutId(parent.getId());
	}

	@Override
	public SitePageLayoutBean getParent() {
		return parent;
	}
	
	public void setWidget(ResourcePackSet widget) {
		this.widget = widget;
	}
	
	public ResourcePackSet getWidget() {
		return widget;
	}
	
	public void setSkin(ResourcePackSet skin) {
		this.skin = skin;
	}
	
	public ResourcePackSet getSkin() {
		return skin;
	}
	
	@Override
	public ResourcePackSet getValue() {
		return widget;
		//throw new UnsupportedOperationException();
	}
	
	public String getLayoutSkinParams() {
		return layoutSkinParams;
	}
	
	public void setLayoutSkinParams(String layoutSkinParams) {
		this.layoutSkinParams = layoutSkinParams;
	}
	
	public String getLayoutWidgetParams() {
		return layoutWidgetParams;
	}
	
	public void setLayoutWidgetParams(String layoutWidgetParams) {
		this.layoutWidgetParams = layoutWidgetParams;
	}
	
	public void setCommandExecutor(I_CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}
	
	public I_CommandExecutor getCommandExecutor() {
		return commandExecutor;
	}
	
	@Override
	public void addChild(SitePageLayoutBean child) {
		if(child == null) throw new IllegalArgumentException("null");
		if(childs == null) childs = new ArrayList<SitePageLayoutBean>();
		childs.add(child);
		child.setPageId(getPageId());
		child.setParent(this);
	}
	
	public void addChild(int index, SitePageLayoutBean child) {
		if(child == null) throw new IllegalArgumentException("null");
		if(childs == null) childs = new ArrayList<SitePageLayoutBean>();
		childs.add(index, child);
		child.setPageId(getPageId());
		child.setParent(this);
	}
	
	@Override
	public boolean removeChild(SitePageLayoutBean child) {
		if(child == null) throw new IllegalArgumentException("null");
		if(!childs.remove(child)) {
			throw new IllegalArgumentException("Child to remove not found. " + child);
		}
		child.setParent(null);
		return false;
	}

	@Override
	public boolean childsIsNull() {
		return childs == null;
	}

	@Override
	public SitePageLayoutBean get(int index) {
		return childs.get(index);
	}

	@Override
	public int size() {
		if(childs == null) return 0;
		return childs.size();
	}
	
	/// FOR ZK BINDING
	public int getSize() {
		return size();
	}
	
	public void clear() {
		if(childs == null) return;
		childs.clear();
	}
	
	//// FROM DELEGATE ///
	
	public void setId(Integer id) {
		delegate.setId(id);
	}
	
	public Integer getId() {
		return delegate.getId();
	}
	
	public void setPageId(Integer pageId) {
		delegate.setPageId(pageId);
	}
	
	public Integer getPageId() {
		return delegate.getPageId();
	}
	
	public void setParentLayoutId(Integer parentLayoutId) {
		delegate.setParentLayoutId(parentLayoutId);
	}
	
	public Integer getParentLayoutId() {
		return delegate.getParentLayoutId();
	}
	
	public void setOrientHor() {
		delegate.setOrient(0);
	}
	
	public void setOrientVer() {
		delegate.setOrient(1);
	}
	
	public boolean isOrientHor() {
		return delegate.getOrient() == 0;
	}
	
	public boolean isOrientVer() {
		return delegate.getOrient() > 0;
	}
	
	public Integer getMinWidth() {
		return delegate.getMinWidth();
	}
	
	public void setMinWidth(Integer minWidth) {
		delegate.setMinWidth(minWidth);
	}
	
	public Integer getWidth() {
		return delegate.getWidth();
	}
	
	public void setWidth(Integer width) {
		delegate.setWidth(width);
	}
	
	public Integer getMaxWidth() {
		return delegate.getMaxWidth();
	}
	
	public void setMaxWidth(Integer maxWidth) {
		delegate.setMaxWidth(maxWidth);
	}
	
	public Integer getMinHeight() {
		return delegate.getMinHeight();
	}
	
	public void setMinHeight(Integer minHeight) {
		delegate.setMinHeight(minHeight);
	}
	
	public Integer getHeight() {
		return delegate.getHeight();
	}
	
	public void setHeight(Integer height) {
		delegate.setHeight(height);
	}
	
	public Integer getMaxHeight() {
		return delegate.getMaxHeight();
	}
	
	public void setMaxHeight(Integer maxHeight) {
		delegate.setMaxHeight(maxHeight);
	}
	public Integer getIndentTop() {
		return delegate.getIndentTop();
	}

	public void setIndentTop(Integer indentTop) {
		delegate.setIndentTop(indentTop);
	}

	public Integer getIndentRight() {
		return delegate.getIndentRight();
	}

	public void setIndentRight(Integer indentRight) {
		delegate.setIndentRight(indentRight);
	}

	public Integer getIndentBottom() {
		return delegate.getIndentBottom();
	}

	public void setIndentBottom(Integer indentBottom) {
		delegate.setIndentBottom(indentBottom);
	}

	public Integer getIndentLeft() {
		return delegate.getIndentLeft();
	}

	public void setIndentLeft(Integer indentLeft) {
		delegate.setIndentLeft(indentLeft);
	}
	
	public String getClazz() {
		return delegate.getClazz();
	}
	
	public void setClazz(String clazz) {
		delegate.setClazz(clazz);
	}
	
	public String getStyle()  {
		return delegate.getStyle();
	}
	
	public void setStyle(String style) {
		delegate.setStyle(style);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName())
			.append(",delegate").append("=").append(delegate)
			.append(",widget").append("=").append(widget)
			.append(",skin").append("=").append(skin)
			.append(",layoutSkinParams").append("=").append(layoutSkinParams)
			.append(",layoutWidgetParams").append("=").append(layoutWidgetParams)
			.toString();
	}

	@Override
	public void setValue(ResourcePackSet value) {
	}
}

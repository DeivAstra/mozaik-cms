/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.model;

import org.zkoss.zul.AbstractTreeModel;

import top.mozaik.frnd.plus.zk.component.Dialog;

public class TestTreeModel extends AbstractTreeModel<TreeElement> {
	
	//private final List<TreeNodeViewBean> root;
	
	private static TestTreeModel instance;
	private static TreeElement ROOT_ELEMENT;
	
	public synchronized static final TestTreeModel getInstance(){
		if(instance == null) {
			ROOT_ELEMENT = new TreeElement(false);
		}
		instance = new TestTreeModel(ROOT_ELEMENT);
		return instance;
	}
		
	
	private TestTreeModel(TreeElement root) {
		super(root);
		
		/*root.addChild(new MarkViewBean(0L, "Model 0"));
		root.addChild(new MarkViewBean(1L, "Model 1"));
		root.addChild(new MarkViewBean(2L, "Model 2"));*/
		
	}

	private static final Long ROOT_ID = new Long(-1);
	/*
	private final ISearchDocService service;
	private final Long docTreeId;
	
	
	
	public DocTreeModel(ISearchDocService service, Long docTreeId) throws Exception {
		super(buildRootElement(service, docTreeId));
		this.service = service;
		this.docTreeId = docTreeId;
	}

	private static ModelViewBean buildRootElement(ISearchDocService service, Long docTreeId) throws Exception {
		final ModelViewBean root = new ModelViewBean();
		root.setId(ROOT_ID);
		final List<ModelViewBean> els = service.findDocTreeById(docTreeId).getChildrenList();
		for(ModelViewBean bean: els) {
			root.addChild(bean);
		}
		return root;
	}
	*/
	private void loadChildrens(TreeElement node){
		try {
			//if(node.getChildren() != null && node.getChildren().size() > 0)
			//		node.getChildren().clear();
			/*
			List<ModelViewBean> els;
			if(node.getId() == ROOT_ID) {
				els = service.findDocTreeById(docTreeId).getChildrenList();
			} else {
				els = service.getChildElementList(node.getId());
			}*/
			
			//if(els == null) return;
			//node.setChildrens(els);
			
			//for(ElementViewBean bean: els) {
			//	node.addChild(bean);
			//}
		} catch (Exception e) {
			Dialog.error("Ошибка при загрузке элементов узла", e);
		}
	} 
	
	public TreeElement getChild(TreeElement node, int index) {
		//loadChildrens(node);
		return node.getChildrens().get(index);
	}

	public int getChildCount(TreeElement node) {
		//loadChildrens(node);
		return node.getChildrens().size();
	}

	public boolean isLeaf(TreeElement node) {
		//Dialog.info(null, "> isLeaf:node=" + node);
		//if(node.getId() != ROOT_ID){
		//	loadChildrens(node);
		//}
		return node.isLeaf();
		//return node.getChildrens().size() == 0;
	}

}

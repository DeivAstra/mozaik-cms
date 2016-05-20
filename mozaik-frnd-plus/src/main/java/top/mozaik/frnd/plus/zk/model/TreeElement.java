/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.model;

import java.util.ArrayList;
import java.util.List;

public class TreeElement {
	
	private TreeElement parent;
    private final List<TreeElement> children;
    private final boolean isLeaf;
    
    public TreeElement(boolean isLeaf) {
    	this.isLeaf = isLeaf;
    	if(isLeaf) {
    		children = null;
    	} else {
    		children = new ArrayList<TreeElement>();
    	}
    }
    
    public boolean isLeaf() {
    	return isLeaf;
    }
    
    public TreeElement getParent() {
        return parent;
    }

    public void setParent(TreeElement parent) {
        this.parent = parent;
    }
    
    public List<TreeElement> getChildrens() {
        return children;
    }

    /*
    public void setChildrens(List<TreeNodeElement> children) {
        this.children = children;
        for(TreeNodeElement el: children){
        	el.setParent(this);
        }
    }*/
    
    public void addChild(TreeElement child) {
        checkLeaf();
    	
    	if(!children.contains(child))
            children.add(child);

        if(child != null)
            child.setParent(this);
    }
    
    public void addChildBefore(TreeElement target, TreeElement child){
    	checkLeaf();
    	
    	int targetIdx = children.indexOf(target);
    	children.add((targetIdx==0)?0:targetIdx-1, child);
    	child.setParent(this);
    }
    
    public void addChildAfter(TreeElement target, TreeElement child){
    	checkLeaf();
    	
    	int targetIdx = children.indexOf(target);
    	children.add(targetIdx+1, child);
    	child.setParent(this);
    }
    
    public void moveChildUp(TreeElement child){
    	checkLeaf();
    	
    	int childIdx = children.indexOf(child);
    	children.remove(childIdx);
    	children.add(childIdx-1, child);
    }
    
    public void moveChildDown(TreeElement child){
    	checkLeaf();
    	
    	int childIdx = children.indexOf(child);
    	children.remove(childIdx);
    	children.add(childIdx+1, child);
    }
    
    private void checkLeaf() {
    	if(this.isLeaf)
        	throw new IllegalArgumentException("Can't to add/move children. TreeElement is leaf.");
    }
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk;

import java.util.Comparator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zul.Column;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.impl.InputElement;

import top.mozaik.frnd.plus.zk.constraint.SwitchConstraint;

public class ZKUtils {
	/*
	public static void validate(Component c) throws WrongValueException {
		if(c instanceof InputElement){
			validateInput((InputElement)c);
			return;
		}
		
		final List<Component> childs  = c.getChildren();
		for(Component c2: childs){
			if(c2 instanceof InputElement){
				validateInput((InputElement)c2);
			}
		}
	}*/
	
	
	public static void validate(Component c) throws WrongValueException {
		validate(c, false);
	}
	
	public static void validate(Component c, boolean setErrorMessage) throws WrongValueException {
		if(c instanceof InputElement){
			validateInput((InputElement)c, setErrorMessage);
			return;
		}
		
		final List<Component> childs  = c.getChildren();
		for(Component child: childs){
			validate(child, setErrorMessage);
		}
	}
	
	private static void validateInput(InputElement ie, boolean setErrorMessage){
		if(!ie.isVisible()) {
			return;
		}
		Constraint c;
		if((c = ie.getConstraint()) != null) {
			try {
				if(c instanceof SwitchConstraint) {
					((SwitchConstraint) c).forceValidate(ie, ie.getRawValue());
					if(ie.getErrorMessage() != null) {
						throw new WrongValueException(ie, ie.getErrorMessage());
					}
				} else {
					c.validate(ie, ie.getRawValue());
					//if(!ie.isValid()) {
					//	throw new WrongValueException(ie, ie.getErrorMessage());
					//}
				}
			} catch (WrongValueException e) {
				if(setErrorMessage) ie.setErrorMessage(e.getMessage());
				throw e;
			}
		}
	}
	
	public static void openTabByConstraintError(WrongValueException e) {
		Component comp = e.getComponent();
		while((comp = comp.getParent()) != null) {
			if(comp instanceof Tabpanel) {
				((Tabpanel) comp).getLinkedTab().setSelected(true);
			}
		}
	}
	
	public static Listitem getItemByValue(Listbox lb, Object value){
		List<Listitem> items = lb.getItems();
		for(Listitem item : items){
			if(value.equals(item.getValue())) {
				return item;
			}
		}
		return null;
	}
	
	public static void setSelectedItemByValue(Listbox lb, Object value){
		lb.setSelectedItem(getItemByValue(lb, value));
	}
	
	/*
	public static void removeChilds(Component c){
		final List<Component> childs = c.getChildren();
		for(int i=0; i < childs.size(); i++){
			c.removeChild(childs.get(i));
		}
		c.invalidate();
	}*/
	
	public static void detachAll(Component parent) {
		List<Component> childrens = parent.getChildren();
		Component[] array = childrens.toArray(new Component[0]);
		for(int i=0; i<array.length; i++) {
			detachAll(array[i]);
			array[i].detach();
			array[i].setPage(null);
			array[i].setId(null);
		}
	}
	
	public static void disableAll(Component c) {
		if(c instanceof Disable){
			((Disable)c).setDisabled(true);
			return;
		}
		
		final List<Component> childs  = c.getChildren();
		for(Component child: childs){
			disableAll(child);
		}
	}
	
	public static Comparator getComparator(Listbox listbox) {
		final List<Listheader> listHeaders = listbox.getListhead().getChildren();
		for(Listheader header: listHeaders) {
			//System.err.println(header.getLabel() + " : "  +header.getSortDirection());
			if(!header.getSortDirection().equals("natural")) {
				final String sortDirection = header.getSortDirection();
				if(sortDirection.equals("ascending")) {
					return header.getSortAscending();
				} else {
					return header.getSortDescending();
				}
			}
		}
		return null;
	}
	
	public static Comparator getComparator(Grid grid) {
		final List<Column> columns = grid.getColumns().getChildren();
		for(Column col: columns) {
			//System.err.println(col.getLabel() + " : "  +col.getSortDirection());
			if(!col.getSortDirection().equals("natural")) {
				final String sortDirection = col.getSortDirection();
				if(sortDirection.equals("ascending")) {
					return col.getSortAscending();
				} else {
					return col.getSortDescending();
				}
			}
		}
		return null;
	}
}

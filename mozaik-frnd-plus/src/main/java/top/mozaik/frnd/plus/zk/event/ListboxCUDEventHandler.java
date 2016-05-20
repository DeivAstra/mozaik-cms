/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.event;

import java.util.Comparator;

import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import top.mozaik.frnd.plus.zk.ZKUtils;

public class ListboxCUDEventHandler<T> implements I_CUDEventHandler<T> {
	
	private final Listbox listbox;
	
	public ListboxCUDEventHandler(Listbox listbox) {
		if(listbox == null)
			throw new NullPointerException("Listbox is null");
		this.listbox = listbox;
	}
	
	public void onCreate(T v) {			
		final ListModelList listModel = (ListModelList) listbox.getModel();
		listModel.add(v);
		final Comparator comparator = ZKUtils.getComparator(listbox);
		if(comparator != null)
			listModel.sort(comparator, listModel.getSortDirection(comparator).equals("ascending"));
	}
	
	public void onUpdate(T v) {
		final ListModelList listModel = (ListModelList) listbox.getModel();
		for(int i=0; i<listModel.getSize();i++){
			final T v2 = (T) listModel.getElementAt(i);
			if(v.equals(v2)) {
				listModel.set(i, v);
			}
		}
	}
	
	public void onDelete(T v) {
		final ListModelList listModel = (ListModelList) listbox.getModel();
		for(int i=0; i<listModel.getSize();i++){
			final T v2 = (T)listModel.getElementAt(i);
			if(v.equals(v2)) {
				listModel.remove(i);
			}
		}
	}
}

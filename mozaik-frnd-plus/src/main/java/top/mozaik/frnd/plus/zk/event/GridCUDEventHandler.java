/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.event;

import java.util.Comparator;

import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;

import top.mozaik.frnd.plus.zk.ZKUtils;

public class GridCUDEventHandler<T> implements I_CUDEventHandler<T> {
	
	private final Grid grid;
	
	public GridCUDEventHandler(Grid grid) {
		if(grid == null)
			throw new NullPointerException("Grid is null");
		this.grid = grid;
	}
	
	public void onCreate(T v) {			
		final ListModelList listModel = (ListModelList) grid.getModel();
		listModel.add(v);
		final Comparator comparator = ZKUtils.getComparator(grid);
		if(comparator != null)
			listModel.sort(comparator, listModel.getSortDirection(comparator).equals("ascending"));
	}
	
	public void onUpdate(T v) {
		final ListModelList listModel = (ListModelList) grid.getModel();
		for(int i=0; i<listModel.getSize();i++){
			final T v2 = (T) listModel.getElementAt(i);
			if(v.equals(v2)) {
				listModel.set(i, v);
			}
		}
	}
	
	public void onDelete(T v) {
		final ListModelList listModel = (ListModelList) grid.getModel();
		for(int i=0; i<listModel.getSize();i++){
			final T v2 = (T)listModel.getElementAt(i);
			if(v.equals(v2)) {
				listModel.remove(i);
			}
		}
	}
}

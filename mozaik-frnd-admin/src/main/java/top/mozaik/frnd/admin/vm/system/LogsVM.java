/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.SizeWrappedList;
import top.mozaik.bknd.api.model.Log;
import top.mozaik.bknd.api.orm.annotation.Column;
import top.mozaik.frnd.admin.misc.ColumnData;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.ListboxCUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class LogsVM extends BaseVM {
	
	@Wire
	Listbox logListbox;
	@Wire
	Toolbarbutton searchToolbarBtn;
	@Wire
	Intbox pageSizeIntbox;
	@Wire
	Paging listboxPaging;
	
	private I_CUDEventHandler<Log> eventHandler;
	
	private boolean isSearchMode = false;
	
	private SortEvent sortEvent;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		eventHandler = new ListboxCUDEventHandler<Log>(logListbox);
	}
	
	private SizeWrappedList<Log> getList(int pageNum, int pageSize) throws Exception {
		final StringBuilder query = new StringBuilder("select * from logs l");
		
		if(isSearchMode){
			appendFilterToQuery(query);
		}
		
		if(sortEvent != null) {
			final Listheader header = (Listheader)sortEvent.getTarget();
			final ColumnData columnData = (ColumnData)header.getValue();
			
			query.append(" order by ")
			.append(
					columnData.getFieldTableAlias() + "." + 
						tableAliases.get(columnData.getFieldTableAlias())
							.getDeclaredField(columnData.getField()).getAnnotation(Column.class).name()
				).append(sortEvent.isAscending()?" asc":" desc");
		}
		final String countQuery = "select count(*) from ("+ query.toString() +") t";
		query.append(" limit ").append(pageNum*pageSize).append(", ").append(pageSize);
		return ServicesFacade.$().getQueryService().readByQuery(
				query.toString(), countQuery, Log.class);
	}
	
	private final Map<String, Class> tableAliases = new HashMap<>();
	{
		tableAliases.put("l", Log.class);
	}
	private void appendFilterToQuery(StringBuilder query) throws Exception {
		boolean firstIter = true;
		for(Component c : logListbox.getListhead().getChildren()) {
			final Listheader h = (Listheader) c;
			final String value = getFilterValue(h);
			if(!h.isVisible() || (value != null && value.equals(""))) {
				h.setSclass(null);
				continue;
			}
			h.setClass("filtered");
			
			if(firstIter) {
				query.append(" where ");
				firstIter = false;
			} else {
				query.append(" and ");
			}
			
			String op = getFilterOperator(h);
			switch (op) {
			case "Equal":
				op = "=";
				break;
			case "Not Equal":
				op = "<>";
				break;
			case "More":
				op = ">";
				break;
			case "Less":
				op = "<";
			}
			final ColumnData columnData = (ColumnData)h.getValue();
			final String columnName = columnData.getFieldTableAlias() + "." + 
					tableAliases.get(columnData.getFieldTableAlias())
					.getDeclaredField(columnData.getField()).getAnnotation(Column.class).name();
			query.append(columnName).append(' ').append(op);
			if(value != null) {
				query.append(" '").append(value).append("'");
			}
		}
	}
	
	private String getFilterOperator(Listheader h) {
		for(Component c: h.getFirstChild().getChildren()){
			if(c instanceof Combobox) {
				return ((Combobox)c).getSelectedItem().getLabel();
			}
		}
		return null;
	}
	
	private String getFilterValue(Listheader h) {
		for(Component c: h.getFirstChild().getChildren()){
			if(!(c instanceof Combobox)) {
				if(!c.isVisible()) return null;
				return ((Textbox) c).getValue();
			}
		}
		return null;
	}
	
	private void resetSortDir() {
		final List<Listheader> headers = logListbox.getListhead().getChildren();
		for(Listheader h: headers) {
			h.setSortDirection("natural");
		}
	}
	
	/// BINDING ///
	
	public List<Log> getLogList() throws Exception {
		final int pageNum = listboxPaging.getActivePage();
		final int pageSize = pageSizeIntbox.getValue();
		
		final SizeWrappedList<Log> list = getList(pageNum, pageSize);
		
		listboxPaging.setPageSize(pageSize);
		listboxPaging.setTotalSize(list.getTotalSize());
		
		return list;
	}
	
	/// COMMANDS ///
	
	@Command
	@NotifyChange("logList")
	public void sort(@BindingParam("event") SortEvent sortEvent) {
		resetSortDir();
		final Listheader header = (Listheader)sortEvent.getTarget();
		header.setSortDirection(sortEvent.isAscending()?"ascending":"descending");
		this.sortEvent = sortEvent;
		sortEvent.stopPropagation();
	}
	
	@Command
	@NotifyChange("logList")
	public void refresh() throws Exception {
		resetSortDir();
		sortEvent = null;
		if(isSearchMode) {
			resetFilter();
			isSearchMode = false;
			listboxPaging.setActivePage(0);
			logListbox.setSclass(null);
		}
	}
	
	@Command
	@NotifyChange("logList")
	public void refreshAfterChangePageSize() {
		listboxPaging.setActivePage(0);
	}
	
	@Command
	@NotifyChange("logList")
	public void refreshAfterOnPage() throws Exception {		
	}
	
	@Command
	public void hideShowSearch(@BindingParam("show") Boolean show) {
		final List<Listheader> headers = logListbox.getListhead().getChildren();
		for(Listheader h: headers) {
			for(Component c: h.getChildren()){
				c.setVisible(show);
			}
		}
		logListbox.invalidate();
	}
	
	@Command
	public void resetFilter(){
		for(Component c : logListbox.getListhead().getChildren()) {
			final Listheader h = (Listheader) c;
			h.setSclass(null);
			for(Component c2: h.getFirstChild().getChildren()){
				if(c2 instanceof Combobox)
					((Combobox) c2).setSelectedIndex(0);
				else {
					((Textbox) c2).setRawValue(null);
					((Textbox) c2).setVisible(true);
				}
			}
		}
	}
	
	@Command
	@NotifyChange("logList")
	public void search(){
		isSearchMode = true;
		listboxPaging.setActivePage(0);
		logListbox.setSclass("filtered");
	}
}

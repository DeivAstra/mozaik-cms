/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm.resourceeditor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Column;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.service.QueryService;

public class QueryResourceEditorVM extends TextResourceEditorVM {
	
	private final QueryService queryService = ServicesFacade.$().getQueryService();
	
	private final ResultSetExtractor<Object> rsExtractor = new ResultSetExtractor<Object>() {
		@Override
		public Object extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			final ResultSetMetaData rsmd = rs.getMetaData();
			
			final int columnCount = rsmd.getColumnCount();
			for(int i=1; i <= columnCount; i++) {
				queryResultGrid.getColumns().appendChild(new Column( rsmd.getColumnName(i) ));
			}
			
			while(rs.next()) {
				final Row row = new Row();
				queryResultGrid.getRows().appendChild(row);
				for(int i=1; i <= columnCount; i++) {
					row.appendChild(new Label(rs.getObject(i)==null?"":rs.getObject(i).toString()));
				}
			}
			return null;
		}
	};
	
	@Wire
	Grid queryResultGrid;
	@Wire
	Intbox limitIntbox;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){
	}
	
	private void showResultPanel() {
		final South south = ((Borderlayout)getView().query("borderlayout")).getSouth();
		south.setSplittable(true);
		south.setStyle("visibility:visible");
	}
	
	private String deleteComments(String query) {
		int startCommentIdx;
		while((startCommentIdx = query.indexOf("/*")) != -1) {
			query = deleteComment(query, startCommentIdx);
		}
		return query.trim();
	}
	
	private String deleteComment(String query, int startCommentIdx) {
		final StringBuilder sb = new StringBuilder(query);
		final int endCommentIdx = sb.indexOf("*/", startCommentIdx);
		if(endCommentIdx != -1) {
			sb.delete(startCommentIdx, endCommentIdx+2);
			sb.insert(startCommentIdx,' ');
		} else {
			sb.delete(startCommentIdx,sb.length());
		}
		return sb.toString();
	}
	
	/// COMMANDS ///
	
	@Command
	public void hideResultPanel() {
		final South south = ((Borderlayout)getView().query("borderlayout")).getSouth();
		south.setStyle("visibility:hidden");
		south.setStyle("display:none");
		south.setSplittable(false);
	}
	
	@Command
	public void execute() {
		final String query = cm.getValue();
		if(query == null || query.trim().length() == 0) return;
		
		queryService.getJdbcTemplate().setMaxRows(limitIntbox.getValue());
		
		final String value = deleteComments( 
				(cm.getSelectedValue()==null || cm.getSelectedValue().length() ==0)
				?cm.getValue().trim():cm.getSelectedValue().trim() 
		);
		
		queryResultGrid.getColumns().getChildren().clear();
		queryResultGrid.getRows().getChildren().clear();
		
		final String lcValue = value.toLowerCase();
		if(lcValue.startsWith("select") || lcValue.startsWith("show")) {
			queryService.query(value, rsExtractor);
		} else if(lcValue.startsWith("insert")
					|| lcValue.startsWith("update")
					|| lcValue.startsWith("delete")) {
			final int rowsAffected = queryService.getJdbcTemplate().update(value);
			queryResultGrid.getColumns().appendChild(new Column("Rows Affected"));
			final Row row = new Row();
			queryResultGrid.getRows().appendChild(row);
			row.appendChild(new Label(rowsAffected+""));
		} else if(lcValue.startsWith("create function")
					|| lcValue.startsWith("create procedure")) {
			queryService.executeUpdate(value);
		} else {
			queryService.execute(value);
			queryResultGrid.getColumns().appendChild(new Column("Result"));
			final Row row = new Row();
			queryResultGrid.getRows().appendChild(row);
			row.appendChild(new Label("Success"));
		}
		
		showResultPanel();
	}
}

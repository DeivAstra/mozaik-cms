/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.misc;

import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Row;

public class ColumnData {
	
	/// ALLOW COMPARE ROWS WHEN THEY ARE RENDERS IN CHILDREN TEMPLATE
	private static class RowValueComparator extends FieldComparator {
		
		public RowValueComparator(String orderBy, boolean ascending) {
			super(orderBy, ascending);
		}

		@Override
		public int compare(Object o1, Object o2) {
			if(o1 instanceof Row)
				o1 = ((Row)o1).getValue();
			if(o2 instanceof Row) 
				o2 = ((Row)o2).getValue();
			return super.compare(o1, o2);
		}
	};
	
	private final String label;
	private final Boolean visible;
	private final String fieldTableAlias;
	private final String field;
	private String width;
	private FieldComparator ascComp;
	private FieldComparator descComp;
	
	public ColumnData(String label, Boolean visible, String field) {
		this.label = label;
		this.visible = visible;
		if(field.indexOf('.') == -1) {
			this.fieldTableAlias = null;
			this.field = field;
		} else {
			final String [] fieldData = field.split("\\.");
			this.fieldTableAlias = fieldData[0];
			this.field = fieldData[1];
		}
	}
	
	public ColumnData(String label, Boolean visible, String field, String width) {
		this(label, visible, field);
		this.width = width;
	}
	
	public String getLabel() { return label; }
	public Boolean getVisible() { return visible; }
	public String getFieldTableAlias() { return fieldTableAlias; }
	public String getField() { return field; }
	public String getWidth() { return width; }
	
	public FieldComparator getAscComp() { 
		if(field == null) return null;
		if(ascComp == null){
			ascComp = new RowValueComparator(field, true);
		}
		return ascComp; 
	}
	public FieldComparator getDescComp() {
		if(field == null) return null;
		if(descComp == null){
			descComp = new RowValueComparator(field, false);
		}
		return descComp; 
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.converter.wcm;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import top.mozaik.frnd.admin.bean.wcm.document.TreeDocument;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocumentFolder;
import top.mozaik.frnd.admin.bean.wcm.document.TreeLibraryDocumentFolder;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

import static top.mozaik.frnd.admin.enums.E_Icon.*;
import static top.mozaik.frnd.admin.enums.E_WcmIcon.*;

public class DocumentTreeImageUrlConverter implements Converter<String, A_TreeElement, Component> {

	private static DocumentTreeImageUrlConverter instance;
	
	private DocumentTreeImageUrlConverter(){}
	
	public static DocumentTreeImageUrlConverter getInstance() {
		if(instance == null) {
			instance = new DocumentTreeImageUrlConverter();
		}
		return instance;
	}
	
	@Override
	public String coerceToUi(A_TreeElement el, Component component, BindContext ctx) {
		if(el instanceof TreeLibraryDocumentFolder) {
			return LIBRARY_SMALL.getPath();
		}
		if(el instanceof TreeDocumentFolder) {
			return FOLDER.getPath();
		}
		if(el instanceof TreeDocument) {
			return DOCUMENT_SMALL.getPath();
		}
		return null;
	}

	@Override
	public A_TreeElement coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}

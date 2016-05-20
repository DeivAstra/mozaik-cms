/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.contextmenu.wcm;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

import top.mozaik.frnd.admin.bean.wcm.document.TreeDocument;
import top.mozaik.frnd.admin.bean.wcm.document.TreeDocumentFolder;
import top.mozaik.frnd.admin.bean.wcm.document.TreeLibraryDocumentFolder;
import top.mozaik.frnd.admin.vm.wcm.document.DocumentsVM;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;

public class DocumentTreeMenuBuilder {
	
	private final DocumentsVM ctrl;
	
	public DocumentTreeMenuBuilder(DocumentsVM ctrl) {
		this.ctrl = ctrl;
	}
	
	public void build(Menupopup menu, A_TreeElement value) {
		if(value instanceof TreeLibraryDocumentFolder) {
			buildLibraryMenu(menu, (TreeLibraryDocumentFolder)value);
		} else if (value instanceof TreeDocumentFolder) {
			buildFolderMenu(menu, (TreeDocumentFolder)value);
		} else if (value instanceof TreeDocument) {
			buildTemplateMenu(menu, (TreeDocument)value);
		}
	}
	
	private void buildLibraryMenu(Menupopup menu, final TreeLibraryDocumentFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Document");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createDocument(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildFolderMenu(Menupopup menu, final TreeDocumentFolder folder) {
		Menuitem item = null;
		item = new Menuitem("New Folder");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createFolder(folder);
			};
		});
		menu.appendChild(item);
		item = new Menuitem("New Document");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.createDocument(folder);
			};
		});
		menu.appendChild(item);
		
		menu.appendChild(new Menuseparator());
		item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteFolder(folder);
			};
		});
		menu.appendChild(item);
	}
	
	private void buildTemplateMenu(Menupopup menu, final TreeDocument document) {
		final Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				ctrl.deleteDocument(document);
			};
		});
		menu.appendChild(item);
	}
}

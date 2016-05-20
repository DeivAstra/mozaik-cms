/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.contextmenu;

import java.io.File;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.studio.vm.JarsVM;

public class JarContextMenuBuilder {
	
	private JarsVM ctrl;
	
	public JarContextMenuBuilder(JarsVM ctrl) {
		this.ctrl = ctrl;
	}
	
	public void build(Menupopup menu, final File file) {
		Menuitem item = new Menuitem("Delete");
		item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				Dialog.confirm("Delete file", "File will be deleted. Continue?", new Dialog.Confirmable() {
					@Override
					public void onConfirm() {
						if(!file.delete())
							Dialog.error("Error", "Could't delete file");
						ctrl.refresh();
					}
					@Override
					public void onCancel() {}
				});
			};
		});
		menu.appendChild(item);
	}
}

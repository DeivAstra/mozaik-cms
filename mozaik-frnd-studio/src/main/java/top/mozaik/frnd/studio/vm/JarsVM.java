/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.vm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Row;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_SettingsKey;
import top.mozaik.bknd.api.service.SettingsService;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.event.GridCUDEventHandler;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;
import top.mozaik.frnd.studio.contextmenu.JarContextMenuBuilder;

public class JarsVM extends BaseVM {
	
	private final SettingsService settingsService = ServicesFacade.$().getSettingsService();
	
	@Wire
	Grid jarGrid;
	
	private I_CUDEventHandler<?> eventHandler;
	
	private String resourcePackAlias;
	private String rpRootFolder;
	
	private JarContextMenuBuilder jarContextMenuBuilder;
	
	@Init
	public void init(@BindingParam("rpa") String resourcePackAlias) {
		this.resourcePackAlias = resourcePackAlias;
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		rpRootFolder = settingsService.readValue(E_SettingsKey.ROOT_FOLDER);
		eventHandler = new GridCUDEventHandler<>(jarGrid);
		jarContextMenuBuilder = new JarContextMenuBuilder(this);
	}
	
	/// BINDING
	
	public File[] getJarList() {
		final File folder = new File(rpRootFolder + File.separator + resourcePackAlias);
		return folder.listFiles();
	}
	
	/// COMMANDS
	
	@Command
	
	public void refresh() {
		reloadComponent();
	}
	
	@Command
    public void upload(@BindingParam("media") final Media media) throws Exception {
        System.out.println(media.getStreamData());
        
        if(!media.getName().endsWith(".jar")) {
        	Dialog.error("Unsupported file type", "You can upload only *.jar files");
        	return;
        }
        final File file = new File(
        		rpRootFolder + File.separator + resourcePackAlias + File.separator + media.getName());
        if(file.exists()) {
        	Dialog.confirm("File already exists", "File '" + media.getName() + "' is will be overwrited. Continue?", 
        	
        	new Dialog.Confirmable() {
				@Override
				public void onConfirm() {
					if(file.delete()) {
						try {
							writeFile(media.getStreamData(), file);
						} catch (Exception e) {
							Dialog.error("Error occured while write filw", e);
						}
					} else {
						Dialog.error("Error", "Could't delete file");
					}
				}
				@Override
				public void onCancel() {
				}
			});
        } else {
        	writeFile(media.getStreamData(), file);
        }
    }
	
	private void writeFile(InputStream in, File file) throws Exception {
		final FileOutputStream out = new FileOutputStream(file);
		try {
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		reloadComponent();
	}
	
	@Command
	public void showContextMenu(@BindingParam("event") OpenEvent event) {
		final Menupopup menu = (Menupopup)event.getTarget();
		final Component ref = event.getReference();
		
		if(ref == null) {
			menu.getChildren().clear();
			return;
		}
		
		final Row row = (Row)ref;
		jarContextMenuBuilder.build(menu, (File) row.getValue());
	}
}

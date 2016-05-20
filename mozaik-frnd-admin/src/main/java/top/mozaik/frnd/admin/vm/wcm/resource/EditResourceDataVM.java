/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_SettingsKey;
import top.mozaik.bknd.api.model.Settings;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.model.WcmResourceData;
import top.mozaik.bknd.api.service.QueryService;
import top.mozaik.bknd.api.service.WcmResourceDataService;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.command.I_CommandExecutor;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditResourceDataVM extends BaseVM implements I_CommandExecutor {
	
	private final WcmResourceDataService resourceDataService = ServicesFacade.$().getWcmResourceDataService();
	private final QueryService queryService = ServicesFacade.$().getQueryService();
	
	private WcmResource bean;
	private Integer resourceDataId;
	
	private final Vlayout vlayout = new Vlayout();
	
	@Init
	public void init(
			@BindingParam("bean") WcmResource bean,
			@BindingParam("commandQueue") CommandExecutionQueue commandQueue) {
		this.bean = bean;
		commandQueue.addListener(this);
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		vlayout.setStyle("padding:20px");
		getView().appendChild(vlayout);
		if(bean.getDataId() == null) {
			buildUploadBtn();
		} else {
			resourceDataId = bean.getDataId();
			buildUploadInfo();
		}
	}
	
	private void buildUploadBtn() {
		final Button uploadBtn = new Button("Upload");
		uploadBtn.setUpload("true,maxsize=16000");
		uploadBtn.addEventListener(Events.ON_UPLOAD, new EventListener<UploadEvent>() {
			@Override
			public void onEvent(UploadEvent event) throws Exception {
				final Media media = event.getMedia();
				if(media == null) return;
				
				final CalcInputStream cis = new CalcInputStream(media);
				
				resourceDataService.startTransaction();
				try {
					final KeyHolder holder = new GeneratedKeyHolder();
					queryService.getJdbcTemplate().update(new PreparedStatementCreator() {
			            @Override
			            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			                final StringBuilder sql = 
			                		new StringBuilder("insert into wcm_resource_data values (default,NULL,NULL,NULL,?,default)");
			            	//System.out.println(sql);
			            	final PreparedStatement stmt = 
			            			con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			            	stmt.setBinaryStream(1, cis);
			                return stmt;
			            }
			        }, holder);
					
					resourceDataId = holder.getKey()==null?null:holder.getKey().intValue();
					resourceDataService.update1(new WcmResourceData()
								.setId(resourceDataId)
								.setFileName(media.getName())
								.setContentType(media.getContentType())
								.setSize(cis.getSize())
					);
					
					resourceDataService.commit();
					Notification.showMessage("Resource Data uploaded succesfully. Size " + cis.getSize());
					buildUploadInfo();
				} catch (Exception e) {
					media.getStreamData().close();
					resourceDataService.rollback();
					e.printStackTrace();
					throw e;
				}
				
			}
		});
		vlayout.appendChild(uploadBtn);
	}
	
	private void buildUploadInfo() {
		vlayout.getChildren().clear();
		final WcmResourceData resourceData = 
				resourceDataService.read1(new WcmResourceData().setId(resourceDataId));
		
		if(resourceData == null) {
			final Label errorLabel = new Label("Resource Data with ID = " + resourceDataId + " not found");
			errorLabel.setStyle("color:red");
			vlayout.appendChild(errorLabel);
			buildUploadBtn();
			return;
		}
		
		final Hlayout hlayout = new Hlayout();
		hlayout.appendChild(new Label("File Name: "));
		final Html html = new Html(
				"<a target='_blank' href='"+
					getSiteUrl()+ "/r$?id="+resourceData.getId()+"'>" + resourceData.getFileName()+"</a>");
		html.setStyle("font-size:90%");
		hlayout.appendChild(html);
		vlayout.appendChild(hlayout);
		final Label ctLabel = new Label("Content Type: " + resourceData.getContentType());
		vlayout.appendChild(ctLabel);
		final Label sizeLabel = new Label("Size: " + resourceData.getSize());
		vlayout.appendChild(sizeLabel);
		final Label dateLabel = new Label("Uploaded: " + new Date(resourceData.getCreateDate()));
		vlayout.appendChild(dateLabel);
		
		final Button deleteBtn = new Button("Delete");
		deleteBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				//if(!resourceDataId.equals(bean.getDataId())){
				//	resourceDataService.delete1(new WcmResourceData().setId(resourceDataId));
				//}
				// all unbinded resource data will be deleted after 'save'
				resourceDataId = null;
				vlayout.getChildren().clear();
				buildUploadBtn();
			};
		});
		vlayout.appendChild(deleteBtn);
	}
	
	@Override
	public void execCommand(int cmdId) {
		if(EditResourceVM.COMMAND_SAVE != cmdId) throw new IllegalArgumentException();
		
		bean.setDataId(resourceDataId);
	}
	
	private class CalcInputStream extends InputStream {
		
		private final InputStream is;
		private int size;
		
		public CalcInputStream(Media media) {
			if(media.isBinary())
				is = media.getStreamData();
			else 
				is = new ByteArrayInputStream(media.getStringData().getBytes());
		}
		
		@Override
		public int read() throws IOException {
			final int v = is.read();
			if(v != -1) size++;
			return v;
		}
		
		@Override
		public void close() throws IOException {
			is.close();
		}
		
		@Override
		public int available() throws IOException {
			return is.available();
		}
		
		public int getSize() {
			return size;
		}
	}
	
	private String getSiteUrl() {
		return ServicesFacade.$().getSettingsService().read1(
				new Settings().setKey(E_SettingsKey.SITE_URL)).getValue();
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.WcmTemplateField;
import top.mozaik.bknd.api.service.WcmTemplateFieldService;
import top.mozaik.frnd.plus.command.CommandExecutionQueue;
import top.mozaik.frnd.plus.command.I_CommandExecutor;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.ListboxCUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditTemplateFieldsVM extends BaseVM implements I_CommandExecutor {
	
	private final WcmTemplateFieldService templateFieldService = ServicesFacade.$().getWcmTemplateFieldService();
	//private final WcmDocumentFieldService documentFieldService = ServicesFacade.$().getWcmDocumentFieldService();
	
	private I_CUDEventHandler<WcmTemplateField> eventHandler;
	
	@Wire
	Listbox templateFieldListbox;
			
	private Integer templateId;
	
	@Init
	public void init(
			@BindingParam("templateId") Integer templateId,
			@BindingParam("commandQueue") CommandExecutionQueue commandQueue) {
		this.templateId = templateId;
		commandQueue.addListener(this);
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		eventHandler = new ListboxCUDEventHandler<WcmTemplateField>(templateFieldListbox){
			@Override
			public void onCreate(WcmTemplateField v) {
				// CHECK IF LISTBOX ALREADY HAS FIELD WITH SUCH CODE
				final ListModel<WcmTemplateField> model = templateFieldListbox.getModel();
				for(int i = 0; i < model.getSize(); i++) {
					final WcmTemplateField field = model.getElementAt(i);
					if(field.getCode().equalsIgnoreCase(v.getCode())){
						throw new IllegalArgumentException("Field with code = '" + v.getCode() + "' already exists");
					}
				}
				super.onCreate(v);
			}
		};
	}
	
	@Override
	public void execCommand(int cmdId) {
		if(EditTemplateVM.COMMAND_SAVE != cmdId) throw new IllegalArgumentException();
		
		// TODO: CHECK IF TEMPLATE HAS DEPENDENT DOCS
		// NEED TO REMOVE DELETED FIELDS FROM DOCS
		
		// GET CURRENT FIELDS
		final List<WcmTemplateField> currentFields = templateFieldService.read(new WcmTemplateField());
		
		//if(currentFields)
		
		// DELETE ALL TEMPLATE FIELDS
		templateFieldService.delete(new WcmTemplateField().setTemplateId(templateId), true);
		
		// ADD NEW FIELDS TO TEMPLATE
		final ListModel<WcmTemplateField> model = templateFieldListbox.getModel();
		for(int i = 0; i < model.getSize(); i++) {
			final WcmTemplateField field = model.getElementAt(i);
			field.setTemplateId(templateId);
			field.setPosition(i);
			templateFieldService.create(field);
		}
	}
	
	/// BINDING ///
	public List<WcmTemplateField> getTemplateFieldList() {
		return templateFieldService.read(new WcmTemplateField().setTemplateId(templateId));
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/wcm/template/createTemplateField.wnd.zul", null, args);
	}
	
	@Command
	public void edit(@BindingParam("bean") WcmTemplateField bean) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("bean", bean);
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/wcm/template/editTemplateField.wnd.zul", null, args);
	}
	
	@Command
	public void remove() {
		final Listitem item = templateFieldListbox.getSelectedItem();
		if(item == null) return;
		
		eventHandler.onDelete((WcmTemplateField)item.getValue());
	}
	
	@Command
	@NotifyChange("templateFieldList")
	public void refresh() {
	}
}

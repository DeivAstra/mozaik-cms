/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zul.Constraint;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_WcmTemplateFieldType;
import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.model.WcmTemplateField;
import top.mozaik.bknd.api.service.WcmDocumentService;
import top.mozaik.bknd.api.service.WcmResourceService;
import top.mozaik.frnd.admin.vm.wcm.template.converter.I_TemplareConverter;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateDateFieldConverter;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateDoubleFieldConverter;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateIntegerFieldConverter;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateTimeFieldConverter;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.ZKUtils;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.constraint.RegexConstraint;
import top.mozaik.frnd.plus.zk.constraint.UrlConstraint;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class CreateTemplateFieldVM extends BaseVM implements I_TemplareConverter {
	
	private static final Constraint urlFieldConstraint = new UrlConstraint();
	/*
	private static final Constraint listFieldConstraint = new Constraint() {
		@Override
		public void validate(Component comp, Object value)
				throws WrongValueException {
			final String msg = (String) value;
			if(msg == null || msg.length() == 0) return;
			//if(!msg.matches("(\\w(;|\040)?)+")) {
			if(!msg.matches("(.*(\n|\040)?)*")) {
				throw new WrongValueException(comp, "Allows only words and ';' symbol");
			}
		}
	};
	*/
	private static final Constraint regexConstraint = new RegexConstraint();
	
	private static final Constraint regexMessageConstraint = new Constraint() {
		@Override
		public void validate(Component comp, Object value)
				throws WrongValueException {
			final String msg = (String) value;
			if(msg == null || msg.length() == 0) return;
			if(!msg.matches("(\\w|\040|\\.|\\,)+")) {
				throw new WrongValueException(comp, "Allows only words and '.,' symbols");
			}
		}
	};
	
	private final WcmDocumentService documentService = ServicesFacade.$().getWcmDocumentService();
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	
	private WcmTemplateField bean = new WcmTemplateField();
	
	private I_CUDEventHandler<WcmTemplateField> eventHandler;
	
	private final Map<String,String> constrMap = new HashMap<>();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(
			@ExecutionArgParam("eventHandler") I_CUDEventHandler<WcmTemplateField> eventHandler) {
		this.eventHandler = eventHandler;
		
		parseConstraint(constrMap, bean.getConstraint());
	}
	
	private void parseConstraint(Map<String,String> map, String constr) {
		if(constr == null || constr.length() == 0) return;
		
		final String [] constrChunks = constr.split(",");
		for(String chunk:constrChunks) {
			final String [] valueMessage = chunk.split(":");
			if(valueMessage.length == 2) {
				map.put(valueMessage[0], valueMessage[1]);
			} else {
				map.put(valueMessage[0], null);
			}
		}
	}
	
	/// BINDING ///
	
	public WcmTemplateField getBean() {
		return bean;
	}
	
	public E_WcmTemplateFieldType[] getTemplateFieldTypeList() {
		return E_WcmTemplateFieldType.values();
	}
	
	public TemplateDateFieldConverter getTemplateDateFieldConverter() {
		return templateDateFieldConverter;
	}
	
	public TemplateTimeFieldConverter getTemplateTimeFieldConverter() {
		return templateTimeFieldConverter;
	}
	
	public TemplateIntegerFieldConverter getTemplateIntegerFieldConverter() {
		return templateIntegerFieldConverter;
	}
	
	public TemplateDoubleFieldConverter getTemplateDoubleFieldConverter() {
		return templateDoubleFieldConverter;
	}
	
	public Constraint getUrlFieldConstraint() {
		return urlFieldConstraint;
	}

	/*
	public Constraint getListFieldConstraint() {
		return listFieldConstraint;
	}
	*/
	
	public Constraint getRegexConstraint() {
		return regexConstraint;
	}
	
	public Constraint getRegexMessageConstraint(){
		return regexMessageConstraint;
	}
	
	public boolean hasConstraint(String constraintString) {
		return constrMap.containsKey(constraintString);
	}
	
	public WcmDocument getDocument(Object value) {
		if(value == null) return null;
		try {
			Integer documentId = null;
			if(value instanceof Integer) {
				documentId = (Integer) value;
			} else {
				documentId = Integer.parseInt(value.toString());
			}
			return documentService.read1(
					new WcmDocument().setId(documentId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public WcmResource getResource(Object value) {
		if(value == null) return null;
		try {
			Integer resourceId = null;
			if(value instanceof Integer) {
				resourceId = (Integer) value;
			} else {
				resourceId = Integer.parseInt(value.toString());
			}
			return resourceService.read1(
					new WcmResource().setId(resourceId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/// COMMANDS ///
	
	@Command
	public void selectDocument() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", new I_CallbackArg<WcmDocument>() {
			public void call(WcmDocument doc) {
				bean.setValue(doc.getId().toString());
				reloadComponent();
			};
		});
		Executions.createComponents("/WEB-INF/zul/wcm/common/selectDocument.wnd.zul", null, args);
	}
	
	@Command
	public void selectResource() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", new I_CallbackArg<WcmResource>() {
			public void call(WcmResource resource) {
				bean.setValue(resource.getId().toString());
				reloadComponent();
			};
		});
		Executions.createComponents("/WEB-INF/zul/wcm/common/selectResource.wnd.zul", null, args);
	}
	
	@Command
	public void updateConstraint(
			@BindingParam("event") CheckEvent event,
			@BindingParam("constr") String constr) {
		if(event.isChecked()) {
			constrMap.put(constr, null);
		} else {
			constrMap.remove(constr);
		}
	}
	
	@Command
	public void afterChangeType() {
		constrMap.clear();
		reloadComponent();
	}
	
	@Command
	public void deleteValue() {
		bean.setValue(null);
		reloadComponent();
	}
	
	@Command
	public void create(){
		ZKUtils.validate(getView());
		
		final StringBuilder constr = new StringBuilder();
		boolean firstIter = true;
		for(Entry<String, String> e : constrMap.entrySet()) {
			if(firstIter) {
				firstIter = false;
			} else {
				constr.append(',');
			}
			if(e.getValue() == null) {
				constr.append(e.getKey());
			} else {
				constr.append(e.getKey()).append(':').append(e.getValue());
			}
			
		}
		bean.setConstraint(constr.length()==0?null:constr.toString());
		
		if(bean.getValue() != null)
			bean.setValue(bean.getValue().trim());
		
		try {
			eventHandler.onCreate(bean);
			getView().detach();
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.error("Error occured while create Template Field", e);
		}
	}
}

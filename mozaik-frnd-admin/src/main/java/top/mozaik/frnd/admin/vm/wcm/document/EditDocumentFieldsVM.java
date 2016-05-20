/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.vm.wcm.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Listbox;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_WcmTemplateFieldType;
import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.bknd.api.model.WcmDocumentField;
import top.mozaik.bknd.api.model.WcmResource;
import top.mozaik.bknd.api.model.WcmTemplateField;
import top.mozaik.bknd.api.service.WcmDocumentFieldService;
import top.mozaik.bknd.api.service.WcmDocumentService;
import top.mozaik.bknd.api.service.WcmResourceService;
import top.mozaik.bknd.api.service.WcmTemplateFieldService;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateDateFieldConverter;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateDoubleFieldConverter;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateIntegerFieldConverter;
import top.mozaik.frnd.admin.vm.wcm.template.converter.TemplateTimeFieldConverter;
import top.mozaik.frnd.plus.callback.I_CallbackArg;
import top.mozaik.frnd.plus.zk.constraint.SwitchConstraint;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class EditDocumentFieldsVM extends BaseVM {
	
	private static final TemplateListFieldConverter templateListFieldConverter = new TemplateListFieldConverter();
	public static final TemplateDateFieldConverter templateDateFieldConverter = new TemplateDateFieldConverter();
	public static final TemplateTimeFieldConverter templateTimeFieldConverter = new TemplateTimeFieldConverter();
	public static final TemplateIntegerFieldConverter templateIntegerFieldConverter = new TemplateIntegerFieldConverter();
	public static final TemplateDoubleFieldConverter templateDoubleFieldConverter = new TemplateDoubleFieldConverter();
	
	private final WcmDocumentService documentService = ServicesFacade.$().getWcmDocumentService();
	private final WcmResourceService resourceService = ServicesFacade.$().getWcmResourceService();
	private final WcmTemplateFieldService templateFieldService = ServicesFacade.$().getWcmTemplateFieldService();
	private final WcmDocumentFieldService documentFieldService = ServicesFacade.$().getWcmDocumentFieldService();
	
	@Wire
	Listbox documentFieldListbox;
			
	private Integer documentId;
	private Integer templateId;
	private Boolean loadTemplateValues;
	
	private List<DocumentTemplateFieldPair> fieldPairs;
	
	@Init
	public void init(
			@BindingParam("documentId") Integer documentId,
			@BindingParam("templateId") Integer templateId,
			@BindingParam("ctrl") BaseVM ctrl,
			@BindingParam("loadTemplateValues") Boolean loadTemplateValues) {
		if(documentId == null || templateId == null) throw new NullPointerException();
		this.documentId = documentId;
		this.templateId = templateId;
		this.loadTemplateValues = loadTemplateValues;
		if(ctrl instanceof EditDocumentVM) {
			((EditDocumentVM)ctrl).setController(this);
		} else if(ctrl instanceof EditFolderVM) {
			((EditFolderVM)ctrl).setController(this);
		}
	}
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
	}
	
	public void save(boolean skipCreateFields) {
		
		//System.out.println(fieldPairs);
		
		// remove all document field values before to save new
		documentFieldService.delete(
				new WcmDocumentField().setDocumentId(documentId), true);
		
		if(skipCreateFields) return;
		
		for(DocumentTemplateFieldPair pair : fieldPairs) {
			if(pair.getDocumentFieldValue() == null) continue;
			
			final String value = pair.getDocumentFieldValue().toString();
			if(value.length() == 0) continue;
			
			documentFieldService.create(
				new WcmDocumentField()
					.setDocumentId(documentId)
					.setTemplateFieldCode(pair.getTemplateField().getCode())
					.setValue(value)
			);
		}
	}
	
	private String getDocumentValueByTemplateFieldCode(List<WcmDocumentField> documentFields, String templateFieldCode) {
		for(WcmDocumentField documentField : documentFields) {
			if(documentField.getTemplateFieldCode().equals(templateFieldCode))
				return documentField.getValue();
		}
		return null;
	}
	
	/// BINDING ///
	
	public List<DocumentTemplateFieldPair> getDocumentTemplateFieldPairList() {
		
		final WcmTemplateField filter = new WcmTemplateField().setTemplateId(templateId);
		filter.getFilter().setSorting("position", "asc");
		final List<WcmTemplateField> templateFields = templateFieldService.read(filter);
		
		final List<WcmDocumentField> documentFields = documentFieldService
				.read(new WcmDocumentField().setDocumentId(documentId));
		
		fieldPairs = new ArrayList<>();
		for(WcmTemplateField templateField : templateFields) {
			String value = getDocumentValueByTemplateFieldCode(documentFields, templateField.getCode());
			if(value == null & loadTemplateValues) {
				// skip LIST
				if(templateField.getType() != E_WcmTemplateFieldType.LIST) {
					value = templateField.getValue();
				}
			}
			fieldPairs.add(new DocumentTemplateFieldPair(templateField, value));
		}
		return fieldPairs;
	}
	
	public TemplateListFieldConverter getTemplateListFieldConverter() {
		return templateListFieldConverter;
	}
	
	public TemplateDateFieldConverter getTemplateDateFieldConverter() {
		return templateDateFieldConverter;
	}
	
	public TemplateIntegerFieldConverter getTemplateIntegerFieldConverter() {
		return templateIntegerFieldConverter;
	}
	
	public TemplateDoubleFieldConverter getTemplateDoubleFieldConverter() {
		return templateDoubleFieldConverter;
	}
	
	public Constraint getConstraint(WcmTemplateField field) {
		final StringBuilder constr = new StringBuilder();
		if(field.getConstraint() != null) {
			constr.append(field.getConstraint());
		}
		if(field.getConstraintRegex() != null) {
			if(constr.length() > 0) {
				constr.append(",");
			}
			constr.append(field.getConstraintRegex());
			if(field.getConstraintRegexErrorMessage() != null) {
				constr.append(":").append(field.getConstraintRegexErrorMessage());
			}
		}
		if(constr.length() > 0)
			return new SwitchConstraint(constr.toString());
		else
			return null;
	}
	
	public WcmDocument getDocument(Object documentFieldValue) {
		if(documentFieldValue == null) return null;
		try {
			Integer documentId = null;
			if(documentFieldValue instanceof Integer) {
				documentId = (Integer) documentFieldValue;
			} else {
				documentId = Integer.parseInt(documentFieldValue.toString());
			}
			return documentService.read1(
					new WcmDocument().setId(documentId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public WcmResource getResource(Object documentFieldValue) {
		if(documentFieldValue == null) return null;
		try {
			Integer resourceId = null;
			if(documentFieldValue instanceof Integer) {
				resourceId = (Integer) documentFieldValue;
			} else {
				resourceId = Integer.parseInt(documentFieldValue.toString());
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
	public void selectDocument(
			@BindingParam("pair") final DocumentTemplateFieldPair pair,
			@BindingParam("documentArea") final Component documentArea) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", new I_CallbackArg<WcmDocument>() {
			public void call(WcmDocument doc) {
				pair.setDocumentFieldValue(doc.getId());
				getBinder().loadComponent(documentArea, false);
			};
		});
		Executions.createComponents("/WEB-INF/zul/wcm/common/selectDocument.wnd.zul", null, args);
	}
	
	@Command
	public void deleteDocument(
			@BindingParam("pair") final DocumentTemplateFieldPair pair,
			@BindingParam("documentArea") final Component documentArea) {
		pair.setDocumentFieldValue(null);
		getBinder().loadComponent(documentArea, false);
	}
	
	@Command
	public void selectResource(
			@BindingParam("pair") final DocumentTemplateFieldPair pair,
			@BindingParam("resourceArea") final Component resourceArea) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", new I_CallbackArg<WcmResource>() {
			public void call(WcmResource resource) {
				pair.setDocumentFieldValue(resource.getId());
				getBinder().loadComponent(resourceArea, false);
			};
		});
		Executions.createComponents("/WEB-INF/zul/wcm/common/selectResource.wnd.zul", null, args);
	}
	
	@Command
	public void deleteResource(
			@BindingParam("pair") final DocumentTemplateFieldPair pair,
			@BindingParam("resourceArea") final Component resourceArea) {
		pair.setDocumentFieldValue(null);
		getBinder().loadComponent(resourceArea, false);
	}
	
	@Command
	public void setFieldPairValue(
			@BindingParam("fieldPair") DocumentTemplateFieldPair fieldPair, 
			@BindingParam("val") Object value) {
		if(value == null) return;
		fieldPair.setDocumentFieldValue(value.toString());
	}
	
	public static class DocumentTemplateFieldPair {
		private final WcmTemplateField templateField;
		private Object documentFieldValue;
		
		public DocumentTemplateFieldPair(WcmTemplateField templateField, String documentFieldValue) {
			this.templateField = templateField;
			this.documentFieldValue = documentFieldValue;
		}
		
		public WcmTemplateField getTemplateField() {
			return templateField;
		}
		
		public Object getDocumentFieldValue() {
			return documentFieldValue;
		}
		
		public void setDocumentFieldValue(Object documentFieldValue) {
			this.documentFieldValue = documentFieldValue;
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
				.append(templateField.toString())
				.append(": ").append(documentFieldValue)
				.toString();
		}
	}
	
	/// CONVERTERS ///
	
	private static class TemplateListFieldConverter implements Converter<ListModelArray<String>, DocumentTemplateFieldPair, Component> {
		@Override
		public ListModelArray<String> coerceToUi(DocumentTemplateFieldPair fieldPair, Component component, BindContext ctx) {
			if(fieldPair == null || fieldPair.getTemplateField().getValue() == null) return null;
			return new ListModelArray<String>(fieldPair.getTemplateField().getValue().split("\n"));
		}
		@Override
		public DocumentTemplateFieldPair coerceToBean(ListModelArray<String> val, Component component, BindContext ctx) {
			return null;
		}
	}
}

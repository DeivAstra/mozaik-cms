/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common.vm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.A_WcmFolder;
import top.mozaik.bknd.api.model.WcmDocument;
import top.mozaik.bknd.api.model.WcmDocumentFolder;
import top.mozaik.bknd.api.model.WcmTemplateField;
import top.mozaik.bknd.api.orm.filter.AnnotationFilter;
import top.mozaik.bknd.api.service.WcmDocumentFieldService;
import top.mozaik.bknd.api.service.WcmDocumentFolderService;
import top.mozaik.bknd.api.service.WcmDocumentService;
import top.mozaik.bknd.api.service.WcmTemplateFieldService;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class DocumentsVM extends BaseVM {
	
	private final WcmDocumentService documentService = ServicesFacade.$().getWcmDocumentService();
	private final WcmDocumentFolderService documentFolderService = ServicesFacade.$().getWcmDocumentFolderService();
	private final WcmDocumentFieldService documentFieldService = ServicesFacade.$().getWcmDocumentFieldService();
	private final WcmTemplateFieldService templateFieldService = ServicesFacade.$().getWcmTemplateFieldService();
	
	@AfterCompose(superclass=true)
	public void doAfterCompose(){}
	
	private void setSorting(AnnotationFilter filter, String sortRule) {
		if(sortRule == null || sortRule.length() == 0) return;
		final String[] v = sortRule.split(":");
		if(v.length == 0) return;
		filter.setSorting(v[0], v.length==2?v[1]:"asc");
	}
	
	private boolean isDocumentField(String fieldName) {
		for(Field field : WcmDocument.class.getDeclaredFields()) {
			if(field.getName().equalsIgnoreCase(fieldName)) return true;
		}
		return false;
	}
	
	public List<WcmDocument> getDocuments(Integer folderId) {
		if(folderId == null) return null;
		try {
			return documentService.read(new WcmDocument().setFolderId(folderId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<WcmDocument> getDocuments(String folderAlias, Integer templateId, String sortRule) {
		return null;
	}
	
	public List<WcmDocument> getDocuments(Integer folderId, String templateAlias, String sortRule) {
		return null;
	}
	
	public List<WcmDocument> getDocuments(String folderAlias, String templateAlias, String sortRule) {
		return null;
	}
	
	public List<WcmDocument> getDocuments(Integer folderId, Integer templateId) {
		return getDocuments(folderId, templateId, null);
	}
	
	public List<WcmDocument> getDocuments(Integer folderId, Integer templateId, String sortRule) {
		if(folderId == null || templateId == null) return null;
		if(sortRule == null || sortRule.length() == 0) {
			return documentService.read(new WcmDocument().setFolderId(folderId));
		} else {
			final String[] v = sortRule.split(":");
			if(v.length > 0) {
				if(isDocumentField(v[0])) {
					final AnnotationFilter<WcmDocument> docFilter = new WcmDocument().setFolderId(folderId).getFilter();
					docFilter.setSorting(v[0], v.length==2?v[1]:"asc");
					return documentService.read(docFilter.getDelegate());
				} else {
					try {
						// check if template has field with such code
						final WcmTemplateField field = templateFieldService.read1(
								new WcmTemplateField()
									.setTemplateId(templateId)
									.setCode(v[0])
						);
						
						// if field not found then do simple request without join and sorting
						if(field == null) {
							return documentService.read(new WcmDocument().setFolderId(folderId));
						} else {
							final StringBuilder query = new StringBuilder(
								"select * from wcm_documents d inner join wcm_document_fields df on (d.id = df.document_id")
									.append(" and d.folder_id =").append(folderId)
									.append(" and df.template_field_code = '").append(v[0])
									.append("') order by df.value ").append(v.length==2?v[1]:"asc");
							System.out.println(query.toString());
							return ServicesFacade.$().getQueryService().readByQuery(
								query.toString(), WcmDocument.class);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
	}
	
	public List<WcmDocumentFolder> getDocumentFolders(Integer folderId) {
		if(folderId == null) return null;
		try {
			return documentFolderService.read(
					new WcmDocumentFolder().setFolderId(folderId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<WcmDocumentFolder> getDocumentFolders(Integer folderId, String folderSortRule) {
		if(folderId == null) return null;
		final AnnotationFilter<WcmDocumentFolder> folderFilter = new WcmDocumentFolder().
				setFolderId(folderId).getFilter();
		setSorting(folderFilter, folderSortRule);
		return documentFolderService.read(folderFilter.getDelegate());
	}
	
	public List getDocumentElements(Integer folderId, Integer templateId, String folderSortRule, String documentSortRule) {
		if(folderId == null || templateId == null) return null;
		final List result = new ArrayList();
		
		final List folders = getDocumentFolders(folderId, folderSortRule);
		
		if(folders != null & folders.size() > 0){
			result.addAll(folders);
		}
		
		final List docs = getDocuments(folderId, templateId, documentSortRule);
		if(docs != null & docs.size() > 0){
			result.addAll(docs);
		}
		return result;
	}
	
	public List getDocumentElements(Integer folderId, Integer templateId, String folderSortingRule) {
		return getDocumentElements(folderId, templateId, folderSortingRule, null);
	}
	
	public List getDocumentElements(Integer folderId, Integer templateId) {
		return getDocumentElements(folderId, templateId, null, null);
	}
	
	public List getDocumentElements(Integer folderId) {
		if(folderId == null) return null;
		try {
			final List result = new ArrayList();
				result.addAll(documentFolderService.read(new WcmDocumentFolder().setFolderId(folderId)));
				result.addAll(documentService.read(new WcmDocument().setFolderId(folderId)));
				return result;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isDocument(Object o) {
		return o instanceof WcmDocument;
	}
	
	public boolean isFolder(Object o) {
		return o instanceof A_WcmFolder;
	}
	
	@Command
	public void showInfo(@BindingParam("title") String title, @BindingParam("message") String message) {
		Dialog.info(title, message);
	}
}

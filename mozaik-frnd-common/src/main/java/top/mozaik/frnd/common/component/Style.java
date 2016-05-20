/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common.component;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model.ResourcePack;
import top.mozaik.bknd.api.model.ResourcePackSet;
import top.mozaik.bknd.api.model._ResourceData;
import top.mozaik.frnd.common.ResourceSetUtils;

public class Style extends org.zkoss.zul.Style {
	
	public static final String ZUL_DEFINITION = "<?component name=\"style\" class=\"" + Style.class.getName()  +"\"?>";
	
	public Style() {}
		
	@Override
	public void setSrc(String src) {
		//final ResourcePack resourcePack = ( ResourcePack) Executions.getCurrent().getAttribute("RESOURCE_PACK");
		//final Integer resourceSetId = (Integer) Executions.getCurrent().getAttribute("RESOURCE_SET_ID");
		
		final ExecutionCtrl execCtrl = ((ExecutionCtrl)Executions.getCurrent());
		final Page page = execCtrl.getCurrentPage();
		
		final ResourcePack resourcePack = (ResourcePack) page.getAttribute("RESOURCE_PACK");
		final ResourcePackSet resourceSet = (ResourcePackSet) page.getAttribute("RESOURCE_SET");
		
		final ResourceSetUtils resourceSetUtils = new ResourceSetUtils(resourcePack, resourceSet.getResourceSetId());
		final _ResourceData resourceData = resourceSetUtils.findResourceDataByPath(src, E_ResourceType.STYLE);
		setContent(new String(resourceData.getSourceData()));
	}
	
	/// TODO: WHILE SAVING STYLE DATA 
	///	1. wrap by the resourcePack#widget selector and compile by the LESS after that
	/// 2. save to compiled_data field
	@Override
	public void setContent(String content) {
		super.setContent(content);
	}
}

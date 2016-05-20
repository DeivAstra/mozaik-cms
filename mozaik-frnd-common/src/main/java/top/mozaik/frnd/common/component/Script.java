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

public class Script extends org.zkoss.zul.Script {
	
	public static final String ZUL_DEFINITION = "<?component name=\"script\" class=\"" + Script.class.getName()  +"\"?>";
	
	public Script() {}
	
	public Script(String content) {
		super(content);
	}
	
	@Override
	public void setSrc(String src) {
		//final ResourcePack resourcePack = ( ResourcePack) Executions.getCurrent().getAttribute("RESOURCE_PACK");
		//final Integer resourceSetId = (Integer) Executions.getCurrent().getAttribute("RESOURCE_SET_ID");
		final ExecutionCtrl execCtrl = ((ExecutionCtrl)Executions.getCurrent());
		final Page page = execCtrl.getCurrentPage();
		
		final ResourcePack resourcePack = (ResourcePack) page.getAttribute("RESOURCE_PACK");
		final ResourcePackSet resourceSet = (ResourcePackSet) page.getAttribute("RESOURCE_SET");
		
		final ResourceSetUtils resourceSetUtils = new ResourceSetUtils(resourcePack, resourceSet.getResourceSetId());
		final _ResourceData resourceData = resourceSetUtils.findResourceDataByPath(src, E_ResourceType.SCRIPT);
		setContent(new String(resourceData.getSourceData()));
	}
}

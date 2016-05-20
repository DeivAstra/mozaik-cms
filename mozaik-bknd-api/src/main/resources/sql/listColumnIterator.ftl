<#macro listColumnIterator items separator=','>
	<#list items as item>
		${item}<#if item_has_next>${separator}</#if>
	</#list>
</#macro>
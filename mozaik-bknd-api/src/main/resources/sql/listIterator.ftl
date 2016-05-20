<#macro listIterator items separator='and'>
	<#list items as item>
		(${item})
		<#if item_has_next>${separator}</#if>
	</#list>
</#macro>
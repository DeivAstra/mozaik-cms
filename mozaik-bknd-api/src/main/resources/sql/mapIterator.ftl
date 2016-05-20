<#macro mapIterator map separator=','>
	<#list map?keys as key>
		${key}=${map[key]}
		<#if key_has_next> ${separator} </#if>
	</#list>
</#macro>
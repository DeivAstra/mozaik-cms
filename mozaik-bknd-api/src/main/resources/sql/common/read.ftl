<#include "../listIterator.ftl">
<#include "../listColumnIterator.ftl">
<@compress single_line=true>
<#if count??>
	select distinct count(${idName})
	from ${tableName}
	<#if filter??>
		where <@listIterator items=filter/>
	</#if>
<#else>
	select <#if columns??><@listColumnIterator items=columns/></#if>
	from ${tableName}
	<#if filter??>
		where <@listIterator items=filter/>
	</#if>
	order by
		<#if sort??>
			${sort}
			<#if dir??>${dir}</#if>
		<#else>
			${idName} desc
		</#if>
		<#if limit??>
			limit
			<#if offset??>${offset},</#if>${limit}
		</#if>
</#if>
</@compress>
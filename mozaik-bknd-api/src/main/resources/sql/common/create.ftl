<#include "../mapIterator.ftl">
insert into ${tableName}
<#if parameters?has_content>
set
	<@mapIterator map=parameters/>
<#else>
	() values()
</#if>
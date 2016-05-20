<#include "../listIterator.ftl">
<#include "../mapIterator.ftl">
delete from ${tableName}
<#if idName??>
	where ${idName}=${idValue}
<#elseif primaryKey??>
	where ${primaryKey}
<#elseif filterList??>
	where <@listIterator items=filterList/>
<#elseif filterMap??>
	where <@mapIterator map=filterMap separator='and'/>
</#if>
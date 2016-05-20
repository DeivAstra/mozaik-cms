<#include "../mapIterator.ftl">
update ${tableName}
set
	<@mapIterator map=updates/>
where ${primaryKey}
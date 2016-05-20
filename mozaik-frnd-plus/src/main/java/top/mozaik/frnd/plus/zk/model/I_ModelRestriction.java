/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.model;

import org.zkoss.zul.ListModelList;

public interface I_ModelRestriction<T> {
	void execute(ListModelList<T> model) throws Exception;
}

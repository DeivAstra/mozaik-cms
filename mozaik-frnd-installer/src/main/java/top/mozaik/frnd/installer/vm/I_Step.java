/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.installer.vm;

import org.zkoss.zul.Button;

public interface I_Step {
	
	void setNextBtn(Button btn);
	boolean onBeforeNext();
}

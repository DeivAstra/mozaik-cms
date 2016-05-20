/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Textbox;

public class AppendTextbox extends Textbox {
	
	public AppendTextbox() {
	}
	
	public void appendText(String text) {
		if(text == null) return;
		Clients.response(new AuInvoke(this, "appendText", text));
	}
}

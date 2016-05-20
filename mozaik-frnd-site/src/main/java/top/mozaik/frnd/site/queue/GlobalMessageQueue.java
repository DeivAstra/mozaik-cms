/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site.queue;

import java.util.HashSet;
import java.util.Set;

public class GlobalMessageQueue {
	
	private final static Set<GlobalMessageListener> set = new HashSet<>(0);
	
	private static GlobalMessageQueue instance;
	
	public static GlobalMessageQueue $() {
		if(instance == null) {
			instance = new GlobalMessageQueue();
		}
		return instance;
	}
	
	public void addListener(GlobalMessageListener listener) {
		set.add(listener);
	}
	
	public void removeListener(GlobalMessageListener listener) {
		set.remove(listener);
	}
	
	public void send(String msg) {
		for(GlobalMessageListener l : set) {
			l.onMessage(msg);
		}
	}
}

/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CommandExecutionQueue implements I_CommandExecutor {
	
	private final Set<I_CommandExecutor> set = new HashSet<>();
	
	public void addListener(I_CommandExecutor lst) {
		set.add(lst);
	}
	
	public void addListener(I_CommandExecutor lst, boolean dropSameClass) {
		removeByClass(lst.getClass());
		addListener(lst);
	}
	
	private void removeByClass(Class clazz) {
		final Iterator i = set.iterator();
		while(i.hasNext()) {
			if(i.next().getClass() == clazz){
				i.remove();
			}
		}
	}
	
	public void removeListener(I_CommandExecutor lst) {
		set.remove(lst);
	}
	
	public void execCommand(final int cmdId) {
		for(I_CommandExecutor e :set) {
			e.execCommand(cmdId);
		}
	}
}

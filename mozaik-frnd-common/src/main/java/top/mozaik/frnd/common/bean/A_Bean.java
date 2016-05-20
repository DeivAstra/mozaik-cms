/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common.bean;

import top.mozaik.frnd.plus.zk.I_StringUI;

public abstract class A_Bean<D> implements I_StringUI {
	
	protected final D delegate;
	
	public A_Bean(D delegate) {
		this.delegate = delegate;
	}
	
	public D getDelegate() {
		return delegate;
	}
	
	@Override
	public String toStringUI() {
		return delegate.toString();
	}
}

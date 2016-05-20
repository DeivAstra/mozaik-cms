/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.event;

public interface I_CUDEventHandler<T> {

	void onCreate(T v);
	void onUpdate(T v);
	void onDelete(T v);
}

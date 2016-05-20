/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.callback;

import java.util.Map;

@Deprecated
interface I_CallbackMap<K,V> {
	void call(Map<K, V> map);
}

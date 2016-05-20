/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus;

public class TimeMeter {
	
	private long timestamp;
	
	public String tick() {
		if(timestamp == 0) {
			timestamp = System.currentTimeMillis();
			return "0";
		}
		
		Long time = System.currentTimeMillis() - timestamp;
		timestamp = System.currentTimeMillis();
		//System.out.println(time);
		//return time.toString();
		
		long second = (time / 1000) % 60;
		long minute = (time / (1000 * 60)) % 60;
		long hour = (time / (1000 * 60 * 60)) % 24;
		
		final String res = String.format("%02d:%02d:%02d:%d", hour, minute, second, time);
		System.out.println(res);
		return res;
	}
}

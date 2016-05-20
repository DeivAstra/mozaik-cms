/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.common;

import top.mozaik.bknd.api.enums.E_ResourceType;

public class ResourceUtils {
	
	public static boolean isType(String type, E_ResourceType eType) {
		return type.equals(eType.name());
	}
	
	public static String removeExtension(String resourceName) {
		final int dotIndex = resourceName.lastIndexOf('.');
		if(dotIndex > 0)
			return resourceName.substring(0, dotIndex);
		return resourceName;
	}
	
	public static String appendExtension(String resourceName, E_ResourceType type) {
		final int extIndex = resourceName.lastIndexOf("."+type.lname());
		if(extIndex > 0) return resourceName;
		return resourceName + "." + type.lname();
	}
	
	public static String fixPackageDef(final String _package, final String source) {
		if(source.startsWith("package ")) {
			final int semicolonIdx = source.indexOf(';');
			final int newLineIndex = source.indexOf('\n');
			final int endOfPackageDef = (semicolonIdx < newLineIndex)?
									((semicolonIdx > 0 )?semicolonIdx:newLineIndex):
									newLineIndex;
			if(endOfPackageDef > 0) {
				if(source.substring(8/*'package '*/, endOfPackageDef).trim().equals(_package)) {
					if(endOfPackageDef != semicolonIdx) {
						return new StringBuilder(source).
								insert(endOfPackageDef, ';').toString();
					}
					return source;
				} else {
					return new StringBuilder(source)
						.replace(8, endOfPackageDef, _package + ((endOfPackageDef == semicolonIdx)?"":";"))
						.toString();
				}
			}
		} else {
			return new StringBuilder(source)
				.insert(0, ";\n\n").insert(0, _package).insert(0, "package ")
				.toString();
		}
		return null;
	}
	
	public static String fixClassName(final String className, final String source) {
		// TODO: fix class name after rename
		return null;
	}
}

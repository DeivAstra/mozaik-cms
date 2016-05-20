/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.admin.bean.resourcepack.tree;

import top.mozaik.bknd.api.enums.E_ResourceSetType;

public class TreeResourceSetTypeFolder extends A_TreeResourcePackFolder<TreeResourcePackFolder, TreeResourceSet, E_ResourceSetType> {

	public TreeResourceSetTypeFolder(E_ResourceSetType value) {
		super(value);
	}
	
	@Override
	public String toString() {
		switch (getValue()) {
		case WIDGET:
			return "Widgets";
		case THEME:
			return "Themes";
		case SKIN:
			return "Skins";
		}
		return "Unknown";
	}
}

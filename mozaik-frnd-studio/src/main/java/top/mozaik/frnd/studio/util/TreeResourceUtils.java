/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.studio.util;

import java.util.ArrayList;
import java.util.List;

import top.mozaik.bknd.api.enums.E_ResourceType;
import top.mozaik.bknd.api.model._Resource;
import top.mozaik.frnd.plus.zk.tree.A_TreeElement;
import top.mozaik.frnd.plus.zk.tree.A_TreeNode;
import top.mozaik.frnd.studio.bean.tree.TreeResource;
import top.mozaik.frnd.studio.bean.tree.TreeResourceFolder;
import top.mozaik.frnd.studio.bean.tree.TreeResourceSetFolder;

public class TreeResourceUtils {
	
	private static final String [] JAVA_KEYWORDS = {
		"abstract", "continue", "for", "new", "switch",
		"assert", "default", "goto", "package", "synchronized",
		"boolean", "do", "if", "private", "this",
		"break", "double", "implements", "protected", "throw",
		"byte", "else", "import", "public", "throws",
		"case", "enum", "instanceof", "return", "transient",
		"catch", "extends", "int", "short", "try",
		"char", "final", "interface", "static", "void",
		"class", "finally", "long", "strictfp", "volatile",
		"const", "float", "native", "super", "while"
	};
	
	public static String buildPath(A_TreeElement<?, _Resource> element) {
		return buildPath(element, false);
	}
	
	public static String buildPath(A_TreeElement<?, _Resource> element, boolean includeResourcePack) {
		return buildPath(element, includeResourcePack, '/');
	}
	
	// <resourcePack><delimiter><resourceSet><delimiter><folder>...<folder>
	public static String buildPath(A_TreeElement element, boolean includeResourcePack, char delimiter) {
		final StringBuilder sb = new StringBuilder(element.toString());
		
		sb.insert(0, delimiter);
		while((element = element.getParent()) != null) {
			if(element instanceof TreeResourceSetFolder) {
				final TreeResourceSetFolder resSetFolder = (TreeResourceSetFolder) element;
				sb.insert(1, delimiter);
				sb.insert(1, resSetFolder);
				if(includeResourcePack) {
					sb.insert(1, delimiter);
					sb.insert(1, resSetFolder.getResourcePackAlias());
				}
				return sb.toString();
			}
			sb.insert(1, delimiter);
			sb.insert(1, element.toString());
		}
		return sb.toString();
	}
	
	public static List<TreeResource> findAllTreeResources(TreeResourceSetFolder folder, E_ResourceType type) {
		final TreeResourceFolder typeFolder = getTypeFolder(folder, type);
		return findAllTreeResources(typeFolder, new ArrayList<TreeResource>());
	}
	
	public static List<TreeResource> findAllTreeResources(TreeResourceFolder folder) {
		return findAllTreeResources(folder, new ArrayList<TreeResource>());
	}
	
	private static List<TreeResource> findAllTreeResources(TreeResourceFolder folder, List<TreeResource> list) {
		for(int i = 0; i < folder.size(); i++) {
			final A_TreeElement res = folder.get(i);
			if(res instanceof TreeResourceFolder) {
				findAllTreeResources((TreeResourceFolder)res, list);
			} else {
				list.add((TreeResource)res);
			}
		}
		return list;
	}
	
	public static final TreeResourceFolder getTypeFolder(TreeResourceSetFolder folder, E_ResourceType type) {
		for(int i = 0; i < folder.size(); i++) {
			if(folder.get(i).isType(type))
				return folder.get(i);
		}
		throw new IllegalArgumentException("Could't to find type folder in ResourceSetFolder");
	}
	
	public static String buildPackagePath(TreeResourceFolder folder) {
		final StringBuilder sb = new StringBuilder();
		
		if(folder.hasType()) {
			final TreeResourceSetFolder resourceSetFolder = (TreeResourceSetFolder) folder.getParent();
			sb.append(resourceSetFolder.getResourcePackAlias())
				.append('.').append(resourceSetFolder.getValue().getType())
				.append('.').append(fixIfJavaKeyword(resourceSetFolder.getValue().getAlias()));
			return sb.toString().toLowerCase();
		}
		
		sb.append(folder.toString());
		A_TreeNode parent = folder;
		while((parent = parent.getParent()) != null) {
			if(parent instanceof TreeResourceSetFolder) {
				final TreeResourceSetFolder resourceSetFolder = (TreeResourceSetFolder) parent;
					sb.insert(0, '.')
					.insert(0, fixIfJavaKeyword(resourceSetFolder.getValue().getAlias()))
					.insert(0, '.')
					.insert(0, resourceSetFolder.getValue().getType())
					.insert(0, '.')
					.insert(0, resourceSetFolder.getResourcePackAlias());
				return sb.toString().toLowerCase();
			}
			/// DON'T INCLUDE 'java' FOLDER TO PACKAGE PATH
			if(parent instanceof TreeResourceFolder & ((TreeResourceFolder)parent).hasType()) {
				continue;
			}
			sb.insert(0, '.');
			sb.insert(0, parent.toString());
		}
		return sb.toString().toLowerCase();
	}
	
	private static String fixIfJavaKeyword(String v) {
		for(String keyword : JAVA_KEYWORDS) {
			if(keyword.equalsIgnoreCase(v)) {
				return "_"+v;
			}
		}
		return v;
	}
	
	/*
	public static TreeResourceSetFolder getTreeResourceSetFolder(TreeResource resource) {
		A_TreeNode parent;
		while((parent = resource.getParent()) != null) {
			if(parent instanceof TreeResourceSetFolder) return (TreeResourceSetFolder)parent;
		}
		return null;
	}*/
	
	public static E_ResourceType getTreeResourceType(A_TreeElement<?, _Resource> el) {
		E_ResourceType type;
		while((el = el.getParent()) != null) {
			if(el instanceof TreeResourceFolder) {
				TreeResourceFolder folder = (TreeResourceFolder) el;
				if(folder.hasType()) return folder.getType();
			}
		}
		return null;
	}
}

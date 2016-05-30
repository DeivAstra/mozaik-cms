Mozaik CMS
---
Based on Java and ZKoss Web Framework<br/>

Official site [www.mozaik.top](http://www.mozaik.top) still in development.

Virtualbox snapshots with pre-installed Mozaik CMS will be later.

Now, you can checkout the code and build by Maven manually then deploy wars(\*-installer-\*.war, \*-admin-\*.war, \*-studio-\*.war) to any modern Java Web/Application Server, for example, Tomcat.

Requirements:
 * Java Development Kit 7+
 * MySQL 5.5+
 
Features:

* **Base**
  * Built-in ZK 8 support
  * Resource Pack concept <sub>(include Widgets, Themes, Skins, Libraries, DB Queries)</sub>
  * Resource Pack migration <sub>(build own resource pack and move it to other CMS instance easily)</sub>
* **Mozaik Installer**
  * 7 steps to install the CMS instance
* **Mozaik Admin**
  * Building layout of pages by graphical tool
  * Built-in WCM <sub>(include Libraries, Documents, Templates, Components, Resources)</sub>
  * User management
  * Resource Pack management (also import / export resource packs)
* **Mozaik Studio**
  * Building Resource Pack's stuff
  * Built-in Codemirror editor
  * Hot Java compilation
* **Mozaik Site**
  * Render pages
  * On the fly Java classes reloading (need't restart webapp to update changed classes code)


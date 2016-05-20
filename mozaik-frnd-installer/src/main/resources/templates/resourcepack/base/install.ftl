CREATE DATABASE `${schema}`;
USE `${schema}`;

<#noparse>

CREATE TABLE `$_resource_sets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(12) COLLATE latin1_bin NOT NULL,
  `title` varchar(100) COLLATE latin1_bin NOT NULL,
  `alias` varchar(100) COLLATE latin1_bin DEFAULT NULL,
  `descr` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

SET @widget_resource_set_id = 1;
SET @theme_resource_set_id = 2;
SET @skin_resource_set_id = 3;

INSERT INTO `$_resource_sets` VALUES (@widget_resource_set_id,'WIDGET','Wcm Widget','wcmwidget',NULL);
INSERT INTO `$_resource_sets` VALUES (@theme_resource_set_id,'THEME','Default','_default',NULL);
INSERT INTO `$_resource_sets` VALUES (@skin_resource_set_id,'SKIN','Default','_default',NULL);

CREATE TABLE `$_resources` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `resource_set_id` int(10) unsigned NOT NULL,
  `parent_id` int(10) unsigned DEFAULT NULL,
  `type` varchar(10) NOT NULL,
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `source_data` blob,
  `compiled_data` blob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

# CREATE TEMPORARY TABLE TO LOAD FILES
CREATE TEMPORARY TABLE `__tmp__` (
  `data` TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

</#noparse>

# LOAD WIDGET RESOURCES

SET @ComponentVM.java = LOAD_FILE('${base_path}/widget/wcmwidget/java/ComponentVM.java');
SET @index.zul = LOAD_FILE('${base_path}/widget/wcmwidget/zul/index.zul');
SET @layout_params.form.zul = LOAD_FILE('${base_path}/widget/wcmwidget/zul/layout_params.form.zul');

INSERT INTO $_resources VALUES (1,@widget_resource_set_id,NULL,'FOLDER','style',NULL,NULL);
INSERT INTO $_resources VALUES (2,@widget_resource_set_id,NULL,'FOLDER','script',NULL,NULL);
INSERT INTO $_resources VALUES (3,@widget_resource_set_id,NULL,'FOLDER','media',NULL,NULL);
INSERT INTO $_resources VALUES (4,@widget_resource_set_id,NULL,'FOLDER','java',NULL,NULL);
INSERT INTO $_resources VALUES (5,@widget_resource_set_id,NULL,'FOLDER','zul',NULL,NULL);
INSERT INTO $_resources VALUES (6,@widget_resource_set_id,4,'JAVA','ComponentVM.java',@ComponentVM.java,NULL);
INSERT INTO $_resources VALUES (7,@widget_resource_set_id,5,'ZUL','index.zul', @index.zul,NULL);
INSERT INTO $_resources VALUES (8,@widget_resource_set_id,5,'ZUL','layout_params.form.zul',@layout_params.form.zul,NULL);

# LOAD THEME RESOURCES

SET @index.zul = LOAD_FILE('${base_path}/theme/default/zul/index.zul');
SET @header.zul = LOAD_FILE('${base_path}/theme/default/zul/header.zul');
SET @footer.zul = LOAD_FILE('${base_path}/theme/default/zul/footer.zul');
SET @theme.style = LOAD_FILE('${base_path}/theme/default/style/theme.style');

INSERT INTO $_resources VALUES (9,@theme_resource_set_id,NULL,'FOLDER','style',NULL,NULL);
INSERT INTO $_resources VALUES (10,@theme_resource_set_id,NULL,'FOLDER','script',NULL,NULL);
INSERT INTO $_resources VALUES (11,@theme_resource_set_id,NULL,'FOLDER','media',NULL,NULL);
INSERT INTO $_resources VALUES (12,@theme_resource_set_id,NULL,'FOLDER','java',NULL,NULL);
INSERT INTO $_resources VALUES (13,@theme_resource_set_id,NULL,'FOLDER','zul',NULL,NULL);

INSERT INTO $_resources VALUES (14,@theme_resource_set_id,13,'ZUL','index.zul',@index.zul,NULL);
INSERT INTO $_resources VALUES (15,@theme_resource_set_id,13,'ZUL','header.zul',@header.zul,NULL);
INSERT INTO $_resources VALUES (16,@theme_resource_set_id,13,'ZUL','footer.zul',@footer.zul,NULL);
INSERT INTO $_resources VALUES (17,@theme_resource_set_id,9,'STYLE','theme.style',@theme.style,NULL);

# LOAD SKIN RESOURCES

SET @index.zul = LOAD_FILE('${base_path}/skin/default/zul/index.zul');
SET @layout_params.form.zul = LOAD_FILE('${base_path}/skin/default/zul/layout_params.form.zul');
SET @skin.style = LOAD_FILE('${base_path}/skin/default/style/skin.style');

INSERT INTO $_resources VALUES (18,@skin_resource_set_id,NULL,'FOLDER','style',NULL,NULL);
INSERT INTO $_resources VALUES (19,@skin_resource_set_id,NULL,'FOLDER','script',NULL,NULL);
INSERT INTO $_resources VALUES (20,@skin_resource_set_id,NULL,'FOLDER','media',NULL,NULL);
INSERT INTO $_resources VALUES (21,@skin_resource_set_id,NULL,'FOLDER','java',NULL,NULL);
INSERT INTO $_resources VALUES (22,@skin_resource_set_id,NULL,'FOLDER','zul',NULL,NULL);

INSERT INTO $_resources VALUES (23,@skin_resource_set_id,22,'ZUL','index.zul',@index.zul,NULL);
INSERT INTO $_resources VALUES (24,@skin_resource_set_id,22,'ZUL','layout_params.form.zul',@layout_params.form.zul,NULL);
INSERT INTO $_resources VALUES (25,@skin_resource_set_id,18,'STYLE','skin.style',@skin.style,NULL);
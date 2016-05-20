CREATE TABLE `${schema}`.`site_page_layouts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `page_id` int(11) NOT NULL,
  `parent_layout_id` int(11) DEFAULT NULL,
  `orient` int(11) NOT NULL DEFAULT '0',
  `min_width` smallint(6) DEFAULT NULL,
  `width` smallint(6) DEFAULT NULL,
  `max_width` smallint(6) DEFAULT NULL,
  `min_height` smallint(6) DEFAULT NULL,
  `height` smallint(6) DEFAULT NULL,
  `max_height` smallint(6) DEFAULT NULL,
  `indent_top` smallint(6) DEFAULT NULL,
  `indent_right` smallint(6) DEFAULT NULL,
  `indent_bottom` smallint(6) DEFAULT NULL,
  `indent_left` smallint(6) DEFAULT NULL,
  `class` varchar(50) DEFAULT NULL,
  `style` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

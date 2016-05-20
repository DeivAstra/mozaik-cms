CREATE TABLE `${schema}`.`$_resources` (
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
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
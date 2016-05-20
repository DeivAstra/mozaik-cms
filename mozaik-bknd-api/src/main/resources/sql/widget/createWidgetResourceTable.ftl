CREATE TABLE `${schemaName}`.`widget_resources` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `widget_id` int(10) unsigned NOT NULL,
  `folder_id` int(10) unsigned DEFAULT NULL,
  `type` varchar(10) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_widget_resource_type_idx` (`type`),
  CONSTRAINT `fk_widget_resource_type` FOREIGN KEY (`type`) REFERENCES `ocean`.`e_widget_resource_types` (`name`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
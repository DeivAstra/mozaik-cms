CREATE TABLE `${schemaName}`.`widget_resource_data` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `widget_resource_id` int(10) unsigned NOT NULL,
  `data` blob,
  PRIMARY KEY (`id`,`widget_resource_id`),
  KEY `fk_widget_resource_data_resource_id_idx` (`widget_resource_id`),
  CONSTRAINT `fk_widget_resource_data_resource_id` FOREIGN KEY (`widget_resource_id`) REFERENCES `widget_resources` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
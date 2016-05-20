CREATE TABLE `${schema}`.`resource_pack_sets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_pack_id` int(11) NOT NULL,
  `resource_set_id` int(11) NOT NULL,
  `resource_set_type` varchar(10) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `title` varchar(200) NOT NULL,
  `alt_title` varchar(200) DEFAULT NULL,
  `is_published` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

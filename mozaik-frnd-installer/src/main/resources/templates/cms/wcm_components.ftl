CREATE TABLE `${schema}`.`wcm_components` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folder_id` int(11) NOT NULL,
  `element_id` int(11) DEFAULT NULL,
  `alias` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `title` varchar(128) NOT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `data` varchar(10000) DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

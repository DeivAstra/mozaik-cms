CREATE TABLE `${schema}`.`wcm_resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folder_id` int(11) NOT NULL,
  `alias` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `title` varchar(128) NOT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `data_content_type` varchar(100) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL,
  `data_id` int(11) DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `${schema}`.`wcm_documents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folder_id` int(11) NOT NULL,
  `template_id` int(11) DEFAULT NULL,
  `alias` varchar(64) NOT NULL,
  `title` varchar(256) NOT NULL,
  `descr` varchar(512) DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `publish_start_date` timestamp NULL DEFAULT NULL,
  `publish_end_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

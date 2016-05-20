CREATE TABLE `${schema}`.`wcm_resource_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(100) DEFAULT NULL,
  `content_type` varchar(100) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `data` longblob,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

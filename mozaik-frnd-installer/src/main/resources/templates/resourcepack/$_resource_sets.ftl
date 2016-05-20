CREATE TABLE `${schema}`.`$_resource_sets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(12) COLLATE latin1_bin NOT NULL,
  `title` varchar(100) COLLATE latin1_bin NOT NULL,
  `alias` varchar(100) COLLATE latin1_bin DEFAULT NULL,
  `descr` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

CREATE TABLE `e_resource_set_types` (
  `name` varchar(12) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `e_resource_set_types` VALUES ('LIBRARY'),('QUERY_FOLDER'),('SKIN'),('THEME'),('WIDGET');

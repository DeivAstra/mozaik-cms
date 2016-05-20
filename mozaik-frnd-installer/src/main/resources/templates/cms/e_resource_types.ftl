CREATE TABLE `e_resource_types` (
  `name` varchar(10) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`name`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `e_resource_types` VALUES ('FOLDER'),('JAVA'),('MEDIA'),('QUERY'),('SCRIPT'),('STYLE'),('ZUL');

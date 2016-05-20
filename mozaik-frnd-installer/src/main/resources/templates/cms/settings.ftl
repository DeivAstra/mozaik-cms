CREATE TABLE `settings` (
  `_key` varchar(100) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `value` varchar(1000) DEFAULT NULL,
  `descr` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`_key`),
  UNIQUE KEY `key_UNIQUE` (`_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
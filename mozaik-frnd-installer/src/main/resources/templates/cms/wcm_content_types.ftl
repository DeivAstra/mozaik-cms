CREATE TABLE `${schema}`.`wcm_content_types` (
  `mime` varchar(100) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  PRIMARY KEY (`mime`),
  UNIQUE KEY `mime_UNIQUE` (`mime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

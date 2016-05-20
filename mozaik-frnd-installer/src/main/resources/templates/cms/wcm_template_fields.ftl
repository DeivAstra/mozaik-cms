CREATE TABLE `${schema}`.`wcm_template_fields` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template_id` int(11) NOT NULL,
  `position` tinyint(4) NOT NULL,
  `code` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `type` varchar(20) CHARACTER SET latin1 NOT NULL,
  `title` varchar(128) NOT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `value` varchar(1024) DEFAULT NULL,
  `constr` varchar(128) DEFAULT NULL,
  `constr_regex` varchar(500) DEFAULT NULL,
  `constr_regex_err_msg` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

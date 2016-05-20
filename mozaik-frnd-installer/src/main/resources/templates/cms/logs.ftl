CREATE TABLE `${schema}`.`logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `component` char(4) NOT NULL,
  `source` varchar(45) NOT NULL,
  `is_error` tinyint(1) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
  
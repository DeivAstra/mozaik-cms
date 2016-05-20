CREATE TABLE `${schemaName}`.`widgets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `workspace_id` int(11) NOT NULL,
  `name` varchar(45) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_widgets_workspace_id` (`workspace_id`),
  CONSTRAINT `fk_widgets_workspace_id` FOREIGN KEY (`workspace_id`) REFERENCES `ocean.workspaces` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
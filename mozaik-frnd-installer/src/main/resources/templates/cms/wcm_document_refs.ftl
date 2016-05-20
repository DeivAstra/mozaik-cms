CREATE TABLE `${schema}`.`wcm_document_refs` (
  `id` int(11) NOT NULL,
  `document_id` int(11) NOT NULL,
  `folder_id` int(11) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

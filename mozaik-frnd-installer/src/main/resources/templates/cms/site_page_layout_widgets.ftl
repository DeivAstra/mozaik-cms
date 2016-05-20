CREATE TABLE `${schema}`.`site_page_layout_widgets` (
  `page_id` int(11) NOT NULL,
  `layout_id` int(11) NOT NULL,
  `skin_id` int(11) DEFAULT NULL,
  `skin_params` varchar(1000) DEFAULT NULL,
  `widget_id` int(11) NOT NULL,
  `widget_params` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`layout_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

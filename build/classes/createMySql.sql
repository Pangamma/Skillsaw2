CREATE TABLE `skillsaw_users` (
	`user_id` INT(11) NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(40) NOT NULL COMMENT 'Last username used on the server',
	`display_name` VARCHAR(128) NULL DEFAULT NULL,
	`uuid` VARCHAR(40) NOT NULL,
	`ipv4` VARCHAR(128) NULL DEFAULT NULL,
	`current_title` VARCHAR(128) NOT NULL,
	`custom_titles` VARCHAR(4096) NOT NULL,
	`chat_color` VARCHAR(32) NOT NULL DEFAULT '',
	`rep_level` INT(11) NOT NULL DEFAULT '0',
	`natural_rep` DOUBLE NOT NULL DEFAULT '0',
	`staff_rep` DOUBLE NOT NULL DEFAULT '0',
	`last_played` BIGINT(20) NOT NULL DEFAULT '0',
	`first_played` BIGINT(20) NOT NULL DEFAULT '0',
	`speaking_channel` VARCHAR(1024) NOT NULL DEFAULT '1',
	`sticky_channels` VARCHAR(4096) NULL DEFAULT '',
	`ignored_players` VARCHAR(4096) NULL DEFAULT '',
	`last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`user_id`),
	UNIQUE INDEX `uuid` (`uuid`),
	INDEX `username` (`username`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

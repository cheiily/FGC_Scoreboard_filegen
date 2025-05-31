CREATE TABLE `Config` (
  `opt_id` int PRIMARY KEY AUTO_INCREMENT,
  `opt_group` string,
  `opt_name` string,
  `opt_value` string
);

CREATE TABLE `MatchData` (
  `data_id` int PRIMARY KEY AUTO_INCREMENT,
  `data_key` string,
  `data_val` string
);

CREATE TABLE `Players` (
  `id` int PRIMARY KEY,
  `tag` string,
  `name` string,
  `nationality` string COMMENT 'ISO2',
  `pronouns` string,
  `remote_id` int
);

CREATE TABLE `RemotePlayers` (
  `id` int PRIMARY KEY,
  `name` string,
  `seed` int,
  `icon` string,
  `checkin` bool,
  `url` string
);

ALTER TABLE `RemotePlayers` ADD FOREIGN KEY (`id`) REFERENCES `Players` (`remote_id`);

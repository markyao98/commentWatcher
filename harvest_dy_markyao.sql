/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.38-log : Database - harvest_douyin_yrw
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
USE `harvest_douyin_yrw`;

/*Table structure for table `buf_word_cloud` */

DROP TABLE IF EXISTS `buf_word_cloud`;

CREATE TABLE `buf_word_cloud` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aids` text COMMENT ',分割',
  `word_cloud` varchar(128) DEFAULT NULL,
  `top_words` text,
  `totals` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1662168387954565122 DEFAULT CHARSET=utf8;

/*Table structure for table `harvest_comment` */

DROP TABLE IF EXISTS `harvest_comment`;

CREATE TABLE `harvest_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) DEFAULT NULL,
  `aid` varchar(64) DEFAULT NULL,
  `did` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_cid_uid_did` (`id`,`uid`,`did`),
  UNIQUE KEY `idx_cid_uid` (`id`,`uid`),
  UNIQUE KEY `idx_cid_did` (`id`,`did`)
) ENGINE=InnoDB AUTO_INCREMENT=1664880553321234434 DEFAULT CHARSET=utf8;

/*Table structure for table `harvest_comment_details` */

DROP TABLE IF EXISTS `harvest_comment_details`;

CREATE TABLE `harvest_comment_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aweme_id` varchar(64) DEFAULT NULL,
  `cid` varchar(64) DEFAULT NULL COMMENT '评论id',
  `ip_label` varchar(32) DEFAULT NULL COMMENT 'ip归属地',
  `create_time` datetime DEFAULT NULL COMMENT '评论创建时间',
  `digg_count` int(11) DEFAULT NULL COMMENT '点赞数',
  `reply_comment_total` int(11) DEFAULT NULL COMMENT '回复数',
  `text` mediumtext COMMENT '评论内容',
  `is_author_digged` tinyint(1) DEFAULT NULL,
  `cur` int(11) DEFAULT NULL COMMENT '页数(爬取时有用)',
  `count` int(11) DEFAULT NULL COMMENT '采集数量每页',
  PRIMARY KEY (`id`),
  KEY `idx_cid` (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=1664084259761766402 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `harvest_comment_url` */

DROP TABLE IF EXISTS `harvest_comment_url`;

CREATE TABLE `harvest_comment_url` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) DEFAULT NULL COMMENT '评论列表url',
  `author_id` varchar(128) DEFAULT NULL COMMENT '视频作者id',
  `video_id` varchar(128) DEFAULT NULL COMMENT '视频id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_deal` tinyint(1) DEFAULT '0' COMMENT '是否被处理过',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `harvest_comment_user` */

DROP TABLE IF EXISTS `harvest_comment_user`;

CREATE TABLE `harvest_comment_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(128) DEFAULT NULL COMMENT 'uid',
  `sec_uid` varchar(128) DEFAULT NULL COMMENT '可调转的uid',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像url',
  `region` varchar(16) DEFAULT NULL COMMENT '注册地',
  `language` varchar(32) DEFAULT NULL COMMENT '使用的语言',
  `nickname` varchar(128) DEFAULT NULL COMMENT '用户昵称',
  `user_age` int(11) DEFAULT NULL COMMENT '用户年龄',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1664089519137947651 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `monitor_comment_digg` */

DROP TABLE IF EXISTS `monitor_comment_digg`;

CREATE TABLE `monitor_comment_digg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cdid` bigint(20) DEFAULT NULL COMMENT '关联details表的id',
  `reply_comment_total` int(11) DEFAULT NULL COMMENT '回复数',
  `dig_count` int(11) DEFAULT NULL COMMENT '点赞数',
  `is_author_digged` tinyint(1) DEFAULT NULL COMMENT '作者赞过',
  `create_time` datetime DEFAULT NULL COMMENT '监视时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1664881635002875906 DEFAULT CHARSET=utf8;

/*Table structure for table `video_group` */

DROP TABLE IF EXISTS `video_group`;

CREATE TABLE `video_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) DEFAULT NULL COMMENT '视频群组标题',
  `desc_msg` varchar(128) DEFAULT NULL COMMENT '视频群组描述信息',
  `img` varchar(128) DEFAULT NULL COMMENT '视频群组照片',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1664614949242769410 DEFAULT CHARSET=utf8;

/*Table structure for table `video_info` */

DROP TABLE IF EXISTS `video_info`;

CREATE TABLE `video_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aweme_id` varchar(64) DEFAULT NULL COMMENT 'aweme_id',
  `watch_link` varchar(256) DEFAULT NULL COMMENT '播放链接',
  `title_info` text COMMENT '标题信息',
  `word_cloud` varchar(32) DEFAULT NULL COMMENT '词云图id',
  `can_monitor` tinyint(1) DEFAULT '0' COMMENT '可否监控',
  `top_words` text COMMENT '热门词前20',
  `totals` int(11) DEFAULT NULL COMMENT '总数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_aid` (`aweme_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1664822164243320835 DEFAULT CHARSET=utf8;

/*Table structure for table `video_info_group` */

DROP TABLE IF EXISTS `video_info_group`;

CREATE TABLE `video_info_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) DEFAULT NULL,
  `info_id` bigint(20) DEFAULT NULL,
  `aid` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;

/*Table structure for table `video_monitored` */

DROP TABLE IF EXISTS `video_monitored`;

CREATE TABLE `video_monitored` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aid` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

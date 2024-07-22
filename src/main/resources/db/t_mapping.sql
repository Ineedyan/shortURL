/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80200
 Source Host           : localhost:3306
 Source Schema         : shorturl

 Target Server Type    : MySQL
 Target Server Version : 80200
 File Encoding         : 65001

 Date: 22/07/2024 09:46:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_mapping
-- ----------------------------
DROP TABLE IF EXISTS `t_mapping`;
CREATE TABLE `t_mapping`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `long_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `short_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_mapping
-- ----------------------------
INSERT INTO `t_mapping` VALUES (1, 'www.scut.edu.cn', 'abc123', '2024-07-22 00:30:15');
INSERT INTO `t_mapping` VALUES (2, 'www.baidu.com', 'EzjBdbnD', '2024-07-22 09:28:01');

SET FOREIGN_KEY_CHECKS = 1;

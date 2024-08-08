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

 Date: 24/07/2024 11:25:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_mapping
-- ----------------------------
DROP TABLE IF EXISTS `t_mapping`;
CREATE TABLE `t_mapping`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `long_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `short_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_mapping
-- ----------------------------
INSERT INTO `t_mapping` VALUES (5, 'https://www.scut.edu.cn', 'XGTzRQl2', '2024-07-22 16:59:21');
INSERT INTO `t_mapping` VALUES (6, 'https://www.bilibili.com/video/BV14S421979h/?spm_id_from=333.1007.tianma.1-1-1.click&vd_source=d83a3b6d8dbdfbe64c15a9f25435eade', 'z0hrug39', '2024-07-23 10:28:37');
INSERT INTO `t_mapping` VALUES (7, 'https://www.baidu.com', 'SPseHv5c', '2024-07-23 10:50:14');
INSERT INTO `t_mapping` VALUES (8, 'http://8.138.115.234:8080/shoppingWeb', 'nfExC4HD', '2024-07-23 11:05:08');

SET FOREIGN_KEY_CHECKS = 1;

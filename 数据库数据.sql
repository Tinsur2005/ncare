/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : elder

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 07/09/2026 20:59:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题（首页滚动条、公告列表展示）',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公告内容（公告详情展示，可为空）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0：下架，1：发布）。前台只展示已发布的公告',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（前台公告列表展示的日期就取这个字段）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '本周五上午社区免费测血压活动，欢迎参加', '本周五上午9:00-11:00，社区服务中心一楼大厅开展免费测血压活动，欢迎各位老年朋友携带健康档案前来参加。', 1, '2026-08-28 09:00:00', '2026-09-03 09:39:22');
INSERT INTO `announcement` VALUES (2, '秋季流感疫苗接种开始预约，请到前台登记', '秋季流感疫苗已到货，有接种需求的老年朋友请携带身份证到社区前台登记预约，接种时请空腹前往。', 1, '2026-08-26 10:00:00', '2026-09-03 09:39:22');
INSERT INTO `announcement` VALUES (3, '体检报告已出，可在\"我的预约\"中查看结果', '上月参加体检的老年朋友，体检报告已全部出齐，可在家属端\"我的预约\"中查看结果，如有异常请及时咨询医生。', 1, '2026-08-25 14:00:00', '2026-09-03 09:39:22');
INSERT INTO `announcement` VALUES (4, '社区食堂本周新增营养粥品，适合老年朋友', '社区食堂本周起新增南瓜小米粥、山药瘦肉粥等多款低糖软烂的营养粥品，欢迎各位老年朋友前来品尝。', 1, '2026-08-24 16:00:00', '2026-09-03 09:39:22');

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '床位ID',
  `room_id` bigint NOT NULL COMMENT '所属房间ID（关联room.id）',
  `elder_id` bigint NULL DEFAULT NULL COMMENT '入住老人ID（关联elder.id，状态为已占用时有值，空闲和维修时为NULL）',
  `bed_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '床位号',
  `monthly_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '床位费（元/月）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0空闲 1已占用 2维修中',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志：0正常 1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bed_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_bed_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '床位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES (1, 1, 3, '1床', 500.00, 1, '靠窗', 0, '2026-09-04 20:54:46', '2026-09-07 19:27:34');
INSERT INTO `bed` VALUES (2, 1, 2, '2床', 450.00, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-07 19:27:25');
INSERT INTO `bed` VALUES (3, 2, 1, '1床', 500.00, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-07 19:27:09');
INSERT INTO `bed` VALUES (4, 2, 11, '2床', 450.00, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-07 19:26:49');
INSERT INTO `bed` VALUES (5, 3, 10, '1床', 600.00, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-07 19:26:24');
INSERT INTO `bed` VALUES (6, 3, 12, '2床', 550.00, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-06 12:12:01');
INSERT INTO `bed` VALUES (7, 4, 8, '1床', 500.00, 1, '靠窗', 0, '2026-09-04 20:54:46', '2026-09-06 12:48:19');
INSERT INTO `bed` VALUES (8, 4, 9, '2床', 450.00, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-07 19:26:04');
INSERT INTO `bed` VALUES (9, 5, 4, '整栋', 9999.00, 1, NULL, 0, '2026-09-07 19:29:31', '2026-09-07 19:29:31');
INSERT INTO `bed` VALUES (10, 6, 5, '整栋', 9999.00, 1, NULL, 0, '2026-09-07 19:29:57', '2026-09-07 19:29:57');
INSERT INTO `bed` VALUES (11, 7, 6, '夫妻套房-1', 15999.00, 1, NULL, 0, '2026-09-07 19:31:16', '2026-09-07 19:38:05');
INSERT INTO `bed` VALUES (12, 7, 7, '夫妻套房-2', 0.00, 1, NULL, 0, '2026-09-07 19:31:42', '2026-09-07 19:31:42');

-- ----------------------------
-- Table structure for building
-- ----------------------------
DROP TABLE IF EXISTS `building`;
CREATE TABLE `building`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '楼栋名称',
  `sort` int NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志：0正常 1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '楼栋表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of building
-- ----------------------------
INSERT INTO `building` VALUES (1, '颐养1号', 0, '', 0, '2026-09-04 20:54:46', '2026-09-06 11:16:52');
INSERT INTO `building` VALUES (2, '颐养2号', 0, '', 0, '2026-09-04 20:54:46', '2026-09-06 11:16:56');
INSERT INTO `building` VALUES (3, '颐养3号', 0, NULL, 1, '2026-09-06 11:16:40', '2026-09-06 11:18:33');
INSERT INTO `building` VALUES (4, '别墅区1号楼', 0, NULL, 0, '2026-09-06 11:16:47', '2026-09-06 11:16:47');
INSERT INTO `building` VALUES (5, '别墅区2号楼', 0, NULL, 0, '2026-09-06 11:17:03', '2026-09-06 11:17:03');
INSERT INTO `building` VALUES (6, '别墅区3号楼', 0, NULL, 0, '2026-09-06 11:17:20', '2026-09-06 11:17:20');

-- ----------------------------
-- Table structure for care_item
-- ----------------------------
DROP TABLE IF EXISTS `care_item`;
CREATE TABLE `care_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单次服务价格',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `requirement` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '护理要求',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of care_item
-- ----------------------------
INSERT INTO `care_item` VALUES (10, '测量血压', 10.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/26deba6db0c649a89471120ef9df7faf.png', '协助老人测量血压，并记录测量结果', 1, 1, '2026-08-29 16:01:51', '2026-09-02 08:46:45');
INSERT INTO `care_item` VALUES (11, '测量血糖', 15.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/1d72ed01e91c45448c103a792599cf61.png', '根据护理计划协助老人测量血糖，并记录测量结果', 2, 1, '2026-08-29 16:01:51', '2026-09-02 08:47:01');
INSERT INTO `care_item` VALUES (12, '测量体温', 5.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/2c65a1e1a5384e329ed932823c83d1f6.png', '协助老人测量体温，并记录测量结果', 3, 1, '2026-08-29 16:01:51', '2026-09-02 08:47:19');
INSERT INTO `care_item` VALUES (13, '协助吃饭', 20.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/cc8ee1584e61474dad0aeed8868f0256.png', '协助老人进餐，关注进食情况，必要时提供喂饭服务', 4, 1, '2026-08-29 16:01:51', '2026-09-02 08:47:37');
INSERT INTO `care_item` VALUES (14, '协助洗澡', 50.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/0f7fc63d95a24e9fa7f29c5b6117b2d4.png', '协助老人完成洗澡，注意防滑、防跌倒，保障老人安全', 5, 1, '2026-08-29 16:01:51', '2026-09-02 08:47:57');
INSERT INTO `care_item` VALUES (15, '协助如厕', 20.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/5b318026094646a59edd07cee11f0afb.png', '协助老人安全如厕，做好必要的清洁和卫生护理', 6, 1, '2026-08-29 16:01:51', '2026-09-02 08:48:13');
INSERT INTO `care_item` VALUES (16, '协助起床', 15.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/db97df1a109c419bafebb37dd6760b72.png', '协助老人安全起床，注意防止跌倒和意外发生', 7, 1, '2026-08-29 16:01:51', '2026-09-02 08:48:34');
INSERT INTO `care_item` VALUES (17, '协助服药', 10.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/5bb5ae725dac47e79d2c0e480a323f65.png', '按照医嘱和用药计划提醒并协助老人服药，不得擅自调整药物剂量', 8, 1, '2026-08-29 16:01:51', '2026-09-02 08:48:55');
INSERT INTO `care_item` VALUES (18, '康复训练', 50.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/a3ce1720061b45cc8ebbdd01f011c121.png', '根据老人身体状况和康复计划协助开展康复训练', 9, 1, '2026-08-29 16:01:51', '2026-09-02 08:49:13');
INSERT INTO `care_item` VALUES (19, '心理陪护', 30.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/37093826cdcf4f0b8d9bc81e46a67a86.png', '陪伴老人交流，关注老人情绪和心理状态', 10, 1, '2026-08-29 16:01:51', '2026-09-02 08:49:25');
INSERT INTO `care_item` VALUES (20, '房间清洁', 30.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/careItem/99f16e333cfc49d49ba36d99759e2cf7.png', '负责老人房间日常清洁，保持房间整洁卫生', 11, 1, '2026-08-29 16:01:51', '2026-09-02 08:51:05');

-- ----------------------------
-- Table structure for care_level
-- ----------------------------
DROP TABLE IF EXISTS `care_level`;
CREATE TABLE `care_level`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级名称',
  `price` decimal(10, 2) NOT NULL COMMENT '护理费用',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '等级说明',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理等级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of care_level
-- ----------------------------
INSERT INTO `care_level` VALUES (1, '自理护理', 1000.00, '老人生活能够基本自理，仅需提供日常生活服务、健康监测和基础照护', 1, 1, '2026-08-29 16:15:50', '2026-08-29 16:15:50');
INSERT INTO `care_level` VALUES (2, '一级护理', 2000.00, '老人部分生活需要协助，需要提供较为频繁的生活照护和健康监测', 1, 2, '2026-08-29 16:15:50', '2026-08-29 16:15:50');
INSERT INTO `care_level` VALUES (3, '二级护理', 3000.00, '老人生活自理能力较弱，需要提供较全面的生活照护、健康监测和康复服务', 1, 3, '2026-08-29 16:15:50', '2026-08-29 16:15:50');
INSERT INTO `care_level` VALUES (4, '三级护理', 4000.00, '老人生活自理能力较差，需要较高频次的生活照护、健康监测和专人护理', 1, 4, '2026-08-29 16:15:50', '2026-08-29 16:15:50');

-- ----------------------------
-- Table structure for care_plan
-- ----------------------------
DROP TABLE IF EXISTS `care_plan`;
CREATE TABLE `care_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `user_id` bigint NOT NULL COMMENT '护理人员ID',
  `care_level_id` bigint NOT NULL COMMENT '护理等级ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `start_date` date NOT NULL,
  `end_date` date NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of care_plan
-- ----------------------------
INSERT INTO `care_plan` VALUES (13, 4, 76, 3, '赵长福-护理体验版', '2026-08-30', '2026-09-03', '2026-09-01 18:25:51', '2026-09-01 18:25:51');
INSERT INTO `care_plan` VALUES (14, 3, 77, 2, '李福振-护理基础版套餐', '2026-09-01', '2026-09-16', '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_plan` VALUES (15, 5, 1, 4, '徐素英-护理高级特惠套餐（10天）', '2026-09-03', '2026-09-13', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan` VALUES (16, 8, 80, 1, '姚振福-护理体验版', '2026-09-03', '2026-09-08', '2026-09-03 16:44:42', '2026-09-06 11:12:11');

-- ----------------------------
-- Table structure for care_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `care_plan_item`;
CREATE TABLE `care_plan_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `care_plan_id` bigint NOT NULL COMMENT '计划id',
  `care_item_id` bigint NOT NULL COMMENT '项目id',
  `execute_time` time NOT NULL COMMENT '计划执行时间',
  `execute_day` tinyint NULL DEFAULT NULL COMMENT '执行日：当执行周期为每周时存周几(1-7,1=周一)，为每月时存几号(1-31)，每天则为空',
  `execute_cycle` tinyint NOT NULL COMMENT '执行周期 0 天 1 周 2月',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理计划和项目关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of care_plan_item
-- ----------------------------
INSERT INTO `care_plan_item` VALUES (21, 7, 11, '18:47:31', 7, 2, '', '2026-08-31 19:10:53', '2026-08-31 19:10:53');
INSERT INTO `care_plan_item` VALUES (22, 8, 11, '19:12:01', NULL, 0, '', '2026-08-31 19:12:09', '2026-08-31 19:12:09');
INSERT INTO `care_plan_item` VALUES (25, 9, 12, '20:04:57', NULL, 0, '', '2026-08-31 20:05:31', '2026-08-31 20:05:31');
INSERT INTO `care_plan_item` VALUES (26, 9, 18, '20:05:20', 1, 1, '', '2026-08-31 20:05:31', '2026-08-31 20:05:31');
INSERT INTO `care_plan_item` VALUES (34, 13, 12, '18:25:44', NULL, 0, '', '2026-09-01 18:25:51', '2026-09-01 18:25:51');
INSERT INTO `care_plan_item` VALUES (35, 14, 10, '12:00:00', NULL, 0, '', '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_plan_item` VALUES (36, 14, 15, '09:00:00', NULL, 0, '', '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_plan_item` VALUES (37, 14, 19, '17:20:00', 3, 1, '', '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_plan_item` VALUES (38, 14, 11, '06:00:00', NULL, 0, '', '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_plan_item` VALUES (39, 15, 19, '19:00:00', 4, 1, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (40, 15, 20, '13:30:00', 1, 1, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (41, 15, 17, '09:00:00', NULL, 0, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (42, 15, 17, '14:00:00', NULL, 0, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (43, 15, 17, '20:00:00', NULL, 0, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (44, 15, 11, '07:00:00', NULL, 0, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (45, 15, 10, '07:00:00', NULL, 0, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (46, 15, 14, '20:40:00', 4, 1, '', '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_plan_item` VALUES (48, 16, 13, '17:00:00', NULL, 0, '', '2026-09-06 11:12:11', '2026-09-06 11:12:11');

-- ----------------------------
-- Table structure for care_task
-- ----------------------------
DROP TABLE IF EXISTS `care_task`;
CREATE TABLE `care_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `care_plan_id` bigint NOT NULL COMMENT '来源护理计划ID',
  `care_item_id` bigint NOT NULL COMMENT '护理项目ID',
  `care_plan_item_id` bigint NULL DEFAULT NULL COMMENT '来源护理项目id(care_plan_item.id)',
  `care_item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '护理项目名称(冗余，防止项目改名历史记录变动)',
  `user_id` bigint NULL DEFAULT NULL COMMENT '指定执行护理员ID/实际执行护理员ID',
  `plan_execute_date` date NOT NULL COMMENT '计划执行日期(如: 2026-08-29)',
  `plan_execute_time` time NOT NULL COMMENT '计划执行时间(如: 08:00:00)',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '任务状态（0：待执行，1：已完成，2：已跳过/取消）',
  `actual_execute_time` datetime NULL DEFAULT NULL COMMENT '实际完成时间',
  `execute_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行结果描述/健康数值(如: \"血压 120/80 mmHg\" 或 \"吃药完成\")',
  `execute_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '现场打卡照片URL(多张以逗号隔开)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '护理员执行备注(如: \"老人精神状态一般\")',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务生成时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 344 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理任务与打卡记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of care_task
-- ----------------------------
INSERT INTO `care_task` VALUES (216, 4, 13, 12, 34, '测量体温', 76, '2026-08-30', '18:25:44', 0, NULL, NULL, NULL, NULL, '2026-09-01 18:25:51', '2026-09-01 18:25:51');
INSERT INTO `care_task` VALUES (217, 4, 13, 12, 34, '测量体温', 76, '2026-08-31', '18:25:44', 0, NULL, NULL, NULL, NULL, '2026-09-01 18:25:51', '2026-09-01 18:25:51');
INSERT INTO `care_task` VALUES (218, 4, 13, 12, 34, '测量体温', 1, '2026-09-01', '18:25:44', 1, '2026-09-01 19:55:51', '已测量体温', '', '', '2026-09-01 18:25:51', '2026-09-01 19:55:51');
INSERT INTO `care_task` VALUES (219, 4, 13, 12, 34, '测量体温', 1, '2026-09-02', '18:25:44', 1, '2026-09-02 08:17:20', '体温36.7 正常', '', '', '2026-09-01 18:25:51', '2026-09-02 08:17:20');
INSERT INTO `care_task` VALUES (220, 4, 13, 12, 34, '测量体温', 76, '2026-09-03', '18:25:44', 0, NULL, NULL, NULL, NULL, '2026-09-01 18:25:51', '2026-09-01 18:25:51');
INSERT INTO `care_task` VALUES (221, 3, 14, 10, 35, '测量血压', 1, '2026-09-01', '12:00:00', 1, '2026-09-01 19:56:21', '血压 120/85 正常', '', '', '2026-09-01 19:49:19', '2026-09-01 19:56:21');
INSERT INTO `care_task` VALUES (222, 3, 14, 15, 36, '协助如厕', 77, '2026-09-01', '09:00:00', 1, '2026-09-01 20:03:29', '如厕完成', '', '', '2026-09-01 19:49:19', '2026-09-01 20:03:29');
INSERT INTO `care_task` VALUES (223, 3, 14, 11, 38, '测量血糖', 77, '2026-09-01', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (224, 3, 14, 10, 35, '测量血压', 77, '2026-09-02', '12:00:00', 2, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-02 08:19:35');
INSERT INTO `care_task` VALUES (225, 3, 14, 15, 36, '协助如厕', 1, '2026-09-02', '09:00:00', 1, '2026-09-02 08:42:40', '任务完成', '', '', '2026-09-01 19:49:19', '2026-09-02 08:42:40');
INSERT INTO `care_task` VALUES (226, 3, 14, 19, 37, '心理陪护', 1, '2026-09-02', '17:20:00', 1, '2026-09-02 15:40:08', '已经陪护', '', '', '2026-09-01 19:49:19', '2026-09-02 15:40:08');
INSERT INTO `care_task` VALUES (227, 3, 14, 11, 38, '测量血糖', 1, '2026-09-02', '06:00:00', 1, '2026-09-02 08:42:23', '测量完成，血糖正常', '', '', '2026-09-01 19:49:19', '2026-09-02 08:42:23');
INSERT INTO `care_task` VALUES (228, 3, 14, 10, 35, '测量血压', 77, '2026-09-03', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (229, 3, 14, 15, 36, '协助如厕', 77, '2026-09-03', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (230, 3, 14, 11, 38, '测量血糖', 77, '2026-09-03', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (231, 3, 14, 10, 35, '测量血压', 77, '2026-09-04', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (232, 3, 14, 15, 36, '协助如厕', 77, '2026-09-04', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (233, 3, 14, 11, 38, '测量血糖', 77, '2026-09-04', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (234, 3, 14, 10, 35, '测量血压', 77, '2026-09-05', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (235, 3, 14, 15, 36, '协助如厕', 77, '2026-09-05', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (236, 3, 14, 11, 38, '测量血糖', 77, '2026-09-05', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (237, 3, 14, 10, 35, '测量血压', 77, '2026-09-06', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (238, 3, 14, 15, 36, '协助如厕', 77, '2026-09-06', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (239, 3, 14, 11, 38, '测量血糖', 77, '2026-09-06', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (240, 3, 14, 10, 35, '测量血压', 77, '2026-09-07', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (241, 3, 14, 15, 36, '协助如厕', 77, '2026-09-07', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (242, 3, 14, 11, 38, '测量血糖', 77, '2026-09-07', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (243, 3, 14, 10, 35, '测量血压', 77, '2026-09-08', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (244, 3, 14, 15, 36, '协助如厕', 77, '2026-09-08', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (245, 3, 14, 11, 38, '测量血糖', 77, '2026-09-08', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (246, 3, 14, 10, 35, '测量血压', 77, '2026-09-09', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (247, 3, 14, 15, 36, '协助如厕', 77, '2026-09-09', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (248, 3, 14, 19, 37, '心理陪护', 77, '2026-09-09', '17:20:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (249, 3, 14, 11, 38, '测量血糖', 77, '2026-09-09', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (250, 3, 14, 10, 35, '测量血压', 77, '2026-09-10', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (251, 3, 14, 15, 36, '协助如厕', 77, '2026-09-10', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (252, 3, 14, 11, 38, '测量血糖', 77, '2026-09-10', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (253, 3, 14, 10, 35, '测量血压', 77, '2026-09-11', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (254, 3, 14, 15, 36, '协助如厕', 77, '2026-09-11', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (255, 3, 14, 11, 38, '测量血糖', 77, '2026-09-11', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (256, 3, 14, 10, 35, '测量血压', 77, '2026-09-12', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (257, 3, 14, 15, 36, '协助如厕', 77, '2026-09-12', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (258, 3, 14, 11, 38, '测量血糖', 77, '2026-09-12', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (259, 3, 14, 10, 35, '测量血压', 77, '2026-09-13', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (260, 3, 14, 15, 36, '协助如厕', 77, '2026-09-13', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (261, 3, 14, 11, 38, '测量血糖', 77, '2026-09-13', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (262, 3, 14, 10, 35, '测量血压', 77, '2026-09-14', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (263, 3, 14, 15, 36, '协助如厕', 77, '2026-09-14', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (264, 3, 14, 11, 38, '测量血糖', 77, '2026-09-14', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (265, 3, 14, 10, 35, '测量血压', 77, '2026-09-15', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (266, 3, 14, 15, 36, '协助如厕', 77, '2026-09-15', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (267, 3, 14, 11, 38, '测量血糖', 77, '2026-09-15', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (268, 3, 14, 10, 35, '测量血压', 77, '2026-09-16', '12:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (269, 3, 14, 15, 36, '协助如厕', 77, '2026-09-16', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (270, 3, 14, 19, 37, '心理陪护', 77, '2026-09-16', '17:20:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (271, 3, 14, 11, 38, '测量血糖', 77, '2026-09-16', '06:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:49:19', '2026-09-01 19:49:19');
INSERT INTO `care_task` VALUES (272, 5, 15, 19, 39, '心理陪护', 1, '2026-09-03', '19:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (273, 5, 15, 17, 41, '协助服药', 1, '2026-09-03', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (274, 5, 15, 17, 42, '协助服药', 1, '2026-09-03', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (275, 5, 15, 17, 43, '协助服药', 1, '2026-09-03', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (276, 5, 15, 11, 44, '测量血糖', 1, '2026-09-03', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (277, 5, 15, 10, 45, '测量血压', 1, '2026-09-03', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (278, 5, 15, 14, 46, '协助洗澡', 1, '2026-09-03', '20:40:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (279, 5, 15, 17, 41, '协助服药', 1, '2026-09-04', '09:00:00', 1, '2026-09-04 09:32:50', '已服药', '', '', '2026-09-01 19:55:17', '2026-09-04 09:32:50');
INSERT INTO `care_task` VALUES (280, 5, 15, 17, 42, '协助服药', 1, '2026-09-04', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (281, 5, 15, 17, 43, '协助服药', 1, '2026-09-04', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (282, 5, 15, 11, 44, '测量血糖', 1, '2026-09-04', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (283, 5, 15, 10, 45, '测量血压', 1, '2026-09-04', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (284, 5, 15, 17, 41, '协助服药', 1, '2026-09-05', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (285, 5, 15, 17, 42, '协助服药', 1, '2026-09-05', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (286, 5, 15, 17, 43, '协助服药', 1, '2026-09-05', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (287, 5, 15, 11, 44, '测量血糖', 1, '2026-09-05', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (288, 5, 15, 10, 45, '测量血压', 1, '2026-09-05', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (289, 5, 15, 17, 41, '协助服药', 1, '2026-09-06', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (290, 5, 15, 17, 42, '协助服药', 1, '2026-09-06', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (291, 5, 15, 17, 43, '协助服药', 1, '2026-09-06', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (292, 5, 15, 11, 44, '测量血糖', 1, '2026-09-06', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (293, 5, 15, 10, 45, '测量血压', 1, '2026-09-06', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (294, 5, 15, 20, 40, '房间清洁', 1, '2026-09-07', '13:30:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (295, 5, 15, 17, 41, '协助服药', 1, '2026-09-07', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (296, 5, 15, 17, 42, '协助服药', 1, '2026-09-07', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (297, 5, 15, 17, 43, '协助服药', 1, '2026-09-07', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (298, 5, 15, 11, 44, '测量血糖', 1, '2026-09-07', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (299, 5, 15, 10, 45, '测量血压', 1, '2026-09-07', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (300, 5, 15, 17, 41, '协助服药', 1, '2026-09-08', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (301, 5, 15, 17, 42, '协助服药', 1, '2026-09-08', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (302, 5, 15, 17, 43, '协助服药', 1, '2026-09-08', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (303, 5, 15, 11, 44, '测量血糖', 1, '2026-09-08', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (304, 5, 15, 10, 45, '测量血压', 1, '2026-09-08', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (305, 5, 15, 17, 41, '协助服药', 1, '2026-09-09', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (306, 5, 15, 17, 42, '协助服药', 1, '2026-09-09', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (307, 5, 15, 17, 43, '协助服药', 1, '2026-09-09', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (308, 5, 15, 11, 44, '测量血糖', 1, '2026-09-09', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (309, 5, 15, 10, 45, '测量血压', 1, '2026-09-09', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (310, 5, 15, 19, 39, '心理陪护', 1, '2026-09-10', '19:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (311, 5, 15, 17, 41, '协助服药', 1, '2026-09-10', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (312, 5, 15, 17, 42, '协助服药', 1, '2026-09-10', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (313, 5, 15, 17, 43, '协助服药', 1, '2026-09-10', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (314, 5, 15, 11, 44, '测量血糖', 1, '2026-09-10', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (315, 5, 15, 10, 45, '测量血压', 1, '2026-09-10', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (316, 5, 15, 14, 46, '协助洗澡', 1, '2026-09-10', '20:40:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (317, 5, 15, 17, 41, '协助服药', 1, '2026-09-11', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (318, 5, 15, 17, 42, '协助服药', 1, '2026-09-11', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (319, 5, 15, 17, 43, '协助服药', 1, '2026-09-11', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (320, 5, 15, 11, 44, '测量血糖', 1, '2026-09-11', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (321, 5, 15, 10, 45, '测量血压', 1, '2026-09-11', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (322, 5, 15, 17, 41, '协助服药', 1, '2026-09-12', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (323, 5, 15, 17, 42, '协助服药', 1, '2026-09-12', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (324, 5, 15, 17, 43, '协助服药', 1, '2026-09-12', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (325, 5, 15, 11, 44, '测量血糖', 1, '2026-09-12', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (326, 5, 15, 10, 45, '测量血压', 1, '2026-09-12', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (327, 5, 15, 17, 41, '协助服药', 1, '2026-09-13', '09:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (328, 5, 15, 17, 42, '协助服药', 1, '2026-09-13', '14:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (329, 5, 15, 17, 43, '协助服药', 1, '2026-09-13', '20:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (330, 5, 15, 11, 44, '测量血糖', 1, '2026-09-13', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (331, 5, 15, 10, 45, '测量血压', 1, '2026-09-13', '07:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-01 19:55:17', '2026-09-01 19:55:17');
INSERT INTO `care_task` VALUES (338, 8, 16, 13, 48, '协助吃饭', 80, '2026-09-03', '17:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-06 11:12:11', '2026-09-06 11:12:11');
INSERT INTO `care_task` VALUES (339, 8, 16, 13, 48, '协助吃饭', 80, '2026-09-04', '17:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-06 11:12:11', '2026-09-06 11:12:11');
INSERT INTO `care_task` VALUES (340, 8, 16, 13, 48, '协助吃饭', 80, '2026-09-05', '17:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-06 11:12:11', '2026-09-06 11:12:11');
INSERT INTO `care_task` VALUES (341, 8, 16, 13, 48, '协助吃饭', 80, '2026-09-06', '17:00:00', 1, '2026-09-06 11:12:24', '已经吃饭', '', '', '2026-09-06 11:12:11', '2026-09-06 11:12:25');
INSERT INTO `care_task` VALUES (342, 8, 16, 13, 48, '协助吃饭', 80, '2026-09-07', '17:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-06 11:12:11', '2026-09-06 11:12:11');
INSERT INTO `care_task` VALUES (343, 8, 16, 13, 48, '协助吃饭', 80, '2026-09-08', '17:00:00', 0, NULL, NULL, NULL, NULL, '2026-09-06 11:12:11', '2026-09-06 11:12:11');

-- ----------------------------
-- Table structure for check_in_record
-- ----------------------------
DROP TABLE IF EXISTS `check_in_record`;
CREATE TABLE `check_in_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '办理单ID',
  `record_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '办理编号（入住RZ/退住TZ+日期+ID，后端生成）',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '办理类型：0入住 1退住',
  `step` tinyint NOT NULL DEFAULT 1 COMMENT '当前步骤：入住1~4，退住1~3（每完成一步+1，支持续办）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0办理中 1已完成 2已取消',
  `elder_id` bigint NOT NULL COMMENT '老人ID（关联elder.id）',
  `family_id` bigint NULL DEFAULT NULL COMMENT '客户ID（关联family.id，入住第一步登记或选择已有客户）',
  `bed_id` bigint NULL DEFAULT NULL COMMENT '床位ID（入住分配的床位/退住释放的床位）',
  `check_in_id` bigint NULL DEFAULT NULL COMMENT '原入住单ID（退住单关联发起退住时的入住单）',
  `checkin_date` date NULL DEFAULT NULL COMMENT '入住日期',
  `checkout_date` date NULL DEFAULT NULL COMMENT '退住日期',
  `contract_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同名称（确认入住时写入contract表）',
  `contract_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同编号',
  `contract_type` tinyint NULL DEFAULT 1 COMMENT '合同类型：0服务合同 1入住合同 2其他',
  `sign_time` date NULL DEFAULT NULL COMMENT '合同签订日期',
  `expire_time` date NULL DEFAULT NULL COMMENT '合同到期日期',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同附件地址',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退住原因',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '确认办理人ID（关联user.id）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志：0正常 1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cir_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_cir_type_status`(`type` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '入住退住办理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of check_in_record
-- ----------------------------
INSERT INTO `check_in_record` VALUES (1, 'RZ202609061', 0, 4, 1, 27, 6, 8, NULL, '2026-09-07', NULL, '123', '123', 1, '2026-09-06', '2026-09-22', '', NULL, NULL, 80, 1, '2026-09-06 12:05:05', '2026-09-06 12:11:15');
INSERT INTO `check_in_record` VALUES (2, 'RZ202609062', 0, 2, 2, 28, 4, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-09-06 12:23:16', '2026-09-06 12:30:54');
INSERT INTO `check_in_record` VALUES (3, 'RZ202609063', 0, 4, 2, 4, 7, 5, NULL, '2026-09-02', NULL, '123', '123', 1, '2026-09-01', '2026-09-09', '', NULL, '', NULL, 1, '2026-09-06 12:34:08', '2026-09-06 12:36:22');
INSERT INTO `check_in_record` VALUES (4, 'RZ202609064', 0, 4, 2, 10, 4, 5, NULL, '2026-09-01', NULL, '123', '123', 1, '2026-09-02', '2026-09-18', '', NULL, '', NULL, 1, '2026-09-06 12:38:08', '2026-09-06 12:45:37');
INSERT INTO `check_in_record` VALUES (5, 'RZ202609065', 0, 2, 2, 10, 4, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-09-06 12:46:21', '2026-09-06 12:46:38');
INSERT INTO `check_in_record` VALUES (6, 'TZ202609066', 1, 3, 1, 10, NULL, 8, NULL, NULL, '2026-09-02', NULL, NULL, 1, NULL, NULL, NULL, '123', '', 80, 0, '2026-09-06 12:49:22', '2026-09-06 12:49:37');
INSERT INTO `check_in_record` VALUES (7, 'TZ202609077', 1, 3, 1, 6, NULL, 11, NULL, NULL, '2026-09-07', NULL, NULL, 1, NULL, NULL, NULL, '离婚了', '', 80, 0, '2026-09-07 19:32:32', '2026-09-07 19:33:16');
INSERT INTO `check_in_record` VALUES (8, 'RZ202609078', 0, 4, 1, 6, 5, 11, NULL, '2026-09-07', NULL, '胡福德的入住合同', 'H20234180947', 1, '2026-09-07', '2027-09-07', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/c3a8422f0f80455e8fcfa7608457779c.pdf', NULL, '又结婚了', 80, 0, '2026-09-07 19:35:32', '2026-09-07 19:38:05');

-- ----------------------------
-- Table structure for contract
-- ----------------------------
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '合同id(主键)',
  `contract_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '合同编号',
  `elder_id` bigint NOT NULL COMMENT '绑定的老人id(elder.id)',
  `contract_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同名称(如:入住服务合同)',
  `contract_type` tinyint NULL DEFAULT 0 COMMENT '合同类型(0:服务合同 1:入住合同 2:其他)',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '合同生效时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '合同过期时间',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同文件URL',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间(系统自动填充)',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间(系统自动填充)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_contract_no`(`contract_no` ASC) USING BTREE,
  INDEX `idx_elder_id`(`elder_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract
-- ----------------------------
INSERT INTO `contract` VALUES (1, 'H20234180941', 4, '赵长福的入住合同', 1, '2025-07-09 20:45:14', '2026-08-03 00:00:00', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/3840ee76b4104ef4ae83685dddb49479.pdf', NULL, 0, '2026-08-28 20:45:35', '2026-08-29 14:21:06');
INSERT INTO `contract` VALUES (2, 'H20234180942', 10, '钟德永的入住合同', 1, '2026-08-29 09:16:08', '2027-08-13 00:00:00', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/d2e83f6ca52149aeb5d389b93e9fafdf.pdf', NULL, 0, '2026-08-29 09:16:17', '2026-08-29 14:21:02');
INSERT INTO `contract` VALUES (3, 'H20234180943', 4, '赵长福的服务合同', 0, '2026-08-29 09:29:22', '2027-08-20 00:00:00', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/eca2fde9cf67411c9fa3fc0c514f6544.pdf', NULL, 0, '2026-08-29 09:29:31', '2026-08-29 14:26:28');
INSERT INTO `contract` VALUES (4, 'H20234180944', 5, '徐素英的入住合同', 1, '2026-09-18 00:00:00', '2027-08-13 00:00:00', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/8e7c6e9328ec44dca68aecb379542766.pdf', NULL, 0, '2026-08-29 09:43:29', '2026-08-29 14:20:49');
INSERT INTO `contract` VALUES (5, 'H20234180945', 1, NULL, 0, NULL, '2026-09-25 00:00:00', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/377c4304048c46728b333a8044558b49.pdf', NULL, 0, '2026-08-29 09:44:09', '2026-08-29 14:20:41');
INSERT INTO `contract` VALUES (6, 'H20234180946', 8, '姚振福的服务合同', 0, '2026-09-02 00:00:00', '2026-09-14 00:00:00', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/754e8eed87714e74a1e3efb38a3d36ab.pdf', '预约', 0, '2026-08-29 09:50:29', '2026-08-29 14:20:35');
INSERT INTO `contract` VALUES (7, '123', 27, '123', 1, '2026-09-06 00:00:00', '2026-09-22 00:00:00', '', NULL, 1, '2026-09-06 12:05:59', '2026-09-07 19:37:14');
INSERT INTO `contract` VALUES (11, 'H20234180947', 6, '胡福德的入住合同', 1, '2026-09-07 00:00:00', '2027-09-07 00:00:00', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/contract/c3a8422f0f80455e8fcfa7608457779c.pdf', NULL, 0, '2026-09-07 19:38:05', '2026-09-07 19:38:05');

-- ----------------------------
-- Table structure for elder
-- ----------------------------
DROP TABLE IF EXISTS `elder`;
CREATE TABLE `elder`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '老人ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '老人姓名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别（0：女，1：男）',
  `id_card_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0：禁用，1：启用，2：请假，3：退住中，4：入住中，5：已退住）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_elder_id_card_no`(`id_card_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '老人表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of elder
-- ----------------------------
INSERT INTO `elder` VALUES (1, 'liudeshan', '$2a$10$ECEWbH4sNHGv2KmaRXdqce7uCIh.gJThnZ8kFinb2pETcgARDDqmO', '刘德山', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/329c7a0c68174de0aee9d1f81dc2e53c.png', 1, '370201194905051102', 1, '15200660011', '1949-05-05', '山东省青岛市中山路街道41号', '无', 0, '2026-08-29 09:04:33', '2026-09-07 13:23:40');
INSERT INTO `elder` VALUES (2, 'wuguizhen', '$2a$10$lEJWbYJiq77ByeJuta7ubevAx5q7VQmQ94wsC90oCnRIuEtmTktMC', '吴桂珍', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/e0ffcd41aa70467da93fecddd22f30ac.png', 0, '37020419510228052X', 1, '13782594613', '1951-02-28', '山东省青岛市水清沟街道35号', '无', 0, '2026-08-29 09:04:33', '2026-09-07 13:23:42');
INSERT INTO `elder` VALUES (3, 'lifuzhen', '$2a$10$8DzXnTaT3U0vrXD7crlY9e/lsSunGMAGh0ADTMyJnq2Hv79gnrEHu', '李福振', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/d7e362dbce6542ad869b37806f146f4e.png', 1, '370203194001232250', 1, '15899774455', '1940-01-23', '山东省青岛市惜福镇街道61号', '无', 0, '2026-08-29 09:04:33', '2026-09-07 13:23:45');
INSERT INTO `elder` VALUES (4, 'zhaochangfu', '$2a$10$wPdomEuJqmVCahrx6LmPC.pH1zzj4koQ0eB.jv7XPJE/MzN3Vw9je', '赵长福', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/12970ad4f97a48f3816c1681d984a055.png', 1, '370213193904177825', 1, '15988725615', '1939-04-17', '山东省青岛市夏庄街道42号', '无', 0, '2026-08-29 09:04:33', '2026-09-07 13:23:48');
INSERT INTO `elder` VALUES (5, 'xusuying', '$2a$10$uciKrvVEpyQkh97cfr3cr.5OJQSpFRVkxdTuhRGemz9nQ0lpXwWje', '徐素英', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/1987d2e12e484dd0a6bb5e7b18f87192.png', 0, '370202193310126781', 1, '17789210252', '1933-10-12', '山东省青岛市黄岛街道16号', '无', 0, '2026-08-29 09:04:33', '2026-09-07 13:24:27');
INSERT INTO `elder` VALUES (6, 'hufude', '$2a$10$MTG/nbE5icbfNTWJgfkpS.c2r60bn2ZVTMOLuO2d3kpEI3Ucxk/ei', '胡福德', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/916ec72063d848709dd46620872837de.png', 1, '370211194006121033', 1, '13249631463', '1940-06-12', '山东省青岛市中山路街道63号', '无', 0, '2026-08-29 09:04:33', '2026-09-07 19:38:05');
INSERT INTO `elder` VALUES (7, 'pengguilan', '$2a$10$/JA0DU1lFvrWdZt0KQpbc.lP98vwFpFnQpfThj4a24gs.N.pKuyq6', '彭桂兰', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/3035f01fe18e4851821c94c39f9f2dec.png', 0, '370203195005076482', 1, '13582479610', '1950-05-07', '山东省青岛市八大关街道81号', '无', 0, '2026-08-29 09:04:33', '2026-09-07 13:24:35');
INSERT INTO `elder` VALUES (8, 'yaozhenfu', '$2a$10$4pySEqJcqiW.FJYRcqsvEeqFO8fvCUcxWaSQzFNEI2GfE8mmFGGOG', '姚振福', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/0386a428c7a94a699856a33b4fd28ff2.png', 0, '370202193811195236', 1, '13175948260', '1938-11-19', '山东省青岛市中山路街道49号', '无', 0, '2026-08-29 09:04:34', '2026-09-07 13:23:16');
INSERT INTO `elder` VALUES (9, 'fusuzhen', '$2a$10$s7LYzoasHNfZRGqXPzH9T.x/Sp23B9vnrXceBFBR51aEXu7m8BnWK', '傅素珍', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/1df7e8773c714086914900d1d2ba8d4e.png', 0, '370211194901253745', 1, '13864927510', '1949-01-25', '山东省青岛市黄岛街道36号', '无', 0, '2026-08-29 09:04:34', '2026-09-07 13:23:23');
INSERT INTO `elder` VALUES (10, 'zhongdeyong', '$2a$10$wM3q2wx0aTLf89.1PacaGecAIA0Nrp.agkLWiQBLSf7Nrz1waKPbq', '钟德永', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/24a663e8f6524a99b8a4884b0f22f36a.png', 1, '370212195806148274', 5, '13397584621', '1958-06-14', '山东省青岛市夏庄街道68号', '无', 0, '2026-08-29 09:04:34', '2026-09-07 13:23:32');
INSERT INTO `elder` VALUES (11, 'jiangxiulian', '$2a$10$SeQjT2oBrbr4UsatU5Cc0.GA7UxtihnWBEy0/Jh3tBt19mj5qWc/2', '姜秀莲', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/db79400191144b72847521015cc7f09c.png', 0, '370213192909036541', 1, '13648275910', '1929-09-03', '山东省青岛市惜福镇街道44号', '无', 0, '2026-08-29 09:04:34', '2026-09-07 13:23:35');
INSERT INTO `elder` VALUES (12, 'fangzhenshan', '$2a$10$6RknGcHMRsxUlgiVajSyF.dxHUgqysDfuuT68yGpzrUexaWPA2rju', '方振山', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/4dc4c1797f524443bf73819fdbd6a991.png', 1, '370205194012187359', 1, '13285964710', '1940-12-18', '山东省青岛市水清沟街道76号', '无', 0, '2026-08-29 09:04:34', '2026-09-07 13:23:37');

-- ----------------------------
-- Table structure for elder_family
-- ----------------------------
DROP TABLE IF EXISTS `elder_family`;
CREATE TABLE `elder_family`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `family_id` bigint NOT NULL COMMENT '家属ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_elder_family`(`elder_id` ASC, `family_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家属-老人关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of elder_family
-- ----------------------------
INSERT INTO `elder_family` VALUES (14, 7, 5, '2026-09-02 10:21:12', '2026-09-02 10:21:12');
INSERT INTO `elder_family` VALUES (26, 2, 4, '2026-09-06 18:18:11', '2026-09-06 18:18:11');
INSERT INTO `elder_family` VALUES (27, 3, 4, '2026-09-06 18:18:11', '2026-09-06 18:18:11');
INSERT INTO `elder_family` VALUES (28, 4, 4, '2026-09-06 18:18:11', '2026-09-06 18:18:11');
INSERT INTO `elder_family` VALUES (29, 8, 4, '2026-09-06 18:18:11', '2026-09-06 18:18:11');
INSERT INTO `elder_family` VALUES (30, 6, 5, '2026-09-07 19:38:05', '2026-09-07 19:38:05');

-- ----------------------------
-- Table structure for elder_tag
-- ----------------------------
DROP TABLE IF EXISTS `elder_tag`;
CREATE TABLE `elder_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_elder_tag`(`elder_id` ASC, `tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 141 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '老人-标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of elder_tag
-- ----------------------------
INSERT INTO `elder_tag` VALUES (38, 1, 2, '2026-08-26 19:59:09', '2026-08-26 19:59:09');
INSERT INTO `elder_tag` VALUES (39, 1, 3, '2026-08-26 19:59:09', '2026-08-26 19:59:09');
INSERT INTO `elder_tag` VALUES (40, 1, 4, '2026-08-26 19:59:09', '2026-08-26 19:59:09');
INSERT INTO `elder_tag` VALUES (53, 2, 1, '2026-08-27 09:06:02', '2026-08-27 09:06:02');
INSERT INTO `elder_tag` VALUES (54, 2, 7, '2026-08-27 09:06:02', '2026-08-27 09:06:02');
INSERT INTO `elder_tag` VALUES (87, 4, 2, '2026-08-27 18:35:56', '2026-08-27 18:35:56');
INSERT INTO `elder_tag` VALUES (88, 4, 3, '2026-08-27 18:35:56', '2026-08-27 18:35:56');
INSERT INTO `elder_tag` VALUES (118, 6, 1, '2026-08-27 19:00:52', '2026-08-27 19:00:52');
INSERT INTO `elder_tag` VALUES (119, 6, 2, '2026-08-27 19:00:52', '2026-08-27 19:00:52');
INSERT INTO `elder_tag` VALUES (120, 6, 3, '2026-08-27 19:00:52', '2026-08-27 19:00:52');
INSERT INTO `elder_tag` VALUES (121, 6, 4, '2026-08-27 19:00:52', '2026-08-27 19:00:52');
INSERT INTO `elder_tag` VALUES (122, 8, 7, '2026-08-27 19:05:22', '2026-08-27 19:05:22');
INSERT INTO `elder_tag` VALUES (128, 11, 4, '2026-08-28 09:01:25', '2026-08-28 09:01:25');
INSERT INTO `elder_tag` VALUES (129, 11, 3, '2026-08-28 09:01:25', '2026-08-28 09:01:25');
INSERT INTO `elder_tag` VALUES (132, 3, 6, '2026-08-28 09:46:34', '2026-08-28 09:46:34');
INSERT INTO `elder_tag` VALUES (133, 3, 1, '2026-08-28 09:46:34', '2026-08-28 09:46:34');
INSERT INTO `elder_tag` VALUES (134, 9, 1, '2026-08-28 09:46:37', '2026-08-28 09:46:37');
INSERT INTO `elder_tag` VALUES (135, 10, 2, '2026-08-28 09:46:41', '2026-08-28 09:46:41');
INSERT INTO `elder_tag` VALUES (136, 7, 3, '2026-08-28 10:18:39', '2026-08-28 10:18:39');
INSERT INTO `elder_tag` VALUES (137, 12, 1, '2026-08-28 10:23:49', '2026-08-28 10:23:49');
INSERT INTO `elder_tag` VALUES (138, 12, 3, '2026-08-28 10:23:49', '2026-08-28 10:23:49');
INSERT INTO `elder_tag` VALUES (139, 12, 6, '2026-08-28 10:23:49', '2026-08-28 10:23:49');
INSERT INTO `elder_tag` VALUES (140, 5, 3, '2026-09-02 15:05:36', '2026-09-02 15:05:36');

-- ----------------------------
-- Table structure for email_code
-- ----------------------------
DROP TABLE IF EXISTS `email_code`;
CREATE TABLE `email_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '验证码记录ID',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收验证码的邮箱',
  `code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '6位数字验证码',
  `scene` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '使用场景：BIND_EMAIL绑定邮箱 CHANGE_PASSWORD邮箱改密 CHANGE_EMAIL更换邮箱',
  `expire_time` datetime NOT NULL COMMENT '过期时间（生成后5分钟内有效）',
  `used` tinyint NOT NULL DEFAULT 0 COMMENT '是否已使用：0未使用 1已使用（使用后作废）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_email_code_email_scene`(`email` ASC, `scene` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮箱验证码表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of email_code
-- ----------------------------
INSERT INTO `email_code` VALUES (1, '2545653474@qq.com', '760820', 'CHANGE_EMAIL', '2026-09-04 11:28:30', 1, '2026-09-04 11:23:30', '2026-09-04 11:23:45');
INSERT INTO `email_code` VALUES (2, 'tinsur@foxmail.com', '847185', 'CHANGE_PASSWORD', '2026-09-04 11:30:57', 1, '2026-09-04 11:25:57', '2026-09-04 11:28:19');
INSERT INTO `email_code` VALUES (3, 'tinsur@foxmail.com', '983356', 'CHANGE_PASSWORD', '2026-09-04 11:33:19', 0, '2026-09-04 11:28:19', '2026-09-04 11:28:19');
INSERT INTO `email_code` VALUES (4, 'tinsur@foxmail.com', '228795', 'CHANGE_EMAIL', '2026-09-04 11:34:19', 1, '2026-09-04 11:29:19', '2026-09-04 11:39:29');
INSERT INTO `email_code` VALUES (5, 'tinsur@foxmail.com', '961066', 'CHANGE_EMAIL', '2026-09-04 11:44:30', 1, '2026-09-04 11:39:30', '2026-09-04 11:46:38');
INSERT INTO `email_code` VALUES (6, '2545653474@qq.com', '173591', 'CHANGE_EMAIL_NEW', '2026-09-04 11:44:37', 1, '2026-09-04 11:39:37', '2026-09-04 11:46:58');
INSERT INTO `email_code` VALUES (7, 'tinsur@foxmail.com', '397023', 'CHANGE_EMAIL', '2026-09-04 11:51:39', 1, '2026-09-04 11:46:39', '2026-09-04 11:47:13');
INSERT INTO `email_code` VALUES (8, '2545653474@qq.com', '459580', 'CHANGE_EMAIL_NEW', '2026-09-04 11:51:59', 1, '2026-09-04 11:46:59', '2026-09-04 11:47:13');
INSERT INTO `email_code` VALUES (9, 'tinsur@foxmail.com', '954789', 'BIND_EMAIL', '2026-09-07 13:45:13', 1, '2026-09-07 13:40:13', '2026-09-07 13:52:13');
INSERT INTO `email_code` VALUES (10, 'tinsur@foxmail.com', '158358', 'BIND_EMAIL', '2026-09-07 13:57:13', 0, '2026-09-07 13:52:13', '2026-09-07 13:52:13');

-- ----------------------------
-- Table structure for exam_appointment
-- ----------------------------
DROP TABLE IF EXISTS `exam_appointment`;
CREATE TABLE `exam_appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '体检记录ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `package_id` bigint NOT NULL COMMENT '体检套餐ID',
  `appointment_date` date NOT NULL COMMENT '预约/体检日期',
  `appointment_time` time NOT NULL COMMENT '预约/体检时间',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '体检套餐价格',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0待体检 1体检中 2已完成 3已取消 4已过期',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '老人预约/体检记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_appointment
-- ----------------------------
INSERT INTO `exam_appointment` VALUES (10, 4, 2, '2026-09-02', '16:18:33', 399.00, 2, NULL, '2026-09-02 16:18:45', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment` VALUES (11, 10, 2, '2026-09-02', '16:27:19', 399.00, 2, NULL, '2026-09-02 16:27:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment` VALUES (12, 1, 5, '2026-12-03', '16:00:00', 699.00, 1, NULL, '2026-09-03 09:15:27', '2026-09-03 09:17:01');
INSERT INTO `exam_appointment` VALUES (13, 2, 5, '2026-10-01', '08:00:00', 699.00, 1, NULL, '2026-09-03 11:37:09', '2026-09-03 16:18:25');
INSERT INTO `exam_appointment` VALUES (14, 8, 1, '2026-09-03', '14:00:00', 199.00, 2, NULL, '2026-09-03 16:13:37', '2026-09-03 16:17:12');

-- ----------------------------
-- Table structure for exam_appointment_item
-- ----------------------------
DROP TABLE IF EXISTS `exam_appointment_item`;
CREATE TABLE `exam_appointment_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '体检记录明细ID',
  `appointment_id` bigint NOT NULL COMMENT '体检记录ID',
  `exam_item_id` bigint NOT NULL COMMENT '体检项目ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称快照',
  `result_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '数值型结果',
  `result_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结果单位',
  `result_text` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文本型结果',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0待检查 1正常 2异常 3未完成',
  `abnormal` tinyint NOT NULL DEFAULT 0 COMMENT '是否异常：0正常 1异常',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 109 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检记录明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_appointment_item
-- ----------------------------
INSERT INTO `exam_appointment_item` VALUES (50, 10, 1, '血常规', NULL, '次', NULL, 0, 0, NULL, '2026-09-02 16:19:34', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment_item` VALUES (51, 10, 2, '尿常规', NULL, '次', NULL, 0, 0, NULL, '2026-09-02 16:19:34', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment_item` VALUES (52, 10, 3, '肝功能', NULL, '次', NULL, 3, 0, NULL, '2026-09-02 16:19:34', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment_item` VALUES (53, 10, 4, '肾功能', 39.00, '次', NULL, 2, 1, NULL, '2026-09-02 16:19:34', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment_item` VALUES (54, 10, 5, '空腹血糖', 3.50, '次', NULL, 2, 1, NULL, '2026-09-02 16:19:34', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment_item` VALUES (55, 10, 6, '血脂', 5.30, '次', NULL, 2, 1, NULL, '2026-09-02 16:19:34', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment_item` VALUES (56, 10, 7, '心电图', NULL, '次', NULL, 0, 0, NULL, '2026-09-02 16:19:34', '2026-09-02 16:19:34');
INSERT INTO `exam_appointment_item` VALUES (64, 11, 1, '血常规', NULL, '次', '正常', 1, 0, NULL, '2026-09-02 16:28:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment_item` VALUES (65, 11, 2, '尿常规', NULL, '次', '正常', 1, 0, NULL, '2026-09-02 16:28:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment_item` VALUES (66, 11, 3, '肝功能', 32.00, '次', NULL, 1, 0, NULL, '2026-09-02 16:28:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment_item` VALUES (67, 11, 4, '肾功能', 45.00, '次', NULL, 1, 0, NULL, '2026-09-02 16:28:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment_item` VALUES (68, 11, 5, '空腹血糖', 4.57, '次', NULL, 1, 0, NULL, '2026-09-02 16:28:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment_item` VALUES (69, 11, 6, '血脂', 5.38, '次', NULL, 2, 1, NULL, '2026-09-02 16:28:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment_item` VALUES (70, 11, 7, '心电图', NULL, '次', '正常', 1, 0, NULL, '2026-09-02 16:28:34', '2026-09-02 16:28:34');
INSERT INTO `exam_appointment_item` VALUES (81, 12, 1, '血常规', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (82, 12, 2, '尿常规', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (83, 12, 3, '肝功能', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (84, 12, 4, '肾功能', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (85, 12, 5, '空腹血糖', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (86, 12, 6, '血脂', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (87, 12, 7, '心电图', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (88, 12, 8, '腹部彩超', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (89, 12, 9, '骨密度检测', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (90, 12, 10, '胸部CT', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 09:17:09', '2026-09-03 09:17:09');
INSERT INTO `exam_appointment_item` VALUES (91, 13, 1, '血常规', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (92, 13, 2, '尿常规', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (93, 13, 3, '肝功能', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (94, 13, 4, '肾功能', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (95, 13, 5, '空腹血糖', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (96, 13, 6, '血脂', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (97, 13, 7, '心电图', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (98, 13, 8, '腹部彩超', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (99, 13, 9, '骨密度检测', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (100, 13, 10, '胸部CT', NULL, NULL, NULL, 0, 0, NULL, '2026-09-03 11:37:09', '2026-09-03 11:37:09');
INSERT INTO `exam_appointment_item` VALUES (105, 14, 1, '血常规', NULL, '次', '白细胞计数 WBC	6.5 ×10⁹/L 正常\n红细胞计数 RBC	4.8 ×10¹²/L 正常\n血红蛋白 HGB	142 g/L 正常\n血小板计数 PLT	228 ×10⁹/L  正常\n中性粒细胞百分比	58.2 %	正常\n淋巴细胞百分比	32.5 %	正常', 1, 0, NULL, '2026-09-03 16:17:12', '2026-09-03 16:17:12');
INSERT INTO `exam_appointment_item` VALUES (106, 14, 2, '尿常规', NULL, '次', '尿蛋白 PRO	阴性 (-)		正常\n尿糖 GLU	阴性 (-)		正常\n尿潜血 BLD	阴性 (-)		正常\n白细胞 LEU	阴性 (-)		正常\n尿比重 SG	1.015		正常', 1, 0, NULL, '2026-09-03 16:17:12', '2026-09-03 16:17:12');
INSERT INTO `exam_appointment_item` VALUES (107, 14, 3, '肝功能', 35.00, '次', NULL, 1, 0, NULL, '2026-09-03 16:17:12', '2026-09-03 16:17:12');
INSERT INTO `exam_appointment_item` VALUES (108, 14, 4, '肾功能', 45.00, '次', NULL, 1, 0, NULL, '2026-09-03 16:17:12', '2026-09-03 16:17:12');

-- ----------------------------
-- Table structure for exam_item
-- ----------------------------
DROP TABLE IF EXISTS `exam_item`;
CREATE TABLE `exam_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '体检项目ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '单项价格',
  `unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `result_type` tinyint NOT NULL DEFAULT 0 COMMENT '结果类型：0文本 1数值',
  `reference_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考范围下限',
  `reference_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考范围上限',
  `reference_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参考范围单位',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项目说明',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_item
-- ----------------------------
INSERT INTO `exam_item` VALUES (1, '血常规', 35.00, '次', 0, NULL, NULL, NULL, '检测红细胞、白细胞、血小板等指标', 1, 1, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (2, '尿常规', 25.00, '次', 0, NULL, NULL, NULL, '检查尿液相关指标', 1, 2, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (3, '肝功能', 80.00, '次', 1, 0.00, 40.00, 'U/L', '检测谷丙转氨酶等肝功能指标', 1, 3, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (4, '肾功能', 70.00, '次', 1, 40.00, 100.00, 'μmol/L', '检测肌酐等肾功能指标', 1, 4, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (5, '空腹血糖', 20.00, '次', 1, 3.90, 6.10, 'mmol/L', '检测空腹血糖水平', 1, 5, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (6, '血脂', 60.00, '次', 1, 0.00, 5.20, 'mmol/L', '检测总胆固醇等血脂指标', 1, 6, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (7, '心电图', 50.00, '次', 0, NULL, NULL, NULL, '检查心脏电生理活动情况', 1, 7, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (8, '腹部彩超', 120.00, '次', 0, NULL, NULL, NULL, '检查肝脏、胆囊、胰腺、脾脏等', 1, 8, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (9, '骨密度检测', 100.00, '次', 1, -1.00, 10.00, 'T值', '检测骨骼密度情况', 1, 9, '2026-08-30 16:17:15', '2026-08-30 16:17:15');
INSERT INTO `exam_item` VALUES (10, '胸部CT', 200.00, '次', 0, NULL, NULL, NULL, '检查肺部及胸部相关情况', 1, 10, '2026-08-30 16:17:15', '2026-08-30 16:17:15');

-- ----------------------------
-- Table structure for exam_package
-- ----------------------------
DROP TABLE IF EXISTS `exam_package`;
CREATE TABLE `exam_package`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '体检套餐ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '套餐名称',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '套餐价格',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '套餐图片',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '套餐说明',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0下架 1上架',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检套餐表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_package
-- ----------------------------
INSERT INTO `exam_package` VALUES (1, '基础体检套餐', 199.00, 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/examPackage/dcb72429443e47c0b8a3721a3eb83e3a.png', '适合身体状况较好的老年人进行基础健康检查', 1, 1, '2026-08-30 16:14:48', '2026-09-04 10:29:22');
INSERT INTO `exam_package` VALUES (2, '老年健康套餐', 399.00, NULL, '针对老年人常见健康问题设计的综合体检套餐', 1, 2, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package` VALUES (3, '心脑血管专项套餐', 499.00, NULL, '针对心脑血管健康进行专项检查', 1, 3, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package` VALUES (4, '骨健康套餐', 299.00, NULL, '针对老年人骨骼健康进行专项检查', 1, 4, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package` VALUES (5, '全面体检套餐', 699.00, NULL, '包含多个身体系统的综合健康检查', 1, 5, '2026-08-30 16:14:48', '2026-08-30 16:14:48');

-- ----------------------------
-- Table structure for exam_package_item
-- ----------------------------
DROP TABLE IF EXISTS `exam_package_item`;
CREATE TABLE `exam_package_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `package_id` bigint NOT NULL COMMENT '体检套餐ID',
  `exam_item_id` bigint NOT NULL COMMENT '体检项目ID',
  `sort` int NOT NULL DEFAULT 0 COMMENT '项目排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检套餐项目关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_package_item
-- ----------------------------
INSERT INTO `exam_package_item` VALUES (5, 2, 1, 1, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (6, 2, 2, 2, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (7, 2, 3, 3, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (8, 2, 4, 4, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (9, 2, 5, 5, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (10, 2, 6, 6, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (11, 2, 7, 7, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (12, 3, 5, 1, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (13, 3, 6, 2, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (14, 3, 7, 3, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (15, 3, 10, 4, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (16, 4, 1, 1, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (17, 4, 4, 2, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (18, 4, 9, 3, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (19, 5, 1, 1, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (20, 5, 2, 2, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (21, 5, 3, 3, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (22, 5, 4, 4, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (23, 5, 5, 5, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (24, 5, 6, 6, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (25, 5, 7, 7, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (26, 5, 8, 8, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (27, 5, 9, 9, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (28, 5, 10, 10, NULL, '2026-08-30 16:14:48', '2026-08-30 16:14:48');
INSERT INTO `exam_package_item` VALUES (63, 1, 1, 0, NULL, '2026-09-04 10:29:22', '2026-09-04 10:29:22');
INSERT INTO `exam_package_item` VALUES (64, 1, 2, 1, NULL, '2026-09-04 10:29:22', '2026-09-04 10:29:22');
INSERT INTO `exam_package_item` VALUES (65, 1, 3, 2, NULL, '2026-09-04 10:29:22', '2026-09-04 10:29:22');
INSERT INTO `exam_package_item` VALUES (66, 1, 4, 3, NULL, '2026-09-04 10:29:22', '2026-09-04 10:29:22');

-- ----------------------------
-- Table structure for family
-- ----------------------------
DROP TABLE IF EXISTS `family`;
CREATE TABLE `family`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '家属ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名（唯一）',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别（0：女，1：男）',
  `relation` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '与老人的关系（子女/配偶/亲属/其他）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0：停用，1：正常）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_family_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家属表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of family
-- ----------------------------
INSERT INTO `family` VALUES (4, 'zhangsan', '123456', '张三', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/0426c46d71ef483eb23e026abfa74fe3.png', 1, '子女', '18365985221', 1, NULL, 0, '2026-09-01 14:14:30', '2026-09-06 18:18:11');
INSERT INTO `family` VALUES (5, 'wanger', '123456', '王二', NULL, 1, '亲属', '18966247751', 1, NULL, 0, '2026-09-02 10:21:12', '2026-09-02 10:21:12');

-- ----------------------------
-- Table structure for floor
-- ----------------------------
DROP TABLE IF EXISTS `floor`;
CREATE TABLE `floor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼层ID',
  `building_id` bigint NOT NULL COMMENT '所属楼栋ID（关联building.id）',
  `floor_no` int NOT NULL COMMENT '楼层号（展示为N层）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志：0正常 1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_floor_building_id`(`building_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '楼层表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of floor
-- ----------------------------
INSERT INTO `floor` VALUES (1, 1, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `floor` VALUES (2, 1, 2, NULL, 0, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `floor` VALUES (3, 2, 1, NULL, 0, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `floor` VALUES (4, 1, 3, NULL, 1, '2026-09-06 11:17:46', '2026-09-07 19:25:38');
INSERT INTO `floor` VALUES (5, 1, 4, NULL, 1, '2026-09-06 11:17:51', '2026-09-07 19:25:38');
INSERT INTO `floor` VALUES (6, 1, 5, NULL, 1, '2026-09-06 11:17:57', '2026-09-07 19:25:38');
INSERT INTO `floor` VALUES (7, 2, 2, NULL, 0, '2026-09-06 11:18:03', '2026-09-06 11:18:03');
INSERT INTO `floor` VALUES (8, 2, 3, NULL, 1, '2026-09-06 11:18:09', '2026-09-07 19:25:38');
INSERT INTO `floor` VALUES (9, 2, 4, NULL, 1, '2026-09-06 11:18:14', '2026-09-07 19:25:38');
INSERT INTO `floor` VALUES (10, 2, 5, NULL, 1, '2026-09-06 11:18:23', '2026-09-07 19:25:38');
INSERT INTO `floor` VALUES (11, 4, 1, NULL, 0, '2026-09-06 11:18:38', '2026-09-06 11:18:38');
INSERT INTO `floor` VALUES (12, 5, 1, NULL, 0, '2026-09-06 11:18:49', '2026-09-06 11:18:49');
INSERT INTO `floor` VALUES (13, 6, 1, NULL, 0, '2026-09-06 11:18:53', '2026-09-06 11:18:53');

-- ----------------------------
-- Table structure for help_request
-- ----------------------------
DROP TABLE IF EXISTS `help_request`;
CREATE TABLE `help_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '求助记录ID',
  `elder_id` bigint NOT NULL COMMENT '求助老人ID（关联elder.id）',
  `type` tinyint NOT NULL DEFAULT 3 COMMENT '求助类型：0健康 1生活 2安全 3其他',
  `urgency` tinyint NOT NULL DEFAULT 0 COMMENT '紧急程度：0普通 1紧急 2非常紧急',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '求助内容（老人填写）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0未处理 1已处理 2已忽略',
  `result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理结果说明（提交已处理时填写）',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID（关联user.id，提交已处理或忽略的管理员）',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间（提交已处理或忽略的时间）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '求助时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_help_request_elder_id`(`elder_id` ASC) USING BTREE,
  INDEX `idx_help_request_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '求助记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of help_request
-- ----------------------------
INSERT INTO `help_request` VALUES (1, 1, 0, 1, '老人反映近三天头晕乏力，请求社区协助联系上门体检', 2, NULL, 1, '2026-09-04 18:54:40', '2026-09-04 18:37:55', '2026-09-04 18:54:40');
INSERT INTO `help_request` VALUES (2, 2, 2, 2, '家中厨房水管突然破裂漏水，急需维修', 1, '已派维修工上门抢修完毕，并电话告知家属', 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55', '2026-09-04 18:37:55');
INSERT INTO `help_request` VALUES (3, 3, 3, 0, '想咨询本周社区有没有敬老活动安排', 2, NULL, 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55', '2026-09-04 18:37:55');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `category_id` bigint NOT NULL COMMENT '资讯分类ID（关联news_category.id）',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资讯标题',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL（列表/详情页展示，走通用上传接口）',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资讯摘要（列表页展示，为空时前台可自行截取正文前100字）',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '资讯正文（富文本编辑器生成的HTML，图片以URL内嵌）',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者（为空时前台可显示“管理员”）',
  `views` int NOT NULL DEFAULT 0 COMMENT '阅读量（前台每打开一次详情 +1）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0：下架，1：发布）。前台只展示已发布的资讯',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_news_category_id`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资讯表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (1, 1, '老年人春季养生小常识', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/news/c897d4a796204721920e69f9612848bf.jpg', '春季气温多变，老年人应注意保暖、合理饮食、适度锻炼，谨防心脑血管疾病复发。', '<p>123123123123132</p>', '管理员', 29, 1, '2026-09-03 14:31:09', '2026-09-04 10:47:35');
INSERT INTO `news` VALUES (2, 3, '社区“九九重阳节”敬老活动报名通知', NULL, '重阳节将至，社区将举办敬老文艺汇演、免费义诊等活动，欢迎各位老人及家属报名参加。', '<p>九九重阳，敬老情浓。为弘扬中华民族尊老敬老的传统美德，社区定于重阳节当天举办敬老主题活动。</p><h3>活动安排</h3><p>上午9:00-11:00：文艺汇演（社区活动中心）</p><p>下午14:00-16:00：免费义诊、健康咨询（社区广场）</p><p>报名方式：前往社区服务中心或联系您的管家，名额有限，先到先得。</p><p><img src=\"https://elder-oss.oss-cn-qingdao.aliyuncs.com/news/48a44fcee3d2470b9a303988b8fdbfda.png\" alt=\"大海帆船水彩画 (4).png\" data-href=\"\" style=\"\"/></p>', '管理员', 23, 1, '2026-09-03 14:31:09', '2026-09-04 10:39:58');
INSERT INTO `news` VALUES (3, 4, '我国持续完善居家社区机构相协调的养老服务体系', NULL, '近年来，我国加快建设居家社区机构相协调、医养康养相结合的养老服务体系，养老服务供给不断优化。', '<p>近年来，我国养老服务体系建设持续推进，居家、社区、机构三类养老服务形态不断融合发展。</p><p>各地积极发展老年助餐服务、日间照料中心等服务设施，让老年人在家门口就能享受到便捷的养老服务。</p><p>未来，随着智慧养老、医养结合等模式不断推广，老年人的晚年生活将更有品质、更有温度。</p>', '管理员', 14, 1, '2026-09-03 14:31:09', '2026-09-04 10:40:02');
INSERT INTO `news` VALUES (4, 1, '老年人秋冬换季保健要点：护住头脚和血管', NULL, '秋冬换季气温骤降，是老年人心脑血管疾病的高发期。注意保暖、合理进补、适度运动，平稳度过季节交替。', '<p>秋冬换季时节，昼夜温差加大，老年人血管收缩、血压波动明显，是心脑血管疾病的高发期。做好以下几点，可以帮助老人平稳换季。</p><h3>一、重点部位保暖</h3><p>头部、颈部、脚部末梢循环差，外出时建议戴上帽子围巾，睡前用温水泡脚15分钟左右，促进血液循环。</p><h3>二、合理进补不上火</h3><p>秋冬进补要循序渐进，多吃温润食物如山药、百合、银耳、莲藕，少吃生冷海鲜和过于油腻的补汤，慢性病患者进补前最好咨询医生。</p><h3>三、运动避开清晨低温</h3><p>清晨气温最低、血压高峰叠加，建议将锻炼时间安排在上午10点后或下午，选择散步、太极拳等舒缓运动，出现胸闷头晕立即停止。</p><h3>四、按时监测血压</h3><p>换季期间血压容易波动，高血压老人应每天定时测量并记录，发现血压持续偏高或偏低，及时联系家庭医生调整用药。</p>', '管理员', 94, 1, '2026-09-04 10:47:07', '2026-09-07 20:06:26');
INSERT INTO `news` VALUES (5, 1, '老年人跌倒预防指南：居家安全改造全攻略', NULL, '跌倒是我国65岁以上老人因伤致死的首位原因。掌握居家防跌倒改造要点和正确跌倒后的处理方法，守护老人安全。', '<p>跌倒是我国65岁以上老年人因伤致死的首位原因，而家中恰恰是最容易跌倒的场所。一次跌倒就可能造成骨折、卧床等一系列严重后果，预防远比治疗重要。</p><h3>一、居家环境改造</h3><p>卫生间安装扶手和防滑垫，是性价比最高的改造；及时清理通道堆积的杂物和散落的电线，保持走道畅通；常活动区域保证充足照明，夜间起床路径安装感应小夜灯。</p><h3>二、选对鞋子和辅助工具</h3><p>老人应穿合脚防滑的平底鞋，避免穿拖鞋外出；行动不稳的老人要正确使用助行器或拐杖，不要因为“嫌麻烦”而硬撑。</p><h3>三、科学锻炼防跌倒</h3><p>太极拳、靠墙站立等平衡训练可以显著降低跌倒风险，建议每周锻炼3次以上，每次30分钟左右，量力而行。</p><h3>四、跌倒后不要急于起身</h3><p>跌倒后应先保持不动，判断是否有骨折，尤其髋部疼痛时强行起身可能加重损伤。应呼救或拨打120，等待专业救助。</p>', '管理员', 134, 1, '2026-09-04 10:47:07', '2026-09-04 14:20:59');
INSERT INTO `news` VALUES (6, 1, '糖尿病老人的控糖饮食：三餐这样吃才稳血糖', NULL, '饮食管理是糖尿病治疗的基石。掌握主食粗细搭配、进餐顺序和加餐技巧，帮助老人吃得好也控得稳。', '<p>很多糖尿病老人以为控糖就是“饿肚子”，其实科学的三餐搭配既能稳住血糖，也能保证营养。饮食管理记住下面几个要点。</p><h3>一、主食粗细搭配</h3><p>白米饭、白馒头升糖快，建议将一半主食换成燕麦、荞麦、糙米等粗杂粮，做到粗细搭配，每餐主食定量，不喝熬得过烂的粥。</p><h3>二、调整进餐顺序</h3><p>先吃蔬菜，再吃鱼肉蛋类，最后吃主食，可以延缓餐后血糖上升。细嚼慢咽，每餐控制在20分钟以上。</p><h3>三、加餐有技巧</h3><p>两餐之间血糖偏低时可以适量加餐，选择无糖酸奶、10颗左右坚果或一个拳头大的低糖水果，避免饼干、蛋糕等精制点心。</p><h3>四、定期复查不能少</h3><p>除了日常测血糖，建议每3个月复查糖化血红蛋白，每年检查眼底和尿微量白蛋白，及早发现并发症。</p>', '管理员', 96, 1, '2026-09-04 10:47:07', '2026-09-04 10:47:32');
INSERT INTO `news` VALUES (7, 1, '阿尔茨海默病的十个早期信号，子女一定要知道', NULL, '记忆力减退不一定是正常衰老。了解阿尔茨海默病的十个早期信号，早发现早干预，为老人争取宝贵的治疗时机。', '<p>阿尔茨海默病俗称老年痴呆，早发现、早干预可以显著延缓病情进展。如果家中老人出现以下信号，建议尽早到记忆门诊就诊。</p><h3>十个早期信号</h3><p>1. 记不住新发生的事，反复询问同一个问题；</p><p>2. 处理熟悉的事情出现困难，如不会做拿手菜；</p><p>3. 说话找词困难，称呼物品叫不出名字；</p><p>4. 分不清时间和地点，在熟悉的地方迷路；</p><p>5. 判断力下降，如大热天穿厚衣、乱买东西；</p><p>6. 理解力下降，跟不上别人谈话的内容；</p><p>7. 东西放错地方，怀疑家人偷了自己的物品；</p><p>8. 情绪性格明显改变，易怒、多疑、淡漠；</p><p>9. 不愿社交，主动性减退；</p><p>10. 计算能力下降，算不清简单的账目。</p><h3>家属可以做什么</h3><p>多陪伴、多交流，鼓励老人参加社区活动；协助规律作息和适度运动；确诊后配合医生规范用药，并做好防走失措施，如佩戴定位手环。</p>', '管理员', 214, 1, '2026-09-04 10:47:07', '2026-09-07 09:49:29');
INSERT INTO `news` VALUES (8, 2, '高龄津贴怎么领？各地养老补贴政策一次讲清', NULL, '80岁以上老人可申领高龄津贴，经济困难老人还有养老服务补贴。了解申请条件、材料和流程，该享的政策一项别落下。', '<p>国家层面建立了高龄津贴、养老服务补贴、护理补贴三项基本制度，但不少老人和家属并不清楚怎么申请，白白错过了本该享受的福利。</p><h3>一、高龄津贴</h3><p>多数地区80周岁以上的老人即可申领，各地标准不同，一般按月发放，百岁老人标准更高。凭身份证、户口本和银行卡到户籍所在地社区（村）居委会申请即可。</p><h3>二、养老服务补贴</h3><p>面向经济困难的老年人，低保、低收入家庭中的老人可向乡镇（街道）民政部门申请，经评估后按月发放或折算为养老服务。</p><h3>三、护理补贴</h3><p>失能老人经能力评估达到相应等级后，可申领护理补贴。评估一般由第三方机构上门进行，家属可代为提出申请。</p><h3>温馨提示</h3><p>各地政策存在差异，申请前可拨打12345政务服务热线或到社区服务中心咨询。行动不便的老人可委托家属代办，部分城市已支持线上申请。</p>', '管理员', 81, 1, '2026-09-04 10:47:07', '2026-09-04 14:41:40');
INSERT INTO `news` VALUES (9, 2, '带薪陪护、独生子女护理假，这些新政策你知道吗', NULL, '多个省份已出台独生子女父母护理假，父母住院期间子女可带薪陪护。了解适用条件和请假流程，尽孝不再两难。', '<p>“父母生病，谁去陪护？”这是摆在许多独生子女面前的现实难题。近年来多个省份陆续出台相关政策，明确独生子女可获得带薪护理假。</p><h3>一、什么是护理假</h3><p>指父母（部分地区扩展至岳父母、公婆）住院或失能期间，子女可以申请的带薪假期，一般每年10到20天不等，各省规定不同。</p><h3>二、申请条件与流程</h3><p>一般要求父母年满60周岁且住院治疗或经评估为失能状态，凭住院证明、评估证明向用人单位提出申请。用人单位不得因此扣减工资、辞退员工。</p><h3>三、政策落地仍需主动争取</h3><p>目前部分企业对该假期的执行还不到位，员工遇到拒批时可向当地人社部门投诉维权。也有城市探索将执行情况纳入企业信用评价。</p><h3>四、其他配套支持</h3><p>除了护理假，长期护理保险试点也在扩大，失能老人入住机构或居家接受护理，符合条件的项目可按比例报销，切实减轻家庭负担。</p>', '管理员', 65, 1, '2026-09-04 10:47:07', '2026-09-04 11:36:24');
INSERT INTO `news` VALUES (10, 3, '本周五社区免费义诊来了：测血糖血压、心电图、眼底检查', NULL, '本周五上午，社区卫生服务中心联合市第一医院开展免费义诊，项目包括血糖血压测量、心电图、眼底检查和用药咨询。', '<p>为提高社区居民健康水平，社区卫生服务中心联合市第一医院开展“健康进社区”免费义诊活动，欢迎广大老年朋友参加。</p><h3>活动时间与地点</h3><p>时间：本周五上午8:30-11:30</p><p>地点：社区服务中心一楼大厅</p><h3>义诊项目</h3><p>1. 免费测量血糖、血压（空腹前来可测血糖）；</p><p>2. 免费心电图检查；</p><p>3. 免费眼底检查，糖尿病、高血压老人建议重点检查；</p><p>4. 三甲医院内科专家现场坐诊，提供用药咨询和慢病指导。</p><h3>温馨提示</h3><p>请携带身份证和平时服用的药物或用药清单，方便医生了解情况；行动不便的老人可由家属陪同，现场备有轮椅；检查报告现场发放，异常结果将由家庭医生跟进随访。</p>', '管理员', 158, 1, '2026-09-04 10:47:07', '2026-09-04 11:36:21');
INSERT INTO `news` VALUES (11, 3, '社区老年大学秋季班开始报名：书法、太极、智能手机课', NULL, '社区老年大学秋季班开放报名，开设书法、太极拳、合唱、智能手机应用等课程，全部免费，名额有限先到先得。', '<p>社区老年大学2026年秋季班即日起开放报名，丰富老年人的精神文化生活，全部课程免费，欢迎社区内60周岁以上老人报名。</p><h3>课程安排</h3><p>书法班：每周二上午，学习楷书基础与创作；</p><p>太极拳班：每周四上午，二十四式简化太极拳；</p><p>合唱班：每周三下午，经典红歌与合唱技巧；</p><p>智能手机班：每周五上午，手把手教微信视频、网上挂号、防范电信诈骗。</p><h3>报名方式</h3><p>即日起可到社区服务中心二楼前台报名，或让家属通过社区公众号线上报名，每人限报2门课程，报满即止。</p><h3>上课地点</h3><p>社区活动中心三楼多功能教室，开课时间为下月第一周，具体课表将在班级群内通知。</p>', '管理员', 118, 1, '2026-09-04 10:47:07', '2026-09-04 10:47:07');
INSERT INTO `news` VALUES (12, 4, '独居老人的“隐形孤独”：比身体疾病更需要关注', NULL, '我国独居和空巢老人已过亿。长期孤独不仅影响心理健康，还会增加认知障碍和心血管疾病风险，关爱从常联系开始。', '<p>随着家庭结构小型化，我国独居和空巢老年人数量已超过一亿。很多老人身体尚可，但长期的孤独正在悄悄损害他们的健康。</p><h3>孤独也是一种“病”</h3><p>研究表明，长期社会隔离会使老年痴呆风险上升约50%，其危害等同于每天吸烟。孤独的老人往往饮食敷衍、作息紊乱、不愿就医，小病拖成大病。</p><h3>信号：老人正在变“沉默”</h3><p>如果发现老人变得沉默寡言、对原先喜欢的活动失去兴趣、反复念叨往事、拒绝与邻居来往，需要引起重视，这可能是心理出现问题甚至认知障碍的早期表现。</p><h3>子女能做的事</h3><p>固定的视频通话比偶尔的红包更有温度；鼓励老人培养兴趣爱好、参加社区活动；有条件的可以养宠物或参加老年大学；发现异常及时寻求社区或专业心理帮助。</p><h3>社区在行动</h3><p>许多社区已建立独居老人定期探访制度，安排网格员、志愿者每周上门或电话问候。如果您的邻里中有独居老人，多一句问候，也许就能帮到他们。</p>', '管理员', 143, 1, '2026-09-04 10:47:07', '2026-09-04 10:47:07');
INSERT INTO `news` VALUES (13, 4, '智慧养老正当时：一块屏幕如何守护老人的晚年', NULL, '紧急呼叫、健康监测、远程问诊……智慧养老产品正在走进普通家庭，科技不是冷冰冰的，它让牵挂触手可及。', '<p>凌晨摔倒在卫生间却无人知晓、忘记关燃气、慢病指标异常浑然不觉——这些养老场景中的痛点，正在被智慧养老产品逐一化解。</p><h3>一、紧急呼叫与跌倒监测</h3><p>一键呼叫器、毫米波跌倒监测雷达等设备，能在老人遇到突发状况时第一时间通知家属和社区服务站，为抢救赢得黄金时间。</p><h3>二、健康数据实时监测</h3><p>智能血压计、血糖仪测量的数据自动上传，子女在手机上就能看到父母的健康曲线，异常波动系统自动提醒，慢病管理从“凭感觉”变成“看数据”。</p><h3>三、远程问诊与配药到家</h3><p>通过视频问诊，慢病老人足不出户就能复诊开方，药品配送到家，大大减少往返医院的奔波。</p><h3>四、科技之外，陪伴不可替代</h3><p>智慧养老的意义在于“安全托底”，它不能替代子女的探望与交流。科技+服务的组合，加上家人常回家看看，才能构成完整的养老答案。</p>', '管理员', 167, 1, '2026-09-04 10:47:07', '2026-09-04 10:47:07');

-- ----------------------------
-- Table structure for news_category
-- ----------------------------
DROP TABLE IF EXISTS `news_category`;
CREATE TABLE `news_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资讯分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称（健康科普/政策法规/活动通知等）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用（禁用后前台不展示该分类及其资讯）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资讯分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of news_category
-- ----------------------------
INSERT INTO `news_category` VALUES (1, '健康科普', 1, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `news_category` VALUES (2, '政策法规', 2, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `news_category` VALUES (3, '活动通知', 3, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `news_category` VALUES (4, '养老资讯', 4, 1, '2026-09-03 14:31:09', '2026-09-06 12:57:30');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `parent_id` bigint NOT NULL COMMENT '所属上级',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '类型(0:目录,1:菜单,2:按钮)',
  `path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由地址',
  `permission_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限值',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(1:正常，0:禁止)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 195 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, 0, '用户与权限', 0, NULL, NULL, 'UserFilled', 8, 1, '2026-08-28 11:20:56', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (2, 1, '用户管理', 1, '/user', NULL, 'User', 1, 1, '2026-08-28 11:24:19', '2026-08-31 16:52:34');
INSERT INTO `permission` VALUES (3, 1, '角色管理', 1, '/role', NULL, 'Briefcase', 2, 1, '2026-08-28 11:24:27', '2026-08-31 16:52:37');
INSERT INTO `permission` VALUES (4, 1, '权限管理', 1, '/permission', NULL, 'Stamp', 3, 1, '2026-08-28 11:24:41', '2026-08-31 16:52:43');
INSERT INTO `permission` VALUES (5, 2, '用户添加', 2, NULL, 'user:add', NULL, 2, 1, '2026-08-28 11:24:53', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (6, 2, '用户删除', 2, NULL, 'user:deleteById', NULL, 3, 1, '2026-08-28 11:25:02', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (7, 3, '角色添加', 2, NULL, 'role:add', NULL, 2, 1, '2026-08-28 11:25:43', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (8, 3, '角色删除', 2, NULL, 'role:deleteById', NULL, 3, 1, '2026-08-28 11:25:51', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (16, 81, '合同管理', 1, '/contract', NULL, 'Document', 4, 1, '2026-08-28 20:59:35', '2026-09-01 14:28:03');
INSERT INTO `permission` VALUES (18, 16, '添加合同', 2, NULL, 'contract:add', NULL, 2, 1, '2026-08-29 10:06:38', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (19, 16, '查看合同', 2, NULL, 'contract:get', NULL, 3, 1, '2026-08-29 10:06:55', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (20, 16, '修改合同', 2, NULL, 'contract:update', NULL, 4, 1, '2026-08-29 10:07:13', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (23, 2, '用户修改', 2, NULL, 'user:update', NULL, 4, 1, '2026-08-29 10:08:07', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (24, 3, '角色编辑', 2, NULL, 'role:update', NULL, 4, 1, '2026-08-29 10:08:41', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (25, 16, '删除合同', 2, NULL, 'contract:deleteById', NULL, 5, 1, '2026-08-29 10:09:06', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (27, 2, '批量删除用户', 2, NULL, 'user:deleteAll', NULL, 5, 1, '2026-08-31 14:44:06', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (28, 3, '批量删除角色', 2, NULL, 'role:deleteAll', NULL, 5, 1, '2026-08-31 14:44:22', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (31, 16, '批量删除合同', 2, NULL, 'contract:deleteAll', NULL, 6, 1, '2026-08-31 14:47:41', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (37, 0, '护理管理', 0, NULL, NULL, 'FirstAidKit', 2, 1, '2026-08-31 16:44:05', '2026-08-31 16:44:36');
INSERT INTO `permission` VALUES (38, 37, '项目管理', 1, '/careItem', NULL, 'List', 0, 1, '2026-08-31 16:45:01', '2026-08-31 16:45:10');
INSERT INTO `permission` VALUES (39, 38, '添加项目', 2, NULL, 'careItem:add', NULL, 2, 1, '2026-08-31 16:45:18', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (40, 38, '修改项目', 2, NULL, 'careItem:update', NULL, 3, 1, '2026-08-31 16:45:33', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (42, 38, '批量删除项目', 2, NULL, 'careItem:deleteAll', NULL, 4, 1, '2026-08-31 16:45:54', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (43, 38, '删除项目', 2, NULL, 'careItem:deleteById', NULL, 5, 1, '2026-08-31 16:47:01', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (44, 0, '老人管理', 0, NULL, NULL, 'User', 1, 1, '2026-08-31 16:48:35', '2026-08-31 16:48:37');
INSERT INTO `permission` VALUES (45, 44, '全部老人', 1, '/elder', NULL, 'Avatar', 1, 1, '2026-08-31 16:49:26', '2026-08-31 16:52:53');
INSERT INTO `permission` VALUES (46, 44, '标签管理', 1, '/tag', NULL, 'CollectionTag', 2, 1, '2026-08-31 16:49:45', '2026-08-31 16:52:57');
INSERT INTO `permission` VALUES (47, 45, '添加老人', 2, NULL, 'elder:add', NULL, 2, 1, '2026-08-31 16:50:06', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (48, 45, '修改老人', 2, NULL, 'elder:update', NULL, 3, 1, '2026-08-31 16:50:20', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (49, 45, '删除老人', 2, NULL, 'elder:deleteById', NULL, 4, 1, '2026-08-31 16:50:30', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (50, 45, '批量删除老人', 2, NULL, 'elder:deleteAll', NULL, 5, 1, '2026-08-31 16:50:50', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (51, 46, '添加标签', 2, NULL, 'tag:add', NULL, 2, 1, '2026-08-31 16:51:02', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (52, 46, '修改标签', 2, NULL, 'tag:update', NULL, 3, 1, '2026-08-31 16:51:20', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (53, 46, '删除标签', 2, NULL, 'tag:delete', NULL, 4, 1, '2026-08-31 16:51:35', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (54, 46, '批量删除标签', 2, NULL, 'tag:deleteAll', NULL, 5, 1, '2026-08-31 16:51:50', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (55, 37, '等级管理', 1, '/careLevel', NULL, 'Medal', 1, 1, '2026-08-31 16:59:38', '2026-08-31 16:59:38');
INSERT INTO `permission` VALUES (56, 55, '添加等级', 2, NULL, 'careLevel:add', NULL, 2, 1, '2026-08-31 16:59:38', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (57, 55, '修改等级', 2, NULL, 'careLevel:update', NULL, 3, 1, '2026-08-31 16:59:38', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (58, 55, '删除等级', 2, NULL, 'careLevel:deleteById', NULL, 4, 1, '2026-08-31 16:59:38', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (59, 55, '批量删除等级', 2, NULL, 'careLevel:deleteAll', NULL, 5, 1, '2026-08-31 16:59:38', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (60, 37, '计划管理', 1, '/carePlan', NULL, 'Calendar', 2, 1, '2026-08-31 17:17:26', '2026-08-31 17:17:26');
INSERT INTO `permission` VALUES (61, 60, '添加计划', 2, NULL, 'carePlan:add', NULL, 2, 1, '2026-08-31 17:17:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (62, 60, '修改计划', 2, NULL, 'carePlan:update', NULL, 3, 1, '2026-08-31 17:17:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (63, 60, '删除计划', 2, NULL, 'carePlan:deleteById', NULL, 4, 1, '2026-08-31 17:17:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (64, 60, '批量删除计划', 2, NULL, 'carePlan:deleteAll', NULL, 5, 1, '2026-08-31 17:17:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (65, 45, '老人导入', 2, NULL, 'elder:import', NULL, 6, 1, '2026-08-31 18:41:04', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (66, 45, '老人导出', 2, NULL, 'elder:export', NULL, 7, 1, '2026-08-31 18:41:04', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (67, 2, '用户导入', 2, NULL, 'user:import', NULL, 6, 1, '2026-08-31 18:41:04', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (68, 2, '用户导出', 2, NULL, 'user:export', NULL, 7, 1, '2026-08-31 18:41:04', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (70, 37, '护理任务', 1, '/careTask', NULL, 'Finished', 3, 1, '2026-08-31 19:08:09', '2026-08-31 19:13:11');
INSERT INTO `permission` VALUES (71, 70, '完成任务', 2, NULL, 'careTask:complete', NULL, 2, 1, '2026-08-31 19:08:09', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (72, 70, '跳过任务', 2, NULL, 'careTask:skip', NULL, 3, 1, '2026-08-31 19:08:09', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (73, 70, '查看详情', 2, NULL, 'careTask:get', NULL, 4, 1, '2026-08-31 19:08:09', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (74, 70, '删除任务', 2, NULL, 'careTask:deleteById', NULL, 5, 1, '2026-09-01 10:39:10', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (75, 0, '首页', 1, '/', NULL, 'HomeFilled', 0, 1, '2026-09-01 10:53:31', '2026-09-01 10:53:31');
INSERT INTO `permission` VALUES (76, 81, '家属管理', 1, '/family', NULL, 'Comment', 0, 1, '2026-09-01 13:41:26', '2026-09-01 15:09:22');
INSERT INTO `permission` VALUES (77, 76, '添加家属', 2, NULL, 'family:add', NULL, 2, 1, '2026-09-01 13:41:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (78, 76, '编辑家属', 2, NULL, 'family:update', NULL, 3, 1, '2026-09-01 13:41:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (79, 76, '删除家属', 2, NULL, 'family:deleteById', NULL, 4, 1, '2026-09-01 13:41:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (80, 76, '批量删除家属', 2, NULL, 'family:deleteAll', NULL, 5, 1, '2026-09-01 13:41:26', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (81, 0, '客户管理', 0, NULL, NULL, 'ChatLineSquare', 6, 1, '2026-09-01 14:26:22', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (83, 70, '批量删除任务', 2, NULL, 'careTask:deleteAll', NULL, 6, 1, '2026-09-01 17:49:52', '2026-09-01 17:49:52');
INSERT INTO `permission` VALUES (84, 76, '查看家属', 2, NULL, 'family:view', NULL, 6, 1, '2026-09-01 20:22:44', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (85, 70, '仅看当前账户任务', 2, NULL, 'careTask:viewMine', NULL, 7, 1, '2026-09-02 10:52:29', '2026-09-02 11:04:26');
INSERT INTO `permission` VALUES (86, 70, '查看全部任务', 2, NULL, 'careTask:viewAll', NULL, 8, 1, '2026-09-02 10:52:29', '2026-09-02 11:04:30');
INSERT INTO `permission` VALUES (87, 45, '标注老人', 2, NULL, 'elder:tag', NULL, 8, 1, '2026-09-02 11:14:08', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (88, 2, '查看列表操作栏', 2, NULL, 'user:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (89, 3, '查看列表操作栏', 2, NULL, 'role:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (90, 4, '查看列表操作栏', 2, NULL, 'permission:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (91, 16, '查看列表操作栏', 2, NULL, 'contract:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (92, 38, '查看列表操作栏', 2, NULL, 'careItem:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (93, 45, '查看列表操作栏', 2, NULL, 'elder:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (94, 46, '查看列表操作栏', 2, NULL, 'tag:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (95, 55, '查看列表操作栏', 2, NULL, 'careLevel:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (96, 60, '查看列表操作栏', 2, NULL, 'carePlan:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (97, 70, '查看列表操作栏', 2, NULL, 'careTask:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (98, 76, '查看列表操作栏', 2, NULL, 'family:operation', NULL, 1, 1, '2026-09-02 11:25:32', '2026-09-02 11:34:23');
INSERT INTO `permission` VALUES (99, 0, '体检管理', 0, NULL, NULL, 'Stopwatch', 3, 1, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `permission` VALUES (100, 99, '体检项目', 1, '/examItem', NULL, 'List', 0, 1, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `permission` VALUES (101, 100, '查看列表操作栏', 2, NULL, 'examItem:operation', NULL, 1, 1, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `permission` VALUES (102, 100, '添加体检项目', 2, NULL, 'examItem:add', NULL, 2, 1, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `permission` VALUES (103, 100, '修改体检项目', 2, NULL, 'examItem:update', NULL, 3, 1, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `permission` VALUES (104, 100, '批量删除体检项目', 2, NULL, 'examItem:deleteAll', NULL, 4, 1, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `permission` VALUES (105, 100, '删除体检项目', 2, NULL, 'examItem:deleteById', NULL, 5, 1, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `permission` VALUES (106, 99, '体检套餐', 1, '/examPackage', NULL, 'Present', 1, 1, '2026-09-02 15:04:36', '2026-09-02 15:04:36');
INSERT INTO `permission` VALUES (107, 106, '查看列表操作栏', 2, NULL, 'examPackage:operation', NULL, 1, 1, '2026-09-02 15:04:36', '2026-09-02 15:04:36');
INSERT INTO `permission` VALUES (108, 106, '添加体检套餐', 2, NULL, 'examPackage:add', NULL, 2, 1, '2026-09-02 15:04:36', '2026-09-02 15:04:36');
INSERT INTO `permission` VALUES (109, 106, '修改体检套餐', 2, NULL, 'examPackage:update', NULL, 3, 1, '2026-09-02 15:04:36', '2026-09-02 15:04:36');
INSERT INTO `permission` VALUES (110, 106, '批量删除体检套餐', 2, NULL, 'examPackage:deleteAll', NULL, 4, 1, '2026-09-02 15:04:36', '2026-09-02 15:04:36');
INSERT INTO `permission` VALUES (111, 106, '删除体检套餐', 2, NULL, 'examPackage:deleteById', NULL, 5, 1, '2026-09-02 15:04:36', '2026-09-02 15:04:36');
INSERT INTO `permission` VALUES (112, 99, '体检预约', 1, '/examAppointment', NULL, 'Clock', 2, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (113, 112, '查看列表操作栏', 2, NULL, 'examAppointment:operation', NULL, 1, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (114, 112, '添加预约', 2, NULL, 'examAppointment:add', NULL, 2, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (115, 112, '修改预约', 2, NULL, 'examAppointment:update', NULL, 3, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (116, 112, '开始体检', 2, NULL, 'examAppointment:start', NULL, 4, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (117, 112, '录入结果完成体检', 2, NULL, 'examAppointment:complete', NULL, 5, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (118, 112, '查看体检结果', 2, NULL, 'examAppointment:viewResult', NULL, 6, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (119, 112, '取消预约', 2, NULL, 'examAppointment:cancel', NULL, 7, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (120, 112, '批量删除', 2, NULL, 'examAppointment:deleteAll', NULL, 8, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (121, 112, '删除预约', 2, NULL, 'examAppointment:deleteById', NULL, 9, 1, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `permission` VALUES (122, 0, '平台管理', 0, NULL, NULL, 'Brush', 7, 1, '2026-09-03 09:39:21', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (123, 122, '公告管理', 1, '/announcement', NULL, 'ChatLineSquare', 0, 1, '2026-09-03 09:39:21', '2026-09-03 09:48:30');
INSERT INTO `permission` VALUES (124, 123, '查看列表操作栏', 2, NULL, 'announcement:operation', NULL, 1, 1, '2026-09-03 09:39:21', '2026-09-03 09:39:21');
INSERT INTO `permission` VALUES (125, 123, '添加公告', 2, NULL, 'announcement:add', NULL, 2, 1, '2026-09-03 09:39:21', '2026-09-03 09:39:21');
INSERT INTO `permission` VALUES (126, 123, '修改公告', 2, NULL, 'announcement:update', NULL, 3, 1, '2026-09-03 09:39:21', '2026-09-03 09:39:21');
INSERT INTO `permission` VALUES (127, 123, '删除公告', 2, NULL, 'announcement:deleteById', NULL, 4, 1, '2026-09-03 09:39:21', '2026-09-03 09:39:21');
INSERT INTO `permission` VALUES (128, 123, '批量删除公告', 2, NULL, 'announcement:deleteAll', NULL, 5, 1, '2026-09-03 09:39:21', '2026-09-03 09:39:21');
INSERT INTO `permission` VALUES (130, 122, '资讯分类', 1, '/newsCategory', NULL, 'FolderOpened', 1, 1, '2026-09-03 14:31:09', '2026-09-03 14:49:58');
INSERT INTO `permission` VALUES (131, 130, '查看列表操作栏', 2, NULL, 'newsCategory:operation', NULL, 1, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (132, 130, '添加分类', 2, NULL, 'newsCategory:add', NULL, 2, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (133, 130, '修改分类', 2, NULL, 'newsCategory:update', NULL, 3, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (134, 130, '删除分类', 2, NULL, 'newsCategory:deleteById', NULL, 4, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (135, 130, '批量删除分类', 2, NULL, 'newsCategory:deleteAll', NULL, 5, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (136, 122, '资讯管理', 1, '/news', NULL, 'Document', 2, 1, '2026-09-03 14:31:09', '2026-09-03 14:49:58');
INSERT INTO `permission` VALUES (137, 136, '查看列表操作栏', 2, NULL, 'news:operation', NULL, 1, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (138, 136, '添加资讯', 2, NULL, 'news:add', NULL, 2, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (139, 136, '修改资讯', 2, NULL, 'news:update', NULL, 3, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (140, 136, '查看资讯', 2, NULL, 'news:get', NULL, 4, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (141, 136, '删除资讯', 2, NULL, 'news:deleteById', NULL, 5, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (142, 136, '批量删除资讯', 2, NULL, 'news:deleteAll', NULL, 6, 1, '2026-09-03 14:31:09', '2026-09-03 14:31:09');
INSERT INTO `permission` VALUES (143, 44, '求助管理', 1, '/help', NULL, 'Bell', 3, 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55');
INSERT INTO `permission` VALUES (144, 143, '查看列表操作栏', 2, NULL, 'help:operation', NULL, 1, 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55');
INSERT INTO `permission` VALUES (145, 143, '提交处理', 2, NULL, 'help:handle', NULL, 2, 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55');
INSERT INTO `permission` VALUES (146, 143, '忽略求助', 2, NULL, 'help:ignore', NULL, 3, 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55');
INSERT INTO `permission` VALUES (147, 143, '删除求助', 2, NULL, 'help:deleteById', NULL, 4, 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55');
INSERT INTO `permission` VALUES (148, 143, '批量删除求助', 2, NULL, 'help:deleteAll', NULL, 5, 1, '2026-09-04 18:37:55', '2026-09-04 18:37:55');
INSERT INTO `permission` VALUES (149, 0, '住宅管理', 0, NULL, NULL, 'OfficeBuilding', 4, 1, '2026-09-04 20:54:46', '2026-09-04 21:57:59');
INSERT INTO `permission` VALUES (152, 149, '楼栋管理', 1, '/building', NULL, 'School', 0, 1, '2026-09-04 20:54:46', '2026-09-04 21:55:38');
INSERT INTO `permission` VALUES (153, 149, '楼层管理', 1, '/floor', NULL, 'Files', 1, 1, '2026-09-04 20:54:46', '2026-09-04 21:55:38');
INSERT INTO `permission` VALUES (154, 149, '房间管理', 1, '/room', NULL, 'House', 2, 1, '2026-09-04 20:54:46', '2026-09-04 21:55:38');
INSERT INTO `permission` VALUES (155, 149, '床位管理', 1, '/bed', NULL, 'Grid', 3, 1, '2026-09-04 20:54:46', '2026-09-04 21:55:38');
INSERT INTO `permission` VALUES (163, 152, '查看列表操作栏', 2, NULL, 'building:operation', NULL, 1, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (164, 152, '添加楼栋', 2, NULL, 'building:add', NULL, 2, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (165, 152, '修改楼栋', 2, NULL, 'building:update', NULL, 3, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (166, 152, '删除楼栋', 2, NULL, 'building:deleteById', NULL, 4, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (167, 152, '批量删除楼栋', 2, NULL, 'building:deleteAll', NULL, 5, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (168, 153, '查看列表操作栏', 2, NULL, 'floor:operation', NULL, 1, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (169, 153, '添加楼层', 2, NULL, 'floor:add', NULL, 2, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (170, 153, '修改楼层', 2, NULL, 'floor:update', NULL, 3, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (171, 153, '删除楼层', 2, NULL, 'floor:deleteById', NULL, 4, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (172, 153, '批量删除楼层', 2, NULL, 'floor:deleteAll', NULL, 5, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (173, 154, '查看列表操作栏', 2, NULL, 'room:operation', NULL, 1, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (174, 154, '添加房间', 2, NULL, 'room:add', NULL, 2, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (175, 154, '修改房间', 2, NULL, 'room:update', NULL, 3, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (176, 154, '删除房间', 2, NULL, 'room:deleteById', NULL, 4, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (177, 154, '批量删除房间', 2, NULL, 'room:deleteAll', NULL, 5, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (178, 155, '查看列表操作栏', 2, NULL, 'bed:operation', NULL, 1, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (179, 155, '添加床位', 2, NULL, 'bed:add', NULL, 2, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (180, 155, '修改床位', 2, NULL, 'bed:update', NULL, 3, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (181, 155, '删除床位', 2, NULL, 'bed:deleteById', NULL, 4, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (182, 155, '批量删除床位', 2, NULL, 'bed:deleteAll', NULL, 5, 1, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `permission` VALUES (186, 0, '入退住管理', 0, NULL, NULL, 'SetUp', 5, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (187, 186, '入住申请', 1, '/checkInProcess', NULL, 'CirclePlus', 0, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (188, 186, '退住申请', 1, '/checkOutProcess', NULL, 'SwitchButton', 1, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (189, 186, '申请记录', 1, '/checkRecord', NULL, 'Tickets', 2, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (190, 187, '取消办理', 2, NULL, 'checkIn:cancel', NULL, 1, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (191, 188, '取消办理', 2, NULL, 'checkOut:cancel', NULL, 1, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (192, 189, '查看列表操作栏', 2, NULL, 'record:operation', NULL, 1, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (193, 189, '删除记录', 2, NULL, 'record:deleteById', NULL, 2, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `permission` VALUES (194, 189, '批量删除记录', 2, NULL, 'record:deleteAll', NULL, 3, 1, '2026-09-06 11:15:12', '2026-09-06 11:15:12');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色名称',
  `code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0（true）未删除， 1（false）已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '管理员', 'super_admin', '系统超级管理员，拥有平台全部操作权限', 0, '2026-08-27 19:59:32', '2026-09-02 10:38:15');
INSERT INTO `role` VALUES (2, '市场', 'marketing', '市场业务人员，负责客户对接、养老业务拓展工作', 0, '2026-08-27 20:35:41', '2026-09-02 10:37:19');
INSERT INTO `role` VALUES (3, '护工', 'nurse_aide', '养老护理人员，负责老人日常照护、生活照料服务', 0, '2026-08-28 09:27:50', '2026-09-02 10:37:44');
INSERT INTO `role` VALUES (4, '后勤', 'logistics', '后勤运维人员，负责院区设施维护、物资管理工作', 0, '2026-08-28 10:26:27', '2026-09-02 10:37:59');
INSERT INTO `role` VALUES (5, '医护', 'medical_staff', '医护人员，负责老人健康监测、诊疗护理、用药管理工作', 0, '2026-09-02 16:23:05', '2026-09-02 16:23:05');
INSERT INTO `role` VALUES (6, '演示账号', 'demo', NULL, 1, '2026-09-04 13:06:07', '2026-09-06 11:14:54');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1580 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1283, 3, 75, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1284, 3, 44, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1285, 3, 45, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1286, 3, 66, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1287, 3, 37, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1288, 3, 38, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1289, 3, 55, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1290, 3, 60, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1291, 3, 70, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1292, 3, 97, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1293, 3, 71, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1294, 3, 72, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1295, 3, 73, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1296, 3, 85, '2026-09-02 11:48:44', '2026-09-02 11:48:44');
INSERT INTO `role_permission` VALUES (1304, 3, 100, '2026-09-02 14:23:24', '2026-09-02 14:23:24');
INSERT INTO `role_permission` VALUES (1312, 3, 106, '2026-09-02 15:04:36', '2026-09-02 15:04:36');
INSERT INTO `role_permission` VALUES (1328, 3, 112, '2026-09-02 16:10:37', '2026-09-02 16:10:37');
INSERT INTO `role_permission` VALUES (1329, 5, 75, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1330, 5, 44, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1331, 5, 45, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1332, 5, 99, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1333, 5, 100, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1334, 5, 106, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1335, 5, 112, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1336, 5, 116, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1337, 5, 117, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1338, 5, 118, '2026-09-02 16:24:11', '2026-09-02 16:24:11');
INSERT INTO `role_permission` VALUES (1414, 1, 75, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1415, 1, 44, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1416, 1, 45, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1417, 1, 93, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1418, 1, 47, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1419, 1, 48, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1420, 1, 49, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1421, 1, 50, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1422, 1, 65, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1423, 1, 66, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1424, 1, 87, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1425, 1, 46, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1426, 1, 94, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1427, 1, 51, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1428, 1, 52, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1429, 1, 53, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1430, 1, 54, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1431, 1, 143, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1432, 1, 144, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1433, 1, 145, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1434, 1, 146, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1435, 1, 147, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1436, 1, 148, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1437, 1, 37, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1438, 1, 38, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1439, 1, 92, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1440, 1, 39, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1441, 1, 40, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1442, 1, 42, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1443, 1, 43, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1444, 1, 55, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1445, 1, 95, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1446, 1, 56, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1447, 1, 57, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1448, 1, 58, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1449, 1, 59, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1450, 1, 60, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1451, 1, 96, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1452, 1, 61, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1453, 1, 62, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1454, 1, 63, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1455, 1, 64, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1456, 1, 70, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1457, 1, 97, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1458, 1, 71, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1459, 1, 72, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1460, 1, 73, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1461, 1, 74, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1462, 1, 83, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1463, 1, 85, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1464, 1, 86, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1465, 1, 99, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1466, 1, 100, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1467, 1, 101, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1468, 1, 102, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1469, 1, 103, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1470, 1, 104, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1471, 1, 105, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1472, 1, 106, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1473, 1, 107, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1474, 1, 108, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1475, 1, 109, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1476, 1, 110, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1477, 1, 111, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1478, 1, 112, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1479, 1, 113, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1480, 1, 114, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1481, 1, 115, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1482, 1, 116, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1483, 1, 117, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1484, 1, 118, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1485, 1, 119, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1486, 1, 120, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1487, 1, 121, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1488, 1, 149, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1489, 1, 152, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1490, 1, 163, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1491, 1, 164, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1492, 1, 165, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1493, 1, 166, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1494, 1, 167, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1495, 1, 153, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1496, 1, 168, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1497, 1, 169, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1498, 1, 170, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1499, 1, 171, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1500, 1, 172, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1501, 1, 154, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1502, 1, 173, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1503, 1, 174, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1504, 1, 175, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1505, 1, 176, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1506, 1, 177, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1507, 1, 155, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1508, 1, 178, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1509, 1, 179, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1510, 1, 180, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1511, 1, 181, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1512, 1, 182, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1513, 1, 81, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1514, 1, 76, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1515, 1, 98, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1516, 1, 77, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1517, 1, 78, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1518, 1, 79, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1519, 1, 80, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1520, 1, 84, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1521, 1, 16, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1522, 1, 91, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1523, 1, 18, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1524, 1, 19, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1525, 1, 20, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1526, 1, 25, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1527, 1, 31, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1528, 1, 122, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1529, 1, 123, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1530, 1, 124, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1531, 1, 125, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1532, 1, 126, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1533, 1, 127, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1534, 1, 128, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1535, 1, 130, '2026-09-04 21:59:26', '2026-09-04 21:59:26');
INSERT INTO `role_permission` VALUES (1536, 1, 131, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1537, 1, 132, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1538, 1, 133, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1539, 1, 134, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1540, 1, 135, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1541, 1, 136, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1542, 1, 137, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1543, 1, 138, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1544, 1, 139, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1545, 1, 140, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1546, 1, 141, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1547, 1, 142, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1548, 1, 1, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1549, 1, 2, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1550, 1, 88, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1551, 1, 5, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1552, 1, 6, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1553, 1, 23, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1554, 1, 27, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1555, 1, 67, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1556, 1, 68, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1557, 1, 3, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1558, 1, 89, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1559, 1, 7, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1560, 1, 8, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1561, 1, 24, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1562, 1, 28, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1563, 1, 4, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1564, 1, 90, '2026-09-04 21:59:27', '2026-09-04 21:59:27');
INSERT INTO `role_permission` VALUES (1565, 1, 186, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1566, 1, 187, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1567, 1, 188, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1568, 1, 189, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1569, 1, 190, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1570, 1, 191, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1571, 1, 192, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1572, 1, 193, '2026-09-06 11:15:12', '2026-09-06 11:15:12');
INSERT INTO `role_permission` VALUES (1573, 1, 194, '2026-09-06 11:15:12', '2026-09-06 11:15:12');

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `floor_id` bigint NOT NULL COMMENT '所属楼层ID（关联floor.id）',
  `room_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间号',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志：0正常 1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_floor_id`(`floor_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 1, '101', '双人间朝南', 0, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `room` VALUES (2, 1, '102', '双人间朝北', 0, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `room` VALUES (3, 2, '201', '双人间', 0, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `room` VALUES (4, 3, '101', '双人间朝南', 0, '2026-09-04 20:54:46', '2026-09-04 20:54:46');
INSERT INTO `room` VALUES (5, 11, '整栋', '整栋别墅', 0, '2026-09-07 19:28:03', '2026-09-07 19:28:20');
INSERT INTO `room` VALUES (6, 12, '整栋', '整栋别墅', 0, '2026-09-07 19:28:17', '2026-09-07 19:28:17');
INSERT INTO `room` VALUES (7, 13, '整栋', '整栋别墅，夫妻套房', 0, '2026-09-07 19:28:32', '2026-09-07 19:30:49');

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签编码（LIVE_ALONE/EMPTY_NEST/...）',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称（独居/空巢/...）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'LIVE_ALONE', '独居', 0, '2026-08-26 14:07:10', '2026-08-29 23:29:18');
INSERT INTO `tag` VALUES (2, 'EMPTY_NEST', '空巢', 0, '2026-08-26 14:07:10', '2026-08-26 14:07:10');
INSERT INTO `tag` VALUES (3, 'AGE_80', '高龄', 0, '2026-08-26 14:07:10', '2026-08-26 14:07:10');
INSERT INTO `tag` VALUES (4, 'DISABLED', '失能', 0, '2026-08-26 14:07:10', '2026-08-26 14:07:10');
INSERT INTO `tag` VALUES (5, 'C_HBP', '高血压', 0, '2026-08-26 14:07:10', '2026-08-26 14:07:10');
INSERT INTO `tag` VALUES (6, 'C_DM', '糖尿病', 0, '2026-08-26 14:07:10', '2026-08-26 14:07:10');
INSERT INTO `tag` VALUES (7, 'C_CHD', '冠心病', 0, '2026-08-26 14:07:10', '2026-08-26 14:07:10');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0：停用，1：正常）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0：未删除，1：已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'Tinsur', '$2a$10$LV/TFD/x0ynJgQvQVGknBeYWVD6AncJm0vb/wxMD7NWcgy74acBN2', 'Tinsur', '15265542669', '2545653474@qq.com', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/77519a6f36034a9d82af22be67c50f90.webp', 1, 0, '2026-08-25 20:41:08', '2026-09-07 13:23:09');
INSERT INTO `user` VALUES (2, 'Test', '$2a$10$TE/4aLKF89jziCfhDWgOXuPih1kYGdZUhAaCIORDZ7vLDFVgRhnxa', 'Test', '13681742950', 'test@example.com', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/55ef0f5ee23f433db5c07871964e58c9.png', 1, 0, '2026-08-26 08:18:42', '2026-09-07 13:23:06');
INSERT INTO `user` VALUES (76, 'zhangsan', '$2a$10$.BbvWD2tW3CCSBUjt97qCOnzwJuo30PPR1tHva8MmFnLCRkf8LJGG', '张三', '18566214201', 'zhangsan@example.com', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/29e10df0160844e0a13cc69a9baf3885.png', 1, 0, '2026-08-27 20:58:45', '2026-09-07 13:23:01');
INSERT INTO `user` VALUES (77, 'lisi', '$2a$10$47L8ANjDXrbotYYkygW6e.xsdkR5IwvTm1D6v5ljv4RPs.QozebuG', '李四', '17952759963', 'lisi@example.com', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/1024ef1ec3d14071ad83adbbbabc01ee.png', 1, 0, '2026-08-28 10:20:55', '2026-09-07 13:22:57');
INSERT INTO `user` VALUES (78, 'wangwu', '$2a$10$Gw3OXtnqiB52hsiDxhpjpeNPQFd4txYP8RsGSrU23pxqiUWLDx6ci', '王五', '18755125566', 'wangwu@example120.cn', NULL, 1, 0, '2026-09-02 16:24:28', '2026-09-07 13:22:53');
INSERT INTO `user` VALUES (80, 'admin', '$2a$10$2LJWPrUFJQlDV26MHcDE1e2f5ztMRGN8zCiaB79R0bdVL2mdpRBrC', '演示账号', '10000000000', '', 'https://elder-oss.oss-cn-qingdao.aliyuncs.com/avatar/131e03ac79a34dec82681c4dcaa0de7e.png', 1, 0, '2026-09-04 14:28:16', '2026-09-07 13:22:46');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工-角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (20, 3, 76, '2026-08-28 10:56:19', '2026-08-28 10:56:19');
INSERT INTO `user_role` VALUES (25, 2, 2, '2026-08-28 13:38:50', '2026-08-28 13:38:50');
INSERT INTO `user_role` VALUES (37, 1, 1, '2026-08-31 19:27:39', '2026-08-31 19:27:39');
INSERT INTO `user_role` VALUES (38, 3, 77, '2026-09-01 19:57:48', '2026-09-01 19:57:48');
INSERT INTO `user_role` VALUES (39, 5, 78, '2026-09-02 16:24:33', '2026-09-02 16:24:33');
INSERT INTO `user_role` VALUES (40, 1, 80, '2026-09-04 14:30:08', '2026-09-04 14:30:08');

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `t_score`;
CREATE TABLE `t_score`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `score` tinyint NOT NULL DEFAULT '1' COMMENT '用户打分，1到5分',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '用户打分表';
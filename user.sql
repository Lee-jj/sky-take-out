DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT '密码',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0:禁用，1:启用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`user_name`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '用户信息表';
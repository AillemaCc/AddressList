
-- 创建数据库（可选）
CREATE DATABASE IF NOT EXISTS contact_system
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE contact_system;

-- 学生基础信息表
CREATE TABLE t_student_info (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    student_id VARCHAR(20) NOT NULL COMMENT '学号，唯一标识',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    major VARCHAR(100) NOT NULL COMMENT '专业',
    class_name VARCHAR(50) NOT NULL COMMENT '班级',
    enrollment_year YEAR NOT NULL COMMENT '入学年份',
    graduation_year YEAR NOT NULL COMMENT '毕业年份',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '注册状态（0待审核，1通过，2拒绝）',
    register_token VARCHAR(64) NOT NULL COMMENT '注册凭证',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_id (student_id),
    UNIQUE KEY uk_register_token (register_token),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生基础信息表';

-- 学生通讯信息表
CREATE TABLE t_student_contact (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    student_id VARCHAR(20) NOT NULL COMMENT '学号',
    employer VARCHAR(100) NOT NULL COMMENT '就业单位',
    city VARCHAR(100) NOT NULL COMMENT '所在城市',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_id (student_id),
    KEY idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生通讯信息表';

-- 管理员账户表
CREATE TABLE t_admin_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员账户表';

-- 学生注册审核日志表
CREATE TABLE t_student_audit_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    student_id VARCHAR(20) NOT NULL COMMENT '学生学号',
    action TINYINT NOT NULL COMMENT '操作类型：1通过，2拒绝',
    admin_id BIGINT UNSIGNED NOT NULL COMMENT '操作管理员ID',
    remark TEXT COMMENT '备注信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_admin_id (admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生注册审核日志表';

-- 字段修改日志表
CREATE TABLE t_field_change_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    student_id VARCHAR(20) NOT NULL COMMENT '学生学号',
    field_name VARCHAR(50) NOT NULL COMMENT '被修改的字段名',
    old_value VARCHAR(255) COMMENT '原值',
    new_value VARCHAR(255) COMMENT '新值',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    change_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_field_name (field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段修改日志表';

-- 学院/专业/班级信息表
CREATE TABLE t_major_info (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    college_name VARCHAR(100) NOT NULL COMMENT '学院名称',
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    is_active TINYINT NOT NULL DEFAULT 1 COMMENT '是否有效（1有效，0无效）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_college (college_name),
    KEY idx_major (major_name),
    KEY idx_class (class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院/专业/班级信息表';

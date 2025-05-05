
USE contact_system;

-- 插入 t_student_info 测试数据
INSERT INTO t_student_info (student_id, name, major, class_name, enrollment_year, graduation_year, phone, email, password, status, register_token)
VALUES
('20210001', '张三', '计算机科学与技术', '计科1班', 2020, 2024, '13800138000', 'zhangsan@example.com', 'Password123', 1, 'token001'),
('20210002', '李四', '计算机科学与技术', '计科2班', 2020, 2024, '13800138001', 'lisi@example.com', 'Password123', 1, 'token002'),
('20210003', '王五', '软件工程', '软工1班', 2021, 2025, '13800138002', 'wangwu@example.com', 'Password123', 0, 'token003'),
('20210004', '赵六', '信息安全', '信安1班', 2019, 2023, '13800138003', 'zhaoliu@example.com', 'Password123', 2, 'token004'),
('20210005', '孙七', '计算机科学与技术', '计科1班', 2020, 2024, '13800138004', 'sunqi@example.com', 'Password123', 1, 'token005'),
('20210006', '周八', '软件工程', '软工1班', 2021, 2025, '13800138005', 'zhouba@example.com', 'Password123', 1, 'token006'),
('20210007', '吴九', '信息安全', '信安2班', 2019, 2023, '13800138006', 'wujiu@example.com', 'Password123', 1, 'token007'),
('20210008', '郑十', '计算机科学与技术', '计科2班', 2020, 2024, '13800138007', 'zhengshi@example.com', 'Password123', 0, 'token008'),
('20210009', '钱十一', '软件工程', '软工2班', 2021, 2025, '13800138008', 'qianshiyi@example.com', 'Password123', 2, 'token009'),
('20210010', '冯十二', '信息安全', '信安1班', 2019, 2023, '13800138009', 'fengshier@example.com', 'Password123', 1, 'token010');

-- 插入 t_student_contact 测试数据
INSERT INTO t_student_contact (student_id, employer, city, phone, email)
VALUES
('20210001', '腾讯', '深圳', '13800138000', 'zhangsan@example.com'),
('20210002', '阿里巴巴', '杭州', '13800138001', 'lisi@example.com'),
('20210003', '百度', '北京', '13800138002', 'wangwu@example.com'),
('20210004', '字节跳动', '北京', '13800138003', 'zhaoliu@example.com'),
('20210005', '美团', '上海', '13800138004', 'sunqi@example.com'),
('20210006', '拼多多', '上海', '13800138005', 'zhouba@example.com'),
('20210007', '小米', '北京', '13800138006', 'wujiu@example.com'),
('20210008', '网易', '广州', '13800138007', 'zhengshi@example.com'),
('20210009', '携程', '上海', '13800138008', 'qianshiyi@example.com'),
('20210010', '京东', '北京', '13800138009', 'fengshier@example.com');

-- 插入 t_admin_user 测试数据
INSERT INTO t_admin_user (username, password, email)
VALUES
('admin1', 'Admin123!', 'admin1@example.com'),
('admin2', 'Admin123!', 'admin2@example.com');

-- 插入 t_student_audit_log 测试数据
INSERT INTO t_student_audit_log (student_id, action, admin_id, remark)
VALUES
('20210001', 1, 1, '通过审核'),
('20210002', 1, 1, '通过审核'),
('20210003', 2, 1, '学号无效'),
('20210004', 2, 2, '信息不完整'),
('20210005', 1, 1, '正常'),
('20210006', 1, 1, '正常'),
('20210007', 1, 2, '正常'),
('20210008', 2, 2, '重复注册'),
('20210009', 2, 2, '格式错误'),
('20210010', 1, 1, '通过');

-- 插入 t_field_change_log 测试数据
INSERT INTO t_field_change_log (student_id, field_name, old_value, new_value, ip_address)
VALUES
('20210001', 'email', 'zhangsan@old.com', 'zhangsan@example.com', '192.168.1.1'),
('20210002', 'phone', '13800000000', '13800138001', '192.168.1.2'),
('20210003', 'email', 'wangwu@old.com', 'wangwu@example.com', '192.168.1.3'),
('20210004', 'phone', '13800000001', '13800138003', '192.168.1.4'),
('20210005', 'email', 'sunqi@old.com', 'sunqi@example.com', '192.168.1.5'),
('20210006', 'phone', '13800000002', '13800138005', '192.168.1.6'),
('20210007', 'email', 'wujiu@old.com', 'wujiu@example.com', '192.168.1.7'),
('20210008', 'phone', '13800000003', '13800138007', '192.168.1.8'),
('20210009', 'email', 'qianshiyi@old.com', 'qianshiyi@example.com', '192.168.1.9'),
('20210010', 'phone', '13800000004', '13800138009', '192.168.1.10');

-- 插入 t_major_info 测试数据
INSERT INTO t_major_info (college_name, major_name, class_name)
VALUES
('计算机学院', '计算机科学与技术', '计科1班'),
('计算机学院', '计算机科学与技术', '计科2班'),
('软件学院', '软件工程', '软工1班'),
('软件学院', '软件工程', '软工2班'),
('信息学院', '信息安全', '信安1班'),
('信息学院', '信息安全', '信安2班'),
('人工智能学院', '人工智能', '人工1班'),
('网络工程学院', '网络工程', '网工1班'),
('大数据学院', '数据科学与大数据技术', '大数据1班'),
('自动化学院', '自动化', '自动1班');

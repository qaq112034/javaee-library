-- ============================================================
-- 图书管理系统 - 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE library_db;

-- ============================================================
-- 1. 系统管理相关表
-- ============================================================

-- 系统用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    username    VARCHAR(50)     NOT NULL                 COMMENT '用户名（登录账号）',
    password    VARCHAR(255)    NOT NULL                 COMMENT '密码（BCrypt加密）',
    real_name   VARCHAR(50)     DEFAULT NULL             COMMENT '真实姓名',
    phone       VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    email       VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    avatar      VARCHAR(255)    DEFAULT NULL             COMMENT '头像URL',
    status      TINYINT         DEFAULT 1                COMMENT '状态：1=启用 0=禁用',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT         DEFAULT 0                COMMENT '逻辑删除：0=未删除 1=已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 系统角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '角色ID',
    role_name   VARCHAR(50)     NOT NULL                 COMMENT '角色名称',
    role_code   VARCHAR(50)     NOT NULL                 COMMENT '角色编码（如 ROLE_ADMIN）',
    description VARCHAR(255)    DEFAULT NULL             COMMENT '角色描述',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '权限ID',
    perm_name   VARCHAR(50)     NOT NULL                 COMMENT '权限名称',
    perm_code   VARCHAR(100)    NOT NULL                 COMMENT '权限编码',
    perm_type   VARCHAR(20)     DEFAULT 'menu'           COMMENT '权限类型：menu=菜单 button=按钮 api=接口',
    parent_id   BIGINT          DEFAULT 0                COMMENT '父级权限ID',
    path        VARCHAR(255)    DEFAULT NULL             COMMENT '路由路径',
    icon        VARCHAR(100)    DEFAULT NULL             COMMENT '图标',
    sort_order  INT             DEFAULT 0                COMMENT '排序',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户-角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id      BIGINT  NOT NULL AUTO_INCREMENT  COMMENT 'ID',
    user_id BIGINT  NOT NULL                 COMMENT '用户ID',
    role_id BIGINT  NOT NULL                 COMMENT '角色ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色-权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id      BIGINT  NOT NULL AUTO_INCREMENT  COMMENT 'ID',
    role_id BIGINT  NOT NULL                 COMMENT '角色ID',
    perm_id BIGINT  NOT NULL                 COMMENT '权限ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================================
-- 2. 图书管理相关表
-- ============================================================

-- 图书分类表
DROP TABLE IF EXISTS book_category;
CREATE TABLE book_category (
    id            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    category_name VARCHAR(50)  NOT NULL                 COMMENT '分类名称',
    parent_id     BIGINT       DEFAULT 0                COMMENT '父分类ID（0=顶级分类）',
    sort_order    INT          DEFAULT 0                COMMENT '排序号',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 图书表
DROP TABLE IF EXISTS book;
CREATE TABLE book (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '图书ID',
    isbn            VARCHAR(20)     NOT NULL                 COMMENT 'ISBN编号',
    title           VARCHAR(200)    NOT NULL                 COMMENT '书名',
    author          VARCHAR(100)    DEFAULT NULL             COMMENT '作者',
    publisher       VARCHAR(100)    DEFAULT NULL             COMMENT '出版社',
    publish_date    DATE            DEFAULT NULL             COMMENT '出版日期',
    category_id     BIGINT          DEFAULT NULL             COMMENT '分类ID',
    description     TEXT            DEFAULT NULL             COMMENT '图书简介',
    cover_url       VARCHAR(255)    DEFAULT NULL             COMMENT '封面图片URL',
    total_copies    INT             DEFAULT 1                COMMENT '馆藏总数',
    available_copies INT            DEFAULT 1                COMMENT '可借数量',
    location        VARCHAR(100)    DEFAULT NULL             COMMENT '馆藏位置（如：A区-3排-5号）',
    status          TINYINT         DEFAULT 1                COMMENT '状态：1=上架 0=下架',
    borrow_count    INT             DEFAULT 0                COMMENT '累计借阅次数',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_isbn (isbn),
    KEY idx_title (title),
    KEY idx_author (author),
    KEY idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- ============================================================
-- 3. 读者管理表
-- ============================================================

DROP TABLE IF EXISTS reader;
CREATE TABLE reader (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '读者ID',
    reader_no       VARCHAR(20)     NOT NULL                 COMMENT '读者编号（借阅证号）',
    real_name       VARCHAR(50)     NOT NULL                 COMMENT '真实姓名',
    gender          TINYINT         DEFAULT NULL             COMMENT '性别：1=男 2=女',
    id_card         VARCHAR(18)     DEFAULT NULL             COMMENT '身份证号',
    phone           VARCHAR(20)     DEFAULT NULL             COMMENT '联系电话',
    email           VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    address         VARCHAR(255)    DEFAULT NULL             COMMENT '地址',
    reader_type     TINYINT         DEFAULT 1                COMMENT '读者类型：1=学生 2=教师 3=校外',
    max_borrow_count INT            DEFAULT 5                COMMENT '最大可借数量',
    max_borrow_days  INT            DEFAULT 30               COMMENT '最大可借天数',
    status          TINYINT         DEFAULT 1                COMMENT '状态：1=正常 0=停用',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_reader_no (reader_no),
    KEY idx_reader_name (real_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者表';

-- ============================================================
-- 4. 借阅管理表
-- ============================================================

DROP TABLE IF EXISTS borrow_record;
CREATE TABLE borrow_record (
    id          BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    reader_id   BIGINT      NOT NULL                 COMMENT '读者ID',
    book_id     BIGINT      NOT NULL                 COMMENT '图书ID',
    borrow_date DATETIME    NOT NULL                 COMMENT '借阅日期',
    due_date    DATETIME    NOT NULL                 COMMENT '应还日期',
    return_date DATETIME    DEFAULT NULL             COMMENT '实际归还日期',
    status      TINYINT     DEFAULT 1                COMMENT '状态：1=借阅中 2=已还 3=已逾期',
    renew_count INT         DEFAULT 0                COMMENT '续借次数',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_reader_id (reader_id),
    KEY idx_book_id (book_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- ============================================================
-- 5. 公告管理表
-- ============================================================

DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '公告ID',
    title       VARCHAR(200)    NOT NULL                 COMMENT '公告标题',
    content     TEXT            DEFAULT NULL             COMMENT '公告内容',
    notice_type TINYINT         DEFAULT 1                COMMENT '类型：1=系统公告 2=催还通知',
    publisher_id BIGINT         DEFAULT NULL             COMMENT '发布人ID',
    status      TINYINT         DEFAULT 1                COMMENT '状态：1=已发布 0=草稿',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ============================================================
-- 6. 初始化数据
-- ============================================================

-- 插入默认角色
INSERT INTO sys_role (id, role_name, role_code, description) VALUES
(1, '超级管理员', 'ROLE_ADMIN',   '拥有系统所有权限'),
(2, '图书管理员', 'ROLE_LIBRARIAN','负责图书管理和借阅处理'),
(3, '普通读者',   'ROLE_READER',  '可浏览和借阅图书');

-- 插入默认权限
INSERT INTO sys_permission (id, perm_name, perm_code, perm_type, parent_id, path, icon, sort_order) VALUES
-- 一级菜单
(1,  '系统管理', 'system:manage',   'menu', 0, '/system',    'Setting',     10),
(2,  '图书管理', 'book:manage',     'menu', 0, '/book',      'Notebook',    20),
(3,  '读者管理', 'reader:manage',   'menu', 0, '/reader',    'User',        30),
(4,  '借阅管理', 'borrow:manage',   'menu', 0, '/borrow',    'Tickets',     40),
(5,  '公告管理', 'notice:manage',   'menu', 0, '/notice',    'Bell',        50),
(6,  '数据统计', 'statistics:view', 'menu', 0, '/statistics','DataAnalysis',60),
-- 用户管理子权限
(7,  '用户列表', 'user:list',       'menu', 1, '/system/user',   NULL, 1),
(8,  '用户新增', 'user:add',        'button', 1, NULL,           NULL, 2),
(9,  '用户编辑', 'user:edit',       'button', 1, NULL,           NULL, 3),
(10, '用户删除', 'user:delete',     'button', 1, NULL,           NULL, 4),
-- 角色管理子权限
(11, '角色列表', 'role:list',       'menu', 1, '/system/role',   NULL, 5),
(12, '角色编辑', 'role:edit',       'button', 1, NULL,           NULL, 6),
-- 图书管理子权限
(13, '图书列表', 'book:list',       'menu', 2, '/book/list',     NULL, 1),
(14, '图书新增', 'book:add',        'button', 2, NULL,           NULL, 2),
(15, '图书编辑', 'book:edit',       'button', 2, NULL,           NULL, 3),
(16, '图书删除', 'book:delete',     'button', 2, NULL,           NULL, 4),
(17, '分类管理', 'category:manage', 'menu', 2, '/book/category', NULL, 5),
-- 读者管理子权限
(18, '读者列表', 'reader:list',     'menu', 3, '/reader/list',   NULL, 1),
(19, '读者新增', 'reader:add',      'button', 3, NULL,           NULL, 2),
(20, '读者编辑', 'reader:edit',     'button', 3, NULL,           NULL, 3),
-- 借阅管理子权限
(21, '借阅记录', 'borrow:list',     'menu', 4, '/borrow/list',   NULL, 1),
(22, '借书操作', 'borrow:add',      'button', 4, NULL,           NULL, 2),
(23, '还书操作', 'borrow:return',   'button', 4, NULL,           NULL, 3),
(24, '续借操作', 'borrow:renew',    'button', 4, NULL,           NULL, 4),
-- 公告管理子权限
(25, '公告列表', 'notice:list',     'menu', 5, '/notice/list',   NULL, 1),
(26, '公告发布', 'notice:add',      'button', 5, NULL,           NULL, 2);

-- 为角色分配权限
-- 超级管理员：所有权限
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT 1, id FROM sys_permission;

-- 图书管理员：图书、读者、借阅、公告、统计
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
(2, 2),(2,3),(2,4),(2,5),(2,6),
(2,13),(2,14),(2,15),(2,16),(2,17),
(2,18),(2,19),(2,20),
(2,21),(2,22),(2,23),(2,24),
(2,25),(2,26);

-- 普通读者：只查看
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
(3,13),(3,18);

-- 插入默认管理员用户（密码: admin123，BCrypt加密）
INSERT INTO sys_user (id, username, password, real_name, phone, email, status) VALUES
(1, 'admin', '$2b$10$oIxB6jQeeVDmDQfwPmfBZeUO17Q76gcunMulZ/Jhp/AMoHM1XdFDu', '系统管理员', '13800138000', 'admin@library.com', 1),
(2, 'libadmin', '$2b$10$oIxB6jQeeVDmDQfwPmfBZeUO17Q76gcunMulZ/Jhp/AMoHM1XdFDu', '图书管理员', '13800138001', 'lib@library.com', 1);

-- 分配用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin -> 超级管理员
(2, 2);  -- libadmin -> 图书管理员

-- 插入示例图书分类
INSERT INTO book_category (id, category_name, parent_id, sort_order) VALUES
(1, '计算机科学', 0, 1),
(2, '文学小说',   0, 2),
(3, '历史地理',   0, 3),
(4, '科学技术',   0, 4),
(5, '哲学心理',   0, 5),
(6, '经济管理',   0, 6),
(7, '编程语言',   1, 1),
(8, '数据库',     1, 2),
(9, '人工智能',   1, 3),
(10,'中国文学',  2, 1),
(11,'外国文学',  2, 2);

-- 插入示例图书
INSERT INTO book (isbn, title, author, publisher, publish_date, category_id, description, total_copies, available_copies, location, borrow_count) VALUES
('978-7-111-70001-1', 'Java编程思想（第5版）', 'Bruce Eckel', '机械工业出版社', '2023-01-15', 7, 'Java语言经典著作，全面讲解Java编程的核心思想与最佳实践。', 5, 5, 'A区-1排-1号', 128),
('978-7-302-60002-2', 'Spring Boot实战', 'Craig Walls', '清华大学出版社', '2023-03-20', 7, '深入浅出地讲解Spring Boot框架的核心原理与实战应用。', 3, 2, 'A区-1排-2号', 95),
('978-7-115-60003-3', 'MySQL技术内幕', '姜承尧', '人民邮电出版社', '2023-06-10', 8, '深入分析MySQL数据库的内部实现机制和优化技巧。', 3, 3, 'A区-1排-3号', 67),
('978-7-121-60004-4', '深度学习入门', '斋藤康毅', '电子工业出版社', '2022-09-01', 9, '用通俗易懂的方式讲解深度学习的数学原理与Python实现。', 4, 4, 'A区-2排-1号', 156),
('978-7-02-60005-5', '活着', '余华', '作家出版社', '2018-05-01', 10, '讲述了农村人福贵悲惨的人生遭遇，展现生命的韧性。', 6, 5, 'B区-1排-1号', 234),
('978-7-5321-60006-6', '百年孤独', '加西亚·马尔克斯', '上海译文出版社', '2019-02-15', 11, '魔幻现实主义代表作，讲述布恩迪亚家族七代人的传奇故事。', 4, 4, 'B区-1排-2号', 189),
('978-7-111-60007-7', '算法导论（第4版）', 'Thomas H. Cormen', '机械工业出版社', '2023-08-01', 7, '计算机算法领域的经典教材，涵盖各种算法的设计与分析。', 2, 1, 'A区-2排-3号', 78),
('978-7-302-60008-8', 'Redis深度历险', '钱文品', '清华大学出版社', '2022-12-01', 8, '从源码角度深度剖析Redis的核心数据结构与实现原理。', 3, 3, 'A区-3排-1号', 45),
('978-7-115-60009-9', '人月神话', 'Frederick P. Brooks', '人民邮电出版社', '2021-07-15', 6, '软件工程领域的经典之作，探讨大型项目管理与团队协作。', 3, 3, 'C区-1排-1号', 112),
('978-7-121-60010-0', '深入理解计算机系统', 'Randal E. Bryant', '电子工业出版社', '2023-01-20', 4, '从程序员视角深入理解计算机系统的各个组成部分。', 2, 2, 'A区-4排-1号', 56);

-- 插入示例读者
INSERT INTO reader (reader_no, real_name, gender, id_card, phone, email, address, reader_type, max_borrow_count, max_borrow_days) VALUES
('R2024001', '张三', 1, '320105200001011234', '13912345678', 'zhangsan@qq.com',      '南京市鼓楼区汉口路22号',    1, 5,  30),
('R2024002', '李四', 2, '320106199902022345', '13898765432', 'lisi@163.com',          '上海市浦东新区张江高科技园', 2, 10, 60),
('R2024003', '王五', 1, '320107200103033456', '13776543210', 'wangwu@gmail.com',      '北京市海淀区中关村南大街5号',1, 5,  30),
('R2024004', '赵六', 2, '320108198804044567', '13654321098', 'zhaoliu@outlook.com',   '杭州市西湖区浙大路38号',    1, 5,  30);

-- 插入示例借阅记录
INSERT INTO borrow_record (reader_id, book_id, borrow_date, due_date, return_date, status, renew_count) VALUES
(1, 1, '2024-10-15 09:30:00', '2024-11-14 09:30:00', '2024-11-10 14:20:00', 2, 0),
(1, 5, '2024-11-01 10:00:00', '2024-12-01 10:00:00', NULL, 1, 1),
(2, 2, '2024-11-05 15:00:00', '2024-12-05 15:00:00', '2024-12-01 11:00:00', 2, 0),
(2, 3, '2024-11-20 08:30:00', '2024-12-20 08:30:00', NULL, 1, 0),
(3, 4, '2024-10-20 13:00:00', '2024-11-19 13:00:00', NULL, 3, 0),
(4, 6, '2024-12-01 16:00:00', '2024-12-31 16:00:00', NULL, 1, 0);

-- 插入示例公告
INSERT INTO notice (title, content, notice_type, publisher_id, status) VALUES
('图书馆寒假开放时间调整通知', '寒假期间（1月20日-2月20日），图书馆开放时间调整为每日9:00-17:00，周末正常开放。', 1, 1, 1),
('关于逾期图书催还的通知', '请各位读者及时归还已逾期图书。逾期超过30天将暂停借阅权限。如需续借，请登录系统操作。', 2, 2, 1),
('新书上架通知', '图书馆近期新采购了一批人工智能和大数据方向的新书，欢迎各位读者前来借阅。', 1, 2, 1);

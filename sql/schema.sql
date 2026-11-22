/* ==========================================================================
   智慧园林·树木智能修剪决策平台 —— 建表脚本
   数据库：SQL Server 2019 / 2022
   客户端：SQL Server Management Studio 22
   数据库名：smart_garden
   说明：脚本可重复执行（先 DROP IF EXISTS 再 CREATE）
   ========================================================================== */

/* ---------- 0. 创建数据库 ---------- */
IF DB_ID('smart_garden') IS NULL
BEGIN
    CREATE DATABASE smart_garden;
END
GO

USE smart_garden;
GO

/* ==========================================================================
   1. 组织架构表 sys_dept（部门 / 养护队，树形结构）
   ========================================================================== */
IF OBJECT_ID('dbo.sys_dept', 'U') IS NOT NULL DROP TABLE dbo.sys_dept;
GO
CREATE TABLE dbo.sys_dept
(
    id            BIGINT IDENTITY(1,1) NOT NULL,          -- 主键
    parent_id     BIGINT               NOT NULL DEFAULT 0,-- 上级部门ID，0 表示顶层
    dept_name     NVARCHAR(64)         NOT NULL,          -- 部门/养护队名称
    dept_type     VARCHAR(20)          NOT NULL DEFAULT 'DEPT', -- 类型：DEPT 部门 / FLEET 养护队
    leader        NVARCHAR(32)         NULL,              -- 负责人
    phone         VARCHAR(20)          NULL,              -- 联系电话
    sort          INT                  NOT NULL DEFAULT 0,-- 排序号
    status        TINYINT              NOT NULL DEFAULT 1,-- 状态：1 正常 / 0 停用
    create_time   DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(), -- 创建时间
    update_time   DATETIME2(0)         NULL,              -- 更新时间
    CONSTRAINT pk_sys_dept PRIMARY KEY (id)
);
GO
CREATE INDEX idx_dept_parent ON dbo.sys_dept(parent_id);
GO

/* ==========================================================================
   2. 客户主体表 sys_client（物业 / 果园等客户实体）
   ========================================================================== */
IF OBJECT_ID('dbo.sys_client', 'U') IS NOT NULL DROP TABLE dbo.sys_client;
GO
CREATE TABLE dbo.sys_client
(
    id            BIGINT IDENTITY(1,1) NOT NULL,           -- 主键
    client_name   NVARCHAR(100)        NOT NULL,           -- 客户名称（物业/果园）
    contact_name  NVARCHAR(50)         NULL,               -- 联系人
    contact_phone VARCHAR(20)          NULL,               -- 联系电话
    address       NVARCHAR(200)        NULL,               -- 地址
    user_id       BIGINT               NULL,               -- 关联的登录账号ID（sys_user.id）
    status        TINYINT              NOT NULL DEFAULT 1, -- 状态：1 正常 / 0 停用
    create_time   DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    update_time   DATETIME2(0)         NULL,
    CONSTRAINT pk_sys_client PRIMARY KEY (id)
);
GO

/* ==========================================================================
   3. 账号表 sys_user
   ========================================================================== */
IF OBJECT_ID('dbo.sys_user', 'U') IS NOT NULL DROP TABLE dbo.sys_user;
GO
CREATE TABLE dbo.sys_user
(
    id            BIGINT IDENTITY(1,1) NOT NULL,           -- 主键
    username      VARCHAR(50)          NOT NULL,           -- 登录账号
    password      VARCHAR(100)         NOT NULL,           -- 密码（BCrypt 密文）
    real_name     NVARCHAR(50)         NOT NULL,           -- 真实姓名
    role          VARCHAR(20)          NOT NULL,           -- 角色：ADMIN 管理员 / WORKER 养护人员 / CLIENT 客户
    phone         VARCHAR(20)          NULL,               -- 手机号
    email         VARCHAR(100)         NULL,               -- 邮箱
    avatar        VARCHAR(255)         NULL,               -- 头像地址
    dept_id       BIGINT               NULL,               -- 所属部门/养护队ID（养护人员使用）
    client_id     BIGINT               NULL,               -- 所属客户ID（客户账号使用）
    status        TINYINT              NOT NULL DEFAULT 1, -- 状态：1 启用 / 0 停用
    create_time   DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    update_time   DATETIME2(0)         NULL,
    CONSTRAINT pk_sys_user PRIMARY KEY (id),
    CONSTRAINT uk_user_username UNIQUE (username)
);
GO
CREATE INDEX idx_user_dept   ON dbo.sys_user(dept_id);
CREATE INDEX idx_user_client ON dbo.sys_user(client_id);
GO

/* ==========================================================================
   4. 树木档案表 tree
   ========================================================================== */
IF OBJECT_ID('dbo.tree', 'U') IS NOT NULL DROP TABLE dbo.tree;
GO
CREATE TABLE dbo.tree
(
    id              BIGINT IDENTITY(1,1) NOT NULL,           -- 主键
    tree_code       VARCHAR(50)          NOT NULL,           -- 树木编号（一树一码）
    species         NVARCHAR(50)         NOT NULL,           -- 树种
    variety         NVARCHAR(50)         NULL,               -- 品种
    area            NVARCHAR(50)         NOT NULL,           -- 所属区域
    location        NVARCHAR(200)        NOT NULL,           -- 具体位置
    longitude       DECIMAL(10,6)        NULL,               -- 经度
    latitude        DECIMAL(10,6)        NULL,               -- 纬度
    tree_age        INT                  NULL,               -- 树龄（年）
    plant_time      DATE                 NULL,               -- 栽种时间
    height          DECIMAL(6,2)         NULL,               -- 树高（米）
    crown_diameter  DECIMAL(6,2)         NULL,               -- 冠幅（米）
    health_status   VARCHAR(20)          NOT NULL DEFAULT 'HEALTHY', -- 健康状态：HEALTHY/SUB_HEALTHY/SICK/DEAD
    client_id       BIGINT               NOT NULL,           -- 所属客户ID
    dept_id         BIGINT               NULL,               -- 归属养护队ID
    image_url       VARCHAR(500)         NULL,               -- 树木照片
    remark          NVARCHAR(500)        NULL,               -- 备注
    create_time     DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    update_time     DATETIME2(0)         NULL,
    CONSTRAINT pk_tree PRIMARY KEY (id),
    CONSTRAINT uk_tree_code UNIQUE (tree_code)
);
GO
CREATE INDEX idx_tree_client ON dbo.tree(client_id);
CREATE INDEX idx_tree_area   ON dbo.tree(area);
CREATE INDEX idx_tree_dept   ON dbo.tree(dept_id);
GO

/* ==========================================================================
   5. 修剪需求表 trim_request（客户发起）
   ========================================================================== */
IF OBJECT_ID('dbo.trim_request', 'U') IS NOT NULL DROP TABLE dbo.trim_request;
GO
CREATE TABLE dbo.trim_request
(
    id            BIGINT IDENTITY(1,1) NOT NULL,             -- 主键
    request_no    VARCHAR(50)          NOT NULL,             -- 需求单号
    client_id     BIGINT               NOT NULL,             -- 发起客户ID
    tree_id       BIGINT               NOT NULL,             -- 需修剪的树木ID
    trim_type     VARCHAR(20)          NULL,                 -- 期望修剪类型
    appoint_time  DATETIME2(0)         NOT NULL,             -- 客户预约时间
    location      NVARCHAR(200)        NOT NULL,             -- 修剪地点
    remark        NVARCHAR(500)        NULL,                 -- 备注
    status        VARCHAR(20)          NOT NULL DEFAULT 'PENDING', -- PENDING/AUDITED/DISPATCHED/COMPLETED/REJECTED/CANCELLED
    auditor_id    BIGINT               NULL,                 -- 审核人ID
    audit_remark  NVARCHAR(500)        NULL,                 -- 审核意见
    audit_time    DATETIME2(0)         NULL,                 -- 审核时间
    create_time   DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    update_time   DATETIME2(0)         NULL,
    CONSTRAINT pk_trim_request PRIMARY KEY (id),
    CONSTRAINT uk_request_no UNIQUE (request_no)
);
GO
CREATE INDEX idx_request_client ON dbo.trim_request(client_id);
CREATE INDEX idx_request_status ON dbo.trim_request(status);
GO

/* ==========================================================================
   6. 修剪任务表 trim_task
   ========================================================================== */
IF OBJECT_ID('dbo.trim_task', 'U') IS NOT NULL DROP TABLE dbo.trim_task;
GO
CREATE TABLE dbo.trim_task
(
    id            BIGINT IDENTITY(1,1) NOT NULL,             -- 主键
    task_no       VARCHAR(50)          NOT NULL,             -- 任务单号
    request_id    BIGINT               NULL,                 -- 来源需求ID（管理员直接派单时为空）
    tree_id       BIGINT               NOT NULL,             -- 树木ID
    worker_id     BIGINT               NULL,                 -- 养护人员ID
    client_id     BIGINT               NULL,                 -- 对应客户ID
    trim_type     VARCHAR(20)          NULL,                 -- 修剪类型
    plan_time     DATETIME2(0)         NULL,                 -- 计划修剪时间
    finish_time   DATETIME2(0)         NULL,                 -- 实际完成时间
    status        VARCHAR(20)          NOT NULL DEFAULT 'PENDING', -- PENDING/ACCEPTED/PROCESSING/SUBMITTED/DONE
    priority      TINYINT              NOT NULL DEFAULT 1,   -- 优先级 1 普通 / 2 紧急
    description   NVARCHAR(500)        NULL,                 -- 任务说明
    create_time   DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    update_time   DATETIME2(0)         NULL,
    CONSTRAINT pk_trim_task PRIMARY KEY (id),
    CONSTRAINT uk_task_no UNIQUE (task_no)
);
GO
CREATE INDEX idx_task_worker ON dbo.trim_task(worker_id);
CREATE INDEX idx_task_status ON dbo.trim_task(status);
CREATE INDEX idx_task_tree   ON dbo.trim_task(tree_id);
GO

/* ==========================================================================
   7. 修剪记录表 trim_record
   ========================================================================== */
IF OBJECT_ID('dbo.trim_record', 'U') IS NOT NULL DROP TABLE dbo.trim_record;
GO
CREATE TABLE dbo.trim_record
(
    id                BIGINT IDENTITY(1,1) NOT NULL,         -- 主键
    task_id           BIGINT               NOT NULL,         -- 关联任务ID
    tree_id           BIGINT               NOT NULL,         -- 树木ID
    worker_id         BIGINT               NOT NULL,         -- 养护人员ID
    client_id         BIGINT               NULL,             -- 客户ID
    trim_type         VARCHAR(20)          NULL,             -- 实际修剪类型
    actual_trim_time  DATETIME2(0)         NOT NULL,         -- 实际修剪时间
    cut_branch_count  INT                  NOT NULL DEFAULT 0, -- 剪除枝条数
    keep_branch_count INT                  NOT NULL DEFAULT 0, -- 保留枝条数
    work_content      NVARCHAR(1000)       NULL,             -- 实际修剪情况说明
    ai_suggestion     NVARCHAR(2000)       NULL,             -- 采纳的 AI 建议文本
    score             INT                  NULL,             -- 客户评分 1~5
    comment           NVARCHAR(500)        NULL,             -- 客户评语
    evaluate_time     DATETIME2(0)         NULL,             -- 评价时间
    status            VARCHAR(20)          NOT NULL DEFAULT 'SUBMITTED', -- SUBMITTED 待评价 / EVALUATED 已评价
    create_time       DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    update_time       DATETIME2(0)         NULL,
    CONSTRAINT pk_trim_record PRIMARY KEY (id)
);
GO
CREATE INDEX idx_record_task   ON dbo.trim_record(task_id);
CREATE INDEX idx_record_worker ON dbo.trim_record(worker_id);
CREATE INDEX idx_record_client ON dbo.trim_record(client_id);
GO

/* ==========================================================================
   8. 修剪记录图片表 trim_record_image
   ========================================================================== */
IF OBJECT_ID('dbo.trim_record_image', 'U') IS NOT NULL DROP TABLE dbo.trim_record_image;
GO
CREATE TABLE dbo.trim_record_image
(
    id          BIGINT IDENTITY(1,1) NOT NULL,               -- 主键
    record_id   BIGINT               NOT NULL,               -- 关联记录ID
    task_id     BIGINT               NULL,                   -- 关联任务ID（冗余，便于按任务查图）
    image_type  VARCHAR(20)          NOT NULL DEFAULT 'SCENE', -- SCENE/BEFORE/AFTER/AI_MARKED
    image_url   VARCHAR(500)         NOT NULL,               -- 图片地址
    create_time DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT pk_trim_record_image PRIMARY KEY (id)
);
GO
CREATE INDEX idx_image_record ON dbo.trim_record_image(record_id);
GO

/* ==========================================================================
   9. AI 分析结果表 ai_analysis
   ========================================================================== */
IF OBJECT_ID('dbo.ai_analysis', 'U') IS NOT NULL DROP TABLE dbo.ai_analysis;
GO
CREATE TABLE dbo.ai_analysis
(
    id               BIGINT IDENTITY(1,1) NOT NULL,          -- 主键
    task_id          BIGINT               NULL,              -- 关联任务ID
    tree_id          BIGINT               NULL,              -- 关联树木ID
    species          NVARCHAR(50)         NULL,              -- 树种（识别时使用的先验信息）
    image_url        VARCHAR(500)         NOT NULL,          -- 原始现场照片
    marked_image_url VARCHAR(500)         NULL,              -- 标注合成图（红线/绿线已绘制）
    cut_branches     NVARCHAR(MAX)        NULL,              -- 建议剪除枝条 JSON：[{x1,y1,x2,y2,label}]
    keep_branches    NVARCHAR(MAX)        NULL,              -- 建议保留枝条 JSON
    suggestion       NVARCHAR(2000)       NULL,              -- 修剪建议文本
    confidence       DECIMAL(5,2)         NULL,              -- 置信度 0~1
    is_mock          TINYINT              NOT NULL DEFAULT 1,-- 是否 Mock 数据：1 是 / 0 否
    model_version    VARCHAR(50)          NULL,              -- 模型版本标识
    create_time      DATETIME2(0)         NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT pk_ai_analysis PRIMARY KEY (id)
);
GO
CREATE INDEX idx_ai_task ON dbo.ai_analysis(task_id);
GO

PRINT '============================================';
PRINT ' smart_garden 建表完成，共 9 张表';
PRINT ' 接下来请执行 init_data.sql 导入初始化数据';
PRINT '============================================';
GO

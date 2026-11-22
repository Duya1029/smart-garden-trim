/* ==========================================================================
   智慧园林·树木智能修剪决策平台 —— 初始化数据脚本
   依赖：先执行 schema.sql
   说明：
     1. sys_user.password 初始为明文 123456，应用启动时由 PasswordInitRunner
        自动转换为 BCrypt 密文（生产环境请直接在 SSMS 中执行密文 UPDATE）。
     2. 演示图片使用在线占位图服务，如需本地图片请替换为 /upload/... 路径。
   ========================================================================== */

USE smart_garden;
GO

/* ---------- 清空数据 ---------- */
DELETE FROM dbo.ai_analysis;
DELETE FROM dbo.trim_record_image;
DELETE FROM dbo.trim_record;
DELETE FROM dbo.trim_task;
DELETE FROM dbo.trim_request;
DELETE FROM dbo.tree;
DELETE FROM dbo.sys_user;
DELETE FROM dbo.sys_client;
DELETE FROM dbo.sys_dept;
GO

/* ==========================================================================
   1. 组织架构 sys_dept
   ========================================================================== */
SET IDENTITY_INSERT dbo.sys_dept ON;
INSERT INTO dbo.sys_dept (id, parent_id, dept_name, dept_type, leader, phone, sort, status, create_time) VALUES
(1, 0, N'智慧园林集团',   'DEPT',  N'李总',  '13900000000', 1, 1, '2026-01-01 09:00:00'),
(2, 1, N'城东养护中心',   'DEPT',  N'王主任', '13900000002', 1, 1, '2026-01-01 09:00:00'),
(3, 2, N'城东养护一队',   'FLEET', N'张队长', '13900000003', 1, 1, '2026-01-01 09:00:00'),
(4, 1, N'城西养护队',     'FLEET', N'赵队长', '13900000004', 2, 1, '2026-01-01 09:00:00'),
(5, 1, N'果园技术服务队', 'FLEET', N'周队长', '13900000005', 3, 1, '2026-01-01 09:00:00');
SET IDENTITY_INSERT dbo.sys_dept OFF;
GO

/* ==========================================================================
   2. 客户主体 sys_client
   ========================================================================== */
SET IDENTITY_INSERT dbo.sys_client ON;
INSERT INTO dbo.sys_client (id, client_name, contact_name, contact_phone, address, user_id, status) VALUES
(1, N'城东物业管理有限公司', N'陈经理', '13811110001', N'城东大道 1 号',       6, 1),
(2, N'城南果园种植合作社',   N'刘场长', '13811110002', N'城南镇果园路 88 号',  7, 1),
(3, N'城北物业管理处',       N'孙主管', '13811110003', N'城北新区园林路 6 号', 8, 1);
SET IDENTITY_INSERT dbo.sys_client OFF;
GO

/* ==========================================================================
   3. 账号 sys_user（初始密码均为 123456）
   ========================================================================== */
SET IDENTITY_INSERT dbo.sys_user ON;
INSERT INTO dbo.sys_user (id, username, password, real_name, role, phone, email, dept_id, client_id, status) VALUES
(1, 'admin',    '123456', N'系统管理员', 'ADMIN',  '13700000001', 'admin@smartgarden.com',  1, NULL, 1),
(2, 'worker01', '123456', N'张师傅',     'WORKER', '13800000001', 'zhang@smartgarden.com',  3, NULL, 1),
(3, 'worker02', '123456', N'李工',       'WORKER', '13800000002', 'li@smartgarden.com',     3, NULL, 1),
(4, 'worker03', '123456', N'赵师傅',     'WORKER', '13800000003', 'zhao@smartgarden.com',   4, NULL, 1),
(5, 'worker04', '123456', N'周师傅',     'WORKER', '13800000004', 'zhou@smartgarden.com',   5, NULL, 1),
(6, 'client01', '123456', N'陈经理',     'CLIENT', '13811110001', 'chen@demo.com',          NULL, 1, 1),
(7, 'client02', '123456', N'刘场长',     'CLIENT', '13811110002', 'liu@demo.com',           NULL, 2, 1),
(8, 'client03', '123456', N'孙主管',     'CLIENT', '13811110003', 'sun@demo.com',           NULL, 3, 1);
SET IDENTITY_INSERT dbo.sys_user OFF;
GO

/* ==========================================================================
   4. 树木档案 tree
   ========================================================================== */
SET IDENTITY_INSERT dbo.tree ON;
INSERT INTO dbo.tree
(id, tree_code, species, variety, area, location, longitude, latitude, tree_age, plant_time, height, crown_diameter, health_status, client_id, dept_id, image_url, remark) VALUES
( 1, 'TREE-20260001', N'香樟',   N'大叶樟', N'城东片区', N'城东大道 128 号绿化带',   116.397428, 39.909230, 12, '2014-03-12', 8.50, 6.20, 'HEALTHY',     1, 3, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=camphor%20tree%20in%20urban%20landscaping%2C%20daylight%2C%20realistic%20photo&image_size=landscape_4_3', N'行道树，注意避让架空线'),
( 2, 'TREE-20260002', N'香樟',   N'小叶樟', N'城东片区', N'城东大道 156 号绿化带',   116.398100, 39.909800, 10, '2016-03-20', 7.20, 5.40, 'HEALTHY',     1, 3, NULL, N'行道树'),
( 3, 'TREE-20260003', N'桂花',   N'金桂',   N'城东片区', N'城东公园北门广场',        116.399000, 39.910500, 8,  '2018-04-01', 4.60, 4.10, 'HEALTHY',     1, 3, NULL, N'观赏树，开花期需谨慎修剪'),
( 4, 'TREE-20260004', N'法桐',   N'悬铃木', N'城东片区', N'城东大道 210 号人行道',   116.399700, 39.911200, 15, '2011-03-05', 11.30, 8.60, 'SUB_HEALTHY', 1, 3, NULL, N'树冠过密，需疏枝'),
( 5, 'TREE-20260005', N'银杏',   N'银杏',   N'城东片区', N'城东大道 266 号庭院',     116.400400, 39.911900, 20, '2006-03-18', 13.50, 7.80, 'HEALTHY',     1, 3, NULL, N'古树名木，修剪需备案'),
( 6, 'TREE-20260006', N'樱花',   N'染井吉野', N'城东片区', N'城东公园樱花大道',      116.401100, 39.912600, 7,  '2019-03-25', 5.20, 4.80, 'HEALTHY',     1, 3, NULL, N'花后修剪'),
( 7, 'TREE-20260007', N'紫薇',   N'紫薇',   N'城东片区', N'城东公园东侧步道',        116.401800, 39.913300, 6,  '2020-04-10', 3.80, 3.20, 'SICK',        1, 3, NULL, N'有蚧壳虫，需清除病虫枝'),
( 8, 'TREE-20260008', N'香樟',   N'大叶樟', N'城南片区', N'城南路 18 号绿化带',      116.392000, 39.901000, 9,  '2017-03-15', 6.80, 5.10, 'HEALTHY',     1, 3, NULL, NULL),
( 9, 'TREE-20260009', N'桂花',   N'银桂',   N'城南片区', N'城南小区 3 号楼前',       116.392700, 39.901700, 11, '2015-04-02', 5.60, 4.50, 'HEALTHY',     1, 3, NULL, NULL),
(10, 'TREE-20260010', N'法桐',   N'悬铃木', N'城南片区', N'城南路 56 号人行道',      116.393400, 39.902400, 14, '2012-03-08', 10.20, 7.60, 'SUB_HEALTHY', 1, 3, NULL, N'枯枝较多'),
(11, 'TREE-20260011', N'银杏',   N'银杏',   N'城南片区', N'城南广场中央',           116.394100, 39.903100, 18, '2008-03-22', 12.10, 6.90, 'HEALTHY',     1, 3, NULL, NULL),
(12, 'TREE-20260012', N'樱花',   N'晚樱',   N'城南片区', N'城南广场西侧',           116.394800, 39.903800, 8,  '2018-03-28', 4.90, 4.30, 'HEALTHY',     1, 3, NULL, NULL),
(13, 'TREE-20260013', N'香樟',   N'大叶樟', N'城西片区', N'城西路 66 号绿化带',      116.388000, 39.912000, 13, '2013-03-10', 8.90, 6.50, 'HEALTHY',     1, 4, NULL, NULL),
(14, 'TREE-20260014', N'桂花',   N'丹桂',   N'城西片区', N'城西公园南门',           116.388700, 39.912700, 9,  '2017-04-05', 5.10, 4.20, 'HEALTHY',     1, 4, NULL, NULL),
(15, 'TREE-20260015', N'法桐',   N'悬铃木', N'城西片区', N'城西路 118 号人行道',     116.389400, 39.913400, 16, '2010-03-12', 12.40, 9.10, 'SUB_HEALTHY', 1, 4, NULL, N'需要重剪'),
(16, 'TREE-20260016', N'紫薇',   N'紫薇',   N'城西片区', N'城西公园花境',           116.390100, 39.914100, 5,  '2021-04-12', 3.20, 2.80, 'HEALTHY',     1, 4, NULL, NULL),
(17, 'TREE-20260017', N'香樟',   N'小叶樟', N'城北片区', N'城北新区园林路 12 号',   116.405000, 39.920000, 11, '2015-03-18', 7.60, 5.60, 'HEALTHY',     3, 4, NULL, NULL),
(18, 'TREE-20260018', N'法桐',   N'悬铃木', N'城北片区', N'城北新区园林路 44 号',   116.405700, 39.920700, 13, '2013-03-20', 10.80, 8.20, 'HEALTHY',     3, 4, NULL, NULL),
(19, 'TREE-20260019', N'银杏',   N'银杏',   N'城北片区', N'城北新区广场东侧',       116.406400, 39.921400, 17, '2009-03-25', 11.60, 6.40, 'HEALTHY',     3, 4, NULL, NULL),
(20, 'TREE-20260020', N'桂花',   N'金桂',   N'城北片区', N'城北新区广场西侧',       116.407100, 39.922100, 10, '2016-04-08', 5.40, 4.60, 'HEALTHY',     3, 4, NULL, NULL),
(21, 'TREE-20260021', N'苹果树', N'红富士', N'果园基地', N'果园基地 A 区 3 行',      116.420000, 39.880000, 9,  '2017-03-01', 3.60, 3.10, 'HEALTHY',     2, 5, NULL, N'结果树，以通风透光为主'),
(22, 'TREE-20260022', N'苹果树', N'红富士', N'果园基地', N'果园基地 A 区 5 行',      116.420700, 39.880700, 9,  '2017-03-01', 3.70, 3.20, 'SUB_HEALTHY', 2, 5, NULL, N'内膛枝过密'),
(23, 'TREE-20260023', N'桃树',   N'水蜜桃', N'果园基地', N'果园基地 B 区 2 行',      116.421400, 39.881400, 6,  '2020-03-05', 2.80, 2.60, 'HEALTHY',     2, 5, NULL, N'幼树整形'),
(24, 'TREE-20260024', N'桃树',   N'水蜜桃', N'果园基地', N'果园基地 B 区 4 行',      116.422100, 39.882100, 7,  '2019-03-08', 3.10, 2.90, 'SICK',        2, 5, NULL, N'流胶病，需清除病枝');
SET IDENTITY_INSERT dbo.tree OFF;
GO

/* ==========================================================================
   5. 修剪需求 trim_request
   ========================================================================== */
SET IDENTITY_INSERT dbo.trim_request ON;
INSERT INTO dbo.trim_request
(id, request_no, client_id, tree_id, trim_type, appoint_time, location, remark, status, auditor_id, audit_remark, audit_time, create_time) VALUES
(1, 'TR202609010001', 1, 4,  '整形修剪', '2026-09-05 09:00:00', N'城东大道 210 号人行道', N'树冠太密，遮挡路灯',       'COMPLETED',  1, N'同意，安排城东一队处理', '2026-09-01 14:00:00', '2026-09-01 10:20:00'),
(2, 'TR202609020001', 1, 10, '枯枝清理', '2026-09-10 08:30:00', N'城南路 56 号人行道',    N'有枯枝掉落风险',           'COMPLETED',  1, N'同意',                   '2026-09-02 09:10:00', '2026-09-02 08:40:00'),
(3, 'TR202609100001', 3, 18, '整形修剪', '2026-09-15 09:00:00', N'城北新区园林路 44 号', N'业主投诉遮挡采光',         'COMPLETED',  1, N'同意，安排城西队',        '2026-09-10 15:00:00', '2026-09-10 11:00:00'),
(4, 'TR202609150001', 2, 22, '疏枝',     '2026-09-20 08:00:00', N'果园基地 A 区 5 行',    N'内膛枝过密影响着色',       'COMPLETED',  1, N'同意，安排果园队',        '2026-09-15 16:20:00', '2026-09-15 09:30:00'),
(5, 'TR202610080001', 1, 7,  '病虫枝清除', '2026-10-20 09:00:00', N'城东公园东侧步道',    N'叶子发黄，疑似虫害',       'AUDITED',    1, N'已审核，安排张师傅处理',   '2026-10-08 10:00:00', '2026-10-08 09:00:00'),
(6, 'TR202610150001', 3, 17, '整形修剪', '2026-10-25 09:30:00', N'城北新区园林路 12 号', N'希望修剪成球形',           'PENDING',    NULL, NULL,                   NULL,                  '2026-10-15 14:20:00');
SET IDENTITY_INSERT dbo.trim_request OFF;
GO

/* ==========================================================================
   6. 修剪任务 trim_task
   ========================================================================== */
SET IDENTITY_INSERT dbo.trim_task ON;
INSERT INTO dbo.trim_task
(id, task_no, request_id, tree_id, worker_id, client_id, trim_type, plan_time, finish_time, status, priority, description, create_time) VALUES
( 1, 'TK202609010001', 1, 4,  2, 1, '整形修剪',   '2026-09-05 09:00:00', '2026-09-05 10:30:00', 'DONE',       1, N'疏除过密枝条，提升通透性', '2026-09-01 14:05:00'),
( 2, 'TK202609020001', 2, 10, 2, 1, '枯枝清理',   '2026-09-10 08:30:00', '2026-09-10 09:40:00', 'DONE',       2, N'清除所有枯死枝，注意行人安全', '2026-09-02 09:15:00'),
( 3, 'TK202609100001', 3, 18, 4, 3, '整形修剪',   '2026-09-15 09:00:00', '2026-09-15 11:00:00', 'DONE',       1, N'降低树冠高度，减少遮光',   '2026-09-10 15:05:00'),
( 4, 'TK202609150001', 4, 22, 5, 2, '疏枝',       '2026-09-20 08:00:00', '2026-09-20 09:20:00', 'DONE',       1, N'疏除内膛枝，改善光照',     '2026-09-15 16:25:00'),
( 5, 'TK202609200001', NULL, 1, 2, 1, '整形修剪', '2026-09-25 09:00:00', '2026-09-25 10:10:00', 'DONE',       1, N'行道树常规整形',           '2026-09-20 09:00:00'),
( 6, 'TK202609260001', NULL, 3, 3, 1, '整形修剪', '2026-09-28 09:00:00', '2026-09-28 10:00:00', 'DONE',       1, N'桂花花前整形',             '2026-09-26 09:00:00'),
( 7, 'TK202610010001', NULL, 15, 4, 1, '重剪',     '2026-10-05 08:30:00', '2026-10-05 11:30:00', 'SUBMITTED',  2, N'法桐重剪，控制树冠',       '2026-10-01 09:00:00'),
( 8, 'TK202610080001', 5, 7,  2, 1, '病虫枝清除', '2026-10-20 09:00:00', NULL,                  'PROCESSING', 2, N'清除病枝并喷药',           '2026-10-08 10:05:00'),
( 9, 'TK202610090001', NULL, 24, 5, 2, '病虫枝清除', '2026-10-15 08:30:00', NULL,                 'ACCEPTED',   2, N'桃树流胶病枝清除',         '2026-10-09 09:00:00'),
(10, 'TK202610120001', NULL, 5,  3, 1, '整形修剪', '2026-10-22 09:00:00', NULL,                  'PENDING',    1, N'银杏古树轻剪，需备案',     '2026-10-12 09:00:00'),
(11, 'TK202610130001', NULL, 14, 4, 1, '整形修剪', '2026-10-23 09:00:00', NULL,                  'PENDING',    1, N'丹桂整形',                 '2026-10-13 09:00:00'),
(12, 'TK202610140001', NULL, 21, 5, 2, '疏枝',     '2026-10-24 08:30:00', NULL,                  'PENDING',    1, N'苹果树疏枝，改善着色',     '2026-10-14 09:00:00');
SET IDENTITY_INSERT dbo.trim_task OFF;
GO

/* ==========================================================================
   7. 修剪记录 trim_record（记录 1~6 已被客户评价，记录 7 待评价）
   ========================================================================== */
SET IDENTITY_INSERT dbo.trim_record ON;
INSERT INTO dbo.trim_record
(id, task_id, tree_id, worker_id, client_id, trim_type, actual_trim_time, cut_branch_count, keep_branch_count, work_content, ai_suggestion, score, comment, evaluate_time, status, create_time) VALUES
(1, 1, 4,  2, 1, '整形修剪',   '2026-09-05 10:30:00', 18, 32, N'疏除内膛过密枝 12 根、下垂枝 6 根，树冠通透性明显改善，剪口涂抹愈合剂。', N'建议疏除交叉重叠的内膛枝，保留主枝延长枝。', 5, N'修剪到位，路灯光线明显好转', '2026-09-06 10:00:00', 'EVALUATED', '2026-09-05 10:40:00'),
(2, 2, 10, 2, 1, '枯枝清理',   '2026-09-10 09:40:00', 9,  28, N'清除枯死枝 9 根，其中 3 根直径超过 5cm，作业时设置警戒区域确保行人安全。', N'枯枝全部清除，注意剪口平滑。',              4, N'及时清理，消除了安全隐患', '2026-09-11 09:20:00', 'EVALUATED', '2026-09-10 09:50:00'),
(3, 3, 18, 4, 3, '整形修剪',   '2026-09-15 11:00:00', 22, 40, N'整体降低树冠高度约 1.5 米，疏除外围过密枝，减少对业主采光遮挡。',           N'建议降低树冠高度，疏除外围过密枝。',        5, N'采光改善明显，非常满意',   '2026-09-16 14:00:00', 'EVALUATED', '2026-09-15 11:10:00'),
(4, 4, 22, 5, 2, '疏枝',       '2026-09-20 09:20:00', 26, 45, N'疏除内膛枝 18 根、竞争枝 8 根，改善树冠通风透光条件，利于果实着色。',     N'疏除内膛枝与竞争枝，保留结果母枝。',        5, N'技术专业，果园光照好了很多', '2026-09-21 08:30:00', 'EVALUATED', '2026-09-20 09:30:00'),
(5, 5, 1,  2, 1, '整形修剪',   '2026-09-25 10:10:00', 15, 30, N'行道树常规整形，剪除下垂枝 10 根、平行枝 5 根，保持树冠均匀。',           N'保持自然开心形，剪除下垂与平行枝。',        4, N'整体不错，希望下次注意剪口处理', '2026-09-26 09:00:00', 'EVALUATED', '2026-09-25 10:20:00'),
(6, 6, 3,  3, 1, '整形修剪',   '2026-09-28 10:00:00', 12, 25, N'桂花花前整形，疏除细弱枝、病虫枝，保持内部通风，促进花芽分化。',           N'花前疏除细弱枝，避免重剪影响开花。',        5, N'修剪很专业，期待秋天开花', '2026-09-29 10:30:00', 'EVALUATED', '2026-09-28 10:10:00'),
(7, 7, 15, 4, 1, '重剪',       '2026-10-05 11:30:00', 35, 40, N'法桐重剪，控制树冠高度与冠幅，剪除主枝上多余萌蘖，伤口涂抹愈合剂。',       N'重剪控制冠幅，注意保留主枝骨架。',          NULL, NULL, NULL, 'SUBMITTED', '2026-10-05 11:40:00');
SET IDENTITY_INSERT dbo.trim_record OFF;
GO

/* ==========================================================================
   8. 修剪记录图片 trim_record_image
   ========================================================================== */
SET IDENTITY_INSERT dbo.trim_record_image ON;
INSERT INTO dbo.trim_record_image (id, record_id, task_id, image_type, image_url) VALUES
(1,  1, 1, 'BEFORE',    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=overgrown%20plane%20tree%20with%20dense%20canopy%20on%20city%20sidewalk%2C%20realistic%20photo&image_size=landscape_4_3'),
(2,  1, 1, 'AFTER',     'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=freshly%20pruned%20street%20tree%20with%20open%20canopy%20on%20city%20sidewalk%2C%20realistic%20photo&image_size=landscape_4_3'),
(3,  2, 2, 'SCENE',     'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=gardener%20cutting%20dead%20branches%20of%20a%20street%20tree%20with%20pruning%20saw%2C%20realistic%20photo&image_size=landscape_4_3'),
(4,  3, 3, 'AFTER',     'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=trimmed%20tall%20plane%20tree%20lowered%20canopy%20in%20residential%20area%2C%20realistic%20photo&image_size=landscape_4_3'),
(5,  4, 4, 'SCENE',     'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=apple%20orchard%20tree%20interior%20branch%20thinning%2C%20sunlight%20through%20canopy%2C%20realistic%20photo&image_size=landscape_4_3'),
(6,  5, 5, 'SCENE',     'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=camphor%20tree%20on%20urban%20boulevard%20daylight%2C%20realistic%20photo&image_size=landscape_4_3'),
(7,  6, 6, 'SCENE',     'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=osmanthus%20tree%20in%20city%20park%20with%20green%20leaves%2C%20realistic%20photo&image_size=landscape_4_3'),
(8,  7, 7, 'BEFORE',    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=plane%20tree%20with%20very%20dense%20large%20canopy%20before%20heavy%20pruning%2C%20realistic%20photo&image_size=landscape_4_3'),
(9,  7, 7, 'AFTER',     'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=plane%20tree%20after%20heavy%20pruning%20with%20skeleton%20branches%2C%20realistic%20photo&image_size=landscape_4_3');
SET IDENTITY_INSERT dbo.trim_record_image OFF;
GO

/* ==========================================================================
   9. AI 分析结果 ai_analysis（演示数据，is_mock = 1）
   ========================================================================== */
SET IDENTITY_INSERT dbo.ai_analysis ON;
INSERT INTO dbo.ai_analysis (id, task_id, tree_id, species, image_url, marked_image_url, cut_branches, keep_branches, suggestion, confidence, is_mock, model_version) VALUES
(1, 1, 4, N'法桐',
 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=overgrown%20plane%20tree%20with%20dense%20canopy%20on%20city%20sidewalk%2C%20realistic%20photo&image_size=landscape_4_3',
 NULL,
 N'[{"x1":0.34,"y1":0.52,"x2":0.18,"y2":0.76,"label":"下垂枝"},{"x1":0.62,"y1":0.46,"x2":0.76,"y2":0.71,"label":"交叉枝"},{"x1":0.48,"y1":0.58,"x2":0.40,"y2":0.82,"label":"内膛枝"}]',
 N'[{"x1":0.50,"y1":0.92,"x2":0.47,"y2":0.42,"label":"主干"},{"x1":0.50,"y1":0.62,"x2":0.74,"y2":0.34,"label":"主枝延长枝"},{"x1":0.50,"y1":0.60,"x2":0.26,"y2":0.36,"label":"主枝"}]',
 N'该法桐树冠内膛枝过密，建议：1) 剪除树冠下部 3 根下垂枝，避免遮挡路灯与行人通行；2) 疏除交叉重叠的内膛枝，改善通风透光；3) 保留主枝延长枝与骨架枝，剪口距枝条基部 1-2cm，呈 45° 斜面，并涂抹愈合剂。整体造型建议维持自然开心形。',
 0.87, 1, 'mock-v1.0'),
(2, 4, 22, N'苹果树',
 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=apple%20orchard%20tree%20interior%20branch%20thinning%2C%20sunlight%20through%20canopy%2C%20realistic%20photo&image_size=landscape_4_3',
 NULL,
 N'[{"x1":0.40,"y1":0.50,"x2":0.32,"y2":0.80,"label":"内膛枝"},{"x1":0.58,"y1":0.48,"x2":0.68,"y2":0.78,"label":"竞争枝"}]',
 N'[{"x1":0.50,"y1":0.92,"x2":0.48,"y2":0.40,"label":"主干"},{"x1":0.50,"y1":0.58,"x2":0.72,"y2":0.30,"label":"结果母枝"},{"x1":0.50,"y1":0.58,"x2":0.28,"y2":0.32,"label":"主枝"}]',
 N'该苹果树内膛枝过密、光照不足，建议：1) 疏除内膛交叉枝与竞争枝，改善树冠通风透光条件，促进果实着色；2) 保留结果母枝与主枝延长枝，控制枝条密度；3) 剪口平滑并涂抹愈合剂，避免流胶。以通风透光、稳定结果为核心目标。',
 0.91, 1, 'mock-v1.0');
SET IDENTITY_INSERT dbo.ai_analysis OFF;
GO

/* ---------- 数据校验 ---------- */
SELECT N'组织架构' AS 表名, COUNT(*) AS 记录数 FROM dbo.sys_dept
UNION ALL SELECT N'客户主体', COUNT(*) FROM dbo.sys_client
UNION ALL SELECT N'账号',     COUNT(*) FROM dbo.sys_user
UNION ALL SELECT N'树木档案', COUNT(*) FROM dbo.tree
UNION ALL SELECT N'修剪需求', COUNT(*) FROM dbo.trim_request
UNION ALL SELECT N'修剪任务', COUNT(*) FROM dbo.trim_task
UNION ALL SELECT N'修剪记录', COUNT(*) FROM dbo.trim_record
UNION ALL SELECT N'记录图片', COUNT(*) FROM dbo.trim_record_image
UNION ALL SELECT N'AI分析',   COUNT(*) FROM dbo.ai_analysis;
GO

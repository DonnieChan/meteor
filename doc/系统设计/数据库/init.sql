
use meteor;

INSERT INTO def_file_type(file_type, file_type_desc, file_type_category, depend_flag) VALUES
('DSJdbc', 'jdbc数据源', 'DS', 0),
('DSRedis', 'redis数据源', 'DS', 0),
('ImportKafka', '导入源头数据', 'Import', 1),
('Hive2Cassandra', 'Hive2Cassandra', 'Import', 3),
('Mysql2Cassandra', 'Mysql2Cassandra', 'Import', 3),
('SqlTask', 'sql', 'BuildModel', 2),
('ExportKafka', '导出至kafka', 'Export', 3),
('ExportCassandra', '导出至Cassandra', 'Export', 3),
('Cassandra2Hive','Cassandra2Hive','Export', 3),
('Cron', '定时器', 'Cron', 1),
('Virtual', '虚拟任务', 'Other', 2),
('Dir', '目录', 'Other', 0);



INSERT INTO def_project(project_id, project_name, remarks, is_valid, update_time, create_user, update_user) VALUES
(100, 'root', '', 1, now(), 'sys', 'sys');

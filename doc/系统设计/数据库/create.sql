CREATE DATABASE meteor DEFAULT CHARACTER SET utf8;
use meteor;


/*==============================================================*/
/* Table: def_file_sys                                          */
/*==============================================================*/
create table def_file_sys
(
   file_id              int not null auto_increment comment '文件id',
   parent_file_id       int not null default 0 comment '所属父亲文件id，0表示无父亲文件',
   project_id           int not null comment '所属项目id',
   file_name            varchar(50) not null comment '文件名称',
   file_type            varchar(20) not null comment '文件类型',
   file_body            text comment '文件内容',
   is_dir               tinyint default 1 comment '是否为目录，1是，0否',
   offline_time         datetime not null comment '下线时间',
   contacts             text comment '任务联系人，用于发短信或邮件(请填passport,多个用英文;号分割)',
   remarks              text comment '备注',
   is_valid             tinyint not null default 1 comment '是否还可用，1可用，0已下线不可用',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   update_time          datetime comment '更新时间',
   create_user          varchar(20) comment '创建用户',
   update_user          varchar(20) comment '更新用户',
   primary key (file_id),
   key(parent_file_id),
   key(project_id)
) comment = '文件系统结构信息'
engine = MYISAM
default charset utf8
auto_increment = 100;

/*==============================================================*/
/* Table: def_depend                                            */
/*==============================================================*/
create table def_depend
(
   file_id              int not null comment '文件ID',
   pre_file_id          int not null comment '前置依赖的文件ID',
   project_id           int not null comment '所属项目id',
   remarks              text comment '备注',
   is_valid             tinyint not null default 1 comment '是否还可用，1可用，0已下线不可用',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   update_time          datetime comment '更新时间',
   create_user          varchar(20) comment '创建用户',
   update_user          varchar(20) comment '更新用户',
   primary key (file_id, pre_file_id),
   key(project_id),
   key(pre_file_id)
) comment = '流程依赖关系'
engine = MYISAM
default charset utf8;

/*==============================================================*/
/* Table: def_file_type                                         */
/*==============================================================*/
create table def_file_type
(
   file_type            varchar(50) not null comment '文件类型代码',
   file_type_desc       varchar(50) not null comment '文件类型描述',
   file_type_category   varchar(50) not null comment '文件类型归属',
   depend_flag          int not null comment '是否可以用于建立依赖关系，1可以，0不可以',
   primary key (file_type)
) comment = '文件类型类型'
engine = MYISAM
default charset utf8;

/*==============================================================*/
/* Table: def_project                                           */
/*==============================================================*/
create table def_project
(
   project_id           int not null auto_increment comment '项目id',
   project_name         varchar(50) not null comment '项目名称',
   remarks              text comment '备注',
   is_valid             tinyint not null default 1 comment '是否还可用，1可用，0已下线不可用',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   update_time          datetime comment '更新时间',
   create_user          varchar(20) comment '创建用户',
   update_user          varchar(20) comment '更新用户',
   primary key (project_id)
) comment = '项目信息'
engine = MYISAM
default charset utf8
auto_increment = 100;

/*==============================================================*/
/* Table: instance_flow                                         */
/*==============================================================*/
create table instance_flow
(
   instance_flow_id     varchar(50) not null comment '流程实例id，用UUID表示',
   source_task_id       int not null comment '源头任务ID',
   init_time            datetime comment '初始化时间',
   start_time           datetime comment '开始时间',
   end_time             datetime comment '结束时间',
   status               varchar(50) not null comment '状态',
   log                  text comment '执行日志',
   remarks              text comment '备注',
   is_valid             tinyint not null default 1 comment '是否还可用，1可用，0已下线不可用',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   update_time          datetime comment '更新时间',
   create_user          varchar(20) comment '创建用户',
   update_user          varchar(20) comment '更新用户',
   primary key (instance_flow_id),
   key(source_task_id)
) comment = '流程实例'
engine = MYISAM
default charset utf8;

/*==============================================================*/
/* Table: instance_task                                         */
/*==============================================================*/
create table instance_task
(
   instance_flow_id     varchar(50) not null comment '流程实例id，用UUID表示',
   file_id              int not null comment '文件id',
   file_body            text comment '文件内容',
   ready_time           datetime comment '准备好时间',
   start_time           datetime comment '开始时间',
   end_time             datetime comment '结束时间',
   status               varchar(20) not null comment '状态',
   retried_times        int not null default 0 comment '已重试次数',
   pool_active_count	int not null default 0 comment '同时运行中的实例数',
   pool_queue_size		int not null default 0 comment '等待中的实例数',
   log                  text comment '执行日志',
   remarks              text comment '备注',
   is_valid             tinyint not null default 1 comment '是否还可用，1可用，0已下线不可用',
   create_user          varchar(20) comment '创建用户',
   update_user          varchar(20) comment '更新用户',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   update_time          datetime comment '更新时间',
   primary key (instance_flow_id, file_id),
   key(start_time, file_id)
) comment = '任务实例'
engine = MYISAM
default charset utf8;

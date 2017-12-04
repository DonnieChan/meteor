----------------------------------------
目录结构说明
----------------------------------------
config          项目配置(暂时没用)
dao             dao层项目
datasync        做定时同步用的：mysql->cassandra, hive->cassandra, cassandra->hive
doc             项目文档
hiveudf         hive的自定义函数，主要是CustomGenericUDTFJSONTuple这个做c_json_tuple,解决原生的json_tuple的OOM问题；其他是特殊业务需要
jetty-server    用内嵌jetty简单部署web应用，如部署rtview模块
mc              前台应用 (暂时没用)
model           展示模型和数据库模型
queueswap       源头流json转换服务
rtview          报表展示服务
server          spark driver，本项目主要入口，重要
service         service层
shell           脚本
task            为server和datasync提供任务定义读取
transfer        数据交换服务，任务运行日志落地和监控数据落地
userinfo        yyuid和passport互转client
userinfo-server yyuid和passport互转server，访问广州udb部门接口
util            工具类项目
webservice      webservice服务端(暂时没用)
webservice-api  webservice接口、客户端(暂时没用)



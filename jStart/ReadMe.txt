1、在类路径下创建应用程序配置文件：ApplicationConfig.xml
2、创建全局容器对象new Container();
3、调用：Container的init();来初始化类容器
4、调用：Container的close();来释放容器连接资源
5、创建自定义Filter类调用：URLDispatcher来处理网络请求操作
6、类库要求(理论上不依赖任何第三方类库)
	1、javax.servlet-api(action包，一般由容器直接提供)
	2、commons-logging(通用日志包,使用该日志包需导入)
	3、log4j(使用log4j需导入)
	4、slf4j-api(使用slf4j需导入)
	5、mongo-java-driver(repository包，MongoDB操作)
	6、jedis(Redis操作包)
注：以下包需固定
	1、start.application.web.action.*;
	2、start.application.context.annotation.*;
	3、start.application.orm.annotation.*;
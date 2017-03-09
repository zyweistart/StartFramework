package start.application.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import start.application.core.context.BeanLoaderContext;
import start.application.core.context.LoaderHandler;
import start.application.core.utils.ClassHelper;
import start.application.orm.context.OrmLoaderContext;
import start.application.web.context.WebLoaderContext;

public class ScannerClassPath {
	
	private String[] classpath;
	
	public ScannerClassPath(String[] classpath){
		this.classpath=classpath;
	}
	
	public void doScanner(){
		List<LoaderHandler> loaders = new ArrayList<LoaderHandler>();
		// 1、Bean对象解析
		loaders.add(new BeanLoaderContext());
		// 2、Web控制器解析
		loaders.add(new WebLoaderContext());
		// 3、ORM实体解析
		loaders.add(new OrmLoaderContext());
		Iterator<LoaderHandler> interceptors = loaders.iterator();
		LoaderHandler handler = null;
		while (interceptors.hasNext()) {
			LoaderHandler currentHandler = interceptors.next();
			currentHandler.setHandler(handler);
			handler = currentHandler;
		}
		// 2、扫描包下所有的类
		for (String packageName : this.classpath) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				// 不解析接口类
				if (clasz.isInterface()) {
					continue;
				}
				handler.load(clasz);
				handler.reset();
			}
		}
	}
	
}

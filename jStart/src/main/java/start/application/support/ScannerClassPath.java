package start.application.support;

import start.application.core.utils.ClassHelper;

public class ScannerClassPath {
	
	private String[] classpath;
	private AnnotationApplicationContext mAnnotationApplicationContext;
	
	public ScannerClassPath(String[] classpath){
		this.classpath=classpath;
		this.mAnnotationApplicationContext=new AnnotationApplicationContext();
	}
	
	public void doScanner(){
		for (String packageName : this.classpath) {
			for (Class<?> clasz : ClassHelper.getClasses(packageName)) {
				this.mAnnotationApplicationContext.load(clasz);
			}
		}
	}
	
}

package start.application.core.beans.factory;

import java.io.Closeable;

public interface Container extends Closeable{
	
	void start();
	
}

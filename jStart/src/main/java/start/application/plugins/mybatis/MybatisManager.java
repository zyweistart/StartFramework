package start.application.plugins.mybatis;

import javax.sql.DataSource;

public class MybatisManager {
	
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}

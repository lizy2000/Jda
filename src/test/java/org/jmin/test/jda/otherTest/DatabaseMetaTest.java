package org.jmin.test.jda.otherTest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jmin.bee.BeeDataSource;
import org.jmin.jda.JdbcSourceInfo;
import org.jmin.jda.impl.connection.BeeDataSourceFactory;
import org.jmin.test.jda.Link;
import org.jmin.test.jda.SQLTestCase;

public class DatabaseMetaTest extends SQLTestCase{

	public void test()throws Throwable{
	 Connection con = null;
	 ResultSet dbMetaRs = null;
	 String driver =Link.getDriver();							
	 String url=Link.getURL(); 
	 String user=Link.getUser();
	 String password=Link.getPassword();
	 
	 try {
			JdbcSourceInfo sourceInfo = new JdbcSourceInfo(driver,url,user,password);
			BeeDataSource pool = BeeDataSourceFactory.createDataSource(sourceInfo);
	   
	    con = pool.getConnection();
		  DatabaseMetaData metaData =con.getMetaData();
		  dbMetaRs=metaData.getTypeInfo();
		 
//     //获取数据库支持的SQL数据类型
//		 while (dbMetaRs.next()){
//		  String typeName = dbMetaRs.getString("TYPE_NAME");////SQLType name
//		  int typeCode = dbMetaRs.getInt("DATA_TYPE");//SQLType code
//      System.out.println(typeName + " : " + typeCode);
//		 } 	 
	} finally {
		try {
			if(dbMetaRs!=null)
				dbMetaRs.close();
			if(con!=null)
				con.close();
		} catch (SQLException e) {
		}
	}
 }
}

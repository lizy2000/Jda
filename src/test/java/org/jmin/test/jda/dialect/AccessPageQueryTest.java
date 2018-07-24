/*
 * Copyright (C) Chris Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmin.test.jda.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jmin.bee.BeeDataSource;
import org.jmin.jda.JdbcSourceInfo;
import org.jmin.jda.impl.connection.BeeDataSourceFactory;
import org.jmin.jda.impl.dialect.AccessDialect;

/**
 * Access数据库分页测试
 * 
 * @author Chris
 * @version 1.0
 */
public class AccessPageQueryTest {
	
	/**
	 * 测试方法 
	 */
	public static void main(String[] args)throws Exception{
		String driver ="sun.jdbc.odbc.JdbcOdbcDriver";							
		String url="jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};PWD=emily0539;DBQ=D:\\dev\\projects\\loan\\WebRoot\\tablesql\\loan.mdb";
		String user="";
		String password="";
	  JdbcSourceInfo sourceInfo = new JdbcSourceInfo(driver,url,user,password);
	  BeeDataSource pool = BeeDataSourceFactory.createDataSource(sourceInfo);
	  AccessDialect dialect= new AccessDialect();
		
		Connection con =null;
		PreparedStatement st=null;
		ResultSet re=null;
		
		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			String sourceSQL ="select * from userInfo";
		
			sourceSQL =  
				"select top 10 * from(select TAB_ID,PARAM_ID,PARAM_NAME,PARAM_DESC,PARAM_TAG1,"
			  +"   PARAM_TAG2,PARAM_TAG3,UPD_BCH_ID,UPD_USER_ID,"
			  +"  UPD_DATE_TIME from COM_CODE where tab_id=?)";
			
			
			
			sourceSQL=" select top 10 uuuu.* from(select base.CUST_NAME,base.CUST_ID_CTRY,base.CUST_ID_TYPE,	" 
				 +"base.CUST_ID_NO,base.CUST_TYPE,base.CUST_LEVEL,base.CUST_STATUS, "
				 +" base.BANK_CODE,base.BANK_ACC,base.CRT_BCH_ID,base.CRT_USER_ID,base.CRT_DATE_TIME,"
				 +" indiv.*, id.TYPE_NAME as ID_TYPE_NAME"
				 +" from ((CUST_INFO base inner join CUST_INDIV_INFO indiv on base.CUST_NO = indiv.CUST_NO)"
				 +" inner join ID_TYPE id on base.CUST_ID_TYPE = id.TYPE_ID))as uuuu";
			     
			String pageSQL=  dialect.getPageQuerySql(sourceSQL,1,10,null);
			st=con.prepareStatement(pageSQL);
			//st.setString(1,"COUNTRY");
			
			re = st.executeQuery();
			while(re.next()){
				System.out.println(re.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		if(re !=null)
			re.close();
		if(st !=null)
			st.close();
		if(con !=null)
			con.close();
		}
	}
}

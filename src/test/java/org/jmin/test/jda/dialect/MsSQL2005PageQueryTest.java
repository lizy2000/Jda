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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * MS SQL��ݿ��ҳ����
 * 
 * @author Chris
 * @version 1.0
 */
public class MsSQL2005PageQueryTest{
	
	/**
	 * ���Է��� 
	 */
	public static void main(String[] args)throws Exception{
		String driver ="com.microsoft.sqlserver.jdbc.SQLServerDriver";							
		String url="jdbc:sqlserver://localhost:1433;DatabaseName=loan";
		String user="sa";
		String password="";
		Connection con =null;
		PreparedStatement st=null;
		ResultSet re=null;
		
		try {
			Class.forName(driver);
			con =DriverManager.getConnection(url,user,password);
			
			String sourceSQL ="select * from userInfo";
			String pageSQL= "select top 10 * from ("+sourceSQL+")";
			
			st =con.prepareStatement(pageSQL);
			re = st.executeQuery();
			while(re.next()){
				System.out.println(re.getString(1));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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

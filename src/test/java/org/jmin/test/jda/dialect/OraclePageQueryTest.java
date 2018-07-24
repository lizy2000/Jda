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
 * Oracle��ݿ��ҳ����
 * 
 * @author Chris
 * @version 1.0
 */
public class OraclePageQueryTest {
	
	/**
	 * ���Է��� 
	 */
	public static void main(String[] args)throws Exception{
		String driver ="oracle.jdbc.driver.OracleDriver";							
		String url="jdbc:oracle:thin:@10.4.2.220:1521:tongdev";
		String user="loan_admin";
		String password="lipsadmin";
		Connection con =null;
		PreparedStatement st=null;
		ResultSet re=null;
		
		try {
			Class.forName(driver);
			con =DriverManager.getConnection(url,user,password);
			
			String sourceSQL ="select * from usr_prfl";
			StringBuffer buff = new StringBuffer();
			
		  buff.append("select * from (select Jda_Temp_View_A.*,rownum no from (");
		  buff.append(sourceSQL);
		  buff.append(")Jda_Temp_View_A where rownum <="+(1 + 10-1)+") where no >="+1);  
		  
			st =con.prepareStatement(buff.toString());
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

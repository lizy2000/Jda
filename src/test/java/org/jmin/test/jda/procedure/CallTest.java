package org.jmin.test.jda.procedure;

import java.sql.SQLException;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaSession;
import org.jmin.jda.impl.config.SqlFileLoader;

public class CallTest {
	
	/**
	 * test method
	 */
	public static void main(String[] args){
		try {
			String file ="org/jmin/test/jda/procedure/jdbc.xml";
			SqlFileLoader loader = new SqlFileLoader();
			JdaContainer sessionFactory = loader.load(file);
			JdaSession session = sessionFactory.openSession();
			
			CallParam param = new CallParam(1,2);
			session.findOne("call.call1",param);
  		System.out.println("call1 Result: " + param.c);
			
  		session.findOne("call.call2",param);
  		System.out.println("call2 Result: " + param.c);
  		
  		session.findOne("call.call3",param);
  		System.out.println("call3 List: " + param.userList.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}


 
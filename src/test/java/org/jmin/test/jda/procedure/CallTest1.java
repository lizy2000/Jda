package org.jmin.test.jda.procedure;

import java.sql.SQLException;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaSession;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.JdbcSourceInfo;
import org.jmin.jda.impl.JdaContainerImpl;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;
import org.jmin.test.jda.Link;

/**
 * 存储过程的调用
 * 
 * @author Chris
 */
public class CallTest1 {
	public static void main(String[] args){
		try{
			JdaSourceInfo dataSourceInfo = new JdbcSourceInfo(Link.getDriver(),Link.getURL(),Link.getUser(),Link.getPassword());
			JdaContainer container = new JdaContainerImpl(dataSourceInfo);
			ParamUnit property1 = container.createParamUnit("a",null);
			ParamUnit property2 = container.createParamUnit("b",null);
			ParamUnit property3 = container.createParamUnit("c",null);
			property3.setParamValueMode(ParamValueMode.OUT);
			property3.setParamColumnTypeName("INTEGER");
			ParamMap map1= container.createParamMap(CallParam.class,new ParamUnit[]{property1,property2,property3});
			String SQL1 ="{call sumInt(?,?,?)}";
			container.registerStaticSql("call",SQL1,map1);
			
			JdaSession session = container.openSession();
			CallParam param = new CallParam(1,2);
			session.update("call",param);
  		System.out.println("call Result: " + param.c);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

package org.jmin.test.jda.procedure;

import java.sql.SQLException;
import java.util.Map;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaSession;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.JdbcSourceInfo;
import org.jmin.jda.impl.JdaContainerImpl;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.mapping.ResultUnit;
import org.jmin.test.jda.Link;

public class CallTest3 {
	public static void main(String[] args){
		try{
			JdaSourceInfo dataSourceInfo = new JdbcSourceInfo(Link.getDriver(),Link.getURL(),Link.getUser(),Link.getPassword());
			JdaContainer container = new JdaContainerImpl(dataSourceInfo);
 
  		ResultUnit result1 = container.createResultUnit("F1",String.class);
  		ResultUnit result2 = container.createResultUnit("F2",String.class);
  		ResultUnit result3 = container.createResultUnit("F3",String.class);
  		ResultMap resultMap= container.createResultMap(Map.class,new ResultUnit[]{result1,result2,result3});
			
			ParamUnit property1 = container.createParamUnit("c",null);
			property1.setParamValueMode(ParamValueMode.OUT);
			property1.setParamColumnTypeName("INTEGER");
			ParamUnit property2 = container.createParamUnit("a",null);
			ParamUnit property3 = container.createParamUnit("b",null);
			ParamUnit property4 = container.createParamUnit("userList",null);
			property4.setCursorResultMap(resultMap);
			property4.setParamColumnTypeName("ORACLECURSOR");
			property4.setParamValueMode(ParamValueMode.OUT);
			ParamMap map2= container.createParamMap(CallParam.class,new ParamUnit[]{property1,property2,property3,property4});
			String SQL2 ="{?=call getList(?,?,?)}";
			container.registerStaticSql("call",SQL2,map2);
			
			JdaSession session = container.openSession();
			CallParam param = new CallParam(1,2);
  		session.update("call",param);
  		System.out.println("Result List: " + param.userList.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

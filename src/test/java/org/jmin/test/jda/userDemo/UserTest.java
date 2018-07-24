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
package org.jmin.test.jda.userDemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaResultPageList;
import org.jmin.jda.JdaSession;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.JdbcSourceInfo;
import org.jmin.jda.impl.JdaContainerImpl;
import org.jmin.jda.impl.config.dynamicsql.DynSqlAnalyzer;
import org.jmin.jda.impl.config.statictext.StaticSqlAnalyzer;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.mapping.ResultUnit;
import org.jmin.jda.statement.DynTag;
import org.jmin.test.jda.Link;
import org.jmin.test.jda.SQLTestCase;

/**
 * mapping demo
 * 
 * @author Chris Liao
 */

public class UserTest extends SQLTestCase{
	
	/**
	 * data source
	 */
	private JdaSourceInfo sourceInfo;
	
	/**
	 * sql mapping container
	 */
	private JdaContainer container;
	
	/**
	 * jda session
	 */
	private JdaSession session;
	
	/**
	 * main 
	 */
	public static void main(String[]args)throws Throwable{
		UserTest test = new UserTest();
		test.setUp();
		test.testRun();
		test.tearDown();
	}
	
	/**
	 * 设置环境
	 */
	public void setUp()throws Throwable{
	 this.initDataSource();
	 this.registerSQL();
	}
	
	/**
	 * init data source and create sql mapping container
	 */
	public void initDataSource()throws Throwable{
		this.sourceInfo = new JdbcSourceInfo(Link.getDriver(),Link.getURL(),Link.getUser(),Link.getPassword());
		this.sourceInfo.setShowSql(true);
		this.container = new JdaContainerImpl(this.sourceInfo);
	}

	/**
	 * register sql into container
	 */
	public void registerSQL()throws Throwable{
		String insertSQL1 ="insert into USERINFO(name,sex)values('liao','Man')";
		String insertSQL2 ="insert into USERINFO(name,sex)values(?,?)";
		ParamUnit insertNameProperty = container.createParamUnit("name",null);
		ParamUnit insertSexProperty = container.createParamUnit("sex",null);
		ParamMap insertParamMap= container.createParamMap(User.class,new ParamUnit[]{insertNameProperty,insertSexProperty});
		this.container.registerStaticSql("insert1",insertSQL1);
		this.container.registerStaticSql("insert2",insertSQL2,insertParamMap);
	
		String updateSQL1 = "update USERINFO set sex=? where name=?";
		ParamUnit updateNameProperty = container.createParamUnit("name",null);
		ParamUnit updateSexProperty = container.createParamUnit("sex",null);
	  ParamMap updateParamMap= container.createParamMap(User.class,new ParamUnit[]{updateSexProperty,updateNameProperty});
	  this.container.registerStaticSql("update",updateSQL1,updateParamMap);
	  
	  
		String selectSQL1 ="select name,sex from USERINFO";
	 	ResultUnit property1 = container.createResultUnit("name",null);
		ResultUnit property2 = container.createResultUnit("sex",null);
	  ResultMap selectResultMap= container.createResultMap(User.class,new ResultUnit[]{property1,property2});
	  this.container.registerStaticSql("select",selectSQL1,selectResultMap);
	  
	  
		String deleteSQL1 ="delete from USERINFO where name='liao'";
		String deleteSQL2 ="delete from USERINFO where name=?";
		ParamUnit deleteNameProperty = container.createParamUnit("name",null);
		ParamMap deleteParamMap= container.createParamMap(User.class,new ParamUnit[]{deleteNameProperty});
		this.container.registerStaticSql("delete1",deleteSQL1);
		this.container.registerStaticSql("delete2",deleteSQL2,deleteParamMap);
		
		
  	String dynSQL1 = "select name,sex from USERINFO"
		  +"<where>"
		  +"<if test=\"name!=null\">name=#{name}</if>"
		  +"<if test=\"sex!=null\">sex=#{sex}</if>"
		  +"</where>";
  	
  	String dynSQL2 = "update USERINFO " 
       +" <set> "
       +"  <if test=\"sex!=null\">"
       +"   sex = #{sex}  "
       +"  </if>"
       +"</set>"
  	   +" where name=#{name}";
  	DynSqlAnalyzer  dynSqlAnalyzer = new DynSqlAnalyzer(new StaticSqlAnalyzer());
  	DynTag[] tags1 = dynSqlAnalyzer.analyzeDynamicSQL("dynSelect",dynSQL1,User.class,container);
  	DynTag[] tags2 = dynSqlAnalyzer.analyzeDynamicSQL("dynUpdate",dynSQL2,User.class,container);
  	this.container.registerDynamicSql("dynSelect",tags1,User.class,selectResultMap);
  	this.container.registerDynamicSql("dynUpdate",tags2,User.class);
		this.session = container.openSession();
	}
  
	/**
	 * register sql into container
	 */
	public void testRun()throws Throwable{
		this.insertTest();
		this.updateTest();
		this.selectTest();
		this.pageSelectTest();
		this.batchUpdateTest();
		this.deleteTest();
		this.dynamicTest();
	}

	/**
	 * test insert sql
	 */
	public void insertTest()throws Throwable{
		session.insert("insert1");//insert1
		session.insert("insert2",new User("Chris","Man"));//object mapping
		
    List paramList = new ArrayList();
		paramList.add("Summer");paramList.add("Girl");
		session.insert("insert2",paramList);//list parameter
		
		Map paramMap = new HashMap();
		paramMap.put("name","Emily");
		paramMap.put("sex","Girl");
		session.insert("insert2",paramMap);//map parameter
		session.insert("insert2",new String[]{"Mike","Boy"});
	}
	
	/**
	 * test update sql
	 */
	public void updateTest()throws Throwable{
		session.update("update",new User("Chris","Girl"));
	}
	
	/**
	 * test select sql
	 */
	public void selectTest()throws Throwable{
		List list = session.findList("select");
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
	
	/**
	 * test page select 
	 */
	public void pageSelectTest()throws Throwable{
		JdaResultPageList list = session.findPageList("select",3);
		list.moveNextPage();
		list.moveToPage(2);
		Object[] array = list.getCurrentPage();
		for(int i=0;i<array.length;i++){
			System.out.println(array[i]);
		}
	}

	/**
	 * test delete sql
	 */
	public void deleteTest()throws Throwable{
		session.delete("delete1");
		session.delete("delete2",new User("Chris"));
		session.delete("delete2",new User("Emily"));
	}

	/**
	 * test dynamic sql
	 */
	public void dynamicTest()throws Throwable{
		session.findList("dynSelect",new User("Chris"));
		session.update("dynUpdate",new User("Chris","Man"));
	}

	/**
	 * test bath update sql
	 */
	public void batchUpdateTest()throws Throwable{
	  session.startBatch();
		User userInfo = null;
		for(int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				userInfo = new User("emily", "Girl");
			} else {
				userInfo = new User("liao", "Man");
			}
			session.insert("insert2", userInfo);
		}

		for(int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				userInfo = new User("emily", "Girl1");
			} else {
				userInfo = new User("liao", "Man1");
			}
			session.update("update", userInfo);
		}
	  session.executeBatch();
	}
	
	/**
	 *清理测试环境
	 */
	public void tearDown()throws Throwable{
		session.close();
		container.destroy();
	}
}

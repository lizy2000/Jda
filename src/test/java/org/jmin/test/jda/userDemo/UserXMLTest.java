package org.jmin.test.jda.userDemo;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaSession;
import org.jmin.jda.impl.config.SqlFileLoader;
import org.jmin.test.jda.SQLTestCase;

public class UserXMLTest extends SQLTestCase{
	/**
	 * test method
	 */
	public void test()throws Throwable{
		String file ="org/jmin/test/jda/userDemo/jda.xml";
		SqlFileLoader loader = new SqlFileLoader();
		JdaContainer sessionFactory = loader.load(file);
		JdaSession session = sessionFactory.openSession();
	
		session.insert("user.Insert1");
		session.insert("user.Insert2",new User("chris", "Man"));
		session.insert("user.Insert3",new User("chris","Man"));
	
		session.update("user.Update1",new User("chris2","Girl"));
		session.update("user.Update2",new User("liao","Girl"));
		
		session.delete("user.Delete1");
		session.delete("user.Delete2","chris");
		session.delete("user.Delete3","chris2");
	
		Object ojb1 = session.findList("user.find1","chris");
		Object ojb2 = session.findList("user.find1","chris");
		if(ojb1==ojb2)
			System.out.println("find from cache");
		else
			throw new Exception("not find from cache");
 
		System.out.println("list1: " + session.findList("user.find3").size());
		System.out.println("list2: " + session.findList("user.find3",1,2));
	
		System.out.println("map1 size: " + session.findMap("user.find2","name").size());
		System.out.println("map2 size: " + session.findMap("user.find2","name","sex").size());
		System.out.println("page size: " + session.findPageList("user.find2",3).getTotalSize());
	}
}
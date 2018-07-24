package org.jmin.test.jda.otherTest;

import org.jmin.test.jda.SQLTestCase;

import ognl.Ognl;

public class BeanOnglTest extends SQLTestCase{
	
	public static void main(String[] args)throws Throwable{
		BeanOnglTest test = new BeanOnglTest();
		test.test();
	}

	public void test()throws Throwable {
		String name  ="name";
		Student1 student1 = new Student1();
		Student2 student2 = new Student2();
	
		Object ognlExpression = Ognl.parseExpression(name);
		Ognl.setValue(ognlExpression,student1,"Chris1");
		if(!"Chris1".equals(Ognl.getValue(ognlExpression,student1)))
		 throw new Exception("Ongl test fail");
		
		Ognl.setValue(ognlExpression,student2,Integer.valueOf(2));
    if(!Integer.valueOf(2).equals(Ognl.getValue(ognlExpression,student2)))
			 throw new Exception("Ongl test fail");
	}
}
class Student1 {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

class Student2 {
	private int name;

	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}
}


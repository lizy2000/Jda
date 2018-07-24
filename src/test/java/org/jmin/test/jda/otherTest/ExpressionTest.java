package org.jmin.test.jda.otherTest;

import java.util.HashMap;
import java.util.Map;

import org.jmin.jda.impl.property.OgnlPropertyUtil;
import org.jmin.test.jda.SQLTestCase;

public class ExpressionTest extends SQLTestCase{
	public static void main(String[] args)throws Throwable {
		Book book = new Book("<java>");
		book.setAuthor("chris");
		
//		Object obj = OgnlPropertyUtil.getPropertyValue(book, "author");
//	  if(obj==null)throw new Exception();
//    
//	  OgnlPropertyUtil.setPropertyValue(book, "map.name", "ddd");
//		obj = OgnlPropertyUtil.getPropertyValue(book, "map.name") ;
//		if(obj==null)throw new Exception();
//	 
//		OgnlPropertyUtil.setPropertyValue(book, "name", "book2");
//		obj = OgnlPropertyUtil.getPropertyValue(book, "name");
//		if(obj==null)throw new Exception();

		Map paramMap = new HashMap();
		paramMap.put("book", book);
		paramMap.put("x2", Integer.valueOf(1));

		//System.out.println(OgnlPropertyUtil.assertBool("book.age != 0", paramMap));
		System.out.println(OgnlPropertyUtil.assertBool("book.author == \'chris\'",paramMap));
		System.out.println(OgnlPropertyUtil.assertBool("book.age > 1",paramMap));
	}
}

class Book {
	private Map map;

	private String name = "  ";

	private String author;

	private int age = 10;

	public Book() {
	}

	public Book(String bookName) {
		this.author = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public static String hello() {
		return "Hello World";
	}
}
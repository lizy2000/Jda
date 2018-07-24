package org.jmin.test.jda.orderDemo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmin.jee.JeeBeanDaoTemplate;
import org.jmin.test.jda.SQLTestCase;

public class OrderTest extends SQLTestCase{
	
	/**
	 * test method
	 * #propertyName:columnTypeName:PropertyType:ParamPersister:ParamValueMode#
	 */
	public void test()throws Throwable{
		JeeBeanDaoTemplate template = null;
		try {
			String file ="org/jmin/test/jda/orderDemo/jda.xml";
			template = new JeeBeanDaoTemplate();
			template.setSourceFile(file);
			ProductDao productDao = new ProductDao();
			CustDao custDao = new CustDao();
			OrderDao orderDao = new OrderDao();
			OrderItemDao orderItemDao = new OrderItemDao();
			productDao.setDaoTemplate(template);
			custDao.setDaoTemplate(template);
			orderDao.setDaoTemplate(template);
			orderItemDao.setDaoTemplate(template);
			
			template.beginTransaction();
			Product product = new Product();
			product.setProductNo((int)System.currentTimeMillis());
			product.setProductType("衣服");
			product.setProductName("Levis衣服");
			product.setProductDesc("很好用");
			product.setProductSize("90");
			product.setProductColor("Blue");
			productDao.insert(product);
			
			Cust cust = new Cust();
			cust.setCustNo((int)System.currentTimeMillis());
			cust.setName("Chris");
			cust.setGender("Man");
			cust.setMobile("13632790758");
			cust.setPhone("0755-876543");
			cust.setAddress("东莞Glossmind生产地");
			custDao.insert(cust);
		
			Order order = new Order();
			order.setOrderNo("" +((int)System.currentTimeMillis()));
			order.setCustNo(""+cust.getCustNo());
			order.setOpenDate(new Date());
			orderDao.insert(order);
 			
			OrderItem item = new OrderItem();
			item.setOrderNo(order.getOrderNo());
			item.setProductNo(product.getProductNo());
			item.setRemark("有货，可以优惠");
			orderItemDao.insert(item);
			
			List colorList = new ArrayList();
			colorList.add("red");
			colorList.add("black");
			product.setColorList(colorList);
			productDao.findList(product);
		
			template.commitTransaction();
	
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(template!=null)template.rollbackTransaction();
			} catch (SQLException e1) {
				 
			}
			throw e;
		} 
	}
}

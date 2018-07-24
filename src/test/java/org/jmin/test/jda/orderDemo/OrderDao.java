package org.jmin.test.jda.orderDemo;

import java.sql.SQLException;

import org.jmin.jee.JeeBeanDao;

public class OrderDao extends JeeBeanDao {
	
	public void insert(Order order)throws SQLException{
		this.getDaoTemplate().insert("order.insert",order);
	}
}

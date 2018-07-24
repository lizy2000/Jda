package org.jmin.test.jda.orderDemo;

import java.sql.SQLException;

import org.jmin.jee.JeeBeanDao;

public class OrderItemDao extends JeeBeanDao {

	public void insert(OrderItem item)throws SQLException{
		this.getDaoTemplate().insert("orderItem.insert",item);
	}
}
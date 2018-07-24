package org.jmin.test.jda.orderDemo;

import java.sql.SQLException;
import java.util.List;

import org.jmin.jee.JeeBeanDao;

public class ProductDao extends JeeBeanDao {

	public void insert(Product product)throws SQLException{
		this.getDaoTemplate().insert("product.insert",product);
	}
	
	public List findList(Product product)throws SQLException{
		return this.getDaoTemplate().findList("product.selectList",product);
	}
}
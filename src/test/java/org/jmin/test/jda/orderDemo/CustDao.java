package org.jmin.test.jda.orderDemo;

import java.sql.SQLException;

import org.jmin.jee.JeeBeanDao;

public class CustDao extends JeeBeanDao {

	public void insert(Cust cust)throws SQLException{
		this.getDaoTemplate().insert("cust.insert",cust);
	}
 }


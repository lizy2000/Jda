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
package org.jmin.jda;

/**
 * Jdbc DataSource Definition
 * 
 * @author Chris
 * @version 1.0
 */

public final class JdbcSourceInfo extends JdaSourceInfo {
	
	/**
	 * Driver class
	 */
	private String dbDriver;

	/**
	 * url link
	 */
	private String dbURL;

	/**
	 * user ID
	 */
	private String dbUser;

	/**
	 * Passsword
	 */
	private String dbPassword;
	
	/**
	 * Constructor
	 */
	public JdbcSourceInfo(){}
	
	/**
	 * Constructor
	 */
	public JdbcSourceInfo(String driver,String URL,String user,String password){
		this.dbDriver = driver;
		this.dbURL =URL;
		this.dbUser =user;
		this.dbPassword=password;
	}
	
	/**
	 * Driver class
	 */
	public String getDbDriver() {
		return dbDriver;
	}
	
	/**
	 * Driver class
	 */
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	
	/**
	 * url link
	 */
	public String getDbURL() {
		return dbURL;
	}
	
	/**
	 * url link
	 */
	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}
	
	/**
	 * url link
	 */
	public String getDbUser() {
		return dbUser;
	}
	
	/**
	 * url link
	 */
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
	/**
	 * url link
	 */
	public String getDbPassword() {
		return dbPassword;
	}
	
	/**
	 * url link
	 */
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
}

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
 * jndi DataSource Definition
 * 
 * @author Chris
 * @version 1.0
 */
public final class JndiSourceInfo extends JdaSourceInfo{
	
	/**
	 * jndi
	 */
	private String contextName;

	/**
	 * factory
	 */
	private String contextFactory;
	
	/**
	 * provideURL;
	 */
	private String contextProvideURL;

	/**
	 * principal
	 */
	private String contextPrincipal;

	/**
	 * credentials
	 */
	private String contextCredentials;
	
	/**
	 * jdbc user ID
	 */
	private String dbUser;

	/**
	 * jdbc  Passsword
	 */
	private String dbPassword;

	/**
	 * constructor
	 */
	public JndiSourceInfo(){}
	
	/**
	 * constructor
	 */
	public JndiSourceInfo(String jndiName,String contextFactory,String contextProvideURL,String contextPrincipal,String contextCredentials){
		this.contextName =jndiName;
		this.contextFactory =contextFactory;
		this.contextProvideURL = contextProvideURL;
		this.contextCredentials = contextCredentials;
	}
	
	/**
	 * jndi
	 */
	public String getContextName() {
		return contextName;
	}
	
	/**
	 * jndi
	 */
	public void setContextName(String jndiName) {
		this.contextName = jndiName;
	}
	
	/**
	 * factory
	 */
	public String getContextFactory() {
		return contextFactory;
	}
	
	/**
	 * factory
	 */
	public void setContextFactory(String contextFactory) {
		this.contextFactory = contextFactory;
	}
	
	/**
	 * provideURL;
	 */
	public String getContextProvideURL() {
		return contextProvideURL;
	}

	/**
	 * provideURL;
	 */
	public void setContextProvideURL(String contextProvideURL) {
		this.contextProvideURL = contextProvideURL;
	}
	
	/**
	 * principal
	 */
	public String getContextPrincipal() {
		return contextPrincipal;
	}
	
	/**
	 * principal
	 */
	public void setContextPrincipal(String contextPrincipal) {
		this.contextPrincipal = contextPrincipal;
	}

	/**
	 * credentials
	 */
	public String getContextCredentials() {
		return contextCredentials;
	}
	
	/**
	 * credentials
	 */
	public void setContextCredentials(String contextCredentials) {
		this.contextCredentials = contextCredentials;
	}
	
	/**
	 * jdbc user ID
	 */
	public String getDbUser() {
		return dbUser;
	}
	
	/**
	 * jdbc user ID
	 */
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
	/**
	 * jdbc  Passsword
	 */
	public String getDbPassword() {
		return dbPassword;
	}
	
	/**
	 * jdbc  Passsword
	 */
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
}

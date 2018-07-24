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
package org.jmin.jda.impl.config.datasource;

/**
 * 节点类型名
 * 
 * @author Chris
 */
public class JdbcSourceTags {
	
	/**
	 * 根节点名
	 */
	public final String Root ="sql-mapping";
	
	/**
	 * 数据源节点名
	 */
	public final String DataSource ="datasource";
	
	/**
	 * 池的size
	 */
	public final String connectionPoolSize ="connection.poolMaxSize";

	/**
	 * 池的size
	 */
	public final String connectionPoolInitSize ="connection.poolInitSize";
	
	/**
	 * 池的实现类名
	 */
	public final String connectionPoolClassName ="connection.poolClassName";
	
	/**
	 * 连接最大闲置时间
	 */
	public final String connectionTimeout ="connection.maxIdleTime";
	
	/**
	 * 请求连接最大同步等待时间
	 */
	public final String connectionRequestTime ="connection.maxWaitTime";
	
 /**
	* 连接池用于测试连接是否链通的SQL
	*/
	public final String connectionTestQuerySql ="connection.testQuerySql";
	
	/**
	 * sql缓存Size
	 */
	public final String statementCacheSize ="connection.statement.cacheSize";
	
	/**
	 * sql批量更新的Size
	 */
	public final String statementBatchSize ="connection.statement.batchSize";
	
	/**
	 * 结果获取的size
	 */
	public final String resultsetFetchSize ="query.resultset.fetchSize";
	
	
	/**
	 * 是否显示SQL
	 */
	public final String showSql ="jdbc.sql.show";
	
	/**
	 * 节点名
	 */
	public final String jdbcSqlDialect ="jdbc.sql.dialect";
	
  
	/**
	 * 缓存是否有效
	 */
	public final String cacheOpen ="query.resultset.cache.open";
	
	/**
	 * 缓存是否有效
	 */
	public final String localCacheOpen ="query.resultset.localCache.open";
	
	/**
	 * 反射缓存大小
	 */
	public final String reflectCacheSize ="java.reflect.cache.size";
	
	/**
	 * 事务是否有效
	 */
	public final String transactionOpen ="transaction.open";

	/**
	 * 事务节点名
	 */
	public final String transactionJtaName ="transaction.jta.name";
	
	/**
	 * 事务节点名
	 */
	public final String transactionJtaFactory ="transaction.jta.factory";
	
	/**
	 * 事务节点名
	 */
	public final String transactionJtaProvider ="transaction.jta.provider";
	
	/**
	 * 事务节点名
	 */
	public final String transactionJtaPrincipal ="transaction.jta.principal";
	
	/**
	 * 事务节点名
	 */
	public final String transactionJtaCredentials ="transaction.jta.credentials";
	public final String transactionIsolation ="transaction.isolation";
	
	/**
	 * Jdbc类型节点
	 */
	public final String JdbcTypes ="jdbc-types";
	
	/**
	 * Handler列表
	 */
	public final String ParamPersisters ="persisters";
	
	/**
	 * Handler列表
	 */
	public final String ParamPersister ="persister";
	
	/**
	 * Converters
	 */
	public final String ResultConverters ="converters";
	
	/**
	 * Converters
	 */
	public final String ResultConverter ="converter";
	
	

	/**
	 * 映射文件节点名
	 */
	public final String Mapping ="mapping";
	
	/**
	 * 映射文件节属性
	 */
	public final String Mapping_Resource ="resource";
	
	
	public final String Jdbc="jdbc";
	public final String Jdbc_Driver="jdbc.driver";
	public final String Jdbc_URL="jdbc.url";
	public final String Jdbc_User="jdbc.user";
  public final String Jdbc_Password="jdbc.password";
	
	public final String Jndi="jndi";
	public final String Jndi_Name="jndi.name";
	public final String Jndi_Factory="jndi.factory";
	public final String Jndi_ProvideURL="jndi.provideURL";
	public final String Jndi_Principal="jndi.principal";
	public final String Jndi_Credentials="jndi.credentials";
	public final String Jndi_Jdbc_User="jndi.jdbc.user";
	public final String Jndi_Jdbc_Password="jndi.jdbc.password";
  public final String Transaction_Jta = "transaction.jta";
  
	public final String ATTR_Name ="name";
	public final String ATTR_Type ="type";
	public final String ATTR_Code ="code";
	
	public final String ATTR_Class ="class";
	public final String ATTR_JavaType ="javaType";
	public final String ATTR_JdbcType ="jdbcType";
	public final String ATTR_Property="property";

}

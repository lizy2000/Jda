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
 * 数据源定义信息
 * 
 * @author Chris Liao
 */
public abstract class JdaSourceInfo implements Cloneable {

	/**
	 * 数据源ID
	 */
	private String sourceID;

	/**
	 * 是否显示执行SQL
	 */
	private boolean showSql = false;

	/**
	 * SQL方言
	 */
	private JdaDialect jdaDialect = null;

	/**
	 * 是否让缓存有效
	 */
	private boolean resultCacheOpen = true;

	/**
	 * 本地缓存是否打开
	 */
	private boolean resultLocalCacheOpen = true;

	/**
	 * 是否让事务有效
	 */
	private boolean transactionOpen = true;

	/**
	 * Java反射缓存大小
	 */
	private int reflectionCacheSize = 100;

	/**
	 * 事务定义
	 */
	private UserTransactionInfo userTransactionInfo = null;

	/**
	 * 事务隔离等级,默认等级为：读提交
	 */
	private TransactionLevel transactionIsolation = TransactionLevel.TRANSACTION_READ_COMMITTED;

	/**
	 * 池初始化size
	 */
	private int poolInitSize;

	/**
	 * 池最大容许的size
	 */
	private int poolMaxSize = 10;

	/**
	 * Connection的Statement缓存容器大小
	 */
	private int statementCacheSize = 20;

	/**
	 * 一次性从数据库中获取的记录个数
	 */
	private int resultFetchSize = 10;

	/**
	 * 批次最大Size,当statement的执行数据达到该size,statement将执行一次
	 */
	private int batchUpdateSize = 10;

	/**
	 * 连接的最大闲置时间，超过将被关闭，默认时间为3分钟
	 */
	private long connectionIdleTimeout = 180000;

	/**
	 * 获取连接的最大同步等待时间:3分钟
	 */
	private long connectionMaxWaitTime = 180000;                                  
										  
	/**
	 * 连接池用于测试连接是否链通的SQL
	 */
	private String connectionTestQuerySql = "";

	/**
	 * 连接池实现类
	 */
	private String connectionPoolClassName = "";

	/**
	 * 数据源ID
	 */
	public String getDataSourceID() {
		return sourceID;
	}

	/**
	 * 数据源ID
	 */
	public void setDataSourceID(String dataSourceID) {
		sourceID = dataSourceID;
	}

	/**
	 * 是否显示执行SQL
	 */
	public boolean isShowSql() {
		return showSql;
	}

	/**
	 * 是否显示执行SQL
	 */
	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	/**
	 * SQL方言
	 */
	public JdaDialect getJdbcDialect() {
		return jdaDialect;
	}

	/**
	 * SQL方言
	 */
	public void setJdbcDialect(JdaDialect dialect) {
		this.jdaDialect = dialect;
	}

	/**
	 * 是否让缓存有效
	 */
	public boolean isResultCacheOpen() {
		return resultCacheOpen;
	}

	/**
	 * 是否让缓存有效
	 */
	public void setResultCacheOpen(boolean resultCacheOpen) {
		this.resultCacheOpen = resultCacheOpen;
	}

	/**
	 * 本地缓存是否打开
	 */
	public boolean isResultLocalCacheOpen() {
		return resultLocalCacheOpen;
	}

	/**
	 * 本地缓存是否打开
	 */
	public void setResultLocalCacheOpen(boolean resultLocalCacheOpen) {
		this.resultLocalCacheOpen = resultLocalCacheOpen;
	}

	/**
	 * 是否让事务有效
	 */
	public boolean isTransactionOpen() {
		return transactionOpen;
	}

	/**
	 * 是否让事务有效
	 */
	public void setTransactionOpen(boolean transactionOpen) {
		this.transactionOpen = transactionOpen;
	}

	/**
	 * JTA事务定义
	 */
	public UserTransactionInfo getUserTransactionInfo() {
		return userTransactionInfo;
	}

	/**
	 * JTA事务定义
	 */
	public void setUserTransactionInfo(UserTransactionInfo jtaTransactionInfo) {
		if (jtaTransactionInfo != null)
			this.userTransactionInfo = jtaTransactionInfo;
	}

	/**
	 * 事务隔离等级
	 */
	public TransactionLevel getTransactionIsolation() {
		return transactionIsolation;
	}

	/**
	 * 事务隔离等级
	 */
	public void setTransactionIsolation(TransactionLevel transactionIsolation) {
		if (transactionIsolation != null)
			this.transactionIsolation = transactionIsolation;
	}

	/**
	 * Java反射缓存大小
	 */
	public int getReflectionCacheSize() {
		return reflectionCacheSize;
	}

	/**
	 * Java反射缓存大小
	 */
	public void setReflectionCacheSize(int reflectionCacheSize) {
		this.reflectionCacheSize = reflectionCacheSize;
	}

	/**
	 * 池最大容许的size
	 */
	public int getPoolMaxSize() {
		return poolMaxSize;
	}

	/**
	 * 池最大容许的size
	 */
	public void setPoolMaxSize(int poolMaxSize) {
		this.poolMaxSize = poolMaxSize;
	}

	/**
	 * 池初始化size
	 */
	public int getPoolInitSize() {
		return poolInitSize;
	}

	/**
	 * 池初始化size
	 */
	public void setPoolInitSize(int poolInitSize) {
		this.poolInitSize = poolInitSize;
	}

	/**
	 * Connection的Statement缓存容器大小
	 */
	public int getStatementCacheSize() {
		return statementCacheSize;
	}

	/**
	 * Connection的Statement缓存容器大小
	 */
	public void setStatementCacheSize(int statementCacheSize) {
		this.statementCacheSize = statementCacheSize;
	}

	/**
	 * 批次最大Size,当statement的执行数据达到该size,statement将执行一次
	 */
	public int getBatchUpdateSize() {
		return batchUpdateSize;
	}

	/**
	 * 批次最大Size,当statement的执行数据达到该size,statement将执行一次
	 */
	public void setBatchUpdateSize(int batchUpdateSize) {
		this.batchUpdateSize = batchUpdateSize;
	}

	/**
	 * 一次性从数据库中获取的记录个数
	 */
	public int getResultFetchSize() {
		return resultFetchSize;
	}

	/**
	 * 一次性从数据库中获取的记录个数
	 */
	public void setResultFetchSize(int resultFetchSize) {
		this.resultFetchSize = resultFetchSize;
	}

	/**
	 * 连接的最大闲置时间，超过将被关闭，默认时间为3分钟
	 */
	public long getConnectionIdleTimeout() {
		return connectionIdleTimeout;
	}

	/**
	 * 连接的最大闲置时间，超过将被关闭，默认时间为3分钟
	 */
	public void setConnectionIdleTimeout(long connectionIdleTimeout) {
		this.connectionIdleTimeout = connectionIdleTimeout;
	}

	/**
	 * 获取连接的最大同步等待时间
	 */
	public long getConnectionMaxWaitTime() {
		return connectionMaxWaitTime;
	}

	/**
	 * 获取连接的最大同步等待时间
	 */
	public void setConnectionMaxWaitTime(long connectionMaxWaitTime) {
		this.connectionMaxWaitTime = connectionMaxWaitTime;
	}

	/**
	 * 连接池用于测试连接是否链通的SQL
	 */
	public String getConnectionTestQuerySql() {
		return connectionTestQuerySql;
	}

	/**
	 * 连接池用于测试连接是否链通的SQL
	 */
	public void setConnectionTestQuerySql(String connectionTestQuerySql) {
		this.connectionTestQuerySql = connectionTestQuerySql;
	}

	/**
	 * 连接池实现类
	 */
	public String getConnectionPoolClassName() {
		return connectionPoolClassName;
	}

	/**
	 * 连接池实现类
	 */
	public void setConnectionPoolClassName(String connectionPoolClassName) {
		this.connectionPoolClassName = connectionPoolClassName;
	}
	
	/**
	 * 克隆
	 */
	public Object clone() throws CloneNotSupportedException{
  	return (JdaSourceInfo)super.clone();
  }
}

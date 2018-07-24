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
										
import java.sql.SQLException;
import java.util.List;

import org.jdom.Element;
import org.jmin.jda.JdaDialect;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.JdbcSourceInfo;
import org.jmin.jda.JndiSourceInfo;
import org.jmin.jda.TransactionLevel;
import org.jmin.jda.impl.exception.DataSourceException;
import org.jmin.jda.impl.exception.TransactionConfigException;
import org.jmin.jda.impl.util.BeanUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.log.LogPrinter;

/**
 * 数据源解析
 * 
 * @author Chris Liao
 */
public class JdbcSourceParser {
	
	/**
	 * logger
	 */
	private LogPrinter logger = LogPrinter.getLogPrinter(JdbcSourceParser.class);
	
	/**
	 * 数据源解析
	 */
	public JdaSourceInfo parse(Element element,JdbcSourceTags sourceTags)throws SQLException{
		if(element == null){
			 throw new DataSourceException("Not found data source info");
		}else{
			List infoList = element.getChildren(sourceTags.ATTR_Property);
			if(infoList.isEmpty())
				throw new DataSourceException("Data source info missed");
			
			Element firstNode =(Element)infoList.get(0);
			String firstNodeName = (String)firstNode.getAttributeValue(sourceTags.ATTR_Name);
			if(firstNodeName.startsWith(sourceTags.Jdbc)){
				return parseJdbcDataSourceInfo(element,sourceTags);
			}else	if(firstNodeName.startsWith(sourceTags.Jndi)){
				return parseJndiDataSourceInfo(element,sourceTags);
			}else{
				throw new DataSourceException("Datasource configed error!");
			}
		}
	}
	
	/**
	 * 解析JDBC数据源
	 */
	private JdbcSourceInfo parseJdbcDataSourceInfo(Element element,JdbcSourceTags sourceTags)throws SQLException{
		String driver=null,URL=null,user=null,password=null;
		int poolSize=0,poolInitSize=0,cacheSize=0,batchSize=0,fetchSize=0;
		long poolTimeout =0,requestTime=0;
		boolean showSql=false;
		boolean cacheOpen=false;
		boolean localCacheOpen=false;
		boolean transactionOpen=true;
		String connectionTestQuerySql=null;
		TransactionLevel isolation =null;
		JdaDialect jdbcDialect=null;
		int reflectCacheSize=0;
		String poolClassName=null;
		
		List infoList = element.getChildren(sourceTags.ATTR_Property);
		for(int i=0,n=infoList.size();i<n;i++) {
			Element subElement = (Element)infoList.get(i);
			String attrName = (String) subElement.getAttributeValue(sourceTags.ATTR_Name);
			 if(!attrName.startsWith(sourceTags.Transaction_Jta)){
				if(sourceTags.Jdbc_Driver.equalsIgnoreCase(attrName)) {
					driver = subElement.getTextTrim();
					logger.info(sourceTags.Jdbc_Driver + ":" + driver);
				} else if (sourceTags.Jdbc_URL.equalsIgnoreCase(attrName)) {
					URL = subElement.getTextTrim();
					logger.info(sourceTags.Jdbc_URL+":" + URL);
				} else if (sourceTags.Jdbc_User.equalsIgnoreCase(attrName)) {
					user = subElement.getTextTrim();
					logger.info(sourceTags.Jdbc_User +":" + user);
				} else if (sourceTags.Jdbc_Password.equalsIgnoreCase(attrName)) {
					password = subElement.getTextTrim();
				} else if (sourceTags.connectionPoolClassName.equalsIgnoreCase(attrName)) {
					poolClassName =subElement.getTextTrim();
					logger.info(sourceTags.connectionPoolClassName +":"+ poolClassName);
				} else if (sourceTags.connectionPoolSize.equalsIgnoreCase(attrName)) {
					poolSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.connectionPoolSize +":"+ poolSize);
				} else if (sourceTags.connectionPoolInitSize.equalsIgnoreCase(attrName)) {
					poolInitSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.connectionPoolInitSize +":"+ poolInitSize);
				} else if (sourceTags.connectionTimeout.equalsIgnoreCase(attrName)) {
					poolTimeout = Long.parseLong(subElement.getTextTrim());
					logger.info(sourceTags.connectionTimeout +":"+ poolTimeout + "ms");
				} else if (sourceTags.connectionRequestTime.equalsIgnoreCase(attrName)) {
					requestTime = Long.parseLong(subElement.getTextTrim());
					logger.info(sourceTags.connectionRequestTime +":"+ requestTime + "ms");
				} else if (sourceTags.connectionTestQuerySql.equalsIgnoreCase(attrName)) {
					connectionTestQuerySql = subElement.getTextTrim();
					logger.info(sourceTags.connectionTestQuerySql +":"+ connectionTestQuerySql);	
				
				} else if (sourceTags.statementCacheSize.equalsIgnoreCase(attrName)) {
					cacheSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.statementCacheSize +":"+ cacheSize);
				} else if (sourceTags.statementBatchSize.equalsIgnoreCase(attrName)) {
					batchSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.statementBatchSize +":"+ batchSize);
				} else if (sourceTags.resultsetFetchSize.equalsIgnoreCase(attrName)) {
					fetchSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.resultsetFetchSize +":"+ fetchSize);
				}else if (sourceTags.showSql.equalsIgnoreCase(attrName)) {
					showSql =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.showSql +":"+ subElement.getTextTrim());			
				}else if (sourceTags.transactionOpen.equalsIgnoreCase(attrName)) {
					transactionOpen =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.transactionOpen +":"+ transactionOpen);
					
				}else if (sourceTags.cacheOpen.equalsIgnoreCase(attrName)) {
					cacheOpen =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.cacheOpen +":"+ cacheOpen);
				}else if (sourceTags.localCacheOpen.equalsIgnoreCase(attrName)) {
					localCacheOpen =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.localCacheOpen +":"+ cacheOpen);
				}else if (sourceTags.transactionIsolation.equalsIgnoreCase(attrName)) {
					isolation = TransactionLevel.getTransactionIsolation(subElement.getTextTrim());
					logger.info(sourceTags.transactionIsolation +":"+ subElement.getTextTrim());
					if(isolation == null){
					 throw new TransactionConfigException("Transaction isolation level config error,right value is[TRANSACTION_READ_UNCOMMITTED,TRANSACTION_READ_COMMITTED,TRANSACTION_REPEATABLE_READ,TRANSACTION_SERIALIZABLE]");
				 }
				} else if (sourceTags.reflectCacheSize.equalsIgnoreCase(attrName)) {
					reflectCacheSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.reflectCacheSize +":"+ subElement.getTextTrim());
				} else if (sourceTags.jdbcSqlDialect.equalsIgnoreCase(attrName)) {
					 try {
						 String className = subElement.getTextTrim();
						 if(!StringUtil.isNull(className)){
						  jdbcDialect = (JdaDialect)BeanUtil.createInstance(className);
						  logger.info(sourceTags.jdbcSqlDialect +":"+ className);
						 }
					} catch(Exception e) {
						throw new DataSourceException("SQL dialect class config error",e);
					}
				}
			}
		}
			
		JdbcSourceInfo sourceInfo = new JdbcSourceInfo(driver,URL,user,password);
		if(poolSize>0)
			sourceInfo.setPoolMaxSize(poolSize);//int
		if(poolInitSize>0)
			sourceInfo.setPoolInitSize(poolInitSize);//int
		if(sourceInfo.getPoolInitSize() > sourceInfo.getPoolMaxSize())
			throw new DataSourceException("Config error,pool init size must be less then max size");
		if(requestTime > 0)
			sourceInfo.setConnectionMaxWaitTime(requestTime);
		if(poolTimeout > 0)
			sourceInfo.setConnectionIdleTimeout(poolTimeout);//long
		if(cacheSize > 0)
			sourceInfo.setStatementCacheSize(cacheSize);//int
		if(batchSize > 0)
			sourceInfo.setBatchUpdateSize(batchSize);//int
		if(fetchSize > 0)
			sourceInfo.setResultFetchSize(fetchSize);//int
		if(reflectCacheSize > 0)
			sourceInfo.setReflectionCacheSize(reflectCacheSize);//int
	
		sourceInfo.setShowSql(showSql);
		sourceInfo.setResultCacheOpen(cacheOpen);
		sourceInfo.setResultLocalCacheOpen(localCacheOpen);
		sourceInfo.setTransactionOpen(transactionOpen);
		sourceInfo.setConnectionTestQuerySql(connectionTestQuerySql);
		sourceInfo.setConnectionPoolClassName(poolClassName);
		if(isolation!=null)
			sourceInfo.setTransactionIsolation(isolation);
	  if(jdbcDialect!=null)
	  	sourceInfo.setJdbcDialect(jdbcDialect);
		return sourceInfo;
	}
	
	/**
	 * 解析Jndi数据源
	 */
	private JndiSourceInfo parseJndiDataSourceInfo(Element element,JdbcSourceTags sourceTags)throws SQLException{
		String name=null,factory=null,provideURL=null;
		String principal=null,credentials=null;
		String jdbcUser=null,jdbcPassword=null;
		int poolSize=0,poolInitSize=0,cacheSize=0,batchSize=0,fetchSize=0;
		long poolTimeout =0,requestTime=0;
		boolean showSql=false;
		boolean cacheOpen=false;
		boolean localCacheOpen=false;
		boolean transactionOpen=true;
		String connectionTestSql=null;
		TransactionLevel isolation =null;
		JdaDialect jdbcDialect=null;
		int reflectCacheSize=0;
		String poolClassName=null;
		
		List infoList = element.getChildren(sourceTags.ATTR_Property);
		for(int i=0,n=infoList.size();i<n;i++) {
			Element subElement = (Element)infoList.get(i);
			String attrName = (String)subElement.getAttributeValue(sourceTags.ATTR_Name);
		  if(!attrName.startsWith(sourceTags.Transaction_Jta)){
				if (sourceTags.Jndi_Name.equalsIgnoreCase(attrName)) {
					name = subElement.getTextTrim();
					logger.info(sourceTags.Jndi_Name +":" + name);
				} else if (sourceTags.Jndi_Factory.equalsIgnoreCase(attrName)) {
					factory = subElement.getTextTrim();
					logger.info(sourceTags.Jndi_Factory +":" + factory);
				} else if (sourceTags.Jndi_ProvideURL.equalsIgnoreCase(attrName)) {
					provideURL = subElement.getTextTrim();
					logger.info(sourceTags.Jndi_ProvideURL +":" + provideURL);
				} else if (sourceTags.Jndi_Principal.equalsIgnoreCase(attrName)) {
					principal = subElement.getTextTrim();
					logger.info(sourceTags.Jndi_Principal +":" + principal);
				} else if (sourceTags.Jndi_Credentials.equalsIgnoreCase(attrName)) {
					credentials = subElement.getTextTrim();
				} else if (sourceTags.Jndi_Jdbc_User.equalsIgnoreCase(attrName)) {
					jdbcUser = subElement.getTextTrim();
					logger.info(sourceTags.Jndi_Jdbc_User+":"+ jdbcUser);
				} else if (sourceTags.Jndi_Jdbc_Password.equalsIgnoreCase(attrName)) {
					jdbcPassword = subElement.getTextTrim();
				} else if (sourceTags.connectionPoolClassName.equalsIgnoreCase(attrName)) {
					poolClassName =subElement.getTextTrim();
					logger.info(sourceTags.connectionPoolClassName +":"+ poolClassName);
				} else if (sourceTags.connectionPoolSize.equalsIgnoreCase(attrName)) {
					poolSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.connectionPoolSize +":"+ poolSize);
				} else if (sourceTags.connectionPoolInitSize.equalsIgnoreCase(attrName)) {
					poolInitSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.connectionPoolInitSize +":"+ poolInitSize);
				} else if (sourceTags.connectionTimeout.equalsIgnoreCase(attrName)) {
					poolTimeout = Long.parseLong(subElement.getTextTrim());
					logger.info(sourceTags.connectionTimeout +":"+ poolTimeout + "ms");
				} else if (sourceTags.connectionRequestTime.equalsIgnoreCase(attrName)) {
					requestTime = Long.parseLong(subElement.getTextTrim());
					logger.info(sourceTags.connectionRequestTime +":"+ requestTime+ "ms");
				} else if (sourceTags.connectionTestQuerySql.equalsIgnoreCase(attrName)) {
					connectionTestSql = subElement.getTextTrim();
					logger.info(sourceTags.connectionTestQuerySql +":"+ connectionTestSql);
				} else if (sourceTags.statementCacheSize.equalsIgnoreCase(attrName)) {
					cacheSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.statementCacheSize +":"+ cacheSize);
				} else if (sourceTags.statementBatchSize.equalsIgnoreCase(attrName)) {
					batchSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.statementBatchSize +":"+ batchSize);
				} else if (sourceTags.resultsetFetchSize.equalsIgnoreCase(attrName)) {
					fetchSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.resultsetFetchSize +":"+ fetchSize);
				
				}else if (sourceTags.showSql.equalsIgnoreCase(attrName)) {
					showSql =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.showSql +":"+ showSql);
					
				}else if (sourceTags.transactionOpen.equalsIgnoreCase(attrName)) {
					transactionOpen =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.transactionOpen +":"+ transactionOpen);
				}else if (sourceTags.cacheOpen.equalsIgnoreCase(attrName)) {
					cacheOpen =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.cacheOpen +":"+ cacheOpen);
				}else if (sourceTags.localCacheOpen.equalsIgnoreCase(attrName)) {
					localCacheOpen =StringUtil.isNull(subElement.getTextTrim())?false:("true".equalsIgnoreCase(subElement.getTextTrim().trim()) || "Y".equalsIgnoreCase(subElement.getTextTrim().trim()));
					logger.info(sourceTags.localCacheOpen +":"+ cacheOpen);
				}else if (sourceTags.transactionIsolation.equalsIgnoreCase(attrName)) {
					isolation = TransactionLevel.getTransactionIsolation(subElement.getTextTrim());
					logger.info(sourceTags.transactionIsolation +":"+ subElement.getTextTrim());
					if(isolation == null){
					 throw new TransactionConfigException("Transaction isolation level config error,right value is[TRANSACTION_READ_UNCOMMITTED,TRANSACTION_READ_COMMITTED,TRANSACTION_REPEATABLE_READ,TRANSACTION_SERIALIZABLE]");
				 }
				} else if (sourceTags.reflectCacheSize.equalsIgnoreCase(attrName)) {
					reflectCacheSize = Integer.parseInt(subElement.getTextTrim());
					logger.info(sourceTags.reflectCacheSize +":"+ subElement.getTextTrim());
				} else if (sourceTags.jdbcSqlDialect.equalsIgnoreCase(attrName)) {
					 try {
						 String className = subElement.getTextTrim();
						 if(!StringUtil.isNull(className)){
						  jdbcDialect = (JdaDialect)BeanUtil.createInstance(className);
						  logger.info(sourceTags.jdbcSqlDialect +":"+ className);
						 }
					} catch(Exception e) {
						throw new DataSourceException("SQL dialect class config error",e);
					}
				}
		  }
		}
			
		JndiSourceInfo sourceInfo = new JndiSourceInfo(name,factory,provideURL,principal,credentials);
		if(jdbcUser!=null)
			sourceInfo.setDbUser(jdbcUser);
		if(jdbcPassword!=null)
			sourceInfo.setDbPassword(jdbcPassword);
		if(poolSize > 0)
			sourceInfo.setPoolMaxSize(poolSize);//int
		if(poolInitSize>0)
			sourceInfo.setPoolInitSize(poolInitSize);//int
		if(sourceInfo.getPoolInitSize() > sourceInfo.getPoolMaxSize())
			throw new DataSourceException("Config error,pool init size must be less then max size");
		if(requestTime > 0)
			sourceInfo.setConnectionMaxWaitTime(requestTime);
		if(poolTimeout > 0)
			sourceInfo.setConnectionIdleTimeout(poolTimeout);//long
		if(cacheSize > 0)
			sourceInfo.setStatementCacheSize(cacheSize);//int
		if(batchSize > 0)
			sourceInfo.setBatchUpdateSize(batchSize);//int
		if(fetchSize > 0)
			sourceInfo.setResultFetchSize(fetchSize);//int
		if(reflectCacheSize > 0)
			sourceInfo.setReflectionCacheSize(reflectCacheSize);//int
		
		sourceInfo.setShowSql(showSql);
		sourceInfo.setResultCacheOpen(cacheOpen);
		sourceInfo.setResultLocalCacheOpen(localCacheOpen);
		sourceInfo.setTransactionOpen(transactionOpen);
		sourceInfo.setConnectionTestQuerySql(connectionTestSql);
		sourceInfo.setConnectionPoolClassName(poolClassName);
	  if(isolation!=null)
			sourceInfo.setTransactionIsolation(isolation);
	  if(jdbcDialect!=null)
	  	sourceInfo.setJdbcDialect(jdbcDialect);
		return sourceInfo;
	}
}

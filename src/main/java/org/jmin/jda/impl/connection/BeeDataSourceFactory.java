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
package org.jmin.jda.impl.connection;

import java.sql.SQLException;

import org.jmin.bee.BeeDataSource;
import org.jmin.bee.BeeDataSourceConfig;
import org.jmin.bee.impl.connection.JdbcPoolConfig;
import org.jmin.bee.impl.connection.JndiPoolConfig;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.JdbcSourceInfo;
import org.jmin.jda.JndiSourceInfo;
import org.jmin.jda.impl.exception.DataSourceException;
import org.jmin.jda.impl.util.StringUtil;

/**
 * 数据库连接池工厂
 * 
 * @author Chris
 * @version 1.0
 */

public final class BeeDataSourceFactory {

	/**
	 * 创建连接池
	 */
	public static BeeDataSource createDataSource(JdaSourceInfo dataSourceInfo)throws SQLException{
		if(dataSourceInfo ==null)
			throw new DataSourceException("Data soruce definition can't be null");
		
		BeeDataSourceConfig poolInfo = null;
		if(dataSourceInfo instanceof JdbcSourceInfo){
			JdbcSourceInfo info = (JdbcSourceInfo)dataSourceInfo;
			JdbcPoolConfig jdbcPoolInfo = new JdbcPoolConfig(
			info.getDbDriver(),
			info.getDbURL(),
			info.getDbUser(),
			info.getDbPassword());
			poolInfo = jdbcPoolInfo;
		}else if(dataSourceInfo instanceof JndiSourceInfo){
			JndiSourceInfo info = (JndiSourceInfo)dataSourceInfo;
			JndiPoolConfig jndiPoolIfo = new JndiPoolConfig( 
			info.getContextName(),
			info.getContextFactory(),
			info.getContextProvideURL(),
			info.getContextPrincipal(),
			info.getContextCredentials());
			
			jndiPoolIfo.setDbUser(info.getDbUser());
			jndiPoolIfo.setDbPassword(info.getDbPassword());
			poolInfo = jndiPoolIfo;
		}else{
			throw new DataSourceException("Unkown data source type");
		}
		
		poolInfo.setPoolInitSize(dataSourceInfo.getPoolInitSize());
		poolInfo.setPoolMaxSize(dataSourceInfo.getPoolMaxSize());
		poolInfo.setConnectionIdleTimeout(dataSourceInfo.getConnectionIdleTimeout());
		poolInfo.setBorrowerMaxWaitTime(dataSourceInfo.getConnectionMaxWaitTime());
		poolInfo.setPreparedStatementCacheSize(dataSourceInfo.getStatementCacheSize());
		poolInfo.setConnectionValidateSQL(dataSourceInfo.getConnectionTestQuerySql());
		if(!StringUtil.isNull(dataSourceInfo.getConnectionPoolClassName()))
		poolInfo.setConnectionPoolClassName(dataSourceInfo.getConnectionPoolClassName());
		poolInfo.setInited(true);
	    return new BeeDataSource(poolInfo);
	}
}

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
package org.jmin.jda.impl.execution.select;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jmin.jda.JdaDialect;
import org.jmin.jda.JdaResultPageList;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.dialect.JdaBaseDialect;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestQueryResult;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.impl.util.StringUtil;

/**
 * 分页执行查询
 * 
 * @author Chris Liao
 */

public class ObjectPageFinder extends BaseFinder{
	
	/**
	 * 执行一个分页查询
	 */
	public JdaResultPageList find(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject,int pageSize,JdaDialect dialect)throws SQLException{
		if(pageSize <=0)
			throw new SqlExecutionException(statement.getSqlId(),"Page size must be more than 0");
		
		SqlRequest request=session.getSqlRequestFactory().createSqlRequest(session,statement,paramObject,SqlOperationType.Select);
		request.setDefinitionType(statement.getSqlType());
		int recordCount=getRecordCount(request);
		int totalSize = recordCount/pageSize;
		int remainSize = recordCount%pageSize; 
		if(remainSize >0)
			totalSize +=1;
	
		ObjectPageList pageList = new ObjectPageList(request,totalSize,pageSize,session,dialect);
		if(totalSize>0)
			pageList.initFirstPage();
		return pageList;
	}
	
	/**
	 * 获得记录总数
	 */
	public int getRecordCount(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject)throws SQLException{
		SqlRequest request=session.getSqlRequestFactory().createSqlRequest(session,statement,paramObject,SqlOperationType.Select);
		request.setDefinitionType(statement.getSqlType());
		return getRecordCount(request);
	}

	/**
	 * 返回某个查询将会返回多少记录
	 */
	private int getRecordCount(SqlRequest request)throws SQLException{
		ResultSet resultSet=null;
		SqlRequestQueryResult queryResult=null;
		JdaSessionImpl session=request.getRequestSession();
		SqlBaseStatement statement = session.getSqlStatement(request.getSqlId());
		Object[] paramValues = request.getParamValues();
		
		try { 
			String countSQL =null;
			String sqlText = request.getSqlText();
			JdaDialect dialect = request.getRequestSession().getJdaDialect();
			if(dialect!=null)
				countSQL = dialect.getRecordCountSQL(sqlText,request.getSqlAttributeTable());
			if(StringUtil.isNull(countSQL))
				countSQL  = new StringBuffer("select count(*) as Record_Size from( ").append(sqlText).append(" )").append(JdaBaseDialect.Table_Jmin_Jda_Record_Size_View).toString();//构造的默认SQL;
			
			request.setSqlText(countSQL);
			Integer count=(Integer)(this.getObjectFromCache(session,statement,countSQL,paramValues,null));//从缓存中获取
			if(count==null){
				queryResult = session.getSqlRequestHandler().handleQueryRequest(request);
				resultSet=queryResult.getResultSet();
				if(resultSet.next()){
					int size =resultSet.getInt(1);
				 	this.putObjectIntoCache(session,statement,countSQL,paramValues,null,new Integer(size));
				 	return size;
				}else{
					throw new SqlExecutionException(request.getSqlId(),"Count query resultSet is null");
				}
			}else{
				return count.intValue();
			}
		}finally{
			if(queryResult!= null){
				CloseUtil.close(queryResult.getResultSet());
				CloseUtil.close(queryResult.getStatement());
			}
		
			if(request.getConnection()!= null){
				session.releaseConnection(request.getConnection());
				request.setConnection(null);
			}
		}
	}
}
		
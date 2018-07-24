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
import java.util.ArrayList;
import java.util.List;

import org.jmin.jda.JdaDialect;
import org.jmin.jda.JdaResultPageList;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestHandler;
import org.jmin.jda.impl.execution.SqlRequestQueryResult;
import org.jmin.jda.impl.execution.worker.ResultObjectFactory;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.CloseUtil;

/**
 * 分页查询列表实现
 * 
 * @author Chris Liao
 */

public class ObjectPageList extends BaseFinder implements JdaResultPageList {
	
	/**
	 * 当前页码
	 */
	private int currentIndex=0;
	
	/**
	 * 分页的记录最大数
	 */
	private int pageSize=10;

	/**
	 * 总页数
	 */
	private int pageTotalSize;

	/**
	 * 当前结果对象列表
	 */
	private List currentDataList;
	
	/**
	 * 是否重新创建过
	 */
	private boolean rebuild =false;
	
	/**
	 * 请求
	 */
	private SqlRequest request;
	
	/**
	 * 方言对象
	 */
	private JdaDialect dialect;
		
	/**
	 * 构造函数
	 */
	public ObjectPageList(SqlRequest request,int totalSize,int pageRowCount,JdaSessionImpl session,JdaDialect dialect){
		this.request=request;
		this.currentIndex=0;//第0页
		this.pageTotalSize=totalSize;
		this.pageSize=pageRowCount;
		this.dialect = dialect;
		this.currentDataList =new ArrayList();
	}
	
	/**
	 * 总页数
	 */
	public int getTotalSize(){
		return pageTotalSize;
	}
	
	 /**
	  * 获取当前页码
	  */
	public int geCurrentIndex(){
		return currentIndex;
	}
	
	/**
	 * 获取当前页面数据
	 */
	public Object[] getCurrentPage(){
		return currentDataList.toArray(new Object[currentDataList.size()]);
	}
	
	/**
	 * 是否为第一页
	 */
	public boolean isFirstPage() {
	 return (currentIndex==1)?true:false;
	}

	/**
	 * 是否为中间页
	 */
	public boolean isMiddlePage() {
		return (1<currentIndex && currentIndex<pageTotalSize)?true:false;
	}

	/**
	 * 是否为最后一页
	 */
	public boolean isLastPage() {
		return (currentIndex==pageTotalSize)?true:false;
	}

	/**
	 * 前进是否可行
	 */
	public boolean isNextPageAvailable() {
		return (1<=currentIndex && currentIndex<pageTotalSize)?true:false;
	}

	/**
	 * 后退是可行
	 */
	public boolean isPreviousPageAvailable() {
		return (1<currentIndex && currentIndex<=pageTotalSize)?true:false;
	}

	/**
	 * 翻到下一页
	 */
	public void movePreviousPage()throws SQLException {
		if(isPreviousPageAvailable()){
			this.loadRecord(currentIndex-1);
		}else{
			throw new SqlExecutionException(request.getSqlId(),"Current page index is First");
		}
	}
	
	/**
	 * 翻到下一页
	 */
	public void moveNextPage()throws SQLException {
		if(isNextPageAvailable()){
			this.loadRecord(currentIndex+1);
		}else{
			throw new SqlExecutionException(request.getSqlId(),"Current page("+currentIndex+")has reach end");
		}
	}

	/**
	 * 跳转到指定页
	 */
	public void moveToPage(int pageNumber)throws SQLException {
		if((1<=pageNumber && pageNumber<=pageTotalSize)){
			this.loadRecord(pageNumber);
		}else{
			throw new SqlExecutionException(request.getSqlId(),"Page index can't be out of range:" + pageTotalSize);
		}
	}
	
	/**
	 * 初试化执行
	 */
	void initFirstPage()throws SQLException{
		this.loadRecord(1);
		this.currentIndex=1;
	}

	/**
	 * 跳转到指定页位置，装载数据
	 */
	private void loadRecord(int pageIndex)throws SQLException{
		if(pageTotalSize ==0)
			throw new SqlExecutionException(request.getSqlId(),"Can't move index on empty record set");
		
		Object[] paramValues=null;
		ResultSet resultSet = null;
		SqlRequestQueryResult queryResult=null;
		String sqlId = request.getSqlId();
		JdaSessionImpl session = request.getRequestSession();
		SqlBaseStatement statement = session.getSqlStatement(sqlId);
		SqlRequestHandler requestHandler=session.getSqlRequestHandler();
		ResultObjectFactory resultObjectFactory=session.getResultObjectFactory();
		if(this.dialect==null)
		 this.dialect=session.getJdaDialect();
		
		try{
		  SqlOperationType sqlOperationType=statement.getSqlType();
		  int targetRowNo=this.getStartRecordNo(pageIndex,pageSize);//获取页的记录位置
		  if(dialect!=null && SqlOperationType.Select.equals(request.getDefinitionType())){
		  	if(!rebuild)
		  		requestHandler.rebuildRequestForPageQuery(request,targetRowNo,pageSize);
		
		   request.setRecordSkipPos(0);
		   request.setRecordMaxRows(0);
		 }else{
		   request.setRecordSkipPos(targetRowNo);
		   request.setRecordMaxRows(pageSize);
		 }
	   
		 paramValues = request.getParamValues();
		 String sqlText=request.getSqlText();
		 Object[] optionals = new Object[]{Integer.valueOf(targetRowNo),Integer.valueOf(pageSize)};
		 List cachedList=(List)this.getObjectFromCache(session,statement,sqlText,paramValues,optionals);//从缓存中获取
		 if(cachedList==null || cachedList.isEmpty()){
			 queryResult=requestHandler.handleQueryRequest(request);
			 resultSet=queryResult.getResultSet();
	
			if(resultSet!=null && targetRowNo>=2){
				this.moveToTargetRow(resultSet,targetRowNo-1,sqlOperationType,dialect,session);//将ResultSet游标移动到目标行前一行
	 
				int readCount=0;
				currentDataList.clear();
				while(resultSet.next()) {
					Object resultObject = resultObjectFactory.readResultObject(session,request.getConnection(),statement,resultSet,null);
					currentDataList.add(resultObject);
					if(pageSize >0 && ++readCount == pageSize)
						break;
				}
				
				this.currentIndex = pageIndex;
				this.putObjectIntoCache(session,statement,sqlText,paramValues,optionals,currentDataList);//将结果放入缓存
			}else{
				throw new SqlExecutionException(sqlId,"Page query resultSet is null");
			}
		}else{
			currentDataList.addAll(cachedList);
		}
		
	}finally {
		if(queryResult!= null){
			CloseUtil.close(queryResult.getResultSet());
			CloseUtil.close(queryResult.getStatement());
		}
		
		if(request!=null && request.getConnection()!= null){
			session.releaseConnection(request.getConnection());
			request.setConnection(null);
		}
	}
 }
	
	/**
	 * 依据页码计算记录记录的位置
	 */
	private int getStartRecordNo(int index,int pageSize){
		return (index-1)*pageSize + 1;
	}
}

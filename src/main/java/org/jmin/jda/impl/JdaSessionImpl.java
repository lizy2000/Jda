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
package org.jmin.jda.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.jmin.jda.JdaCache;
import org.jmin.jda.JdaDialect;
import org.jmin.jda.JdaResultPageList;
import org.jmin.jda.JdaResultRowHandler;
import org.jmin.jda.JdaSession;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.JdaTypeConvertFactory;
import org.jmin.jda.JdaTypeConverter;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.cache.CacheKey;
import org.jmin.jda.impl.cache.CacheManager;
import org.jmin.jda.impl.cache.impl.LruCache;
import org.jmin.jda.impl.cache.impl.MapCache;
import org.jmin.jda.impl.dialect.JdaBaseDialect;
import org.jmin.jda.impl.dynamic.DynSqlBlockParser;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.execution.SqlRequestFactory;
import org.jmin.jda.impl.execution.SqlRequestHandler;
import org.jmin.jda.impl.execution.select.ObjectListFinder;
import org.jmin.jda.impl.execution.select.ObjectRelateFinder;
import org.jmin.jda.impl.execution.update.BatchHandler;
import org.jmin.jda.impl.execution.worker.ParamObjectFactory;
import org.jmin.jda.impl.execution.worker.RelationObjectFactory;
import org.jmin.jda.impl.execution.worker.ResultObjectFactory;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.statement.SqlPropertyCenter;
import org.jmin.jda.impl.transaction.Transaction;
import org.jmin.jda.impl.transaction.TransactionCaheOperation;
import org.jmin.jda.impl.transaction.TransactionException;
import org.jmin.jda.impl.transaction.TransactionManager;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.impl.util.StringUtil;

/**
 * 持久化操作.
 * 
 * SQL可以被看作一个输入(参数)输出(结果）
 * 
 * 一条SQL依照参数对照表进行参数设置,查询结果依据结果参照表进行读取
 * 
 * @author Chris
 * @version 1.0
 */

public class JdaSessionImpl implements JdaSession {
	
	/**
	 * 是否处于打开状态，
	 */
	private boolean closed;
	
	/**
	 * 批量更新操作执行器
	 */
	private BatchHandler batch;
	
	/**
	 * 批量更新是否正在执行
	 */
	private boolean batchInRunning;
	
	/**
	 * 当前事务
	 */
	private Transaction transaction;

	/**
	 * 所属于的SQL Container
	 */
	private JdaContainerImpl sqlContainer;
		
	/**
	 * session一级缓存
	 */
	private JdaCache queryResultCache;
	
	/**
	 * 保存一些事务缓存操作动作,在事务提交后,可按次序执行
	 */
	private List transationCacheOpertionList;
	
	/**
	 * 事物过程中的查询缓存，事物结束后，需要放入正式缓存
	 */
	private Map queryResultTempCacheInTrans;
	
	/**
	 * 反射表达式缓存
	 */
	private JdaCache javaReflectionCache;

	/**
	* 是否支持批量更新
	*/
	private boolean supportBatch,supportBatchChecked;
	
	/**
	* 是否支持批量Fecth,是否检查过可支持Fecth
	*/
	private boolean supportFetch,supportFetchChecked;
	
	/**
	* 是否支持记录精确定位,是否检查过记录精确定位
	*/
	private boolean supportAbsolute,supportAbsoluteChecked;
	
	/**
	 * Constructor
	 */
	public JdaSessionImpl(JdaContainerImpl sqlContainer) {
		this.sqlContainer = sqlContainer;
		this.queryResultCache = new MapCache(100);//一级缓最大允许100个
		this.queryResultTempCacheInTrans=new HashMap();
		this.transationCacheOpertionList= new LinkedList();
		this.javaReflectionCache=new LruCache(sqlContainer.getDataSourceInfo().getReflectionCacheSize());
	}

	/**
	 * 检查当前Session是否有效
	 */
	public boolean isClosed() {
		return this.closed;
	}
	
	/**
	 * 关闭当前Session,使其变为不可用
	 */
	public void close()throws SQLException{
		this.clearCache();
		this.javaReflectionCache.clear();
		this.closed = true;
	}
	
	/**
	 * 插入操作
	 */
	public int insert(String id) throws SQLException {
		return this.executeUpdate(id,null,SqlOperationType.Insert);
	}

	/**
	 * 插入操作
	 */
	public int insert(String id,Object paramObject) throws SQLException {
		return this.executeUpdate(id,paramObject,SqlOperationType.Insert);
	}

	/**
	 * 更新操作
	 */
	public int update(String id) throws SQLException {
		return this.executeUpdate(id, null,SqlOperationType.Update);
	}

	/**
	 * 更新操作
	 */
	public int update(String id,Object paramObject) throws SQLException {
		return this.executeUpdate(id, paramObject,SqlOperationType.Update);
	}

	/**
	 * 删除操作
	 */
	public int delete(String id) throws SQLException {
		return this.executeUpdate(id, null,SqlOperationType.Delete);
	}

	/**
	 * 删除操作
	 */
	public int delete(String id, Object paramObject) throws SQLException {
		return this.executeUpdate(id, paramObject,SqlOperationType.Delete);
	}

	/**
	 * 执行数据改变操作
	 */
	private int executeUpdate(String id,Object paramObject,SqlOperationType operateType) throws SQLException {
		this.validateIsOpen();
	    if(!this.isInTransaction())this.clearCache();
	    return this.sqlContainer.getUpdateHandler().update(this,getSqlStatement(id),paramObject,operateType); 
	}
	
	/**
	 * 查找一个对象，如果出现多个或出错，将抛出异常
	 */
	public Object findOne(String id) throws SQLException {
		return this.findOne(id, null);
	}

	/**
	 * 查找一个对象，如果出现多个或出错，将抛出异常
	 */
	public Object findOne(String id, Object paramObject) throws SQLException {
		return this.findOne(id, paramObject, null);
	}

	/**
	 * 查找一个对象，如果出现多个或出错，将抛出异常
	 */
	public Object findOne(String id,Object paramObject,Object resultObject)throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getObjectFinder().find(this,getSqlStatement(id),paramObject,resultObject);
	}

	/**
	 * 查找对象列表
	 */
	public List findList(String id) throws SQLException {
		return this.findList(id,0,0);
	}
	
	/**
	 * 查找对象列表
	 */
	public List findList(String id,Object paramObject) throws SQLException {
		return this.findList(id, paramObject,0,0);
	}

	/**
	 * 获取记录的结果记录数
	 */
	public int getResultSize(String id) throws SQLException {
		return this.getResultSize(id, null);
	}

	/**
	 * 获取记录的结果记录数
	 */
	public int getResultSize(String id, Object paramObject) throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getObjectPageFinder().getRecordCount(this, getSqlStatement(id), paramObject);
	}
	
	/**
	 * 查找对象列表，skip跳动位置，收集Rownumber记录
	 */
	public List findList(String id, int rowNo, int pageSize)throws SQLException {
	 return this.findList(id,rowNo,pageSize,null);
	}
	
	/**
	 * 查找对象列表，skip跳动位置，收集Rownumber记录
	 */
	public List findList(String id, int rowNo, int pageSize,JdaDialect dialect)throws SQLException {
		return this.findList(id,null,rowNo,pageSize,dialect);
	}
	
	/**
	 * 查找对象列表,skip跳动位置，收集Rownumber记录
	 */
	public List findList(String id,Object paramObject,int rowNo,int paeSize)throws SQLException {
		return this.findList(id,paramObject,rowNo,paeSize,null);
	}
	
	/**
	 * 查找对象列表,skip跳动位置，收集Rownumber记录
	 */
	public List findList(String id,Object paramObject,int rowNo,int pageSize,JdaDialect dialect)throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getObjectListFinder().find(this,getSqlStatement(id),paramObject,rowNo,pageSize,dialect);
	}
	
	
	
	

	/**
	 * 查找对象Map,keyPropertyName作Key属性
	 */
	public Map findMap(String id,String keyPropertyName)throws SQLException {
		return this.findMap(id,null,keyPropertyName,null);
	}

	/**
	 * 查找对象Map,keyPropertyName作Key属性
	 */
	public Map findMap(String id,Object paramObject,String keyPropertyName)throws SQLException {
		return this.findMap(id,paramObject,keyPropertyName,null);
	}

	/**
	 * 查找对象Map,keyPropertyName作Key属性,keyPropValue属性为见键值
	 */
	public Map findMap(String id,Object paramObject,String keyPropName,String valuePropName) throws SQLException {
		this.validateIsOpen();
		if(StringUtil.isNull(keyPropName))
			throw new SqlExecutionException(id,"Result map key property name can't be null");
		if(valuePropName!=null && StringUtil.isNull(keyPropName))
			throw new SqlExecutionException(id,"Result map value property name can't be null");
		return this.sqlContainer.getObjectMapFinder().find(this,getSqlStatement(id),paramObject,keyPropName,valuePropName);
	}

	/**
	 * 翻页查询
	 */
	public JdaResultPageList findPageList(String id, int pageSize) throws SQLException {
		return this.findPageList(id, pageSize, null);
	}

	/**
	 * 翻页查询
	 */
	public JdaResultPageList findPageList(String id, int pageSize, JdaDialect dialect) throws SQLException {
		return this.findPageList(id, null, pageSize, dialect);
	}

	/**
	 * 翻页查询
	 */
	public JdaResultPageList findPageList(String id, Object paramObject, int pageSize) throws SQLException {
		return this.findPageList(id, paramObject, pageSize, null);
	}

	/**
	 * 翻页查询
	 */
	public JdaResultPageList findPageList(String id, Object paramObject, int pageSize, JdaDialect dialect)
			throws SQLException {
		this.validateIsOpen();
		 return this.sqlContainer.getObjectPageFinder().find(this,getSqlStatement(id),paramObject,pageSize,dialect);
    }
	
	/**
	 * 通过rowhandlder进行查询
	 */
	public void findWithRowHandler(String id, JdaResultRowHandler rowHandler) throws SQLException {
		this.findWithRowHandler(id, null, rowHandler);
	}

	/**
	 * 通过rowhandlder进行查询
	 */
	public void findWithRowHandler(String id, Object paramObject, JdaResultRowHandler rowHandler) throws SQLException {
		this.validateIsOpen();
		if (rowHandler == null)
			throw new SqlExecutionException(id, "Row handler can't be null");
		this.sqlContainer.getObjectRowFinder().find(this, getSqlStatement(id), paramObject, rowHandler);
	}	
	
	
	/**
	 * 执行用户自定义SQL,skip跳动位置
	 */
	public int updateForUserSql(String sql) throws SQLException {
		this.validateIsOpen();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			SqlOperationType type = this.sqlContainer.getSqlOperateType(null, sql);
			if (!type.equals(SqlOperationType.Select) && !type.equals(SqlOperationType.Procedure)) {
				if(!this.isInTransaction())this.clearCache();
				
				conn = this.getConnection();
				ps = conn.prepareStatement(sql);
				int code = ps.executeUpdate();
				return code;
			} else {
				throw new SqlExecutionException(
						"User self-defination update sql not support:select type or procedure type");
			}
		} finally {
			CloseUtil.close(ps);
			this.releaseConnection(conn);
		}
	}
	
	/**
	 * 获取用户自定义SQL记录的结果记录数
	 */
	public int getResultSizeForUserSql(String sql) throws SQLException {
		this.validateIsOpen();
		Connection conn = null;
		ResultSet res = null;
		PreparedStatement pst = null;

		try {
			SqlOperationType type = this.sqlContainer.getSqlOperateType(null, sql);
			if (type.equals(SqlOperationType.Select)) {
				JdaDialect dialect = this.getJdaDialect();
				String countSQL = null;
				if (dialect != null)
					countSQL = dialect.getRecordCountSQL(sql, new SqlPropertyCenter(new Properties()));
				if (StringUtil.isNull(countSQL))
					countSQL = new StringBuffer("select count(*) as Record_Size from( ").append(sql).append(" )")
							.append(JdaBaseDialect.Table_Jmin_Jda_Record_Size_View).toString();// 构造的默认SQL;

				conn = this.getConnection();
				pst = conn.prepareStatement(countSQL);
				res = pst.executeQuery();
				if (res != null && res.next()) {
					return res.getInt(1);
				} else {
					throw new SqlExecutionException("Count query resultSet is null");
				}
			} else {
				throw new SqlExecutionException("User self-defination query sql just support:select type");
			}
		} finally {
			CloseUtil.close(res);
			CloseUtil.close(pst);
			this.releaseConnection(conn);
		}
	}

	/**
	 * 查找用户自定义SQL列表,skip跳动位置
	 */
	public List findListForUserSql(String sql) throws SQLException {
		return this.findListForUserSql(sql, 0, 0);
	}

	/**
	 * 查找用户自定义SQL列表,skip跳动位置
	 */
	public List findListForUserSql(String sql, int rowNo, int pageSize) throws SQLException {
		this.validateIsOpen();
		Connection con = null;
		PreparedStatement ps = null;
		java.sql.ResultSet re = null;
		List resultList = new ArrayList();

		try {
			String pageSql = sql;
			if (rowNo <= 0 && pageSize > 0)
				rowNo = 1;
			SqlOperationType type = this.sqlContainer.getSqlOperateType(null, sql);
			if (type.equals(SqlOperationType.Select)) {
				JdaDialect dialect = this.getJdaDialect();
				if (rowNo > 0 && pageSize > 0 && dialect != null) {// 当前SQL是分页查询，需要重新改造Request
					pageSql = dialect.getPageQuerySql(sql, rowNo, pageSize, new SqlPropertyCenter(new Properties()));// 构造出分页查询的SQL
				}

				con = this.getConnection();
				ps = con.prepareStatement(pageSql);
				re = ps.executeQuery();

				if (rowNo >= 2)
					this.getObjectListFinder().moveToTargetRow(re, rowNo - 1, type, dialect, this);// 将ResultSet游标移动到目标行前一行

				int readCount = 0;
				ResultSetMetaData metaData = re.getMetaData();
				while (re.next()) {
					Map rowMap = new LinkedHashMap();
					resultList.add(rowMap);
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						rowMap.put(metaData.getColumnName(i), re.getObject(metaData.getColumnName(i)));
					}

					if (pageSize > 0 && ++readCount == pageSize)
						break;
				}
			} else {
				throw new SqlExecutionException("User self-defination query sql just support:select type");
			}
		} finally {
			CloseUtil.close(re);
			CloseUtil.close(ps);
			this.releaseConnection(con);
		}
		return resultList;
	}
	
  
	/**
	 * 是否已在批量执行中
	 */
	public boolean isInBatch() throws SQLException {
		return (batch != null) ? true : false;
	}

	/**
	 * 开始执行批量操作
	 */
	public void startBatch() throws SQLException {
		this.validateIsOpen();
		if (!supportBatchChecked) {
			Connection con = this.getConnection();
			this.supportBatch = this.sqlContainer.getUpdateHandler().isSupportBatchUpdate(con);
			this.supportBatchChecked = true;
			this.releaseConnection(con);
		}
		
		
		if(!this.isInTransaction())this.clearCache();
		if (supportBatchChecked && !supportBatch)
			throw new SQLException("Batch is not supported");
		else if (batch != null)
			throw new SQLException("A batch is in processing");
		else
			this.batch = new BatchHandler(this.sqlContainer.getDataSourceInfo().getBatchUpdateSize());
	}

	/**
	 * 执行批量操作
	 */
	public int executeBatch() throws SQLException {
		this.validateIsOpen();
		if (batch == null) {
			throw new SQLException("Batch update not begin");
		} else {
			Connection connection = null;
			try {
				batchInRunning = true;
				if(!this.isInTransaction())this.clearCache();
				
				connection = this.getConnection();
				return this.batch.execute(connection, this);
			} finally {
				this.batch.clear();
				this.batch = null;
				this.batchInRunning = false;
				this.releaseConnection(connection);
			}
		}
	}

	/**
	 * 放弃执行批量操作
	 */
	public void discardBatch() throws SQLException {
		if (this.batchInRunning) {
			throw new SQLException("Batch is running,can't be discarded");
		}
		
		if(!this.isInTransaction())this.clearCache();
		if (this.batch != null && !this.batchInRunning) {
			this.batch.clear();
			this.batch = null;
		}
	}
  
	/**
	 * 清理session一级缓存
	 */
	public void clearCache() throws SQLException {
		this.validateIsOpen();
	    this.queryResultCache.clear();//一级缓存
		this.transationCacheOpertionList.clear();
		this.queryResultTempCacheInTrans.clear();
	}
 
	/**
	 * Session中是否存在事物中
	 */
	public boolean isInTransaction() throws SQLException {
		this.validateIsOpen();
		if (this.isTransactionOpen())
			return (this.transaction != null);
		else
			return false;
	}

	/**
	 * 开始一个事务
	 */
	public void beginTransaction() throws SQLException {
		this.beginTransaction(sqlContainer.getDataSourceInfo().getTransactionIsolation().getIsolationCode());
	}

	/**
	 * 开始一个事务
	 */
	public void beginTransaction(int isolation) throws SQLException {
		this.validateIsOpen();
		if (this.isTransactionOpen()) {
			if (this.transaction != null) {
				throw new TransactionException("Session has been in a transtion");
			} else {
				this.clearCache();
				this.transaction = this.getTransactionManager().begin(this.getConnection(), isolation);
			}
		}
	}

	/**
	 * 提交一个事务
	 */
	public void commitTransaction() throws SQLException {
		try {
			this.validateIsOpen();
			if (this.isTransactionOpen()) {
				if (this.transaction == null) {
					throw new TransactionException("No transaction with the session");
				} else {
					this.queryResultCache.clear();//一级缓存
					this.queryResultTempCacheInTrans.clear();
					 
					this.transaction.commit();//提交事务
					
					Iterator itor= transationCacheOpertionList.iterator();
					while(itor.hasNext()){
						TransactionCaheOperation opertion = (TransactionCaheOperation)itor.next();
						itor.remove();
						if(opertion.isClearInd()){
							this.getCacheManager().clearCache(opertion.getFlushCacheId());
						}else{
							this.queryResultCache.put(opertion.getCacheKey(), opertion.getCacheValue());//一级缓存
							this.getCacheManager().putObject(opertion.getCacheId(), opertion.getCacheKey(), opertion.getCacheValue());
						}
					}
				}
			}
		} finally {
			if (this.isTransactionOpen()) {
				this.transationCacheOpertionList.clear();
				this.clearCurrentTransaction();
			}
		}
	}

	/**
	 * 回滚一个事务
	 */
	public void rollbackTransaction() throws SQLException {
		try {
			this.validateIsOpen();
			if (this.isTransactionOpen()) {
			    this.clearCache();
			    
				if (this.transaction == null) {
					throw new TransactionException("No transaction with the session");
				} else {
					this.transaction.rollback();
				}
			}
		} finally {
			if (this.isTransactionOpen()){
				this.clearCurrentTransaction();
			}
		}
	}
	
	/**
	 * 获取一个可用的连接
	 */
	public Connection getConnection() throws SQLException {
		try {
			this.validateIsOpen();
			if (this.isTransactionOpen() && this.transaction != null)
				return this.transaction.getConnection();
			else {
				return this.getDataSource().getConnection();
			}
		} catch (TransactionException e) {
			throw new SQLException("Internal transaction error");
		}
	}
	
	/**
	 * 获得数据源定义信息
	 */
	public JdaSourceInfo getJdaSourceInfo() throws SQLException {
		try {
			JdaSourceInfo info = (JdaSourceInfo) this.sqlContainer.getDataSourceInfo().clone();
			return info;
		} catch (CloneNotSupportedException e) {
			throw new SQLException(e);
		}
	}

	/**
	 * 释放一个可用的连接
	 */
	public void releaseConnection(Connection con) throws SQLException {
		this.validateIsOpen();
		if(!this.isInTransaction())
			CloseUtil.close(con);
	}

	/**
	 * 是否为直接映射类型
	 */
	public BatchHandler getBatchUpdateList() throws SQLException {
		this.validateIsOpen();
		return this.batch;
	}
	
	// **********************************容器扩展方法*************************************
	/**
	 * 是否为静态SQL
	 */
	public boolean isStaticSql(String id) throws SQLException {
		return this.sqlContainer.isStaticSql(id);
	}

	/**
	 * 是否为动态SQL
	 */
	public boolean isDynamicSql(String id) throws SQLException {
		return this.sqlContainer.isDynamicSql(id);
	}

	/**
	 * 获取一个SQL定义
	 */
	public SqlBaseStatement getSqlStatement(String id) throws SQLException {
		return this.sqlContainer.getSqlStatement(id);
	}

	/**
	 * 执行SQL是否需要打印
	 */
	public boolean needShowSql() {
		return this.sqlContainer.getDataSourceInfo().isShowSql();
	}

	/**
	 * 获取一个SQL定义
	 */
	public JdaDialect getJdaDialect() {
		return this.sqlContainer.getDataSourceInfo().getJdbcDialect();
	}

	/**
	 * 记录Fectch size
	 */
	public int getResultSetFetchSize() {
		return this.sqlContainer.getDataSourceInfo().getResultFetchSize();
	}

	/**
	 * 获得参数持久器
	 */
	public JdaTypePersister getTypePersister(Class type) throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getTypePersister(type);
	}

	/**
	 * 获得参数持久器
	 */
	public JdaTypePersister getTypePersister(Class type, String jdbcName) throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getTypePersister(type, jdbcName);
	}

	/**
	 * 是否包含参数持久器
	 */
	public boolean supportsPersisterType(Class type) throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.containsTypePersister(type);
	}

	/**
	 * 是否包含参数持久器
	 */
	public boolean supportsPersisterType(Class type, String jdbcName) throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.containsTypePersister(type, jdbcName);
	}

	/**
	 * 是否包含类型转换器
	 */
	public boolean supportsConversionType(Class type) throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.containsTypeConverter(type);
	}

	/**
	 * 获得类型转换器
	 */
	public JdaTypeConverter getTypeConverter(Class type) throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getTypeConverter(type);
	}

	/**
	 * 获得类型转换器
	 */
	public JdaTypeConvertFactory getTypeConvertFactory() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getTypeConvertFactory();
	}

	/**
	 * 缓存管理器
	 */
	public CacheManager getCacheManager() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getCacheManager();
	}

	/**
	 * 参数辅助对象
	 */
	public ParamObjectFactory getParamObjectFactory() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getParamObjectFactory();
	}

	/**
	 * 结果辅助对象
	 */
	public ResultObjectFactory getResultObjectFactory() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getResultObjectFactory();
	}

	/**
	 * 关联辅助对象
	 */
	public RelationObjectFactory getRelationObjectFactory() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getRelationObjectFactory();
	}

	/**
	 * List对象查找器
	 */
	public ObjectListFinder getObjectListFinder() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getObjectListFinder();
	}

	/**
	 * 关联查找器
	 */
	public ObjectRelateFinder getObjectRelateFinder() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getObjectRelateFinder();
	}

	/**
	 * 获得动态SQL块解析
	 */
	public DynSqlBlockParser getDynSqlBlockParser() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getDynSqlBlockParser();
	}

	/**
	 * 获得sql请求构造工厂
	 */
	public SqlRequestFactory getSqlRequestFactory() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getSqlRequestFactory();
	}

	/**
	 * 获得sql请求处理器
	 */
	public SqlRequestHandler getSqlRequestHandler() throws SQLException {
		this.validateIsOpen();
		return this.sqlContainer.getSqlRequestHandler();
	}

	/**
	 * 获取ognl表达式缓存
	 */
	public Object getReflectExpression(String expression) throws SQLException {
		this.validateIsOpen();
		return this.javaReflectionCache.get(expression);
	}

	/**
	 * 放入ognl表达式缓存
	 */
	public void putReflectExpression(String expression, Object expressionObject) throws SQLException {
		this.validateIsOpen();
		this.javaReflectionCache.put(expression, expressionObject);
	}

	/**
	 * 从缓存中读取缓存(包括事务中调用）
	 */
	public Object getObjectFromCache(String cacheId,CacheKey key) throws SQLException {
		Object value = null;
		if (this.isInTransaction()) // 在事务中，有可能存在在于临时缓存中
			value = this.queryResultTempCacheInTrans.get(key);
		if (value == null && this.isResultCacheOpen() && !StringUtil.isNull(cacheId))
			value = this.getCacheManager().getObject(cacheId, key);
		if (value == null && this.isResultLocalCacheOpen())//尝试从本地缓存中读取
			value = this.queryResultCache.get(key);
		return value;
	}

	/**
	 * 将结果放入缓存(包括事务中调用）
	 */
	public void putObjectToCache(String cacheId,CacheKey key,Object value) throws SQLException {
		if (value != null) {
			if (this.isInTransaction()) { // 如果当期处于事务中，存放于事务临时缓存中，等事务结束后，放入Cache中
				if (this.isResultCacheOpen() && !StringUtil.isNull(cacheId)){//等待事务提交后,再放入二级缓存
					TransactionCaheOperation opertion = new TransactionCaheOperation(cacheId,key,value);
					this.queryResultTempCacheInTrans.put(key, value);
					this.transationCacheOpertionList.add(opertion);
				}
			} else {
				if(this.isResultLocalCacheOpen()) 
			      this.queryResultCache.put(key,value);
			   if (this.isResultCacheOpen() && !StringUtil.isNull(cacheId))
				  this.getCacheManager().putObject(cacheId,key,value);
			}
		}
	}
	
	/**
	 * 清理二级缓存
	 */
	public void clearApplicationCache(String cacheId) throws SQLException {
		if (!StringUtil.isNull(cacheId)) {
			if (this.isResultCacheOpen()) {
				if (this.isInTransaction())// 如果当期处于事务中，存放于事务临时缓存中，等事提交后再清理
					this.transationCacheOpertionList.add(new TransactionCaheOperation(cacheId));
			    else
				  this.getCacheManager().clearCache(cacheId);
			}
		}
	}
	
	/************** 检查驱动是否支持 ************************************/
	/**
	 * 缓存是否开放
	 */
	public boolean isResultCacheOpen() {
		return this.sqlContainer.getDataSourceInfo().isResultCacheOpen();
	}
	
	/**
	 * 本地缓存是否开放
	 */
	public boolean isResultLocalCacheOpen() {
		return this.sqlContainer.getDataSourceInfo().isResultLocalCacheOpen();
	}
	
	/**
	 * 是否支持批量更新
	 */
	public boolean supportBatch() {
		return supportBatch;
	}

	/**
	 * 是否支持批量更新
	 */
	public void setSupportBatch(boolean support) {
		this.supportBatch = support;
	}

	/**
	 * 是否支持批量Fecth,是否检查过可支持Fecth
	 */
	public boolean supportFetch() {
		return supportFetch;
	}

	/**
	 * 是否支持批量Fecth,是否检查过可支持Fecth
	 */
	public void setSupportFetch(boolean support) {
		this.supportFetch = support;
	}

	/**
	 * 是否支持批量Fecth,是否检查过可支持Fecth
	 */
	public boolean supportFetchChecked() {
		return supportFetchChecked;
	}

	/**
	 * 是否支持批量Fecth,是否检查过可支持Fecth
	 */
	public void setSupportFetchChecked(boolean checked) {
		this.supportFetchChecked = checked;
	}

	/**
	 * 是否支持记录精确定位,是否检查过记录精确定位
	 */
	public boolean isSupportAbsolute() {
		return supportAbsolute;
	}

	/**
	 * 是否支持记录精确定位,是否检查过记录精确定位
	 */
	public void setSupportAbsolute(boolean supportAbsolute) {
		this.supportAbsolute = supportAbsolute;
	}

	/**
	 * 是否支持记录精确定位,是否检查过记录精确定位
	 */
	public boolean isSupportAbsoluteChecked() {
		return supportAbsoluteChecked;
	}

	/**
	 * 是否支持记录精确定位,是否检查过记录精确定位
	 */
	public void setSupportAbsoluteChecked(boolean supportAbsoluteChecked) {
		this.supportAbsoluteChecked = supportAbsoluteChecked;
	}

	/**
	 * 转换对象
	 */
	public Object convert(Object ob, Class type) throws SQLException {
		return this.sqlContainer.convertObject(ob, type);
	}

	/** *********************************************************私有方法********************************************************* */
	/**
	 * 获取数据库的连接池
	 */
	private DataSource getDataSource() {
		return this.sqlContainer.getDataSource();
	}

	/**
	 * 获得数据源信息
	 */
	private TransactionManager getTransactionManager() {
		return this.sqlContainer.getTransactionManager();
	}

	/**
	 * 检查当前Session是否处于打开状态
	 */
	private void validateIsOpen() throws SQLException {
		if (this.closed)
			throw new SQLException("Invalid opertion,the session has been closed!");
	}

	/**
	 * 查看事务是否开放
	 */
	private boolean isTransactionOpen() {
		return this.sqlContainer.getDataSourceInfo().isTransactionOpen();
	}

	/**
	 * 清理当前事务
	 */
	private void clearCurrentTransaction() {
		try {
			if (this.transaction != null) {
				Connection con = transaction.getConnection();
				this.transaction.clear();
				con.close();
			}
		} catch (SQLException e) {

		} finally {
			this.transaction = null;
		}
	}
}

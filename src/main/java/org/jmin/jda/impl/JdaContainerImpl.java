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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.jmin.bee.BeeDataSource;
import org.jmin.jda.JdaCache;
import org.jmin.jda.JdaCacheInfo;
import org.jmin.jda.JdaCacheType;
import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaSession;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.JdaTypeConvertFactory;
import org.jmin.jda.JdaTypeConverter;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.cache.CacheManager;
import org.jmin.jda.impl.connection.BeeDataSourceFactory;
import org.jmin.jda.impl.converter.JdaTypeBaseConverter;
import org.jmin.jda.impl.converter.JdaTypeConvertFactoryImpl;
import org.jmin.jda.impl.converter.base.BoolConverter;
import org.jmin.jda.impl.converter.base.ByteConverter;
import org.jmin.jda.impl.converter.base.BytesConverter;
import org.jmin.jda.impl.converter.base.CharConverter;
import org.jmin.jda.impl.converter.base.DoubleConverter;
import org.jmin.jda.impl.converter.base.FloatConverter;
import org.jmin.jda.impl.converter.base.IntegerConverter;
import org.jmin.jda.impl.converter.base.LongConverter;
import org.jmin.jda.impl.converter.base.ShortConverter;
import org.jmin.jda.impl.converter.base.StringConverter;
import org.jmin.jda.impl.converter.blob.BlobConverter;
import org.jmin.jda.impl.converter.clob.ClobConverter;
import org.jmin.jda.impl.converter.date.CalendarConverter;
import org.jmin.jda.impl.converter.date.DateConverter;
import org.jmin.jda.impl.converter.date.DateTimeConverter;
import org.jmin.jda.impl.converter.date.DateTimestampConverter;
import org.jmin.jda.impl.converter.date.UtilDateConverter;
import org.jmin.jda.impl.converter.math.BigDecimalConverter;
import org.jmin.jda.impl.converter.math.BigIntegerConverter;
import org.jmin.jda.impl.dynamic.DynSqlBlockParser;
import org.jmin.jda.impl.dynamic.DynTagValidator;
import org.jmin.jda.impl.exception.ObjectCacheException;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.exception.SqlDefinitionException;
import org.jmin.jda.impl.exception.SqlDynTagException;
import org.jmin.jda.impl.execution.SqlRequestFactory;
import org.jmin.jda.impl.execution.SqlRequestHandler;
import org.jmin.jda.impl.execution.select.ObjectFinder;
import org.jmin.jda.impl.execution.select.ObjectIterateFinder;
import org.jmin.jda.impl.execution.select.ObjectListFinder;
import org.jmin.jda.impl.execution.select.ObjectMapFinder;
import org.jmin.jda.impl.execution.select.ObjectPageFinder;
import org.jmin.jda.impl.execution.select.ObjectRelateFinder;
import org.jmin.jda.impl.execution.select.ObjectRowFinder;
import org.jmin.jda.impl.execution.update.UpdateHandler;
import org.jmin.jda.impl.execution.worker.ParamObjectFactory;
import org.jmin.jda.impl.execution.worker.RelationObjectFactory;
import org.jmin.jda.impl.execution.worker.ResultObjectFactory;
import org.jmin.jda.impl.mapping.param.ParamMapImpl;
import org.jmin.jda.impl.mapping.param.ParamUnitImpl;
import org.jmin.jda.impl.mapping.param.ParamValidator;
import org.jmin.jda.impl.mapping.result.RelationUnitImpl;
import org.jmin.jda.impl.mapping.result.ResultMapImpl;
import org.jmin.jda.impl.mapping.result.ResultUnitImpl;
import org.jmin.jda.impl.mapping.result.ResultValidator;
import org.jmin.jda.impl.persister.ObjectHandler;
import org.jmin.jda.impl.persister.base.BooleanHandler;
import org.jmin.jda.impl.persister.base.ByteHandler;
import org.jmin.jda.impl.persister.base.BytesHandler;
import org.jmin.jda.impl.persister.base.DoubleHandler;
import org.jmin.jda.impl.persister.base.FloatHandler;
import org.jmin.jda.impl.persister.base.IntegerHandler;
import org.jmin.jda.impl.persister.base.LongHandler;
import org.jmin.jda.impl.persister.base.ShortHandler;
import org.jmin.jda.impl.persister.base.StringHandler;
import org.jmin.jda.impl.persister.blob.BlobHandler;
import org.jmin.jda.impl.persister.clob.ClobHandler;
import org.jmin.jda.impl.persister.date.CalendarDateHandler;
import org.jmin.jda.impl.persister.date.CalendarDateTimeHandler;
import org.jmin.jda.impl.persister.date.CalendarDateTimestampHandler;
import org.jmin.jda.impl.persister.date.DateHandler;
import org.jmin.jda.impl.persister.date.DateTimeHandler;
import org.jmin.jda.impl.persister.date.DateTimestampHandler;
import org.jmin.jda.impl.persister.date.UtilDateHandler;
import org.jmin.jda.impl.persister.date.UtilDateTimeHandler;
import org.jmin.jda.impl.persister.date.UtilDateTimestampHandler;
import org.jmin.jda.impl.persister.math.BigDecimalHandler;
import org.jmin.jda.impl.persister.math.BigIntegerHandler;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlDynStatement;
import org.jmin.jda.impl.statement.SqlFieldTypes;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.statement.SqlPropertyCenter;
import org.jmin.jda.impl.statement.SqlStaticStatement;
import org.jmin.jda.impl.transaction.TransactionManager;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.impl.util.Symbols;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.RelationUnit;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.mapping.ResultUnit;
import org.jmin.jda.statement.DynTag;
import org.jmin.jda.statement.SqlPropertyTable;
import org.jmin.jda.statement.tag.ChooseTag;
import org.jmin.jda.statement.tag.ForeachTag;
import org.jmin.jda.statement.tag.IterateTag;
import org.jmin.jda.statement.tag.TextTag;
import org.jmin.log.LogPrinter;

/**
 * SQL映射容器工厂实现
 * 
 * @author Chris liao
 */

public class JdaContainerImpl implements JdaContainer{

	/**
	 *存放jdbc Typ映射
	 */
	private Map jdbcTypeMap;
	
	/**
	 *存放jdbc的持久器
	 */
	private Map paramPersisterMap;
	
	/**
	 * 存放SQL描述定义
	 */
	private Map sqlDefinitionMap;
	
	/**
	 * 数据源定义
	 */
	private JdaSourceInfo dataSourceInfo;

	/**
	 * 连接代理池
	 */
	private BeeDataSource beeDataSource;
	
	/**
	 * 事务管理
	 */
	private TransactionManager transactionManager;
	
	/**
	 *缓存管理器
	 */
	private CacheManager queryResultCacheManager;
	
	
	/**
	 * 参数映射验证
	 */
	private ParamValidator paramValidator = new ParamValidator();
	
	/**
	 * 结果映射验证
	 */
	private ResultValidator resultValidator = new ResultValidator();
	
	/**
	 * 参数辅助对象
	 */
	private ParamObjectFactory paramObjectFactory = new ParamObjectFactory();
	
	/**
	 * 结果辅助对象
	 */
	private ResultObjectFactory resultObjectFactory = new ResultObjectFactory();
	
	/**
	 * 关联辅助对象
	 */
	private RelationObjectFactory relationObjectFactory = new RelationObjectFactory();

	/**
	 * 动态SQL检查器
	 */
	private DynTagValidator dynTagValidator = new DynTagValidator();

	/**
	 * 动态SQL块解析
	 */
	private DynSqlBlockParser dynSqlBlockParser = new DynSqlBlockParser();
	
	/**
	 * sql请求构造工厂
	 */
	private SqlRequestFactory sqlRequestFactory = new SqlRequestFactory();
	
	/**
	 * sql请求处理器
	 */
	private SqlRequestHandler sqlRequestHandler = new SqlRequestHandler();
	
	/**
	 * sql Update处理器
	 */
	private UpdateHandler updateHandler = new UpdateHandler();
	
	
	/**
	 * 单个对象查找
	 */
	private ObjectFinder objectFinder = new ObjectFinder();
	
	/**
	 * List对象查找
	 */
	private ObjectListFinder objectListFinder = new ObjectListFinder();
	
	/**
	 * 叠达器查询
	 */
	private ObjectIterateFinder objectIterateFinder = new ObjectIterateFinder();
	
	/**
	 * 查询，结果返回一个Map
	 */
	private ObjectMapFinder objectMapFinder = new ObjectMapFinder();
	
	/**
	 * 分页执行查询
	 */
	private ObjectPageFinder objectPageFinder=new ObjectPageFinder();
	
	/**
	 * 关联查询
	 */
	private ObjectRelateFinder objectRelateFinder=new ObjectRelateFinder();
	
	/**
	 * 执行RowHandler操作
	 */
	private ObjectRowFinder objectRowFinder = new ObjectRowFinder();
	
	/**
	 * 转换器列表
	 */
	private JdaTypeConvertFactoryImpl typeConvertFactory = new JdaTypeConvertFactoryImpl();
	
	/**
	 * logger
	 */
	private LogPrinter logger = LogPrinter.getLogPrinter(JdaContainerImpl.class);
	
	/**
	 * 构造函数
	 */
	public JdaContainerImpl(JdaSourceInfo dataSourceInfo)throws SQLException{
		this.jdbcTypeMap=new HashMap();
		this.paramPersisterMap=new HashMap();
		this.sqlDefinitionMap=new HashMap();
		
		this.registerDefaultJdbcType();
		this.registerDefaultConverter();
		this.registerDefaultParamPersister();
		this.queryResultCacheManager = new CacheManager();
		this.dataSourceInfo = dataSourceInfo;
		this.beeDataSource =BeeDataSourceFactory.createDataSource(dataSourceInfo);
		this.transactionManager= new TransactionManager(dataSourceInfo.getUserTransactionInfo());
	}

	/**
	 * 是否包含表达式
	 */
	public boolean containsSql(String id)throws SQLException{
		return this.sqlDefinitionMap.containsKey(id);
	}
	
	/**
	 * 是否为静态SQL
	 */
	public boolean isStaticSql(String id)throws SQLException{
		SqlBaseStatement statement = this.getSqlStatement(id);
		return (statement instanceof SqlStaticStatement);
	}

	/**
	 * 是否为动态SQL
	 */
	public boolean isDynamicSql(String id)throws SQLException{
		SqlBaseStatement statement = this.getSqlStatement(id);
		return (statement instanceof SqlDynStatement);
	}
	
	/**
	 * 注销SQL
	 */
	public void unregisterSql(String id)throws SQLException{
		this.sqlDefinitionMap.remove(id);
		logger.debug("Unregistered sql with id:"+id);
	}
	
	/**
	 * 注册SQL的表达式
	 */
	public void registerStaticSql(String id,String sql)throws SQLException{
		this.registerStaticSql(id,sql,null,null);
	}
	
	/**
	 * 注册SQL的表达式
	 */
	public void registerStaticSql(String id,String sql,ParamMap paramMap)throws SQLException{
		this.registerStaticSql(id,sql,paramMap,null);
	}
	
	/**
	 * 注册SQL的表达式
	 */
	public void registerStaticSql(String id,String sql,ResultMap resultMap)throws SQLException{
		this.registerStaticSql(id,sql,null,resultMap);
	}
	
	/**
	 * 注册SQL的表达式
	 */
	public void registerStaticSql(String id,String sql,ParamMap paramMap,ResultMap resultMap)throws SQLException{
		this.registerStaticSql(id,sql,paramMap,resultMap,null);
	}
	
	/**
	 * 注册SQL的表达式
	 */
	public void registerStaticSql(String id,String sql,ParamMap paramMap,ResultMap resultMap,SqlPropertyTable table)throws SQLException{
    sql=(sql!=null)?sql.trim():sql;
		SqlOperationType sqlType = this.getStaticOpertionType(id,sql);
		int paramCount = StringUtil.getWordCount(sql,Symbols.Question);//通过SQL中?来获得参数个数。
		if(paramCount ==1 && paramMap == null)
			paramMap = this.createParamMap(Object.class,null);
	
		this.paramValidator.checkParamMap(id,sqlType,paramCount,(ParamMapImpl)paramMap,this);
		this.resultValidator.checkResultMap(id,sqlType,(ResultMapImpl)resultMap,this);
		this.sqlDefinitionMap.put(id,new SqlStaticStatement(id,sql,sqlType,paramMap,resultMap,table));
		logger.debug("Registered static sql("+id+ "): " + sql);
	}
	
	/**
	 * 注册动态SQL的
	 */
	public void registerDynamicSql(String id, DynTag[] tags,Class paramClass)throws SQLException{
		this.registerDynamicSql(id,tags,paramClass,null);
	}
	
	/**
	 * 注册动态SQL的
	 */
	public void registerDynamicSql(String id,DynTag[] tags,Class paramClass,ResultMap resultMap)throws SQLException{
		this.registerDynamicSql(id,tags,paramClass,resultMap,null);
	}
	
	/**
	 * 注册动态SQL的
	 */
	public void registerDynamicSql(String id,DynTag[] tags,Class paramClass,ResultMap resultMap,SqlPropertyTable table)throws SQLException{
		SqlOperationType sqlOpType = this.getDynamicOpertionType(id,tags);
		this.checkDynTags(id,tags,paramClass,sqlOpType);
		this.resultValidator.checkResultMap(id,sqlOpType,(ResultMapImpl)resultMap,this);
		if(tags!=null){
			for(int i=0,n=tags.length;i<n;i++){
				if(tags[i] instanceof ForeachTag){
					ForeachTag tag =(ForeachTag)tags[i];
					tag.setDynParamUnit(dynSqlBlockParser.analyzeDynParamSQL(tag.getSubSqlText()));
				}else if(tags[i] instanceof IterateTag){
					IterateTag tag =(IterateTag)tags[i];
					tag.setDynParamUnit(dynSqlBlockParser.analyzeDynParamSQL(tag.getSubSqlText()));
				}
			}
		}
	
		this.sqlDefinitionMap.put(id,new SqlDynStatement(id,sqlOpType,tags,paramClass,resultMap,table));
		logger.debug("Registered dynamic sql("+id+ ")");
	}

  /**
   * 注销SQL
   */
  public void deregisterSql(String id)throws SQLException{
    this.sqlDefinitionMap.remove(id);
  }

  /**
   * 破坏容器
   * 1:关闭所有Session
   * 2:清理所有定义
   */
  public void destroy() throws SQLException{
   this.jdbcTypeMap.clear();
   this.paramPersisterMap.clear();
   this.sqlDefinitionMap.clear(); 
   this.beeDataSource.close();
   this.queryResultCacheManager.destroy();
  }
	
  /**
   * 创建Session
   */
  public JdaSession openSession() throws SQLException{ 
  	return new JdaSessionImpl(this);
  }
	
  /**
	 * 获得数据源定义信息
	 */
	public JdaSourceInfo getJdaSourceInfo()throws SQLException{
		try{
			return (JdaSourceInfo)this.dataSourceInfo.clone();
		}catch(CloneNotSupportedException e){
			throw new SQLException("Jda source info can't be clone");
		}
	}
  
 
  /**************************************缓存定义***********************************************************/
  
	/**
	 * 创建缓存定义对象
	 */
	public JdaCacheInfo createCacheInfo(JdaCacheType type,int size)throws SQLException{
		if(type==null)throw new ObjectCacheException("Cache type can't be null");
		if(size<=0)throw new ObjectCacheException("Cache size must be more than zero");
		if(JdaCacheType.MEMORY.equals(type) && type.getRefenceType()==null)
			throw new ObjectCacheException("Momery cache refence type can't be null");
		return new JdaCacheInfo(type,size);
	}
	
	/**
	 * 创建缓存定义对象
	 */
	public JdaCacheInfo createCacheInfo(Class cacheImplementClass,int size)throws SQLException{
		if(cacheImplementClass==null)throw new ObjectCacheException("Cache implement class can't be null");
		if(size<=0)throw new ObjectCacheException("Cache size must be more than zero");
		if(!JdaCache.class.isAssignableFrom(cacheImplementClass))
			throw new ObjectCacheException("Cache implement class must be a sub class from class["+JdaCache.class.getName()+"]");
		return new JdaCacheInfo(cacheImplementClass,size);
	}
	
	/**
	 * 注册Cache
	 */
	public void registerCache(String cacheId,JdaCacheInfo cacheInfo)throws SQLException{
		if(cacheId==null)throw new ObjectCacheException("Cache id can't be null");
		if(cacheInfo==null)throw new ObjectCacheException("Cache definition can't be null");
		if(cacheInfo.getCacheImplementClass()!=null && !JdaCache.class.isAssignableFrom(cacheInfo.getCacheImplementClass()))
			throw new ObjectCacheException("Cache implement class must be a sub class from class["+JdaCache.class.getName()+"]");
		this.queryResultCacheManager.registerCache(cacheId,cacheInfo);
	}

	/**
	 *  注销Cache
	 */
	public void deregisterCache(String cacheId)throws SQLException{
		this.queryResultCacheManager.deregisterCache(cacheId);
	}
	
	/**
	 * 清理Cache
	 */
	public void clearCache(String id)throws SQLException{
		this.queryResultCacheManager.clearCache(id);
	}
	
	/**
	 * 清理所有Cache
	 */
	public void clearAllCache()throws SQLException{
		this.queryResultCacheManager.clearAllCache();
	}
	
 /**************************************缓存定义***********************************************************/
  
  
  
	/**
	 * 获得结果验证器
	 */
	public ResultValidator getResultValidator()throws SQLException{
  	return this.resultValidator;
	}

	/**
	 * 创建参数属性
	 */
	public ParamUnit createParamUnit(String name)throws SQLException{
		return createParamUnit(name,null);
	}

	/**
	 * 创建结果属性
	 */
	public ResultUnit createResultUnit(String name)throws SQLException{
		return createResultUnit(name,null);
	}
	

	/**
	 * 创建参数属性
	 */
	public ParamUnit createParamUnit(String name,Class type)throws SQLException{
		if(StringUtil.isNull(name))throw new ParamMapException(null,"Parameter property name can't be null");
		return new ParamUnitImpl(name,type);
	}
	
	
	/**
	 * 创建结果属性
	 */
	public ResultUnit createResultUnit(String name,Class type)throws SQLException{
		if(StringUtil.isNull(name))throw new ParamMapException(null,"Result property name can't be null");
		return new ResultUnitImpl(name,type);
	}
	
	/**
	 * 创建关联属性
	 */
	public RelationUnit createRelationUnit(String name,String sqlId)throws SQLException{
		return this.createRelationUnit(name,null,sqlId);
	}
	
	/**
	 * 创建关联属性
	 */
	public RelationUnit createRelationUnit(String name,Class type,String sqlId)throws SQLException{
		if(StringUtil.isNull(sqlId))throw new ResultMapException(null,"Relation sql id can't be null");
		if(StringUtil.isNull(name))throw new ParamMapException(null,"Relation property name can't be null");
		return new RelationUnitImpl(sqlId,name,type);
	}
	
	/**
	 * 创建属性列表
	 */
	public SqlPropertyTable createSqlPropertyTable()throws SQLException{
		return new SqlPropertyCenter();
	}
	
	/**
	 * 创建属性列表
	 */
	public SqlPropertyTable createSqlPropertyTable(Properties properties)throws SQLException{
	 return new SqlPropertyCenter(properties);
	}
	
	/**
	 * 创建参数映射列表
	 */
	public ParamMap createParamMap(Class paramClass,ParamUnit[]paramUnits)throws SQLException{
		if(paramClass == null)
		 throw new ResultMapException("Parameter class can't be null");
		return new ParamMapImpl(paramClass,paramUnits);
	}
	
	/**
	 * 创建结果映射列表
	 */
	public ResultMap createResultMap(Class resultClass,ResultUnit[]resultUnits)throws SQLException{
		if(resultClass == null)
			throw new ResultMapException("Result class can't be null");
		if(!Map.class.isAssignableFrom(resultClass)&& ClassUtil.isAbstractClass(resultClass))
			throw new ResultMapException("Result class can't be abstract");
		if(Collection.class.isAssignableFrom(resultClass))
			throw new ResultMapException("Result class can't be sub class of collection");
		if(resultClass.isArray())
	    throw new ResultMapException("Result class can't be array type");
		return new ResultMapImpl(resultClass,resultUnits);
	}
	
	/**
	 * 创建结果映射列表
	 */
	public ResultMap createResultMap(Class resultClass,ResultUnit[]resultUnits,RelationUnit[] relationUnits)throws SQLException{
		return new ResultMapImpl(resultClass,resultUnits,relationUnits);
	}
	
	/**
	 * 验证映射列表中是否包含Jdbc type
	 */
	public boolean containsJdbcType(String typeName)throws SQLException{
		if(typeName!=null)typeName = typeName.toUpperCase();
		return this.jdbcTypeMap.containsKey(typeName);
	}
	
	/**
	 *  获得jdbc type code
	 */
	public int getJdbcTypeCode(String typeName)throws SQLException{
		if(typeName!=null)typeName = typeName.toUpperCase();
		Integer code =(Integer)this.jdbcTypeMap.get(typeName);
		if(code!=null)
			return code.intValue();
		else
			throw new ParamMapException("Not found jdbc type for name:"+typeName);
	}

	/**
	 * 注册Jdbc类
	 */
	public void removeJdbcType(String typeName)throws SQLException{
		if(typeName!=null)typeName = typeName.toUpperCase();
		this.jdbcTypeMap.remove(typeName);
	}
	
	/**
	 * 注册Jdbc type
	 */
	public void addJdbcType(String typeName,int typeCode)throws SQLException{
		if(typeName!=null)typeName=typeName.toUpperCase();
		this.jdbcTypeMap.put(typeName,Integer.valueOf(typeCode));
	}


	/**
	 * 删除类型转换器
	 */
	public void removeTypeConverter(Class type)throws SQLException{
		this.typeConvertFactory.removeTypeConverter(type);
	}
	
	/**
	 * 是否包含类型转换器
	 */
	public boolean containsTypeConverter(Class type)throws SQLException{
		return this.typeConvertFactory.supportsType(type);
	}
	
  /**
   * 将对象转换为目标类型对象
   */
  public Object convertObject(Object obj,Class type)throws SQLException{
  	return typeConvertFactory.convert(obj,type);
  }

	/**
	 * 获得类型转换器
	 */
	public JdaTypeConverter getTypeConverter(Class type)throws SQLException{
		return this.typeConvertFactory.getTypeConverter(type);
	}
	
	/**
	 * 注册类型转换器
	 */
	public void addTypeConverter(Class type,JdaTypeConverter converter)throws SQLException{
		this.typeConvertFactory.putTypeConverter(type,converter);
	}
	
	/**
	 * 获得类型转换器列表
	 */
	public JdaTypeConvertFactory getTypeConvertFactory()throws SQLException{
		return this.typeConvertFactory;
	}
	
	/**
	 * 删除参数持久器
	 */
	public void removeTypePersister(Class type)throws SQLException{
		this.removeTypePersister(type,null);
	}

	/**
	 *  删除参数持久器
	 */
	public void removeTypePersister(Class type,String jdbcName)throws SQLException{
		Map subMap=(Map)paramPersisterMap.get(type);
		if(jdbcName!=null)jdbcName=jdbcName.toLowerCase();
		if(subMap != null)
			subMap.remove(jdbcName);
	}
	
	/**
	 * 获得参数持久器
	 */
	public JdaTypePersister getTypePersister(Class type)throws SQLException{
		return this.getTypePersister(type,null);
	}
	
	/**
	 * 获得参数持久器
	 */
	public JdaTypePersister getTypePersister(Class type,String jdbcName)throws SQLException{
		JdaTypePersister paramPersister= null;
		Map subMap=(Map)paramPersisterMap.get(type);
		if(jdbcName!=null)jdbcName=jdbcName.toLowerCase();
		if(subMap != null){
			paramPersister=(JdaTypePersister)subMap.get(jdbcName);	
			if(paramPersister == null)
				paramPersister=(JdaTypePersister)subMap.get(null);
		}
		
		if(paramPersister==null)//使用默认持久器
			paramPersister = this.getTypePersister(Object.class);
		
		return paramPersister;
	}

	/**
	 * 删除参数持久器
	 */
	public boolean containsTypePersister(Class type)throws SQLException{
		return this.paramPersisterMap.containsKey(type);
	}
	
	/**
	 * 删除参数持久器
	 */
	public boolean containsTypePersister(Class type,String jdbcName)throws SQLException{
		Map subMap =(Map)this.paramPersisterMap.get(type);
		if(jdbcName!=null)jdbcName=jdbcName.toLowerCase();
		if (subMap!= null)
			return subMap.containsKey(jdbcName);
		else 
			return false;
	}
	
	/**
	 * 注册参数持久器
	 */
	public void addTypePersister(Class type,JdaTypePersister persister)throws SQLException{
		this.addTypePersister(type,null,persister);
	}
 	
	/**
	 *  注册参数持久器
	 */
	public void addTypePersister(Class type,String jdbcName,JdaTypePersister persister)throws SQLException{
	  Map subMap =(Map)this.paramPersisterMap.get(type);
		if(jdbcName!=null)jdbcName = jdbcName.toLowerCase();
	  if(subMap == null) {
	  	subMap = new HashMap();
			this.paramPersisterMap.put(type,subMap);
		}
	  
	  subMap.put(jdbcName,persister);
	}
 
	
  
	/**
	* 获取一个SQL定义
	*/
	public SqlBaseStatement getSqlStatement(Object id)throws SQLException{
		SqlBaseStatement statement = (SqlBaseStatement)this.sqlDefinitionMap.get(id);
		if(statement==null)
			throw new SqlDefinitionException(id,"SQL definition not found");
		else
		  return statement;
	 }
  
 
	/****************************以下为内部使用方法********************************/
  
  /**
	 * 获取数据库的连接池
	 */
   DataSource getDataSource(){
  	return this.beeDataSource;
  }
  
  /**
   * 获得数据源信息
   */
	JdaSourceInfo getDataSourceInfo(){
		return this.dataSourceInfo;
	}
	
  /**
   * 获得数据源信息
   */
	TransactionManager getTransactionManager() {
		return this.transactionManager;
	}
	/****************************以上为内部使用方法********************************/
	
	
	/***************************以下为Session获得共有对象**************************/
	
	/**
	 * 缓存管理器
	 */
	CacheManager getCacheManager(){
		return this.queryResultCacheManager;
	}
	
  /**
	 * 参数辅助对象
   */
	ParamObjectFactory getParamObjectFactory() {
		return this.paramObjectFactory;
	}
	
  /**
	 * 结果辅助对象
   */
	ResultObjectFactory getResultObjectFactory() {
		return this.resultObjectFactory;
	}
	
  /**
	 * 关联辅助对象
   */
	RelationObjectFactory getRelationObjectFactory() {
		return this.relationObjectFactory;
	}
	
  /**
   * 获得单个对象查找
   */
	ObjectFinder getObjectFinder() {
		return this.objectFinder;
	}
	
  /**
   *  List对象查找
   */
	ObjectListFinder getObjectListFinder() {
		return this.objectListFinder;
	}
	
  /**
   *  叠达器查询
   */
	ObjectIterateFinder getObjectIterateFinder() {
		return this.objectIterateFinder;
	}
	
  /**
   *  Map对象器查询
   */
	ObjectMapFinder getObjectMapFinder() {
		return this.objectMapFinder;
	}
	
  /**
   * 分页执行查询
   */
	ObjectPageFinder getObjectPageFinder() {
		return this.objectPageFinder;
	}
	
	/**
	 * 关联查询
	 */
	ObjectRelateFinder getObjectRelateFinder() {
		return this.objectRelateFinder;
	}

  /**
   * 分页执行查询
   */
	ObjectRowFinder getObjectRowFinder() {
		return this.objectRowFinder;
	}
	
   /**
	 * 动态SQL块解析
	 */
	DynSqlBlockParser getDynSqlBlockParser() {
		return this.dynSqlBlockParser;
	}
	
  /**
   * 获得sql请求构造工厂
   */
	SqlRequestFactory getSqlRequestFactory() {
		return this.sqlRequestFactory;
	}
	
  /**
   * 获得sql请求处理器
   */
	SqlRequestHandler getSqlRequestHandler() {
		return this.sqlRequestHandler;
	}
	
	/**
	 * sql Update处理器
	 */
	UpdateHandler getUpdateHandler() {
		return this.updateHandler;
	}
	
	/***********************以上为Session获得共有对象***************************/

	
	/**
	 * 初始化JDbc type
	 */
	private void registerDefaultJdbcType(){
		try{
			this.jdbcTypeMap = new HashMap();
			this.addJdbcType("BIT",SqlFieldTypes.BIT);
			this.addJdbcType("TINYINT",SqlFieldTypes.TINYINT);
			this.addJdbcType("SMALLINT",SqlFieldTypes.SMALLINT);
			this.addJdbcType("INTEGER",SqlFieldTypes.INTEGER);
			this.addJdbcType("BIGINT",SqlFieldTypes.BIGINT);
			this.addJdbcType("FLOAT",SqlFieldTypes.FLOAT);
			this.addJdbcType("REAL",SqlFieldTypes.REAL);
			this.addJdbcType("DOUBLE",SqlFieldTypes.DOUBLE);
			this.addJdbcType("NUMERIC",SqlFieldTypes.NUMERIC);
			this.addJdbcType("DECIMAL",SqlFieldTypes.DECIMAL);
			this.addJdbcType("CHAR",SqlFieldTypes.CHAR);
			this.addJdbcType("VARCHAR",SqlFieldTypes.VARCHAR);
			this.addJdbcType("LONGVARCHAR",SqlFieldTypes.LONGVARCHAR);
			
			this.addJdbcType("DATE",SqlFieldTypes.DATE);
			this.addJdbcType("TIME",SqlFieldTypes.TIME);
			this.addJdbcType("TIMESTAMP",SqlFieldTypes.TIMESTAMP);
			this.addJdbcType("BINARY",SqlFieldTypes.BINARY);
			this.addJdbcType("VARBINARY",SqlFieldTypes.VARBINARY);
			this.addJdbcType("LONGVARBINARY",SqlFieldTypes.LONGVARBINARY);
			this.addJdbcType("NULL",SqlFieldTypes.NULL);
			this.addJdbcType("OTHER",SqlFieldTypes.OTHER);
			this.addJdbcType("JAVA_OBJECT",SqlFieldTypes.JAVA_OBJECT);
			this.addJdbcType("DISTINCT",SqlFieldTypes.DISTINCT);
			this.addJdbcType("STRUCT",SqlFieldTypes.STRUCT);
			this.addJdbcType("ARRAY",SqlFieldTypes.ARRAY);
			this.addJdbcType("BLOB",SqlFieldTypes.BLOB);
			this.addJdbcType("CLOB",SqlFieldTypes.CLOB);
			this.addJdbcType("REF",SqlFieldTypes.REF);
			this.addJdbcType("DATALINK",SqlFieldTypes.DATALINK);
			this.addJdbcType("BOOLEAN",SqlFieldTypes.BOOLEAN);
			this.addJdbcType("ORACLECURSOR",SqlFieldTypes.ORACLECURSOR);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化type handler
	 */
	private void registerDefaultConverter(){
			JdaTypeConverter converter = new BoolConverter();
			this.typeConvertFactory.putTypeConverter(Boolean.class,converter);
			this.typeConvertFactory.putTypeConverter(boolean.class,converter);
		
			converter = new ByteConverter();
			this.typeConvertFactory.putTypeConverter(Byte.class,converter);
			this.typeConvertFactory.putTypeConverter(byte.class,converter);
 
			converter = new ShortConverter();
			this.typeConvertFactory.putTypeConverter(Short.class,converter);
			this.typeConvertFactory.putTypeConverter(short.class,converter);
			
			converter = new IntegerConverter();
			this.typeConvertFactory.putTypeConverter(Integer.class,converter);
			this.typeConvertFactory.putTypeConverter(int.class,converter);
			
			converter = new LongConverter();
			this.typeConvertFactory.putTypeConverter(Long.class,converter);
			this.typeConvertFactory.putTypeConverter(long.class,converter);

			converter = new FloatConverter();
			this.typeConvertFactory.putTypeConverter(Float.class,converter);
			this.typeConvertFactory.putTypeConverter(float.class,converter);
		
			converter = new DoubleConverter();
			this.typeConvertFactory.putTypeConverter(Double.class,converter);
			this.typeConvertFactory.putTypeConverter(double.class,converter);
 
			converter = new CharConverter();
			this.typeConvertFactory.putTypeConverter(char.class,converter);
			this.typeConvertFactory.putTypeConverter(Character.class,converter);
			
			this.typeConvertFactory.putTypeConverter(Object.class,new JdaTypeBaseConverter());
			this.typeConvertFactory.putTypeConverter(BigInteger.class,new BigIntegerConverter());
			this.typeConvertFactory.putTypeConverter(BigDecimal.class,new BigDecimalConverter());
			this.typeConvertFactory.putTypeConverter(String.class,new StringConverter());
		
			this.typeConvertFactory.putTypeConverter(Clob.class,new ClobConverter());
			this.typeConvertFactory.putTypeConverter(Blob.class,new BlobConverter());
			this.typeConvertFactory.putTypeConverter(byte[].class,new BytesConverter());

		
			this.typeConvertFactory.putTypeConverter(Date.class,new DateConverter());
			this.typeConvertFactory.putTypeConverter(Time.class,new DateTimeConverter());
			this.typeConvertFactory.putTypeConverter(Timestamp.class,new DateTimestampConverter());
	
			this.typeConvertFactory.putTypeConverter(Calendar.class,new CalendarConverter());
			this.typeConvertFactory.putTypeConverter(java.util.Date.class,new UtilDateConverter());
	
	}
	
	/**
	 * 初始化type handler
	 */
	private void registerDefaultParamPersister(){
		try {
			JdaTypePersister persister = new BooleanHandler();
			this.addTypePersister(Boolean.class,persister);
			this.addTypePersister(boolean.class,persister);

			persister = new ByteHandler();
			this.addTypePersister(Byte.class,persister);
			this.addTypePersister(byte.class,persister);
		 		
			persister = new ShortHandler();
			this.addTypePersister(Short.class,persister);
			this.addTypePersister(short.class,persister);
		 		
			persister = new IntegerHandler();
			this.addTypePersister(Integer.class,persister);
			this.addTypePersister(int.class,persister);
		 		
			persister = new LongHandler();
			this.addTypePersister(Long.class,persister);
			this.addTypePersister(long.class,persister);
		 		
			persister = new FloatHandler();
			this.addTypePersister(Float.class,persister);
			this.addTypePersister(float.class,persister);
			 		
			persister = new DoubleHandler();
			this.addTypePersister(Double.class,persister);
			this.addTypePersister(double.class,persister);
		 		
			this.addTypePersister(BigInteger.class,new BigIntegerHandler());
			this.addTypePersister(BigDecimal.class,new BigDecimalHandler());
		  this.addTypePersister(String.class,new StringHandler());
			this.addTypePersister(Object.class,new ObjectHandler());
			this.addTypePersister(Clob.class,new ClobHandler());
			this.addTypePersister(Blob.class,new BlobHandler());	
			this.addTypePersister(byte[].class,new BytesHandler());	
 
			this.addTypePersister(java.sql.Date.class,new DateHandler());
			this.addTypePersister(java.sql.Time.class,new DateTimeHandler());
			this.addTypePersister(java.sql.Timestamp.class,new DateTimestampHandler());
			
			UtilDateHandler utilDateHandler =new UtilDateHandler();
			this.addTypePersister(java.util.Date.class,utilDateHandler);
			this.addTypePersister(java.util.Date.class,"DATE",utilDateHandler);
			this.addTypePersister(java.util.Date.class,"TIME",new UtilDateTimeHandler());
			this.addTypePersister(java.util.Date.class,"TIMESTAMP",new UtilDateTimestampHandler());
			
			CalendarDateHandler calendarDateHandler = new CalendarDateHandler();
			this.addTypePersister(java.util.Calendar.class,calendarDateHandler);
			this.addTypePersister(java.util.Calendar.class,"DATE",calendarDateHandler);
			this.addTypePersister(java.util.Calendar.class,"TIME",new CalendarDateTimeHandler());
			this.addTypePersister(java.util.Calendar.class,"TIMESTAMP",new CalendarDateTimestampHandler());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * 检查映射结果属性
	 */
	private SqlOperationType getStaticOpertionType(String id,String sql)throws SQLException{
		if(id==null)
			throw new SqlDefinitionException(null,"SQL id can't be null");
		if(StringUtil.isNull(sql))
			throw new SqlDefinitionException(id,"SQL can't be null");
		if(this.containsSql(id))
			throw new SqlDefinitionException(id,"SQL id has been registered by another sql");

		return getSqlOperateType(id,sql);
	}
	
	/**
	 * 检查映射结果属性
	 */
	private SqlOperationType getDynamicOpertionType(String id,DynTag[] tas)throws SQLException{
		if(id==null)
			throw new ParamMapException("SQL id can't be null");
		if(tas==null || tas.length==0)
			throw new SqlDefinitionException(id,"Dynamic sql tags can't be null or emtpty");
		if(this.containsSql(id))
			throw new SqlDefinitionException(id,"SQL id has been registered with another sql");
		
		String firstBlock = getFistTextBlock(id,tas[0]);
		return getSqlOperateType(id,firstBlock);
	}

	/**
	 * 检查节点
	 */
	private void checkDynTags(Object id,DynTag[]tags,Class paramClass,SqlOperationType sqlOpType)throws SQLException{
		if(tags== null || tags.length==0)
			throw new SqlDefinitionException(id,"Sql dynamic tags can't be null or empty");
		if(paramClass==null)
			throw new SqlDefinitionException(id,"Parameter class can't be null");
		if(this.containsTypePersister(paramClass))
			throw new SqlDefinitionException(id,"Parameter class can't be base direct map type for dynamic sql");
		
		for(int i=0;i<tags.length;i++)
			dynTagValidator.checkDynTag(id,tags[i],paramClass,sqlOpType,this);
	}
	
	/**
	 * 获得第一个SQL文本片段
	 */
	private String getFistTextBlock(Object id,DynTag tag)throws SQLException{
		if(tag instanceof TextTag){
			return ((TextTag)tag).getText();
		}else if(tag instanceof ChooseTag){
			if(((ChooseTag)tag).getSubWhenTagCount()==0)
				throw new SqlDynTagException(id,"Invalid dynamic sql,missed children in tag["+tag.getTagName()+"]");
			return getFistTextBlock(id,((ChooseTag)tag).getSubWhenTag(0));
		}else {
			if(tag.getChildrenCount()==0)
				throw new SqlDynTagException(id,"Invalid dynamic sql,missed children in tag["+tag.getTagName()+"]");
			return getFistTextBlock(id,tag.getChildren(0));
		}
	}
	
	/**
	 * 获得SQL操作类型
	 */
	SqlOperationType getSqlOperateType(Object id,String sql)throws SqlDefinitionException {
		sql=sql.trim();
		int pos =sql.indexOf(Symbols.Space);
		if(pos == -1)throw new SqlDefinitionException(id,"SQL definition error:"+sql);
		
		String firstWord=sql.substring(0,pos);//第一个单词
		if(sql.trim().startsWith(Symbols.Left_Braces)){
			return SqlOperationType.Procedure;
		}else if(SqlOperationType.Insert.getName().equalsIgnoreCase(firstWord)){
			return SqlOperationType.Insert;
		}else if(SqlOperationType.Update.getName().equalsIgnoreCase(firstWord)){
			return SqlOperationType.Update;
		}else if(SqlOperationType.Delete.getName().equalsIgnoreCase(firstWord)){
			return SqlOperationType.Delete;
		}else if(SqlOperationType.Select.getName().equalsIgnoreCase(firstWord)){
			return SqlOperationType.Select;
		}else {
			return SqlOperationType.Unknown;
		}
	}
}
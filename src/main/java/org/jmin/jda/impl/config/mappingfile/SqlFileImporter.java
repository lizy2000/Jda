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
package org.jmin.jda.impl.config.mappingfile;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jmin.jda.JdaCacheInfo;
import org.jmin.jda.JdaCacheRefType;
import org.jmin.jda.JdaCacheType;
import org.jmin.jda.JdaContainer;
import org.jmin.jda.impl.config.SqlFileFinder;
import org.jmin.jda.impl.config.dynamicsql.DynSqlAnalyzer;
import org.jmin.jda.impl.config.statictext.ParamResult;
import org.jmin.jda.impl.config.statictext.SqlTextUtil;
import org.jmin.jda.impl.config.statictext.StaticSqlAnalyzer;
import org.jmin.jda.impl.exception.ObjectCacheException;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.exception.SqlDefinitionException;
import org.jmin.jda.impl.exception.SqlDefinitionFileException;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.impl.util.Symbols;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.statement.DynTag;
import org.jmin.jda.statement.SqlPropertyTable;

/**
 * 解析映射文件
 * 
 * @author Chris Liao
 */
public class SqlFileImporter {
	
	/**
	 * xml标记
	 */
	private SqlFileXMLTags xmlTags = new SqlFileXMLTags();
	
	/**
	 * 参数解析
	 */
	private AliasClassParser aliasClassParser = new AliasClassParser();
	
	/**
	 * 参数解析
	 */
	private ParamMapParser paramMapInfoParser = new ParamMapParser();
	
	/**
	 * 结果解析
	 */
	private ResultMapParser resultMapInfoParser = new ResultMapParser();
	
	
	/**
	 * 静态SQL分析
	 */
	private StaticSqlAnalyzer staticSqlAnalyzer=new StaticSqlAnalyzer();
	
	/**
	 * 动态SQL分析
	 */
	private DynSqlAnalyzer dynSqlAnalyzer=new DynSqlAnalyzer(staticSqlAnalyzer);
	
	/**
	 * SQL辅助类
	 */
	private SqlTextUtil sqlTextUtil = new SqlTextUtil();

	/**
	 * 解析映射文件
	 */
	public void importSQLMapFile(URL url,JdaContainer container,SqlFileFinder sqlFileFinder,SAXBuilder saxBuilder)throws SQLException {
		String sqlFilename=null;		
		try {
			sqlFilename = url.getFile();		
			sqlFileFinder.validateXMLFile(url);
			Document document =saxBuilder.build(url);
			Element rootElement = document.getRootElement();
			sqlFileFinder.validateXMLRoot(rootElement,xmlTags.Root);
			
			String spanceID = rootElement.getAttributeValue(xmlTags.ATTR_Space);
			List classNodeList = rootElement.getChildren(xmlTags.Class);
			List resultMapNodeList = rootElement.getChildren(xmlTags.ResultMap);
			List paramMapNodeList = rootElement.getChildren(xmlTags.ParameterMap);
			List insertNodeList = rootElement.getChildren(xmlTags.Insert);
			List updateNodeList = rootElement.getChildren(xmlTags.update);
			List deleteNodeList = rootElement.getChildren(xmlTags.Delete);
			List selectNodeList = rootElement.getChildren(xmlTags.Select);
			List procedureNodeList = rootElement.getChildren(xmlTags.Procedure);
			List cacheNodeList = rootElement.getChildren(xmlTags.CacheModel);
			
			Map aliasClassMap = aliasClassParser.loadAliasMap(classNodeList,container,xmlTags);
			Map resultMapTable = this.resolveResultMapNodeList(resultMapNodeList,container,aliasClassMap);
			Map paramMapTable = this.resolveParamMapNodeList(paramMapNodeList,container,aliasClassMap,resultMapTable);
			
			this.importCache(spanceID,cacheNodeList,container);
			this.importSQLNodeList(spanceID,paramMapTable,resultMapTable,insertNodeList,container,aliasClassMap,SqlOperationType.Insert);
			this.importSQLNodeList(spanceID,paramMapTable,resultMapTable,updateNodeList,container,aliasClassMap,SqlOperationType.Update);
			this.importSQLNodeList(spanceID,paramMapTable,resultMapTable,deleteNodeList,container,aliasClassMap,SqlOperationType.Delete);
			this.importSQLNodeList(spanceID,paramMapTable,resultMapTable,selectNodeList,container,aliasClassMap,SqlOperationType.Select);
			this.importSQLNodeList(spanceID,paramMapTable,resultMapTable,procedureNodeList,container,aliasClassMap,SqlOperationType.Procedure);
		  
			aliasClassMap.clear();
			resultMapTable.clear();
			paramMapTable.clear();
		}catch(SQLException e){
			throw e;
		} catch (JDOMException e) {
			throw new SqlDefinitionFileException(null,"Failed to parse sql map file:"+sqlFilename,e);
		} catch (IOException e) {
			throw new SqlDefinitionFileException(null,"Failed to open sql map file:"+sqlFilename,e);
		}
	}
		
	/**
	 * 参数映射列表
	 */
	private Map resolveParamMapNodeList(List paramMapNodeList,JdaContainer container,Map classMap,Map resultMapTable)throws SQLException{
		Map listMap = new HashMap(paramMapNodeList.size());
		for(int i=0,n=paramMapNodeList.size();i<n;i++) {
      Element mapNode =(Element)paramMapNodeList.get(i);
			String mapId = mapNode.getAttributeValue(xmlTags.ATTR_Id);
			if(StringUtil.isNull(mapId)) 
				throw new ParamMapException("Parameter map id can't be null");
			ParamMap paramMap = paramMapInfoParser.parse(mapId,mapNode,container,classMap,resultMapTable,xmlTags);
			
			listMap.put(mapId,paramMap);
		}
		return listMap;
	}
	
	/**
	 * 获得映射的列表
	 */
	private Map resolveResultMapNodeList(List resultMapNodeList,JdaContainer container,Map classMap)throws SQLException{
		Map listMap = new HashMap(resultMapNodeList.size());
		for(int i=0,n=resultMapNodeList.size();i<n;i++) {
			Element mapNode =(Element)resultMapNodeList.get(i);
			String mapID = mapNode.getAttributeValue(xmlTags.ATTR_Id);
			if(StringUtil.isNull(mapID)) 
				throw new ResultMapException("Parameter map id can't be null");
			
			ResultMap info = resultMapInfoParser.parse(mapID,mapNode,container,classMap,xmlTags);
			listMap.put(mapID,info);
		}
		return listMap;
	}

	/**
	 * 将缓存信息导入容器
	 */
	private void importCache(String spanceID,List cacheNodeList,JdaContainer container)throws SQLException{
	  for(int n=0,len=cacheNodeList.size();n<len;n++){//循环找出所有的节点
			Element cacheElement =(Element)cacheNodeList.get(n);
			String cachId = cacheElement.getAttributeValue(xmlTags.ATTR_Id);
			String type = cacheElement.getAttributeValue(xmlTags.ATTR_Type);
			String sizeTxt = cacheElement.getAttributeValue(xmlTags.ATTR_Size);
			String intervalTxt = cacheElement.getAttributeValue(xmlTags.ATTR_Flush_Interval);
			String readOnlyTxt = cacheElement.getAttributeValue(xmlTags.ATTR_ReadOnly);
			String serializeTxt = cacheElement.getAttributeValue(xmlTags.ATTR_Serialize);
			String referenceTypeTxt = cacheElement.getAttributeValue(xmlTags.ATTR_Reference_Type);
			
			if(StringUtil.isNull(cachId)) 
				throw new ObjectCacheException("Cache id can't be null");
			if(StringUtil.isNull(type)) 
				throw new ObjectCacheException("Cache type can't be null,must be one of[LRU,FIFO,MEMORY,OSCACHE]");
			
		  List propertyList = cacheElement.getChildren(xmlTags.ATTR_Property);
		  for(int i=0,k=propertyList.size();i<k;i++){
		      Element child =(Element)propertyList.get(i);
		      String name =child.getAttributeValue(xmlTags.ATTR_Name);
		      String value =child.getAttributeValue(xmlTags.ATTR_Value);
	      
			  if(xmlTags.ATTR_Type.equalsIgnoreCase(name)) type=value;
			  if(xmlTags.ATTR_Size.equalsIgnoreCase(name)) sizeTxt=value;
			  if(xmlTags.ATTR_ReadOnly.equalsIgnoreCase(name)) readOnlyTxt=value;
			  if(xmlTags.ATTR_Serialize.equalsIgnoreCase(name)) serializeTxt=value;
			  if(xmlTags.ATTR_Reference_Type.equalsIgnoreCase(name)) referenceTypeTxt=value;
			}
		  
			long interval=60*60*1000;//默认一个小时清理一次
			if(StringUtil.isNull(intervalTxt)){
				List intervalList = cacheElement.getChildren(xmlTags.ATTR_Flush_Interval);
				 for(int m=0,k=intervalList.size();m<k;m++){
			 		Element child =(Element)intervalList.get(m);
			 		String hours = child.getAttributeValue(xmlTags.ATTR_Hour);
			 		String minutes = child.getAttributeValue(xmlTags.ATTR_Minutes);
			 		String seconds = child.getAttributeValue(xmlTags.ATTR_Seconds);
			 		String millisecondss = child.getAttributeValue(xmlTags.ATTR_Millisecondss);
			 		try{
				 		if(!StringUtil.isNull(hours)){
				 			interval = Integer.parseInt(hours)*60*60*1000;
				 		}else if(!StringUtil.isNull(minutes)){
				 			interval = Integer.parseInt(minutes)*60*1000;
				 		}else if(!StringUtil.isNull(seconds)){
				 			interval = Integer.parseInt(seconds)*1000;
				 		}else if(!StringUtil.isNull(millisecondss)){
				 			interval = Integer.parseInt(millisecondss);
				 		}
			 		}catch(Throwable e){}
			 	}
			}
		  
		    //开始转换缓存需要的参数
			int size =100;
			boolean readOnly=false;
			boolean serialize=false;
			try {
				if(!StringUtil.isNull(sizeTxt))
				 size=Integer.parseInt(sizeTxt);
			} catch (Throwable e1) {}
			
			try {
				if(!StringUtil.isNull(readOnlyTxt))
			   readOnly =Boolean.parseBoolean(readOnlyTxt);
			} catch (Throwable e) {}
			
			try {
				if(!StringUtil.isNull(serializeTxt))
				 serialize =Boolean.parseBoolean(serializeTxt);
			} catch (Throwable e){}
			
			
			JdaCacheInfo cacheInfo =null;
			if(!StringUtil.isNull(type))type = type.trim();
			JdaCacheType cacheType = JdaCacheType.getJdaCacheType(type);
			if(cacheType!=null){
				if(!StringUtil.isNull(referenceTypeTxt)){
					referenceTypeTxt = referenceTypeTxt.trim();
					cacheType.setRefenceType(JdaCacheRefType.getJdaCacheRefType(referenceTypeTxt));
				}
				cacheInfo = container.createCacheInfo(cacheType,size);
			}else if(!StringUtil.isNull(type)){
				try {
					Class cacheImplementClass = ClassUtil.loadClass(type);
					cacheInfo = container.createCacheInfo(cacheImplementClass,size);
				} catch (ClassNotFoundException e) {
					 throw new ObjectCacheException("Can't find cache implement class["+type+"]");
				}  
			}
		 
			if (cacheInfo != null) {// 缓存定义信息不为空
				cacheInfo.setFlushInterval(interval);
				cacheInfo.setReadOnly(readOnly);
				cacheInfo.setSerialize(serialize);
				
				if (!StringUtil.isNull(spanceID))
					cachId = spanceID + Symbols.Dot + cachId;
				container.registerCache(cachId, cacheInfo);// 注册一个缓存
			}
		}
  }
	
	/**
	 * 解析SQL定义
	 */
	private void importSQLNodeList(String spaceID,Map paramListMap,Map resultListMap,List sqlNodeList,JdaContainer container,Map classMap,SqlOperationType sqlTpye)throws SQLException{
		for(int i=0,n=sqlNodeList.size();i<n;i++) {//循环找出所有的SQL节点
			Element sqlElement =(Element)sqlNodeList.get(i);
			List attributeList= sqlElement.getAttributes();
			SqlPropertyTable propertyTable= container.createSqlPropertyTable();
			for(int j=0,k=(attributeList==null)?0:attributeList.size();j<k;j++){
				Attribute attribute =(Attribute)attributeList.get(j);
				propertyTable.putValue(attribute.getName(),attribute.getValue());
			}
			
			String sqlID = sqlElement.getAttributeValue(xmlTags.ATTR_Id);
			if(StringUtil.isNull(sqlID))
				throw new SqlDefinitionException(null,"Missed sql id");
			if(!StringUtil.isNull(spaceID)) 
				sqlID = spaceID+Symbols.Dot+sqlID;
			
			String sqlText = sqlElement.getTextTrim();
			if(StringUtil.isNull(sqlText))
				throw new SqlDefinitionException(sqlID,"Missed sql text");
			
			sqlText=sqlTextUtil.removeCdata(sqlID,sqlText);
			if(StringUtil.isNull(sqlText))
				throw new SqlDefinitionException(sqlID,"Missed sql text");
			
			
			if(sqlTextUtil.isDynamicText(sqlText)){//动态SQL 
				importDynamicSQL(sqlID,sqlText,sqlElement,container,paramListMap,resultListMap,classMap,propertyTable);
			}else{//静态SQL
				importStaticSQL(sqlID,sqlText,sqlElement,container,paramListMap,resultListMap,classMap,propertyTable,sqlTpye);
			}
		}
	}

	
	/**
	 * 导入静态SQL
	 */
	private void importStaticSQL(String sqlID,String sqlText,Element sqlElement,JdaContainer container,Map paramListMap,Map resultListMap,Map classMap,SqlPropertyTable table,SqlOperationType sqlTpe)throws SQLException{
		ParamMap paramMap =null;
		ResultMap resultMap=null;
		Class paramClass = null;
		Class resultClass = null;
		
		String strParamClass = sqlElement.getAttributeValue(xmlTags.ATTR_ParameterType);
		String strResultClass =sqlElement.getAttributeValue(xmlTags.ATTR_ResultType);
		if(StringUtil.isNull(strParamClass))
		  strParamClass = sqlElement.getAttributeValue(xmlTags.ATTR_ParameterClass);
		if(StringUtil.isNull(strResultClass))
			strResultClass = sqlElement.getAttributeValue(xmlTags.ATTR_ResultClass);
		
		String strParamMapID = sqlElement.getAttributeValue(xmlTags.ATTR_parameterMap);
		String strResultMapID = sqlElement.getAttributeValue(xmlTags.ATTR_ResultMap);
		if(!StringUtil.isNull(strParamClass) && !StringUtil.isNull(strParamMapID))
			throw new SqlDefinitionException(sqlID,"Can't config 'parameterClass' and 'parameterMap' meantime");
		if(!StringUtil.isNull(strResultClass) && !StringUtil.isNull(strResultMapID))
			throw new SqlDefinitionException(sqlID,"Can't config 'resultClass' and 'resultMap' meantime");
		if(SqlOperationType.Select.equals(sqlTpe) && StringUtil.isNull(strResultClass) && StringUtil.isNull(strResultMapID))
			throw new SqlDefinitionException(sqlID,"Must config 'resultClass' or 'resultMap'");
		
		if(!StringUtil.isNull(strParamMapID)){
			if(sqlText.indexOf(Symbols.Question)==-1)
				throw new SqlDefinitionException(sqlID,"must contains '?' in sql text for 'parameter mapping");
			paramMap=(ParamMap)paramListMap.get(strParamMapID);
		  if(paramMap==null)
			  throw new SqlDefinitionException(sqlID,"Not found param map with id["+strParamMapID+"]");
		}else if(!StringUtil.isNull(strParamClass)){
			if(sqlText.indexOf(Symbols.Question)>0)
				throw new SqlDefinitionException(sqlID,"Can't contains '?' in sql text for 'parameter class' mapping");
			paramClass =this.loadClass(sqlID,strParamClass,true,classMap);
		}
		
		if(!StringUtil.isNull(strResultMapID)){
			resultMap=(ResultMap)resultListMap.get(strResultMapID);
			if(resultMap==null)
			 throw new SqlDefinitionException(sqlID,"Not found result map with id["+strResultMapID+"]");
		}else if(!StringUtil.isNull(strResultClass)){
			resultClass =this.loadClass(sqlID,strResultClass,false,classMap);
			resultMap=container.createResultMap(resultClass,null);
		}
		
		if(paramMap==null && sqlText.indexOf(Symbols.Question)==-1){//解析没有问号的sql
			ParamResult result = staticSqlAnalyzer.analyzeStaticSQL(sqlID,sqlText,paramClass,container);
			sqlText = result.getExeSQL();
			paramMap = result.getParamMap();
		}
		
		container.registerStaticSql(sqlID,sqlText,paramMap,resultMap,table);		
	}
	
	/**
	 * 导入动态SQL
	 */
	private void importDynamicSQL(String sqlID,String sqlText,Element sqlElement,JdaContainer container,Map paramListMap,Map resultListMap,Map classMap,SqlPropertyTable table)throws SQLException{
		ResultMap resultMap=null;
		Class paramClass = null;
		Class resultClass = null;
 
		String strParamClass = sqlElement.getAttributeValue(xmlTags.ATTR_ParameterClass);
		String strParamMapID = sqlElement.getAttributeValue(xmlTags.ATTR_parameterMap);
	  String strResultClass = sqlElement.getAttributeValue(xmlTags.ATTR_ResultClass);
		String strResultMapID = sqlElement.getAttributeValue(xmlTags.ATTR_ResultMap);

		if(StringUtil.isNull(strParamClass))
			throw new SqlDefinitionException(sqlID,"Parameter class can't not be configed");
		if(StringUtil.isNull(strParamClass) && !StringUtil.isNull(strParamMapID))
			throw new SqlDefinitionException(sqlID,"Parameter map don't be supported by dynamic sql");
		if(!StringUtil.isNull(strResultClass) && !StringUtil.isNull(strResultMapID))
			throw new SqlDefinitionException(sqlID,"Can't config 'resultClass' and 'resultMap' meantime");
		
		if(!StringUtil.isNull(strParamClass))
			paramClass =loadClass(sqlID,strParamClass,true,classMap);
		
		if(!StringUtil.isNull(strResultMapID)){
			resultMap=(ResultMap)resultListMap.get(strResultMapID);
			if(resultMap==null)
			 throw new SqlDefinitionException(sqlID,"Not found result map with id["+strResultMapID+"]");
		}else if(!StringUtil.isNull(strResultClass)){
			resultClass =this.loadClass(sqlID,strResultClass,false,classMap);
			resultMap=container.createResultMap(resultClass,null);
		}
		
		
		DynTag[]tags = dynSqlAnalyzer.analyzeDynamicSQL(sqlID,sqlText,paramClass,container);
		container.registerDynamicSql(sqlID,tags,paramClass,resultMap,table);	
	}
	
	/**
	 * 获得类
	 */
	private Class loadClass(String sqlId,String className,boolean isParameter,Map classMap)throws SQLException{
		try {
			if(classMap.containsKey(className))
				return (Class)(classMap.get(className));
			else
			return ClassUtil.loadClass(className);
		} catch (ClassNotFoundException e) {
			if(isParameter)
				throw new ParamMapException(sqlId,"Parameter Class("+className+")not found",e);
			else
				throw new ResultMapException(sqlId,"Result Class("+className+")not found",e);
		}
	}
}

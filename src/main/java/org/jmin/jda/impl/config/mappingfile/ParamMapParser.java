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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;
import org.jmin.jda.mapping.ResultMap;

/**
 * 参数映射表解析
 * 
 * @author Chris Liao
 */

public class ParamMapParser {
	
	/**
	 * 解析参数映射列表节点
	 */
	public ParamMap parse(String mapID,Element elment,JdaContainer container,Map classMap,Map resultMapTable,SqlFileXMLTags tags)throws SQLException {
		Class paramClass = null;
		String classname = elment.getAttributeValue(tags.ATTR_Class);
		if(!StringUtil.isNull(classname)) 
			paramClass = this.getClassType(mapID,classname,classMap);

		List paramUnitNodeList = elment.getChildren(tags.ATTR_Parameter);
		if(paramUnitNodeList==null || paramUnitNodeList.isEmpty())
		 throw new ParamMapException("Parameter map["+mapID+"]missed 'parameter' children node");
		
		List paramUnitList = new ArrayList(paramUnitNodeList.size());//参数映射单元
		for(int i=0,n=paramUnitNodeList.size();i<n;i++) {
			Element paramElement =(Element)paramUnitNodeList.get(i);
			paramUnitList.add(resolveParamUnitNode(mapID,paramElement,container,classMap,resultMapTable,tags));
		}
		
		ParamUnit[] units = (ParamUnit[])paramUnitList.toArray(new ParamUnit[paramUnitList.size()]);
		return container.createParamMap(paramClass,units);
	}

	/**
	 * 解析单个参数节点
	 */
	private ParamUnit resolveParamUnitNode(String mapID,Element paramUnitNode,JdaContainer container,Map classMap,Map resultMapTable,SqlFileXMLTags tags)throws SQLException{
		Class propertyType = null;
		String propertyName =paramUnitNode.getAttributeValue(tags.ATTR_Property);
		String javaType=paramUnitNode.getAttributeValue(tags.ATTR_JavaType);
		String jdbcType = paramUnitNode.getAttributeValue(tags.ATTR_JdbcType);
		String paramMode= paramUnitNode.getAttributeValue(tags.ATTR_Mode); 
		String paramPersisterName= paramUnitNode.getAttributeValue(tags.ATTR_Param_Persister); 
		
		if(StringUtil.isNull(propertyName))
			throw new ParamMapException(null,"ParamMap("+mapID+")property name can't be null");
		if(!StringUtil.isNull(javaType))
			propertyType = getClassType(mapID,javaType,classMap);
		
		ParamUnit paramUnit=container.createParamUnit(propertyName,propertyType);
		
		if(!StringUtil.isNull(jdbcType)){
		  paramUnit.setParamColumnTypeName(jdbcType);
		  if(!container.containsJdbcType(jdbcType)) 
			 throw new ParamMapException(null,"ParamMap("+mapID+")invalidate jdbc type:"+jdbcType);
		}
		
		if(!StringUtil.isNull(paramMode)){
	  	ParamValueMode mode = ParamValueMode.getParamMode(paramMode);
	  	if(mode == null)
	  		throw new ParamMapException(null,"ParamMap("+mapID+")invalidate parameter mode: "+ mode);
	  	paramUnit.setParamValueMode(mode);
	  }

	  if(!StringUtil.isNull(paramPersisterName))
	  	paramUnit.setJdbcTypePersister(loadParamPersister(mapID,paramPersisterName,classMap));
	  
	  
		String cursorResultMapId=paramUnitNode.getAttributeValue(tags.ATTR_ResultMap); 
		String cursorresultClassName=paramUnitNode.getAttributeValue(tags.ATTR_ResultClass); 
		
		Class cursorResultClass=null;
		ResultMap cursorResultMap=null;
		if(!StringUtil.isNull(cursorresultClassName)) 
			cursorResultClass = this.getClassType(mapID,cursorresultClassName,classMap);
		if(!StringUtil.isNull(cursorResultMapId))
			cursorResultMap = (ResultMap) resultMapTable.get(cursorResultMapId);
		else if(cursorResultClass != null)
			cursorResultMap = container.createResultMap(cursorResultClass, null);
	
		paramUnit.setCursorResultMap(cursorResultMap);
	  return paramUnit;
	}
	
	/**
	 * 获得类
	 */
	private Class getClassType(String mapID,String className,Map classMap)throws SQLException{
		try {
			Class clazz= (Class)(classMap.get(className));
      return (clazz ==null)?ClassUtil.loadClass(className):clazz;
		} catch (ClassNotFoundException e) {
			throw new ParamMapException(null,"ParamMap("+mapID+")not found class:" + className,e);
		}
	}
	
	/**
	 * 装载自定义的Type callbackHandler
	 */
	private JdaTypePersister loadParamPersister(String mapID,String className,Map classMap)throws SQLException{
		try {
			Class clazz= (Class)(classMap.get(className));
			if(clazz == null)
				clazz= ClassUtil.loadClass(className);
			
			return (JdaTypePersister)clazz.newInstance();
		} catch (Exception e) {
			throw new ParamMapException(null,"ParamMap("+mapID+")failed load persister class:" + className,e);
		}
	}
}
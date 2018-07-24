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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.RelationUnit;
import org.jmin.jda.mapping.ResultMap;
import org.jmin.jda.mapping.ResultUnit;

/**
 * 结果映射表解析
 * 
 * @author Chris Liao
 */

public class ResultMapParser {
	
	/**
	 * 解析结果映射列表节点
	 */
	public ResultMap parse(String mapId,Element elment,JdaContainer container,Map classMap,SqlFileXMLTags tags)throws SQLException {
		Class resultClass = null;
		String classname = elment.getAttributeValue(tags.ATTR_Class);
		if(!StringUtil.isNull(classname)) 
			resultClass = this.getClassType(mapId,classname,classMap);
		
		if(!Map.class.isAssignableFrom(resultClass) && ClassUtil.isAbstractClass(resultClass))
			throw new ResultMapException("Result map["+mapId+"]result class can't be abstract");
		if(Collection.class.isAssignableFrom(resultClass))
			throw new ResultMapException("Result map["+mapId+"]result class can't be sub class of collection");
		if(resultClass.isArray())
			throw new ResultMapException("Result map["+mapId+"]result class can't be array type");

	
		List resultNodeList = elment.getChildren(tags.Node_Result);
		if(resultNodeList==null || resultNodeList.isEmpty())
			 throw new ResultMapException("Result map["+mapId+"]missed 'result' children node");
		
		List relationUnitList = new ArrayList();
		List resultUnitList = new ArrayList(resultNodeList.size());
		for(int i=0,n=resultNodeList.size();i<n;i++) {
			Element resultElement=(Element)resultNodeList.get(i);
			String selectID= resultElement.getAttributeValue(tags.ATTR_Select); 
			if(StringUtil.isNull(selectID)){
				resultUnitList.add(resolveResultUnitNode(mapId,resultElement,container,classMap,tags));
			}else {
				relationUnitList.add(resolveRelationUnitNode(mapId,resultElement,container,classMap,tags));
			}
		}
		
		ResultUnit[] resultUnits = (ResultUnit[])resultUnitList.toArray(new ResultUnit[resultUnitList.size()]);
		RelationUnit[] relationUnits = (RelationUnit[])relationUnitList.toArray(new RelationUnit[relationUnitList.size()]);
		return container.createResultMap(resultClass,resultUnits,relationUnits);
	}
	
	/**
	 * 解析单个参数节点
	 */
	private ResultUnit resolveResultUnitNode(String mapID,Element resultUnitNode,JdaContainer container,Map classMap,SqlFileXMLTags tags)throws SQLException{
		Class propertyType = null;
		String propertyName = resultUnitNode.getAttributeValue(tags.ATTR_Property);
		String javaType = resultUnitNode.getAttributeValue(tags.ATTR_JavaType);
		String jdbcType = resultUnitNode.getAttributeValue(tags.ATTR_JdbcType);
		String columnName= resultUnitNode.getAttributeValue(tags.ATTR_Column);
		String resultConvertName=  resultUnitNode.getAttributeValue(tags.ATTR_ResultConverter); 
		
		if(StringUtil.isNull(propertyName))
			throw new ResultMapException(null,"ResultMap("+mapID+")property name can't be null");
		if(!StringUtil.isNull(javaType))
			propertyType = getClassType(mapID,javaType,classMap);
		
		ResultUnit resultUnit=container.createResultUnit(propertyName,propertyType);
		
		if(!StringUtil.isNull(jdbcType)){
			resultUnit.setResultColumnTypeName(jdbcType);
		  if(!container.containsJdbcType(jdbcType)) 
			 throw new ParamMapException(null,"ResultMap("+mapID+")invalidate jdbc type:"+jdbcType);
		}
		
	  if(!StringUtil.isNull(columnName))
	  	resultUnit.setResultColumnName(columnName);
	  if(!StringUtil.isNull(resultConvertName))
	  	resultUnit.setJdbcTypePersister(loadResultConverter(mapID,resultConvertName,classMap));
	 
		return resultUnit;
	}
	
	/**
	 * 解析单个参数节点
	 */
	private RelationUnit resolveRelationUnitNode(String mapID,Element relationUnitNode,JdaContainer container,Map classMap,SqlFileXMLTags tags)throws SQLException{
		Class propertyType = null;
		String propertyName = relationUnitNode.getAttributeValue(tags.ATTR_Property);
		String javaType = relationUnitNode.getAttributeValue(tags.ATTR_JavaType);
		String columnName= relationUnitNode.getAttributeValue(tags.ATTR_Column);
		String selectID= relationUnitNode.getAttributeValue(tags.ATTR_Select); 
		String keyPropertyName= relationUnitNode.getAttributeValue(tags.ATTR_Key_Property); 
		String valuePropertyName= relationUnitNode.getAttributeValue(tags.ATTR_Value_Property); 
		String lazyValue= relationUnitNode.getAttributeValue(tags.ATTR_Lazy); 
		
		if(StringUtil.isNull(propertyName))
			throw new ResultMapException(null,"ResultMap("+mapID+")property name can't be null");
		if(!StringUtil.isNull(javaType))
			propertyType = getClassType(mapID,javaType,classMap);
		
		RelationUnit relationUnit=container.createRelationUnit(propertyName,propertyType,selectID);
		
		if(!StringUtil.isNull(columnName)) {
			relationUnit.setRelationColumnNames(StringUtil.split(columnName,","));
		} else {
			throw new ResultMapException(null,"ResultMap("+mapID+")missed relation fields");
		}

		if(!StringUtil.isNull(keyPropertyName)) 
			relationUnit.setMapKeyPropertyName(keyPropertyName);
		
		if (!StringUtil.isNull(valuePropertyName)) 
			relationUnit.setMapValuePropertyName(valuePropertyName);
		
		if (!StringUtil.isNull(lazyValue)) 
			relationUnit.setLazyLoad(Boolean.getBoolean(lazyValue));
	
		return  relationUnit;
	}
	
	/**
	 * 获得类
	 */
	private Class getClassType(String mapID,String className,Map classMap)throws SQLException{
		try {
			if(classMap.containsKey(className))
				return (Class)(classMap.get(className));
			else
			return ClassUtil.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ResultMapException(null,"ResultMap("+mapID+")not found parameter class:" + className,e);
		}
	}
	
	/**
	 * 装载自定义的Type callbackHandler
	 */
	private JdaTypePersister loadResultConverter(String mapID, String className,Map classMap)throws SQLException{
		try {
			Class clazz = null; 
			if(classMap.containsKey(className))
				clazz= (Class)(classMap.get(className));
			else
				clazz= ClassUtil.loadClass(className);
			return (JdaTypePersister)clazz.newInstance();
		} catch (Exception e) {
			throw new ParamMapException(null,"ResultMap("+mapID+")failed load converter class:" + className,e);
		}
	}
}
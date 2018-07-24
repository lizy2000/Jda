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
package org.jmin.jda.impl.mapping.result;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jmin.jda.impl.JdaContainerImpl;
import org.jmin.jda.impl.exception.ResultMapException;
import org.jmin.jda.impl.property.LazyProperty;
import org.jmin.jda.impl.property.LazyPropertyEditor;
import org.jmin.jda.impl.property.PropertyException;
import org.jmin.jda.impl.property.PropertyUtil;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.statement.SqlStaticStatement;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.RelationUnit;
import org.jmin.jda.mapping.ResultUnit;

/**
 * 结果映射验证
 * 
 * @author Chris
 */

public class ResultValidator {
	
	/**
	 * 类的编辑改造器
	 */

	private LazyPropertyEditor lazyPropertyEditor = new LazyPropertyEditor();
		
	/**
	 * 检查映射结果属性
	 */
	public void checkResultMap(Object id,SqlOperationType sqlType,ResultMapImpl resultMap,JdaContainerImpl container)throws SQLException{
		if(resultMap!=null && !resultMap.isInited()){
			Class resultClass=resultMap.getResultClass();
			ResultUnit[]resultUnits=resultMap.getResultUnits();
			RelationUnit[]relationUnits=resultMap.getRelationUnits();
			
			if(resultClass==null)
				throw new ResultMapException(id,"Result class can't be null");
		  if(Collection.class.isAssignableFrom(resultClass))
		  	throw new ResultMapException(id,"Result class must be map type or bean type");
			if(!Map.class.isAssignableFrom(resultClass) && ClassUtil.isAbstractClass(resultClass))
				throw new ResultMapException(id,"Result class can't be abstract");
			if(!Map.class.isAssignableFrom(resultClass) && !ClassUtil.existDefaultConstructor(resultClass))
				throw new ResultMapException(id,"Result class("+resultClass.getName()+")missed default constructor");

			if(!SqlOperationType.Select.equals(sqlType) && !SqlOperationType.Procedure.equals(sqlType)) //不是查询Select或过程
				throw new ResultMapException(id,"Target sql is not query expression");

			if(container.containsTypeConverter(resultClass)){////结果类为可直接映射
				if(resultUnits==null || resultUnits.length==0){
					resultUnits= new ResultUnit[1];
					resultUnits[0]= new ResultUnitImpl(null,resultClass);
					ResultUnitImpl unit=(ResultUnitImpl)resultUnits[0];
					unit.setJdbcTypePersister(container.getTypePersister(unit.getPropertyType(),unit.getResultColumnTypeName()));
					unit.setInited(true);
					unit.setMapOwner(resultMap);
				}else if(resultUnits.length>=1){
					ResultUnitImpl unit=(ResultUnitImpl)resultUnits[0];
					unit.setPropertyType(resultClass);
					if(unit.getJdbcTypePersister()==null)
				  unit.setJdbcTypePersister(container.getTypePersister(unit.getPropertyType(),unit.getResultColumnTypeName()));
					unit.setInited(true);
					unit.setMapOwner(resultMap);
				}
			}else{//结果类间接映射
				for(int i=0,n=(resultUnits==null)?0:resultUnits.length;i<n;i++){
					ResultUnitImpl unit=(ResultUnitImpl)resultUnits[i];
					if(StringUtil.isNull(unit.getPropertyName()))
						throw new ResultMapException(id,"Result unit["+i+"]property name can't be null");	
					if(unit.getMapOwner()!=null && unit.getMapOwner()!=resultMap)
						throw new ResultMapException(id,"Result unit["+i+"]("+unit.getPropertyName()+")property used in another map");
				
					if(StringUtil.isNull(unit.getResultColumnName()))
						throw new ResultMapException(id,"Result unit["+i+":"+unit.getPropertyName()+"]result column name can't be null");
				
					if(unit.getPropertyType()==null && !Map.class.isAssignableFrom(resultClass)){
						try {
								unit.setPropertyType(PropertyUtil.getPropertyType(resultClass,unit.getPropertyName()));
						} catch (PropertyException e) {
							throw new ResultMapException(id,"Result unit["+i+"]("+unit.getPropertyName()+")property exception",e);
						}
					}
					
					if(unit.getPropertyType()==null)
						throw new ResultMapException(id,"Result unit["+i+"]("+unit.getPropertyName()+")property type can't be null");
					if(unit.getJdbcTypePersister()==null)
						unit.setJdbcTypePersister(container.getTypePersister(unit.getPropertyType(),unit.getResultColumnTypeName()));
					if(unit.getJdbcTypePersister()==null)
						throw new ResultMapException(id,"Result unit["+i+"]("+unit.getPropertyName()+")converter can't be null");
				 
					unit.setInited(true);
					unit.setMapOwner(resultMap);
				}
			
				
				//检查关联映射
				for(int i= 0,n=(relationUnits==null)?0:relationUnits.length;i<n; i++) {
					RelationUnitImpl unit = (RelationUnitImpl) relationUnits[i];
					if(StringUtil.isNull(unit.getPropertyName()))
						throw new ResultMapException(id,"Relation unit["+i+"]property name can't be null");
					if(unit.getMapOwner()!=null && unit.getMapOwner()!=resultMap)
						throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")property used in another map");
				
					if(StringUtil.isNull(unit.getSqlId()))
						throw new ResultMapException(id, "Relation unit["+i+"]("+unit.getPropertyName()+")sql id can't be null");
					if(unit.getPropertyType()==null && !Map.class.isAssignableFrom(resultClass)){//当前定义属性类型为空
						try {
							unit.setPropertyType(PropertyUtil.getPropertyType(resultClass,unit.getPropertyName()));
						} catch (PropertyException e) {
							throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")property exception",e);
						}
					}
				
					if(unit.isLazyLoad()){//当前属性需要延迟加载，延迟加载的属性必须位于结果类，不能为多级别
						if(Map.class.isAssignableFrom(resultClass))
							throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")property not support lazy load in Map result");
						if(unit.getPropertyName().indexOf(".")>=0)
							throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")property not support lazy load with '.' nested");
					}
						
					if(unit.getPropertyType()==null)
						throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")property type can't be null");
			    if(Map.class.isAssignableFrom(unit.getPropertyType()) && StringUtil.isNull(unit.getMapKeyPropertyName()))
			    	throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")key property name can't be null");
					
					if(container.containsSql(unit.getSqlId())){
						SqlBaseStatement statment = container.getSqlStatement(unit.getSqlId());
						if(!(statment instanceof SqlStaticStatement))
							throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")sql("+unit.getSqlId()+")is not a static statement");
						if(!SqlOperationType.Select.equals(statment.getSqlType()))
							throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")sql("+unit.getSqlId()+")is not select statement");
						
						SqlStaticStatement targetStatment = (SqlStaticStatement)statment;
						ParamUnit[] paramUnits = targetStatment.getParamUnits();
						String[] relateColumnNames = unit.getRelationColumnNames();
						if(paramUnits!=null){
							int columnNamesLen  =(relateColumnNames!=null)?relateColumnNames.length:0;
							if(paramUnits.length!=columnNamesLen)
							throw new ResultMapException(id,"Relation unit["+i+"]("+unit.getPropertyName()+")column names length not match sql("+unit.getSqlId()+")parameter units");
						}
					 }
					
					unit.setInited(true);
					unit.setMapOwner(resultMap);
		 		 }
			}
			
			rebuildResultClassForLazy(id,resultMap);
			resultMap.setInited(true);
		}
	}
	
	/**
	 * 为延迟加载属性，进行结果类的改造
	 */
	private void rebuildResultClassForLazy(Object sqlId,ResultMapImpl resultMap)throws SQLException{
	  try {
	  	 if(resultMap!=null){
		  	Class resultClass=resultMap.getResultClass();
				RelationUnit[]relationUnits=resultMap.getRelationUnits();
				List propertyList = new ArrayList();
				if(resultMap!=null && !resultMap.isLayLoadInited()){//结果Map中还没有对初始化延迟加载属性
					for(int i=0,n=(relationUnits==null)?0:relationUnits.length;i<n;i++) {
						RelationUnitImpl unit =(RelationUnitImpl)relationUnits[i];
						if(unit.isLazyLoad()){
							propertyList.add(new LazyProperty(unit.getPropertyName(),unit.getPropertyType()));
						}
					}
					
					if(!propertyList.isEmpty()){
						LazyProperty[] properties =(LazyProperty[])propertyList.toArray(new LazyProperty[propertyList.size()]);
						Class subClass = lazyPropertyEditor.createSubClass(resultClass,properties);
						resultMap.setLayLoadresultClass(subClass);
					 }else{
						resultMap.setLayLoadresultClass(resultClass);
					 }
					
					resultMap.setLayLoadInited(true);
				} 
	  	}
		} catch (Throwable e) {
			throw new ResultMapException(sqlId,e.getMessage(),e);
		}
	}
}
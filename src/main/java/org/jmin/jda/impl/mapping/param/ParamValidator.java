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
package org.jmin.jda.impl.mapping.param;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.jmin.jda.impl.JdaContainerImpl;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.impl.mapping.result.ResultMapImpl;
import org.jmin.jda.impl.property.PropertyException;
import org.jmin.jda.impl.property.PropertyUtil;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;

/**
 * 参数映射验证
 * 
 * @author Chris
 */
public class ParamValidator {
	
	/**
	 * 检查映射参数属性
	 */
	public void checkParamMap(Object id,SqlOperationType sqlType,int paramCount,ParamMapImpl paramMap,JdaContainerImpl container)throws SQLException{
		if(paramMap!=null && !paramMap.isInited()){
			Class paramClass= paramMap.getParamClass();
			ParamUnit[]paramUnits=paramMap.getParamUnits();
			
			if(paramClass==null)
				throw new ParamMapException(id,"Parameter class can't be null");
			if(Collection.class.isAssignableFrom(paramClass))
		  	throw new ParamMapException(id,"Parameter class must be map type or bean type");
			if((paramUnits==null || paramUnits.length==0)&&!container.containsTypePersister(paramClass))
				throw new ParamMapException(id,"Parameter units missed");			
		 
			if(container.containsTypePersister(paramClass)){//参数类为可直接映射
				if(paramCount !=1)
					throw new ParamMapException(id,"Sql parameter count can't match parameter map definition");			
				
				if(paramUnits==null || paramUnits.length==0){
					paramUnits= new ParamUnit[1];
					paramUnits[0]= new ParamUnitImpl(null,paramClass);
					((ParamUnitImpl)paramUnits[0]).setJdbcTypePersister(container.getTypePersister(paramClass));
					this.checkParamUnitForProcedure(id,sqlType,0,(ParamUnitImpl)paramUnits[0],paramMap,container);
				}else if(paramUnits.length>=1){
					ParamUnitImpl unit=(ParamUnitImpl)paramUnits[0];
					unit.setPropertyType(paramClass);
					if(!StringUtil.isNull(unit.getParamColumnTypeName()))
					 unit.setParamColumnTypeCode(container.getJdbcTypeCode(unit.getParamColumnTypeName()));
					if(unit.getJdbcTypePersister()==null)
					 unit.setJdbcTypePersister(container.getTypePersister(unit.getPropertyType(),unit.getParamColumnTypeName()));
					this.checkParamUnitForProcedure(id,sqlType,0,unit,paramMap,container);
				}
			}else{//不可直接映射
				if(paramUnits.length !=paramCount)
					throw new ParamMapException(id,"Sql parameter count can't match parameter map definition: "+ (paramUnits.length) + ":" + paramCount);			
				
				for(int i=0,n=paramUnits.length;i<n;i++){
					ParamUnitImpl unit=(ParamUnitImpl)paramUnits[i];
					ParamValueMode valueMode=unit.getParamValueMode();
					
					if(StringUtil.isNull(unit.getPropertyName()))
						throw new ParamMapException(id,"Parameter unit["+i+"]property name can't be null");	
					if(unit.getMapOwner()!=null && unit.getMapOwner()!=paramMap)
						throw new ParamMapException(id,"Parameter unit["+i+"]("+unit.getPropertyName()+")has been used in another map");
					if(!SqlOperationType.Procedure.equals(sqlType) && !ParamValueMode.IN.equals(valueMode))
						throw new ParamMapException(id,"Parameter unit["+i+"]("+unit.getPropertyName()+")can't be out type parameter in sql type:"+sqlType);
					
					if(unit.getPropertyType()==null){
						if(Map.class.isAssignableFrom(paramClass)){
							 unit.setPropertyType(Object.class);
						}else{
							try {
								 unit.setPropertyType(PropertyUtil.getPropertyType(paramClass,unit.getPropertyName()));
						  } catch (PropertyException e) {
							  throw new ParamMapException(id,"Parameter unit["+i+"]("+unit.getPropertyName()+")property exception",e);
						  }
						}
					}
								
					if(unit.getPropertyType()==null)
						throw new ParamMapException(id,"Parameter unit["+i+"]("+unit.getPropertyName()+")property type can't be null");
					if(!StringUtil.isNull(unit.getParamColumnTypeName()))
						unit.setParamColumnTypeCode(container.getJdbcTypeCode(unit.getParamColumnTypeName()));
					if(unit.getJdbcTypePersister()==null)
						unit.setJdbcTypePersister(container.getTypePersister(unit.getPropertyType(),unit.getParamColumnTypeName()));
					if(unit.getJdbcTypePersister()==null)
						throw new ParamMapException(id,"Parameter unit["+i+"]persister can't be null");	
					
					this.checkParamUnitForProcedure(id,sqlType,i,unit,paramMap,container);
				 }
		  }
			paramMap.setInited(true);
		}
	}
	
	/**
	 * 为存储过程检查
	 */
	private void checkParamUnitForProcedure(Object id, SqlOperationType sqlType,int i,ParamUnitImpl unit,ParamMapImpl paramMap,JdaContainerImpl container)throws SQLException{
	 if(SqlOperationType.Procedure.equals(sqlType) && unit.getCursorResultMap()!=null){
		if(!ParamValueMode.OUT.equals(unit.getParamValueMode()))
			throw new ParamMapException(id,"Parmeter mode must be [out] for procedure mapping unit["+i+"]");
		
		container.getResultValidator().checkResultMap(id,sqlType,(ResultMapImpl)unit.getCursorResultMap(),container);
		unit.setInited(true);
		unit.setMapOwner(paramMap);
		}
	}
 }
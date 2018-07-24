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
package org.jmin.jda.impl.execution.worker;

import java.sql.SQLException;

import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;

/**
 * 关联辅助类
 * 
 * @author chris 
 */

public class RelationObjectFactory {
	
	/**
	 * 获得参数名
	 */
	public String[] getParamNames(ParamUnit[]paramUnits)throws SQLException{
		String[] paramNames = new String[paramUnits==null?0:paramUnits.length];
		for(int i=0,n=paramNames.length;i<n;i++){
			paramNames[i]=paramUnits[i].getPropertyName();
		}
		return paramNames;
	}
	
	/**
	 * 获得参数值
	 */
	public Object[] getParamValues(JdaSessionImpl session,String sqlID,Class paramClass,Object paramObj,ParamUnit[]paramUnits)throws SQLException{
		if(session.supportsPersisterType(paramClass)){//直接可映射类型
			return new Object[]{paramObj};
		}else{
			Object[] paramValues = new Object[paramUnits==null?0:paramUnits.length];
			ParamObjectFactory paramObjectFactory=session.getParamObjectFactory();
			for(int i=0,n=paramValues.length;i<n;i++){
				paramValues[i]= paramObjectFactory.getPropertyValue(session,sqlID,paramObj,paramUnits[i].getPropertyName());
			}
			return paramValues;
	  }
	}
	
	/**
	 * 获得参数名
	 */
	public Class[] getParamTypes(ParamUnit[]paramUnits,Object[]paramValues)throws SQLException{
		Class[] paramTypes = new Class[paramUnits==null?0:paramUnits.length];
		for(int i=0,n=paramTypes.length;i<n;i++){
			if(paramUnits[i].getPropertyType()!=null)
			 paramTypes[i]= paramUnits[i].getPropertyType();
			else
				paramTypes[i]= (paramValues[i]==null)?Object.class:paramValues[i].getClass();
		}
		return paramTypes;
	}
	
	/**
	 * 获得参数名
	 */
	public int[] getParamSQLTypes(ParamUnit[]paramUnits)throws SQLException{
		int[] sqlTypes = new int[paramUnits==null?0:paramUnits.length];
		for(int i=0,n=sqlTypes.length;i<n;i++){
			sqlTypes[i]=paramUnits[i].getParamColumnTypeCode();
		}
		return sqlTypes;
	}
	
	/**
	 * 获得参数值模式
	 */
  public ParamValueMode[] getParamValueMode(ParamUnit[]paramUnits)throws SQLException{
  	ParamValueMode[] modes = new ParamValueMode[paramUnits==null?0:paramUnits.length];
  	for(int i=0,n=modes.length;i<n;i++){
  		modes[i]=paramUnits[i].getParamValueMode();
		}
  	return modes;
  }
	
	
  /**
	 * 获得参数持久器
	 */
  public JdaTypePersister[] getParamTypePersisters(JdaSessionImpl session,ParamUnit[]paramUnits,Object[] paramValues)throws SQLException{
  	JdaTypePersister[] persisters = new JdaTypePersister[paramUnits==null?0:paramUnits.length];
  	for(int i=0,n=persisters.length;i<n;i++){
  		if(paramUnits[i].getJdbcTypePersister()!=null){
  			persisters[i]=paramUnits[i].getJdbcTypePersister();
  		}else{
  			Class paramType = paramUnits[i].getPropertyType(); 
  			if(paramType==null && paramValues[i]!=null){
  				paramType = paramValues[i].getClass();
  			}
  			persisters[i]= session.getTypePersister(paramType,paramUnits[i].getParamColumnTypeName());
  		}
		}
  	return persisters;
  }
}

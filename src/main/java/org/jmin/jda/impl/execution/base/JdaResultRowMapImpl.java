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
package org.jmin.jda.impl.execution.base;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jmin.jda.JdaResultRowMap;

/**
 * 记录行
 * 
 * @author Chris
 */

public class JdaResultRowMapImpl implements JdaResultRowMap{
	
	/**
	 * 结构
	 */
	private Map dataMap = new HashMap();
	
	/**
	 * 获得列值
	 */
	public boolean exists(int index)throws SQLException{
		return	dataMap.containsKey(Integer.valueOf(index));
	}

	/**
	 * 获得列值
	 */
	public boolean exists(String column)throws SQLException{
		if(column!=null)
			return dataMap.containsKey(column);
		else
			return false;
	}

	/**
	 * 获得列值
	 */
	public Object get(int index)throws SQLException{
		return	dataMap.get(Integer.valueOf(index));
	}

	/**
	 * 获得列值
	 */
	public Object get(String column)throws SQLException{
		if(column!=null)
			return dataMap.get(column);
		else
			return null;
	}
	
	/**
	 *设置列值
	 */
	public void set(int index,Object value)throws SQLException{
		dataMap.put(Integer.valueOf(index),value);
	}
	
	/**
	 *设置列值
	 */
	public void set(String column,Object value)throws SQLException{
		if(column!=null)
			dataMap.put(column,value);
	}
}

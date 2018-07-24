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
package org.jmin.jda.impl.cache;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 缓存Key
 * 
 * @author Liao
 */
public class CacheKey implements Serializable {

	/**
	 * SQL定义Id
	 */
	private String sqlId;
	
	/**
	 * 执行的SQL
	 */
	private String sqlText;
	
	/**
	 * 参数对象
	 */
	private Object[] parmValues;

	/**
	 * 附加参数对象
	 */
	private Object[] optionalValues;

	/**
	 * hash Code
	 */
	private int hashCode;

	/**
	 * 构造函数
	 */
	public CacheKey(String sqlId,String sqlText, Object[] parmValues,Object[] optionalValues) {
		this.sqlId = sqlId;
		this.sqlText =sqlText;
		this.parmValues = parmValues;
		this.optionalValues=optionalValues;
		
		this.hashCode=sqlId.hashCode();
		this.hashCode=31*hashCode + sqlText.hashCode();
		this.hashCode=31*hashCode + Arrays.hashCode(parmValues);
		this.hashCode=31*hashCode + Arrays.hashCode(optionalValues);
	}

	/**
	 * SQL定义Id
	 */
	public Object getSqlId() {
		return sqlId;
	}

	/**
	 * 执行的SQL
	 */
	public String getSqlText() {
		return sqlText;
	}

	/**
	 * 参数对象
	 */
	public Object[] getParameterValues() {
		return parmValues;
	}

	/**
	 * 重写HashCode
	 */
	public int hashCode() {
		return this.hashCode;
	}

	/**
	 * 重写equals
	 */
	public boolean equals(Object obj) {
		if (obj instanceof CacheKey) {
			CacheKey other = (CacheKey) obj;
			return (this.sqlId.equals(other.sqlId)
					&& this.sqlText.equals(other.sqlText)
					&& Arrays.equals(this.parmValues, other.parmValues)
					&& Arrays.equals(this.optionalValues,other.optionalValues));
		} else {
			return false;
		}
	}

	/**
	 * 重写toString
	 */
	public String toString() {
		StringBuffer returnValue = new StringBuffer();
		returnValue.append(hashCode());
		returnValue.append('|');
		returnValue.append(sqlId);
		returnValue.append('|');
		returnValue.append(sqlText);
		for (int i = 0; parmValues != null && i < parmValues.length; i++)
			returnValue.append('|').append(parmValues[i]);
		for (int i = 0; optionalValues != null && i < optionalValues.length; i++)
			returnValue.append('|').append(optionalValues[i]);
		return returnValue.toString();
	}
}

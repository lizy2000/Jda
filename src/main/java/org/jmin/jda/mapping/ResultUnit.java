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
package org.jmin.jda.mapping;

import org.jmin.jda.JdaTypePersister;

/**
 * 对象中的结果映射属性
 * 
 * @author Chris
 */

public interface ResultUnit{
		
	/**
	 * 获得结果名称
	 */
	public String getPropertyName();

	/**
	 * 结果单元类别
	 */
	public Class getPropertyType();

	/**
	 * 结果单元类别
	 */
	public void setPropertyType(Class type);
	
	/**
	 * 获得属性类别
	 */
	public String getResultColumnName();

	/**
	 * 设置属性类别
	 */
	public void setResultColumnName(String name);
	
	/**
	 * 返回映射列的类型名
	 */
	public String getResultColumnTypeName();

	/**
	 * 设置映射列的类型名
	 */
	public void setResultColumnTypeName(String name);
	
	/**
	 * 获得结果映射
	 */
	public JdaTypePersister getJdbcTypePersister();
	
	/**
	 * 设置结果映射
	 */
	public void setJdbcTypePersister(JdaTypePersister persister);
}

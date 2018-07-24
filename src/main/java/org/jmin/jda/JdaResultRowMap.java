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
package org.jmin.jda;

import java.sql.SQLException;

/**
 * 持久化记录行,便于类型转换
 * 
 * @author Chris
 */

public interface JdaResultRowMap {
	
	/**
	 * 获得列值
	 */
	public Object get(int index)throws SQLException;

	/**
	 * 获得列值
	 */
	public Object get(String column)throws SQLException;
	
	/**
	 *设置列值
	 */
	public void set(int index,Object value)throws SQLException;
	
	/**
	 *设置列值
	 */
	public void set(String column,Object value)throws SQLException;
	
	/**
	 * 判断某列是否已经设值
	 */
	public boolean exists(int index)throws SQLException;
	
	/**
	 * 判断某列是否已经设值
	 */
	public boolean exists(String column)throws SQLException;
	
}

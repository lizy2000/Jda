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
package org.jmin.jda.statement;

/**
 * SQL定义的一些属性映射
 * 
 * @author Chris Liao
 */
public interface SqlPropertyTable {
	
	/**
	 * 获得属性值名字
	 */
	public String[] getNames();
	
	/**
	 * 获得属性值
	 */
	public String getValue(String name);
	
	/**
	 * 删除值
	 */
	public void removeValue(String name);
	
	/**
	 * 设置值
	 */
	public void putValue(String name,String value);

}

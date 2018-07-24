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
package org.jmin.jda.impl.property;

/**
 * 延迟加载属性
 * 
 * @author Chris Liao
 */
public class LazyProperty {

	/**
	 * 属性名
	 */
	private String propertyName;

	/**
	 * 属性类型
	 */
	private Class propertyType;
	
	/**
	 * 构造函数
	 */
	public LazyProperty(String propertyName,Class propertyType){
		this.propertyName = propertyName;
		this.propertyType = propertyType;
	}
	
	/**
	 * 属性名
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * 属性类型
	 */
	public Class getPropertyType() {
		return propertyType;
	}
}

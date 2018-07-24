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

/**
 * 类型转换器列表
 * 
 * Chris Liao 
 */

public interface JdaTypeConvertFactory {
	
	/**
	 * 是否包含转换器
	 */
	public boolean supportsType(Class type);
	
	/**
	 * 获取类的转换器
	 */
	public JdaTypeConverter getTypeConverter(Class type);
	
	/**
	 * 转换对象
	 */
	public Object convert(Object object,Class type)throws JdaTypeConvertException;
	
}

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
package org.jmin.jda.impl.exception;

import org.jmin.jda.JdaException;

/**
 * SQL定义异常
 *
 * @author Chris
 * @version 1.0
 */
public class SqlDefinitionException extends JdaException {
	
	/**
	 * 构造函数
	 */
	public SqlDefinitionException(Object sqlId){
		super(sqlId);
	}
	
	/**
	 * 构造函数
	 */
	public SqlDefinitionException(Object sqlId,String message) {
		super(sqlId,message);
	}
	
	/**
	 * 构造函数
	 */
	public SqlDefinitionException(Object sqlId,Throwable cause) {
		super(sqlId,cause);
	}
	
	/**
	 * 构造函数
	 */
	public SqlDefinitionException(Object sqlId,String message,Throwable cause) {
		super(sqlId,message,cause);
	}
}
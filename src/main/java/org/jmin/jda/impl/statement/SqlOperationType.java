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
package org.jmin.jda.impl.statement;

import org.jmin.jda.impl.util.Symbols;

/**
 * SQL文本操作类型
 * 
 * @author Chris Liao
 */

public final class SqlOperationType {
	
	/**
	 * Insert操作类型
	 */
	public static final SqlOperationType Insert = new SqlOperationType(1,"Insert");
 
	/**
	 * Update操作类型 
	 */
	public static final SqlOperationType Update = new SqlOperationType(2,"Update");
	
	/**
	 * Delete操作类型
	 */
	public static final SqlOperationType Delete = new SqlOperationType(3,"Delete");

	/**
	 * 查询操作类型
	 */
	public static final SqlOperationType Select = new SqlOperationType(4,"Select");

	/**
	 * 存储过程调用类型
	 */
	public static final SqlOperationType Procedure = new SqlOperationType(5,"Procedure");
	
	/**
	 * 未知的操作类型，一般是那些DDL的操作
	 */
	public static final SqlOperationType Unknown = new SqlOperationType(6,"Unknown");
	
	/**
	 * type code
	 */
	private int code;
	
	/**
	 * type name
	 */
	private String name;
	
	/**
	 * 构造函数
	 */
	private SqlOperationType(int code,String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * type code
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * type name
	 */
	public String getName() {
		return name;
	}

	/**
	 * hashcode
	 */
	public int hashCode() {
		return this.code;
	}
	
	/**
	 * equals
	 */
	public boolean equals(Object obj) {
		if (obj instanceof SqlOperationType) {
			SqlOperationType other = (SqlOperationType) obj;
			return this.code == other.code;
		} else {
			return false;
		}
	}
	
	/**
	 * override method
	 */
	public String toString(){
		String type =Symbols.Blank;
		switch(code){
			case 1:type=Insert.getName();break;
			case 2:type=Update.getName();break;
			case 3:type=Delete.getName();break;
			case 4:type=Select.getName();break;
			case 5:type=Procedure.getName();break;
			case 6:type=Unknown.getName();break;
			default:type=Unknown.getName();break;
		}
		return type;
	}
}

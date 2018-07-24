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

import java.sql.Connection;

/**
 * 事物隔离等级
 * 
 * @author Chris Liao
 */

public class TransactionLevel {

	/**
	 * 隔离代码
	 */
	private int code;
	
	/**
	 * 描述
	 */
	private String name;

	/**
	 * 构造函数
	 */
	TransactionLevel(int code,String name){
		this.code = code;
		this.name = name;
	}

	/**
	 * 获取隔离代码
	 */
	public int getIsolationCode() {
		return code;
	}
	
	/**
	 * 获取隔离代码
	 */
	public String getIsolationName() {
		return name;
	}
	
	/**
	 * 重新方法
	 */
	public int hashCode() {
		return this.code;
	}
	
	/**
	 * 重新方法
	 */
	public boolean equals(Object obj) {
		if (obj instanceof TransactionLevel) {
			TransactionLevel other = (TransactionLevel) obj;
			return this.code == other.code;
		} else {
			return false;
		}
	}
	
	public static final TransactionLevel TRANSACTION_READ_UNCOMMITTED = new TransactionLevel(Connection.TRANSACTION_READ_UNCOMMITTED,"TRANSACTION_READ_UNCOMMITTED");
	
	public static final TransactionLevel TRANSACTION_READ_COMMITTED = new TransactionLevel(Connection.TRANSACTION_READ_COMMITTED,"TRANSACTION_READ_COMMITTED");
	
	public static final TransactionLevel TRANSACTION_REPEATABLE_READ = new TransactionLevel(Connection.TRANSACTION_REPEATABLE_READ,"TRANSACTION_REPEATABLE_READ");
	
	public static final TransactionLevel TRANSACTION_SERIALIZABLE = new TransactionLevel(Connection.TRANSACTION_SERIALIZABLE,"TRANSACTION_SERIALIZABLE");

	/**
	 * 获得事物隔离等级
	 */
	public static TransactionLevel getTransactionIsolation(String name){
		if(TRANSACTION_READ_UNCOMMITTED.name.equalsIgnoreCase(name)){
			return TRANSACTION_READ_UNCOMMITTED;
		}else if(TRANSACTION_READ_COMMITTED.name.equalsIgnoreCase(name)){
			return TRANSACTION_READ_COMMITTED;
		}else if(TRANSACTION_REPEATABLE_READ.name.equalsIgnoreCase(name)){
			return TRANSACTION_REPEATABLE_READ;
		}else if(TRANSACTION_SERIALIZABLE.name.equalsIgnoreCase(name)){
			return TRANSACTION_SERIALIZABLE;
		}else{
		    return TRANSACTION_READ_COMMITTED;
		}
	}
	
	/**
	 * 获得事物隔离等级
	 */
	public static TransactionLevel getTransactionIsolation(int code){
	   if(TRANSACTION_READ_UNCOMMITTED.code == code){
			return TRANSACTION_READ_UNCOMMITTED;
		}else if(TRANSACTION_READ_COMMITTED.code == code){
			return TRANSACTION_READ_COMMITTED;
		}else if(TRANSACTION_REPEATABLE_READ.code == code){
			return TRANSACTION_REPEATABLE_READ;
		}else if(TRANSACTION_SERIALIZABLE.code == code){
			return TRANSACTION_SERIALIZABLE;
		}else{
			return TRANSACTION_READ_COMMITTED;
		}
	}
}

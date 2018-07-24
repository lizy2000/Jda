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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Jdbc异常
 *
 * @author Chris Liao
 * @version 1.0
 */

public class JdaException extends SQLException {
	
	/**
	 * 产生异常的SQL Id
	 */
	private Object sqlId;
	
	/**
	 * 异常触发原因
	 */
	private Throwable reason=null;

	/**
	 * 构造函数
	 */
	public JdaException(Object sqlId){
		this(sqlId,(String)null);
	}
	
	/**
	 * 构造函数
	 */
	public JdaException(Object sqlId,String message) {
		this(sqlId,message,null);
	}
	
	/**
	 * 构造函数
	 */
	public JdaException(Object sqlId,Throwable cause) {
		this(sqlId,null,cause);
	}
	
	/**
	 * 构造函数
	 */
	public JdaException(Object sqlId,String message,Throwable cause) {
		super(message);
		this.sqlId =sqlId;
		this.reason = cause;
	}
	
	/**
	 * 获得SQL ID
	 */
	public Object getSqlId() {
		return this.sqlId;
	}
	
	/**
	 * 获得触发原因
	 */
	public Throwable getCauseException() {
		return reason;
	}
	
	/**
	 * 重写消息
	 */
  public String getMessage() {
  	if(sqlId==null)
  		return (super.getMessage()==null)?"":super.getMessage();
  	else
  	 return new StringBuffer("SQL("+sqlId+")").append(((super.getMessage()==null)?"":super.getMessage())).toString();
	}
  
	/**
	 * 打印堆栈，重写方法
	 */
	public void printStackTrace() {
		this.printStackTrace(System.err);
	}
	
	/**
	 * 打印堆栈，重写方法
	 */
	public void printStackTrace(PrintStream s) {
	 super.printStackTrace(s);
	 if(reason!=null){
			s.print("Caused by: ");
			reason.printStackTrace(s);
	 }
	}
	
	/**
	 * 打印堆栈，重写方法
	 */
	public void printStackTrace(PrintWriter w) {
		super.printStackTrace(w);
		if(reason!=null){
			w.print("Caused by: ");
			reason.printStackTrace(w);
		}
	}
}
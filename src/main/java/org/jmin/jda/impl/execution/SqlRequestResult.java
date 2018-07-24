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
package org.jmin.jda.impl.execution;

/**
 * SQL处理结果
 */
public class SqlRequestResult {

	/**
	 * 请求对象
	 */
	private SqlRequest request;
	
	/**
	 * 执行时间
	 */
	private long excuteTimeMis;
	
	/**
	 * 失败异常
	 */
	private Throwable failCause;

	/**
	 * 构造函数
	 */
	SqlRequestResult(SqlRequest request) {
		this.request = request;
	}
	
	/**
	 * 获得SQL ID
	 */
	public String getSQLId() {
		return this.request.getSqlId(); 
	}
	
	/**
	 * 获得SQL内容
	 */
	public String getSQL() {
		return this.request.getSqlText();
	}
	
	/**
	 * 获得失败异常
	 */
	public Throwable getFailCause() {
		return this.failCause;
	}
	
	/**
	 * 放入异常
	 */
	void setFailCause(Throwable failCause) {
		this.failCause = failCause;
	}
	
	/**
	 * 执行时间
	 */
	public long getExcuteTimeMis() {
		return excuteTimeMis;
	}
	
	/**
	 * 执行时间
	 */
	void setExcuteTimeMis(long excuteTimeMis) {
		this.excuteTimeMis = excuteTimeMis;
	}

}

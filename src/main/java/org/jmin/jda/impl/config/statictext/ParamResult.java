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
package org.jmin.jda.impl.config.statictext;

import org.jmin.jda.mapping.ParamMap;

/**
 * SQL文本分析结果
 * 
 * @author Chris Liao
 */
public class ParamResult {
	
	/**
	 * 可执行的SQL语句
	 */
	private String exeSQL=null;
	
	/**
	 * 可执行的SQL语句
	 */
	private ParamMap paramMap=null;

	public String getExeSQL() {
		return exeSQL;
	}

	public void setExeSQL(String exeSQL) {
		this.exeSQL = exeSQL;
	}

	public ParamMap getParamMap() {
		return paramMap;
	}

	public void setParamMap(ParamMap paramMap) {
		this.paramMap = paramMap;
	}
}

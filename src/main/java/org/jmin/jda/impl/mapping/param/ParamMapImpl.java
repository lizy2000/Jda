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
package org.jmin.jda.impl.mapping.param;

import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;

/**
 * 参数映射列表
 * 
 * @author Chris 
 */

public class ParamMapImpl implements ParamMap{
	
	/**
	 *是否初始化过
	 */
	private boolean inited;
	
	/**
	 * 参数类
	 */
	private Class paramClass;
	
	/**
	 * 映射单元
	 */
	private ParamUnit[] paramUnits;

	/**
	 * 构造函数
	 */
	public ParamMapImpl(Class paramClass,ParamUnit[] paramUnits) {
		this.paramClass = paramClass;
		this.paramUnits = paramUnits;
	}
	
	
	/**
	 *是否初始化过
	 */
	public boolean isInited() {
		return inited;
	}
	
	/**
	 *是否初始化过
	 */
	public void setInited(boolean inited) {
		this.inited = inited;
	}

	/**
	 * 参数类
	 */
	public Class getParamClass() {
		return paramClass;
	}
	
	/**
	 * 映射单元
	 */
	public ParamUnit[] getParamUnits() {
		return paramUnits;
	}
	
	/**
	 * 映射单元
	 */
	public void setParamUnits(ParamUnit[] paramUnits) {
		this.paramUnits = paramUnits;
	}
}

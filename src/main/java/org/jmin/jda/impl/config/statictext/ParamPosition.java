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

/**
 * SQL参数的位置
 * 
 * @author Chris Liao
 */

public class ParamPosition implements Comparable{

	/**
	 * 开始索引位置
	 */
	private int startIndex;
	
	/**
	 * 结束索引位置
	 */
	private int endIndex;
	
	/**
	 * 字串内容
	 */
	private ParamSymbol parameterSymbol;
	
	/**
	 * 构造函数
	 */
	public ParamPosition(int startIndex,int endIndex,ParamSymbol parameterSymbol){
		this.startIndex =startIndex;
		this.endIndex = endIndex;
		this.parameterSymbol = parameterSymbol;
	}
	
	public int getStartIndex() {
		return startIndex;
	}

  public int getEndIndex() {
		return endIndex;
	}

	public ParamSymbol getParamSymbol() {
		return parameterSymbol;
	}
	
 	/**
 	 * 比较
	 */
  public int compareTo(Object o){
  	if(o!=null && o instanceof ParamPosition){
	  	ParamPosition other =(ParamPosition)o;
	  	if(this.startIndex < other.startIndex){
	  		return -1;
	  	}else if(this.startIndex == other.startIndex){
	  		return 0;
	  	}else {
	  		return 1;
	  	}
  	}else{
  		return -1;
  	}
  }
}

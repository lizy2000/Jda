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
package org.jmin.jda.mapping;

/**
 * 存储过程调用的参数模式
 * 
 * @author Chris
 */
public final class ParamValueMode {
	public final static ParamValueMode IN =new ParamValueMode(0);//进参数
	public final static ParamValueMode OUT =new ParamValueMode(1);//出参数
	public final static ParamValueMode INOUT =new ParamValueMode(2);//进出参数
	
	private int modeValue;
	public ParamValueMode(int modeValue){
		this.modeValue = modeValue;
	}
	
	/**
	 * Override hashCode
	 */
	public int hashCode(){
		return this.modeValue;
	}
  
	
	/**
	 * 重写方法
	 */
	public boolean equals(Object obj){
		if(obj instanceof ParamValueMode){
			ParamValueMode other = (ParamValueMode)obj;
			return this.modeValue == other.modeValue;
		}else {
			return false;
		}
	}
	
	/**
	 * 获得参数模式
	 */
	public static ParamValueMode getParamMode(String name){
	 if(name.equalsIgnoreCase("in")){
		 return IN;
	 }else if(name.equalsIgnoreCase("out")){
		 return OUT;
	 }else if(name.equalsIgnoreCase("inout")){
		 return INOUT;
	 }else{
		 return null;
	 }
	}
}
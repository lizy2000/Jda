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
package org.jmin.jda.statement.tag;

import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.statement.DynTag;
 
/**
 * SQL 文本片段
 * 
 * @author Chris
 */

public final class TextTag extends DynTag{
	
	/**
	 * SQL文本
	 */
	private String content;
	
	/**
	 * 映射参数
	 */
	private ParamUnit[] paramUnits;
	
	/**
	 * 构造函数
	 */
	public TextTag(String text) {
		 this(text,null);
	}
	
	/**
	 * 构造函数
	 */
	public TextTag(String text,ParamUnit[] paramUnits){
		this.content =(text!=null)?text.trim():null;
		this.paramUnits =paramUnits;
	}
	
	/**
	 * 标签名
	 */
	public String getTagName(){
		return "";
	}

	/**
	 * SQL文本
	 */
	public String getText() {
		return this.content;
	}
	
	/**
	 * 映射参数
	 */
	public ParamUnit[] getParamUnit() {
		return this.paramUnits;
	}
	
	/**
	 * 增加子标签
	 */
	public void addChild(DynTag tag) {
		throw new UnsupportedOperationException("Operation not support");
	}
	
	/**
	 * 删除子标签
	 */
	public void removeChild(DynTag tag) {
		throw new UnsupportedOperationException("Operation not support");
	}
	
	/**
	 * 获得子属性
	 */
	public DynTag getChildren(int index){
		 throw new UnsupportedOperationException("Operation not support");
	}
	/**
	 * 获得子属性
	 */
	public DynTag[] getChildren(){
		throw new UnsupportedOperationException("Operation not support");
	}
	
	/**
	 * 获得
	 */
	public void apend(StringBuffer buf){
		buf.append(content);
	}
}

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

import org.jmin.jda.statement.DynTag;

/**
 * 动态标签
 * 
 * @author Chris Liao
 */

public final class WhenTag extends DynTag{
	
	/**
	 * 测试脚本
	 */
	private String expression;
	
	/**
	 * 构造函数
	 */
	public WhenTag(String expression){
		this.expression = expression;
	}

	/**
	 * 标签名
	 */
	public String getTagName(){
		return "if";
	}
 
  /**
	 * 测试脚本
	 */
  public String getExpression(){
  	return expression;
  }

	/**
	 * 获得
	 */
	public void apend(StringBuffer buf){
		buf.append("<").append(this.getTagName());
		buf.append(" test=").append(expression);
		buf.append(">");

  	for(int i=0,size=subList.size();i<size;i++){
  		DynTag sub =(DynTag)subList.get(i);
  		sub.apend(buf);
  	}
  	buf.append("</").append(this.getTagName()).append(">");
	}
}
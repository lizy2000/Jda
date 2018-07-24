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

public final class TrimTag extends DynTag{
	
	/**
	 * 前缀填加部分
	 */
	private String prefixSymbol="WHERE"; 
	
	/**
	 * 排除重复多余的字符
	 */
	private String prefixOverrides="AND|OR"; 
	
	/**
	 * 标签名
	 */
	public String getTagName(){
		return "trim";
	}
	
	/**
	 * 前缀填加部分
	 */
	public String getPrefixSymbol() {
		return prefixSymbol;
	}
	
	/**
	 * 前缀填加部分
	 */
	public void setPrefixSymbol(String prefixSymbol) {
		this.prefixSymbol = prefixSymbol;
	}
	
	/**
	 * 排除重复多余的字符
	 */
	public String getPrefixOverride() {
		return prefixOverrides;
	}
	
	/**
	 * 排除重复多余的字符
	 */
	public void setPrefixOvreride(String prefixOverride) {
		this.prefixOverrides = prefixOverride;
	}

	/**
	 * 输出到一个buffer中
	 */
	public void apend(StringBuffer buf){
		buf.append("<").append(this.getTagName());
		buf.append("  prefix="+prefixSymbol);
		buf.append("  prefixOverrides="+prefixSymbol);
		
		buf.append(">");
		for(int i=0,size=subList.size();i<size;i++){
     DynTag sub =(DynTag)subList.get(i);
  	 sub.apend(buf);
  	}
  	buf.append("</").append(this.getTagName()).append(">");
	}
}
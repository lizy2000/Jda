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

import org.jmin.jda.statement.DynParamUnit;
import org.jmin.jda.statement.DynTag;

/**
 * 动态标签
 * 
 * @author Chris Liao
 */

public final class IterateTag extends DynTag{
	
	/**
	 * 前缀追加符号
	 */
	private String prependSymbol;
	
	/**
	 * 属性名
	 */
	private String propetyName;
	
	/**
	 * 开始符号
	 */
	private String startSymbol =" ";
	
	/**
	 * 分割符号
	 */
	private String spaceSymbol =" ";
	
	/**
	 * 结束符号
	 */
	private String endSymbol =" ";
	
	/**
	 * SQL片段
	 */
	private String subSqlText;
	
  /**
   * 解析后的参数片断
   */
  private DynParamUnit[] dynParamUnit=null;
	
	/**
	 * 构造函数
	 */
	public IterateTag(String propetyName,String subSqlText){
		this.propetyName = propetyName;
		this.subSqlText = subSqlText;
	}
	
	/**
	 * 属性名
	 */
	public String getPropetyName() {
		return propetyName;
	}
	
	/**
	 * SQL片段
	 */
	public String getSubSqlText() {
		return subSqlText;
	}
	
	
	/**
	 * 前缀追加符号
	 */
	public String getPrependSymbol() {
		return prependSymbol;
	}
	
	/**
	 * 前缀追加符号
	 */
	public void setPrependSymbol(String prependSymbol) {
		this.prependSymbol = prependSymbol;
	}
	
	/**
	 * 开始符号
	 */
	public String getStartSymbol() {
		return startSymbol;
	}
	
	/**
	 * 开始符号
	 */
	public void setStartSymbol(String startSymbol) {
		this.startSymbol = startSymbol;
	}
	
	/**
	 * 结束符号
	 */
	public String getEndSymbol() {
		return endSymbol;
	}
	
	/**
	 * 结束符号
	 */
	public void setEndSymbol(String endSymbol) {
		this.endSymbol = endSymbol;
	}
	
	/**
	 * 分割符号
	 */
	public String getSpaceSymbol() {
		return spaceSymbol;
	}
	
	/**
	 * 分割符号
	 */
	public void setSpaceSymbol(String spaceSymbol) {
		this.spaceSymbol = spaceSymbol;
	}

	/**
	 * 标签名
	 */
	public String getTagName(){
		return "iterate";
	}
	
 /**
  * 解析后的参数片断
  */
	public DynParamUnit[] getDynParamUnit() {
		return dynParamUnit;
	}
	
	/**
   * 解析后的参数片断
   */
	public void setDynParamUnit(DynParamUnit[] dynParamUnit) {
		if(this.dynParamUnit==null)
		 this.dynParamUnit = dynParamUnit;
	}
	
	
	/**
	 * 获得子属性数量
	 */
	public int getChildrenCount(){
		throw new UnsupportedOperationException("Operation not support");
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
	 * 输出到一个buffer中
	 */
	public void apend(StringBuffer buf){
		buf.append("<"+this.getTagName());
		buf.append("  propety="+propetyName);
		buf.append("  open="+startSymbol);
		buf.append("  separator="+spaceSymbol);
		buf.append("  close="+endSymbol +" >");
		buf.append(subSqlText);
		buf.append("</" + this.getTagName() +">");
	}
}

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

public final class ChooseTag extends DynTag{
	
	/**
	 * 子节点
	 */
	private OtherwiseTag otherwiseTag;
	
	/**
	 * 标签名
	 */
	public String getTagName(){
		return "choose";
	}
	
	/**
	 * 增加子标签
	 */
	public void addWhenTag(WhenTag tag) {
		if(tag==null)
			throw new NullPointerException();
		 super.addChild(tag);
	}
	
	/**
	 * 删除子标签
	 */
	public void removeWhenTag(WhenTag tag) {
		 super.removeChild(tag);
	}
	
	/**
	 * 获得子属性数量
	 */
	public int getSubWhenTagCount(){
		return subList.size();
	}
	
	/**
	 * 获得子属性
	 */
	public WhenTag getSubWhenTag(int index){
		 return (WhenTag)super.getChildren(index);
	}
	/**
	 * 获得子属性
	 */
	public WhenTag[] getSubWhenTags(){
		return (WhenTag[])subList.toArray(new WhenTag[subList.size()]);
	}

	/**
	 * 子节点
	 */
	public OtherwiseTag getOtherwiseTag() {
		return otherwiseTag;
	}

	/**
	 * 子节点
	 */
	public void setOtherwiseTag(OtherwiseTag otherwiseTag) {
		this.otherwiseTag = otherwiseTag;
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
	 * 获得
	 */
	public void apend(StringBuffer buf){
		buf.append("<"+this.getTagName() +" >");
    for(int i=0,size=subList.size();i<size;i++){
  		DynTag sub = (DynTag)subList.get(i);
  		sub.apend(buf);
  	}
  	
		if(otherwiseTag!=null)
			otherwiseTag.apend(buf);
  	buf.append("</" + this.getTagName() +">");
	}
}
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
package org.jmin.jda.statement;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态标签
 * 
 * @author Chris
 */
public abstract class DynTag {
	
	/**
	 * 子标签列表
	 */
	protected List subList = new ArrayList();

	/**
	 * 标签名
	 */
	public abstract String getTagName();
	
	/**
	 * 增加子标签
	 */
	public void addChild(DynTag tag) {
		if(tag!=null)
	  	subList.add(tag);
	}
	
	/**
	 * 获得子属性数量
	 */
	public int getChildrenCount(){
		return subList.size();
	}
	
	/**
	 * 删除子标签
	 */
	public void removeChild(DynTag tag) {
		subList.remove(tag);
	}
	
	/**
	 * 获得子属性
	 */
	public DynTag getChildren(int index){
		return (DynTag)subList.get(index);
	}
	
	/**
	 * 获得子属性
	 */
	public DynTag[] getChildren(){
		return (DynTag[])subList.toArray(new DynTag[subList.size()]);
	}
	
	/**
	 * 获得
	 */
	public void apend(StringBuffer buf){
		buf.append("<");
		buf.append(this.getTagName());
		buf.append(">");

  	for(int i=0,size=subList.size();i<size;i++){
  		DynTag sub = (DynTag)subList.get(i);
  		sub.apend(buf);
  	}
  	
  	buf.append("</");
		buf.append(this.getTagName());
		buf.append(">");
	}
}

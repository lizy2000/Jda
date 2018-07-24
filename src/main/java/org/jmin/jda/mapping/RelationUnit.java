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
 * 关联属性
 * 
 * @author Chris Liao 
 */
public interface RelationUnit {
	
	/**
	 * 获得关联ID
	 */
	public String getSqlId();
	
	/**
	 * 获得属性名
	 */
	public String getPropertyName();

	/**
	 * 获得属性类别
	 */
	public Class getPropertyType();

	/**
	 * 设置属性类别
	 */
	public void setPropertyType(Class type);

	/**
	 * 获得参数影射单元
	 */
	public String[] getRelationColumnNames();
	
	/**
	 * 获得参数影射单元
	 */
	public void setRelationColumnNames(String[] columnNames);
	
	/**
	 * Map关联的对象的Key propertyName
	 */
	public String getMapKeyPropertyName();//只有当前关联属性为 map类型才有效
	
	/**
	 * Map关联的对象的Key propertyName
	 */
	public void setMapKeyPropertyName(String keyPropertyName);//只有当前关联属性为 map类型才有效
	
	/**
	 * Map关联的对象的Value propertyName
	 */
	public String getMapValuePropertyName();//只有当前关联属性为 map类型才有效
	
	/**
	 * Map关联的对象的Value propertyName
	 */
	public void setMapValuePropertyName(String valuePropertyName);//只有当前关联属性为 map类型才有效
	
	/**
	 * 延迟加载
	 */
	public boolean isLazyLoad();

	/**
	 * 延迟加载
	 */
	public void setLazyLoad(boolean lazy);
	
}
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
package org.jmin.jda;

import java.sql.SQLException;

/**
 * 翻页集合对象
 * 
 * @author Chris
 */
public interface JdaResultPageList{
	
	/**
	 * 总页数
	 */
	public int getTotalSize();
	
	 /**
	  * 获取当前页码
	  */
	public int geCurrentIndex();
	
	/**
	 * 获取当前页面数据
	 */
	public Object[] getCurrentPage();
	
	/**
	 * 是否为第一页
	 */
	public boolean isFirstPage();
	
	/**
	 * 是否为中间页
	 */
	public boolean isMiddlePage();
	
	/**
	 * 是否为最后一页
	 */
	public boolean isLastPage();
	
	/**
	 * 后退是可行
	 */
	public boolean isNextPageAvailable();
	
	/**
	 * 前进是否可行
	 */
	public boolean isPreviousPageAvailable();
	
	/**
	 * 翻到下一页
	 */
	public void moveNextPage()throws SQLException;
	
	/**
	 * 后退下一页
	 */
	public void movePreviousPage()throws SQLException;
	
	/**
	 * 跳转到指定页
	 */
	public void moveToPage(int pageIndex)throws SQLException;
	
}

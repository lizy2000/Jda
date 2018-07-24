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
package org.jmin.jda.impl.config.dynamicsql.tag;

import org.htmlparser.tags.CompositeTag;

/**
 * SQL标签
 * 
 * @author Chris Liao
 */
public class DynWhereTag extends CompositeTag {
	/**
	 * 标签对
	 */
	private String[] tagPairs=new String[]{"where"};
 
	/**
	 * 标签对
	 */
  public String[] getIds(){
    return (tagPairs);
  }
  /**
	 * 标签对
	 */
  public String[] getEnders(){
    return (tagPairs);
  }
}
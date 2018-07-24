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
package org.jmin.jda.impl.config.statictext;

import org.jmin.jda.impl.exception.SqlDefinitionException;
import org.jmin.jda.impl.exception.SqlDefinitionFileException;

/**
 * SQL文本辅助类
 * 
 * @author Chris Liao
 */
public class SqlTextUtil {

	private String CDATA = "cdata";
	
	private String LEFT_XML= "<";
	
	private String RIGHT_XML= ">";
	
	private String LEFT_XML1= "[";
	
	private String RIGHT_XML1= "]";

	/**
	 * 动态标签名
	 */
	private String[] dynTags = new String[] {
		"if",
		"where", 
		"choose",
		"when", 
		"otherwise", 
		"set", 
		"trim", 
		"foreach",
		"iterate"
	};
	
	/**
	 * 获得SQL动态或动态类型
	 */
	public boolean isDynamicText(String SQL) {
		boolean existTagInd = (SQL.indexOf(LEFT_XML) > 0 && SQL.indexOf(RIGHT_XML) > 0);
		if (existTagInd) {
			for (int i = 0, n = dynTags.length; i < n; i++) {
				if (SQL.indexOf(LEFT_XML + dynTags[i]) > 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	* 获得SQL动态或静态类型
	*/
	public boolean isStaticText(String SQL){
	 return (SQL.indexOf(LEFT_XML)==-1 && SQL.indexOf(RIGHT_XML)==-1);
	}
	
	/**
	 * 获取XML内容
	 */
	public String removeCdata(String sqlID,String XML)throws SqlDefinitionException {
		int pos = XML.indexOf(CDATA);
		if(pos==-1){
			return XML;
		}else{
			pos = XML.indexOf(LEFT_XML1,pos);
			if(pos==-1)
				throw new SqlDefinitionFileException(sqlID,"Error CDATA content");
			XML = XML.substring(pos+1);
			int endPos = XML.lastIndexOf(RIGHT_XML1);
			if(endPos==-1)
				throw new SqlDefinitionFileException(sqlID,"Error CDATA content");
			XML = XML.substring(0,endPos);
			
			endPos = XML.lastIndexOf(RIGHT_XML1);
			if(endPos==-1)
				throw new SqlDefinitionFileException(sqlID,"Error CDATA content");
			return XML.substring(0,endPos);
		}
	}
}

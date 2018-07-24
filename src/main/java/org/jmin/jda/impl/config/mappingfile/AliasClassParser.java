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
package org.jmin.jda.impl.config.mappingfile;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.impl.exception.SqlDefinitionFileException;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.StringUtil;

/**
 * 导入别名类型
 * 
 * @author chris liao
 */
public class AliasClassParser {
	
	/**
	 * 数据源解析
	 */
	public Map loadAliasMap(List elementList,JdaContainer container,SqlFileXMLTags tags)throws SQLException{
		Map aliasMap = new HashMap(elementList.size());
		try {
			for(int i=0,n=elementList.size();i<n;i++) {
				Element subElement = (Element)elementList.get(i);
				String aliasName =subElement.getAttributeValue(tags.ATTR_Name);
				String className =subElement.getAttributeValue(tags.ATTR_Type);
				
				if(StringUtil.isNull(aliasName))
					throw new SqlDefinitionFileException(null,"Alias name can't be null");
				if(StringUtil.isNull(className))
					throw new SqlDefinitionFileException(null,"Class name can't be null at alias[" + aliasName+"]");
				
				aliasMap.put(aliasName,ClassUtil.loadClass(className));
			}
		} catch (ClassNotFoundException e) {
			throw new SqlDefinitionFileException(null,e.getMessage(),e);
		}
 
		return aliasMap;
	}
}

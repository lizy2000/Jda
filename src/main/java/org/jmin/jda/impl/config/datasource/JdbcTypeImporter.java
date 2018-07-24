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
package org.jmin.jda.impl.config.datasource;

import java.sql.SQLException;
import java.util.List;

import org.jdom.Element;

import org.jmin.jda.JdaContainer;

/**
 * 数据源解析
 * 
 * @author Chris Liao
 */

public class JdbcTypeImporter {

	/**
	 * 数据源解析
	 */
	public void importJdbcTypes(Element element, JdaContainer container,JdbcSourceTags sourceTags)throws SQLException {
		if (element != null) {
			List typeNodeList = element.getChildren(sourceTags.ATTR_Type);
			for(int i=0,n=typeNodeList.size();i<n;i++) {
				Element subElement = (Element)typeNodeList.get(i);
				String code = subElement.getAttributeValue(sourceTags.ATTR_Code);
				String name = subElement.getAttributeValue(sourceTags.ATTR_Name);
				container.addJdbcType(name,Integer.parseInt(code));
			}
		}
	}
}
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
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.util.ClassUtil;

/**
 * 导入Handler类型
 * 
 * @author chris liao
 */
public class JdbcPersisterImporter {
	
	/**
	 * 导入Handler类型
	 */
	public void importParamPersisters(Element element, JdaContainer container,JdbcSourceTags sourceTags)throws SQLException{
	 if (element != null) {
			String javaType=null,jdbcType=null,className=null;
			List list = element.getChildren(sourceTags.ParamPersister);
			for(int i=0,n=list.size();i<n;i++) {
				Element subElement = (Element)list.get(i);
				javaType = subElement.getAttributeValue(sourceTags.ATTR_JavaType);
				jdbcType = subElement.getAttributeValue(sourceTags.ATTR_JdbcType);
				className = subElement.getAttributeValue(sourceTags.ATTR_Class);
				
				Class type = loadClass(javaType);
				JdaTypePersister handler = createTypeHandler(className);
				container.addTypePersister(type,jdbcType,handler);
			}
		}
	}

	/**
	 * 创建TypeHandler
	 */
	private JdaTypePersister createTypeHandler(String hanlderClassName)throws SQLException{
		try {
			Class JavaType = loadClass(hanlderClassName);
			Object handler = JavaType.newInstance();
			if(handler instanceof JdaTypePersister){
				return (JdaTypePersister)handler;
			}else{
				throw new SQLException("Class " + hanlderClassName  + " is not a validate type handler");
			}
		} catch (InstantiationException e) {
			throw new SQLException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new SQLException(e.getMessage());
		}
	}
	
	private Class loadClass(String className)throws SQLException{
		try {
			return ClassUtil.loadClass(className,true,JdbcPersisterImporter.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new SQLException(e.getMessage());
		}
	}
}

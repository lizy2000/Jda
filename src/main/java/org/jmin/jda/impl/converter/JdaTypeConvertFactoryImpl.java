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
package org.jmin.jda.impl.converter;

import java.util.HashMap;
import java.util.Map;

import org.jmin.jda.JdaTypeConvertException;
import org.jmin.jda.JdaTypeConverter;
import org.jmin.jda.JdaTypeConvertFactory;

/**
 * 类型转换器列表
 * 
 * Chris Liao 
 */

public class JdaTypeConvertFactoryImpl implements JdaTypeConvertFactory{
	
	/**
	 * 列表
	 */
	private Map map = new HashMap();
	
	/**
	 * 是否包含转换器
	 */
	public boolean supportsType(Class type){
	  return 	this.map.containsKey(type);
	}
	
	/**
	 * 删除转换器
	 */
	public void removeTypeConverter(Class type){
		this.map.remove(type);
	}
	
	/**
	 * 放置类的转换器
	 */
	public void putTypeConverter(Class type,JdaTypeConverter converter){
		this.map.put(type,converter);
	}
	
	/**
	 * 获取类的转换器
	 */
	public JdaTypeConverter getTypeConverter(Class type){
		 return (JdaTypeConverter)this.map.get(type);
	}
	
	/**
	 * 转换对象
	 */
	public Object convert(Object ob,Class type)throws JdaTypeConvertException{
		if(ob==null)return ob;
		if(type==null)throw new JdaTypeConvertException("Target type can't be null");
		JdaTypeConverter converter = this.getTypeConverter(type);
		if(converter == null)
			throw new JdaTypeConvertException("Not found matched type converter for class[" + type.getName() + "]");
		
		try{
		  return converter.convert(ob);
		}catch(JdaTypeConvertException e){
			throw e;
		}catch(Throwable  e){
			throw new JdaTypeConvertException("Failed to convert object["+ob+((ob!=null)?",type:"+ob.getClass().getName():"")+"]to type["+type+"]",e);
		}
	}
}

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
package org.jmin.jda.impl.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 反射辅助类
 *
 * @author Chris Liao
 * @version 1.0
 */
public class BeanUtil {
	
	/**
	 * 存放抽象的子类实现
	 */
	private static Map subClassMap = new HashMap();
	
	static{
  	subClassMap.put(Map.class,HashMap.class);
  	subClassMap.put(Set.class,HashSet.class);
  	subClassMap.put(List.class,ArrayList.class);
  	subClassMap.put(Collection.class,ArrayList.class);  	
  }
	
	/**
	 * 创建结果对象
	 */
	public static Object createInstance(String classname)throws ClassNotFoundException,InstantiationException, IllegalAccessException{
		return createInstance(ClassUtil.loadClass(classname));
	}
	
	/**
	 * 创建结果对象
	 */
	public static Object createInstance(Class clazz)throws InstantiationException, IllegalAccessException{
		if(ClassUtil.isAbstractClass(clazz)){
			Class subClass =(Class)subClassMap.get(clazz);
			if(subClass!=null)
				return subClass.newInstance();
			else
				throw new InstantiationException("Not found configed sub class for abstract class:"+clazz.getName());
		}else{
			return clazz.newInstance();
		}
	}
	
  /**
   * 通过反射调用某个方法
   */
  public static Object invokeMethod(Object bean,Method method,Object[] paramValues)throws IllegalAccessException,IllegalArgumentException,InvocationTargetException{
  	if(method!=null){
 	    method.setAccessible(true);
  	   return method.invoke(bean,paramValues);
  	}else{
  		return null;
  	}
  }
}

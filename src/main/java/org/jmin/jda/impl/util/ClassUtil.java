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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class helper
 *
 * @author Chris Liao
 * @version 1.0
 */

public class ClassUtil {
	
	/**
	 * 存放静态基本
	 */
  private static Map classMap = new HashMap();
  
	/**
	 * 存放抽象的子类实现
	 */
	private static Map subClassMap = new HashMap();
  
	/**
	 * 存放基本类型到包装的映射
	 */
	private static Map primitiveToWrapper = new HashMap();

	/**
	 * 存放包装装型到基本类的映射
	 */
	private static Map wrappereToPrimitive = new HashMap();
	
  static{
  	classMap.put("bool",boolean.class);
  	classMap.put("boolean",boolean.class);
  	classMap.put("byte",byte.class);
  	classMap.put("short",short.class);
  	classMap.put("char",char.class);
  	classMap.put("int",int.class);
  	classMap.put("long",long.class);
   	classMap.put("float",float.class);
  	classMap.put("double",double.class);
  	classMap.put("string",String.class);
  	classMap.put("map",HashMap.class);
  	classMap.put("hashmap",HashMap.class);
  	classMap.put("hashtable",Hashtable.class);
  	
  	classMap.put("list",ArrayList.class);
  	classMap.put("arraylist",ArrayList.class);
  	classMap.put("linkedlist",LinkedList.class);
  	
  	primitiveToWrapper.put(Boolean.TYPE, Boolean.class);
  	primitiveToWrapper.put(Byte.TYPE, Byte.class);
  	primitiveToWrapper.put(Short.TYPE, Short.class);
  	primitiveToWrapper.put(Character.TYPE, Character.class);
  	primitiveToWrapper.put(Integer.TYPE, Integer.class);
  	primitiveToWrapper.put(Long.TYPE, Long.class);
  	primitiveToWrapper.put(Float.TYPE, Float.class);
  	primitiveToWrapper.put(Double.TYPE, Double.class);
  	primitiveToWrapper.put(Boolean.TYPE.getName(),Boolean.class);
  	primitiveToWrapper.put(Byte.TYPE.getName(),Byte.class);
  	primitiveToWrapper.put(Short.TYPE.getName(),Short.class);
  	primitiveToWrapper.put(Character.TYPE.getName(),Character.class);
  	primitiveToWrapper.put(Integer.TYPE.getName(),Integer.class);
  	primitiveToWrapper.put(Long.TYPE.getName(),Long.class);
  	primitiveToWrapper.put(Float.TYPE.getName(),Float.class);
  	primitiveToWrapper.put(Double.TYPE.getName(),Double.class);
  	
  
  	wrappereToPrimitive.put(Boolean.class, boolean.class);
  	wrappereToPrimitive.put(Byte.class, byte.class);
  	wrappereToPrimitive.put(Short.class, short.class);
  	wrappereToPrimitive.put(Character.class, char.class);
  	wrappereToPrimitive.put(Integer.class, int.class);
  	wrappereToPrimitive.put(Long.class,long.class);
  	wrappereToPrimitive.put(Float.class, float.class);
  	wrappereToPrimitive.put(Double.class, double.class);
  	wrappereToPrimitive.put(Boolean.class.getName(),boolean.class);
  	wrappereToPrimitive.put(Byte.class.getName(), byte.class);
  	wrappereToPrimitive.put(Short.class.getName(),short.class);
  	wrappereToPrimitive.put(Character.class.getName(),char.class);
  	wrappereToPrimitive.put(Integer.class.getName(),int.class);
  	wrappereToPrimitive.put(Long.class.getName(),long.class);
  	wrappereToPrimitive.put(Float.class.getName(),float.class);
  	wrappereToPrimitive.put(Double.class.getName(),double.class);

  	subClassMap.put(Map.class,HashMap.class);
  	subClassMap.put(Set.class,HashSet.class);
  	subClassMap.put(List.class,ArrayList.class);
  	subClassMap.put(Collection.class,ArrayList.class);  	
  }
  
	/**
	 * 是否为抽象类
	 */
	public static boolean isAbstractClass(Class clazz){
		return !isPrimitiveClass(clazz) && Modifier.isAbstract(clazz.getModifiers());
	}
	
	/**
	 * 是否为公开类
	 */
	public static boolean isPublicClass(Class clazz){
		return Modifier.isPublic(clazz.getModifiers());
	}
	
	/**
	 * 是否为Final类
	 */
	public static boolean isFinalClass(Class clazz){
		return Modifier.isFinal(clazz.getModifiers());
	}

	/**
	 * 是否为基础类
	 */
	public static boolean isPrimitiveClass(Class clazz){
		return clazz.isPrimitive();
	}
	
	/**
	 * 获得包装类
	 */
	public static Class getPrimitiveWrappClass(Class primitiveClass){
		return (Class)primitiveToWrapper.get(primitiveClass);
	}
	
	/**
	 * 通过包装类获得基础类
	 */
	public static Class getPrimitiveClass(Class primitiveWrappClass){
		return (Class)wrappereToPrimitive.get(primitiveWrappClass);
	}

	/**
	 * 是否为基础类
	 */
	public static boolean isPrimitiveClass(String clazz){
		if(clazz!=null)
			return classMap.containsKey(clazz.trim().toLowerCase());
		else
			return false;
	}

	/**
	 * 是否为基础类
	 */
	public static boolean isPrimitiveWrapperClass(Class clazz){
		return  wrappereToPrimitive.containsKey(clazz);
	}
	
	/**
	 * 是否为基础类
	 */
	public static boolean isPrimitiveWrapperClass(String clazz){
		if(clazz!=null)
			return wrappereToPrimitive.containsKey(clazz.trim());
		else
			return false;
	}
	
	/**
	 * 是否无参数构造函数
	 */
	public static boolean existDefaultConstructor(Class clazz){
		boolean exist=false;
		if(!isPrimitiveClass(clazz) && !isPrimitiveWrapperClass(clazz)){
			Constructor[] constructos = clazz.getConstructors();
			for(int i=0,n=constructos.length;i<n;i++){
				if(constructos[i].getParameterTypes().length == 0){
					exist=true;
					break;
				}
			}
		}else{
			exist=true;
		}
		return exist;
	}
	
  /**
   * Load a class
   */
  public static Class loadClass(String clsName) throws ClassNotFoundException {
    return loadClass(clsName,true,ClassUtil.class.getClassLoader());
  }
  
  /**
   * Load a class
   */
  public static Class loadClass(String clsName,ClassLoader classLoader)throws ClassNotFoundException {
    return loadClass(clsName,true,classLoader);
  }
  
  /**
   * Load a class by name
   */
  public static Class loadClass(String name, boolean initialize, ClassLoader loader)throws ClassNotFoundException {
   if(classMap.containsKey(name.trim().toLowerCase())) {
			return (Class) classMap.get(name.trim().toLowerCase());
		} else {
			return Class.forName(name, initialize, loader);
		}
	}

	/**
	 * Check object to match class type
	 */
	public static boolean isAcceptableInstance(Class type, Object instance) {
		if (type.isInstance(instance)) {
			return true;
		}else if (type.isPrimitive()) {
			Class wrapClass =(Class)primitiveToWrapper.get(type);
			return wrapClass!= null && wrapClass.isInstance(instance);
		}else {
			return false;
		}
	}	
}
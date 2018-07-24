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
package org.jmin.jda.impl.property;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.Symbols;

/**
 * 属性信息辅助类
 * 
 * @author Chris
 * @version 1.0
 */

public class PropertyUtil {

	/**
	 * 通过解析get方法获得属性名
	 */
	public static String getPropertyGetMethodName(String propertyName){
	  return new StringBuffer(PropertyConstants.GET).append(propertyName.substring(0,1).toUpperCase()).append(propertyName.substring(1)).toString();
	}
	
	/**
	 * 通过解析get方法获得属性名
	 */
	public static String getPropertySetMethodName(String propertyName){
	 return new StringBuffer(PropertyConstants.SET).append(propertyName.substring(0,1).toUpperCase()).append(propertyName.substring(1)).toString();
	}
	
	/**
	 * 设置属性值
	 */
	public static Object getPropertyValue(Object bean,String propertyName)throws PropertyException{
	  return getPropertyValue(bean,propertyName,null);
	}
	
	/**
	 * 设置属性值
	 */
	public static Object getPropertyValue(Object bean,String propertyName,JdaSessionImpl session)throws PropertyException{
		if(bean==null || propertyName==null || propertyName.trim().length()==0){
		 return null;
	  }else{
			try{
				if(!Map.class.isInstance(bean))
				 propertyName =getPropertyFixedName(propertyName);
				
			 return OgnlPropertyUtil.getPropertyValue(bean,propertyName,session);
			}catch(Throwable e){
				throw new PropertyException(new StringBuffer("Failed to get value at property[").append(propertyName).append("]from bean[").append(bean).append("]").toString(),e);
			}
		}
	}
	
	/**
	 * 设置属性值
	 */
	public static void setPropertyValue(Object bean,String propertyName,Object value)throws PropertyException{
		setPropertyValue(bean,propertyName,value,null);
	}
	
	/**
	 * 设置属性值
	 */
	public static void setPropertyValue(Object bean,String propertyName,Object value,JdaSessionImpl session)throws PropertyException{
	  if(bean!=null && value != null && propertyName!=null && propertyName.trim().length() >0){
	  	try{
				if(!Map.class.isInstance(bean))
					propertyName =getPropertyFixedName(propertyName);
				
			  OgnlPropertyUtil.setPropertyValue(bean,propertyName,value,session);
	  	}catch(Throwable e){
				throw new PropertyException(new StringBuffer("Failed to set value at property[").append(propertyName).append("]to bean[").append(bean).append("]").toString(),e);
			}
		}
	}

	/**
	 * 获得调整后的属性名
	 */
	private static String getPropertyFixedName(String propertyName){
	 if(propertyName.trim().length() >=2){
		 char[]propertyCharNames  = propertyName.toCharArray();
		 if(Character.isLowerCase(propertyCharNames[0]) &&  Character.isUpperCase(propertyCharNames[1])) {
		 	 propertyName = propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
		 }
	 }
	 return propertyName;
	}
	
	/**
	 * 查找属性信息类
	 */
	public static Class getPropertyType(Class beanClass,String propertyName)throws PropertyException{
		if(beanClass==null)
			throw new PropertyException("Bean class c an't be null");
		if(propertyName==null || propertyName.trim().length()==0)
			throw new PropertyException("Property name can't be null");
		if(propertyName.indexOf(Symbols.Dot)>0)
			throw new PropertyException("A Illegal character[.] in property name");
		
	  Method propertyGetMethod =getPropertyGetMethod(beanClass,propertyName);
	  if(propertyGetMethod!=null)
	  	return propertyGetMethod.getReturnType();
	  else
	  	throw new PropertyException(new StringBuffer("Class:").append(beanClass.getName()).append(" missed get method[").append(getPropertyGetMethodName(propertyName)).append("]for Property[").append(propertyName).append("]").toString());
	}
	
	/**
	* 获取Get属性方法
	*/
	public static Method getPropertyGetMethod(Class clazz,String propertyName){
		Method getMethod=null;
		if(clazz!=null && propertyName!=null && propertyName.trim().length() >0){
			String getMethodName =getPropertyGetMethodName(propertyName);
		  try{
				getMethod=clazz.getMethod(getMethodName,new Class[0]);
			} catch(Exception e) {
			} 
	  }
	  return getMethod;
	}

	/**
	* 获取Set属性方法
	*/
	public static Method getPropertySetMethod(Class clazz,String propertyName){
		return getPropertySetMethod(clazz,propertyName,(Class)null);
	}
	
	
	/**
	* 获取Set属性方法
	*/
	public static Method getPropertySetMethod(Class clazz,String propertyName,Object propertyValue){
	 return getPropertySetMethod(clazz,propertyName,(propertyValue==null)?null:propertyValue.getClass());
	}

	/**
	* 获取Set属性方法
	*/
	public static Method getPropertySetMethod(Class clazz,String propertyName,Class propertyType){
		Method setMethod=null;
		if(clazz!=null && propertyName!=null && propertyName.trim().length() >0){
			String setMethodName =getPropertySetMethodName(propertyName);
			if(propertyType!=null){//精确定位
				try{
				setMethod = clazz.getMethod(setMethodName,new Class[]{propertyType});
				}catch(Exception e){
				}
			 }
					
			 if(setMethod == null) {//模糊定位
				try{
					BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
					PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
					for(int i = 0; i < descriptors.length; i++) {
						Method writeMethod = descriptors[i].getWriteMethod();
						if(writeMethod != null && writeMethod.getName().equals(setMethodName)) {
							Class parameterType = writeMethod.getParameterTypes()[0];
						  if(propertyType==null 
									|| (propertyType !=null && parameterType.isAssignableFrom(propertyType))
									|| (propertyType !=null && propertyType.isPrimitive()&& propertyType.equals(ClassUtil.getPrimitiveClass(parameterType)))
								  || (propertyType !=null && parameterType.isPrimitive()&& parameterType.equals(ClassUtil.getPrimitiveClass(propertyType)))){
						  	
								 setMethod = writeMethod;
								 break;
							 }
						}
					}
			 }catch(Exception e){
			 }	 
		 }
	 }
	 return setMethod;
	}	
}
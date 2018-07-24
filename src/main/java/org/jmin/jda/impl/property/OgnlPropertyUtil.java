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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.NullHandler;
import ognl.Ognl;
import ognl.OgnlRuntime;

import org.jmin.jda.impl.JdaSessionImpl;

/**
 * OGNL 辅助类，主要用来获取对象属性或验证表达式
 *  
 * @author Chris
 */

public class OgnlPropertyUtil {
	
	/**
	 * 属性操作识别
	 */
	private static ThreadLocal propertyOPLocal = new ThreadLocal();

	/**
	 * 注册默认的NUll Property的Handler
	 */
	static {
		OgnlRuntime.setNullHandler(Object.class, new PropertyNullHandlerProxy(propertyOPLocal));
	}

	/**
	 * 布尔表达式测定
	 */
	public static boolean assertBool(String expression,Object paramObj)throws Exception {
		 return assertBool(expression,paramObj,null);
	}
	
	/**
	 * 布尔表达式测定
	 */
	public static boolean assertBool(String expression,Object paramObj,JdaSessionImpl session)throws Exception {
		try{
			propertyOPLocal.set(PropertyConstants.Assert);
			return ((Boolean)Ognl.getValue(getOgnlExpression(expression,session),paramObj)).booleanValue();
		}finally{
			propertyOPLocal.remove();
		}
	}
	
	/**
	 * 获取属性值
	 */
	public static Object getPropertyValue(Object bean, String propertyName)throws Exception {
	 return getPropertyValue(bean,propertyName,null);
	}
 
	/**
	 * 获取属性值
	 */
	public static Object getPropertyValue(Object bean, String propertyName,JdaSessionImpl session)throws Exception {
		try{
			propertyOPLocal.set(PropertyConstants.GetProperty);
			return Ognl.getValue(getOgnlExpression(propertyName,session),bean);
		}finally{
			propertyOPLocal.remove();
		}
	}
	
 	/**
 	 * 设置属性值
 	 */
 	public static void setPropertyValue(Object bean, String propertyName,Object value) throws Exception {
 		 setPropertyValue(bean,propertyName,value,null);
 	}
	
	/**
	 * 设置属性值
	 */
	public static void setPropertyValue(Object bean, String propertyName,Object value,JdaSessionImpl session) throws Exception {
		try{
			propertyOPLocal.set(PropertyConstants.SetProperty);
		  Ognl.setValue(getOgnlExpression(propertyName,session),bean,value);
		}finally{
			propertyOPLocal.remove();
		}
	}
	
	/**
	 * 获取解析表达式
	 */
	private static Object getOgnlExpression(String expression,JdaSessionImpl session)throws Exception{
		if(session!=null){
			Object ognlExpression = session.getReflectExpression(expression);
			if(ognlExpression==null){
				ognlExpression = Ognl.parseExpression(expression);
				session.putReflectExpression(expression,ognlExpression);
			}
			return ognlExpression;
		}else{
			return Ognl.parseExpression(expression);
		}
	}
}


class PropertyNullHandlerProxy implements NullHandler{
	private ThreadLocal propertyOPLocal;
	
	public PropertyNullHandlerProxy(ThreadLocal propertyOPLocal){
		this.propertyOPLocal =propertyOPLocal;
	}
	
	public Object nullPropertyValue(Map context,Object target,Object property){
  	Object propetyValue = null;
  	if(PropertyConstants.SetProperty.equals(propertyOPLocal.get())){
    	try {
  		  String propertyName = property.toString();
  		  Class propertyType = getPropertyType(target.getClass(),propertyName);
  		  propetyValue = propertyType.newInstance();
  			Ognl.setValue(propertyName,target,propetyValue);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  	}
		return propetyValue;
  }
  
  public Object nullMethodResult(Map context, Object target, String methodName, Object[] args){
   return null;
  }
  
  /**
	 * 获得属性的类型
	 */
  private Class getPropertyType(Class parentClass, String propertyName)throws NoSuchFieldException {
		Class propertyType = null;
		if(Map.class.isAssignableFrom(parentClass)) {
			propertyType = parentClass;
		} else {
			Method method = PropertyUtil.getPropertySetMethod(parentClass,propertyName);
			if(method!=null)
			 propertyType = method.getParameterTypes()[0];
		}
		
		if(propertyType == null){
			throw new NoSuchFieldException("Not found field["+propertyName+"] in class["+parentClass.getName()+"]");
		}else if(isAbstractClass(propertyType)){
	  	if(Map.class.isAssignableFrom(propertyType))
	  		propertyType = HashMap.class;
	  	else if(Set.class.isAssignableFrom(propertyType))
	  		propertyType = HashSet.class;
	  	else if(List.class.isAssignableFrom(propertyType))
	  		propertyType = ArrayList.class;
	  	else if(Collection.class.isAssignableFrom(propertyType))
	  		propertyType = ArrayList.class;
		}
		return propertyType;
	}

	/**
	 * 是否为抽象类
	 */
	private boolean isAbstractClass(Class clazz){
		return Modifier.isAbstract(clazz.getModifiers());
	}
}

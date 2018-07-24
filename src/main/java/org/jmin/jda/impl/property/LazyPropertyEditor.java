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

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * 类的编辑改造器
 *
 * @author chris liao
 */

public class LazyPropertyEditor {
	
	/**
	 * 将方法改名为class$impl,然后构造出一个原方法名的方法
	 */    
	public Class createSubClass(Class beanClass,LazyProperty[]propertyties)throws NotFoundException,IOException, CannotCompileException,Exception {
		ClassPool classPool = ClassPool.getDefault();
		CtClass parentClass = classPool.get(beanClass.getName());
		CtClass subClass = classPool.makeClass(parentClass.getName()+ "$"+genRandomNum(5),parentClass);
		
		CtConstructor[]superConstructors= parentClass.getDeclaredConstructors();
	  addOverrideConstructors(classPool,subClass,superConstructors);
	  
	  for(int i=0,n=(propertyties==null)?0:propertyties.length;i<n;i++){
			createLazySetRequestMethod(classPool,subClass,propertyties[i].getPropertyName());
			rebuildLazyPropertyMethod(classPool,parentClass,subClass,propertyties[i]);
		}
		
 		return subClass.toClass(beanClass.getClassLoader(),null);
	}
	
	/**
	 * 创建Setxxxx$SqlRequest(SQLRequest request);
	 */
	private void createLazySetRequestMethod(ClassPool pool,CtClass subClass,String propertyName)throws NotFoundException, CannotCompileException {
		String lazyPropertyName=propertyName +"$SqlRequest";
		String lazyPropertyCalled=propertyName+"$ISCalled";
		String lazyPropertySetMethod=PropertyUtil.getPropertySetMethodName(lazyPropertyName);
		
		CtField reqField = new CtField(pool.get("org.jmin.jda.impl.execution.SqlRequest"),lazyPropertyName,subClass);
		reqField.setModifiers(Modifier.PRIVATE);
		subClass.addField(reqField);
		
		CtField calledField = new CtField(pool.get("boolean"),lazyPropertyCalled,subClass);
		calledField.setModifiers(Modifier.PRIVATE);
		subClass.addField(calledField);
		
		CtMethod setMethod = new CtMethod(CtClass.voidType,lazyPropertySetMethod, new CtClass[]{pool.get("org.jmin.jda.impl.execution.SqlRequest")},subClass);
		setMethod.setModifiers(Modifier.PUBLIC);
		setMethod.setBody("this."+lazyPropertyName+" =$1;");
		subClass.addMethod(setMethod);
	}
	
	/**
	 * 将方法改名为methodName$impl,然后构造出一个原方法名的方法
	 */    
	private void rebuildLazyPropertyMethod(ClassPool classPool,CtClass parentClass,CtClass subClass,LazyProperty property)throws NotFoundException, CannotCompileException {
		String propertyName = property.getPropertyName();
		Class  propertyType = property.getPropertyType();
		
		String lazyPropertyName=propertyName +"$SqlRequest";
		String lazyPropertyCalled=propertyName+"$ISCalled";		
		String superPropertyGetMethod =PropertyUtil.getPropertyGetMethodName(propertyName);
		String superPropertySetMethod =PropertyUtil.getPropertySetMethodName(propertyName);
		StringBuffer methodBody = new StringBuffer();
	
		methodBody.append("{\n");
		methodBody.append("  if(!this.");methodBody.append(lazyPropertyCalled); methodBody.append(" && this."); methodBody.append(lazyPropertyName);methodBody.append("!=null){\n");
		methodBody.append("    try{\n");
		methodBody.append("     Object result=org.jmin.jda.impl.execution.select.ObjectRelateFinder.getRelaionValue(this."+lazyPropertyName+",true);\n");
		methodBody.append("     this."+lazyPropertyCalled+ "=true;\n");
		methodBody.append("     super."+superPropertySetMethod+"("+getResultString(classPool.get(propertyType.getName()))+");\n");
		methodBody.append("     return "); methodBody.append(getResultString(classPool.get(propertyType.getName()))); methodBody.append(";\n");
		 
		methodBody.append("    }catch(Throwable e){\n");
		methodBody.append("     throw new org.jmin.jda.impl.exception.PropertyLazyLoadException(e);\n");
		methodBody.append("    }\n");
		methodBody.append("  }else{\n");
		methodBody.append("    return super.");methodBody.append(superPropertyGetMethod);methodBody.append("();\n");
		methodBody.append("  }\n");
		methodBody.append("}\n");
	
		CtMethod overrideMethod= new CtMethod(classPool.get(propertyType.getName()),PropertyUtil.getPropertyGetMethodName(propertyName),new CtClass[]{},subClass);
		overrideMethod.setBody(methodBody.toString());
		subClass.addMethod(overrideMethod);
		
}
  /**
   * add override constructors in sub class
   */
  private void addOverrideConstructors(ClassPool pool, CtClass subClass,CtConstructor[] superConstructors) throws Exception {
    for(int i = 0,n=superConstructors.length; i <n; i++) {
      if (Modifier.isPublic(superConstructors[i].getModifiers())) {
        addOverrideConstructor(pool, subClass,superConstructors[i]);
      }
    }
  }
	
  /**
   * 拷贝构造方法
   */
  private void addOverrideConstructor(ClassPool pool,CtClass targetClass,CtConstructor superConstructor) throws Exception {
    /**
     * New a simliar Constructor with super
     */
    CtConstructor subClassConstructor = new CtConstructor(superConstructor
        .getParameterTypes(), targetClass);
    subClassConstructor.setExceptionTypes(superConstructor
        .getExceptionTypes());
    subClassConstructor.setModifiers(superConstructor.getModifiers());

    /**
     * Construct constructor body
     */
    StringBuffer body = new StringBuffer("super(");
    if (superConstructor.getParameterTypes().length == 0) {
    	body.append(");");
    } else {
      int len = superConstructor.getParameterTypes().length;
      for(int i = 1; i <= len; i++) {
      	body.append("$").append(i);
        if (i < len)
        	body.append(",");
        else
        	body.append(");");
      }
    }
    subClassConstructor.setBody(body.toString());
    targetClass.addConstructor(subClassConstructor);
  }
	
	/**
	 * 返回结果
	 */
	private String getResultString(CtClass resultType){
		if(CtClass.booleanType.equals(resultType)){
		  return  "((Boolean)result).getBoolean()";
		}else if(CtClass.byteType.equals(resultType)){
			return  "((Byte)result).byteValue()";
		}else if(CtClass.shortType.equals(resultType)){
			return  "((Short)result).shortValue()";
		}else if(CtClass.intType.equals(resultType)){
			return  "((Integer)result).intValue()";
		}else if(CtClass.longType.equals(resultType)){
			return  "((Long)result).longValue()";
		}else if(CtClass.floatType.equals(resultType)){
			return  "((Float)result).floatValue()";
		}else if(CtClass.doubleType.equals(resultType)){
			return  "((Double)result).doubleValue()";
		}else if(CtClass.charType.equals(resultType)){
			return  "((Character)result).charValue()";
		}else{
			return "("+ resultType.getName()+")result";
		}
	}

  /**  
	 * 生成随即密码  
	 */
	private String genRandomNum(int len) { 
    char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',  'l', 'm', 
    							 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',  'y', 'z', 
    							 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',  'L', 'M', 
    							 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',  'Y', 'Z', 
    							 '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    
    StringBuffer pwd = new StringBuffer();
    for(int i=0; i<len;i++) {
      int pos =(int)(Math.random()*str.length);
      pwd.append(str[pos]);
    }
		return pwd.toString();
	}   
}

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
package org.jmin.jda.impl.persister.clob;

import java.io.StringReader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jmin.jda.JdaException;
import org.jmin.jda.JdaTypeConvertFactory;
import org.jmin.jda.impl.persister.ObjectHandler;

/**
 * Clob 类型
 * 
 * @author Chris Liao
 */

public class ClobHandler extends ObjectHandler {
	
	
	/**
	 * 获得持久化类型
	 */
	public Class getPersisterType(){
		return Clob.class;
	}
	
	 /**
   * 设置参数
   */
  public void set(PreparedStatement ps,int index,Object value,int typeCode,JdaTypeConvertFactory convertFactory)throws SQLException{
  	try{
	  	String clobText =(String)convertFactory.convert(value,this.getPersisterType());
	  	if(clobText==null)
	  		this.setNullParamValue(ps,index,typeCode);
	  	else{
	      StringReader reader = new StringReader(clobText);
	      ps.setCharacterStream(index,reader,clobText.length());
	  	}
   } catch (Throwable e) {
			throw new JdaException(null,e);
	 }	
  }
}
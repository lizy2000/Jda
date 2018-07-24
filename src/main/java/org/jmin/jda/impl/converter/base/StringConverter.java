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
package org.jmin.jda.impl.converter.base;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.util.Calendar;
import java.util.Date;

import org.jmin.jda.JdaTypeConvertException;
import org.jmin.jda.impl.converter.JdaTypeBaseConverter;

/**
 * 转换为字符串类型
 * 
 * @author chris
 */
public class StringConverter extends JdaTypeBaseConverter{

	/**
	 * 转换为目标类型
	 */
	public Object convert(Object value)throws JdaTypeConvertException{
		if(value ==null){
			return null;
		}else if(value instanceof String){
		 return value;
		}else if(value instanceof Character){
			return ((Character)value).toString();
		}else if(value instanceof Number){
			return ((Number)value).toString();
		}else if(value instanceof Date){
			return ((Date)value).toString();
		}else if(value instanceof Calendar){
			return ((Calendar)value).toString();
		}else if(value instanceof Boolean || Boolean.TYPE.isInstance(value)){
			return value.toString();
		}else if(value instanceof Clob){
		 try{
				String reString = "";  
				Clob clob =(Clob)value;
				Reader is = clob.getCharacterStream();// 得到流 
				BufferedReader br = new BufferedReader(is); 
				String s = br.readLine(); 
				StringBuffer sb = new StringBuffer(); 
				while (s != null) {// 执行循环将字符串全部取出付值给 StringBuffer由StringBuffer转成STRING 
				sb.append(s); 
				s = br.readLine(); 
				} 
				reString = sb.toString(); 
				return reString; 
			} catch (Exception e) {
				throw new JdaTypeConvertException("Fail to get text from clob:"+e.getMessage(),e);
			}
		}else{
			return value.toString();
		} 
	}
}

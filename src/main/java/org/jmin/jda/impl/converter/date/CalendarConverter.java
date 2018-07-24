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
package org.jmin.jda.impl.converter.date;

import java.util.Calendar;
import java.util.Date;

import org.jmin.jda.JdaTypeConvertException;

/**
 * 日历转换
 * 
 * @author chris
 */

public class CalendarConverter extends UtilDateConverter{

	/**
	 * 转换为目标类型
	 */
	public Object convert(Object value)throws JdaTypeConvertException{
		if(value ==null){
			return null;
		}else if(value instanceof Calendar){
			return value;
		}else if(value instanceof Date){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date)value);
			return calendar;
		}else if(value instanceof Number){
			Number numValue =(Number)value;
			long time= numValue.longValue();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(time));
			return calendar;
		}else if(value instanceof String){
      Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.stringToDate((String)value));
			return calendar;
		}else{
			throw new JdaTypeConvertException("Doesn't support object conversion from type: "+ value.getClass().getName() + " to type: Calendar.class");
		}
	}
}

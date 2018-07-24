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

import java.util.ArrayList;
import java.util.List;

/**
 * 字符川辅助类
 * 
 * @author Chris
 */
public class StringUtil {
	/**
	 * 判断字符是否为空
	 */
	public static boolean isNull(String value) {
		return (value == null || value.trim().length()==0);
	}
	
	/**
	 * 将字符串分割成多个子串
	 */
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
	public static String[] split(String source,String sepString) {
	  if(isNull(source)) 
			return EMPTY_STRING_ARRAY;
		if(sepString == null || sepString.length() ==0) 
			sepString =Symbols.Space;
		
		int startPos =0,indexPos=0;
		int sourceLen = source.length();
		int sepStringLen = sepString.length();
		List list = new ArrayList(sourceLen/sepStringLen);
	
		while(startPos < sourceLen){
			indexPos = source.indexOf(sepString,startPos);
			if(indexPos==0){//从头开始的字符，只递增开始位置
				startPos =sepStringLen;
			}else if(indexPos >0){//中途截取
	    	list.add(source.substring(startPos,indexPos));
	    	startPos = indexPos+sepStringLen;
	    }else{//最后一次截取
	    	list.add(source.substring(startPos));
	    	startPos = sourceLen;
	    }
		}
		
		return (String[])list.toArray(new String[list.size()]);
	}
	
	/**
	 * 字符串替换
	 */
  public static String replace(String text, String oldStr,String newStr) {
    if(isNull(text) || isNull(oldStr) || newStr==null)
      return text;  
    
		int startPos = 0, indexPos = 0;
		int sourceLen = text.length();
		int oldStringLen = oldStr.length();
		StringBuffer buf = new StringBuffer(text.length());
		while (startPos < sourceLen) {
			indexPos = text.indexOf(oldStr, startPos);
			if (indexPos == 0) {// 从头开始的字符，只递增开始位置
				buf.append(newStr);
				startPos = oldStringLen;
			} else if (indexPos > 0) {
				buf.append(text.substring(startPos, indexPos));
				buf.append(newStr);
				startPos = indexPos + oldStringLen;
			} else {//最后一次截取
				buf.append(text.substring(startPos));
				startPos = sourceLen;
			}
		}
		return buf.toString(); 
  }
  
	/**
	 * 统计某个子串出现的次数
	 */
	public static int getWordCount(String src,String sub) {
	  if(isNull(src))return 0;
		int count = 0,index = 0;
		int subLen = sub.length();
		while(true) {
			index = src.indexOf(sub,index);
			if(index >= 0) {
				count++;
				index = index + subLen;
			} else {
				break;
			}
		}
		return count;
	}
}



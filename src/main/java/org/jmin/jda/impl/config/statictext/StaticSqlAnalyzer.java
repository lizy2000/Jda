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
package org.jmin.jda.impl.config.statictext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaTypePersister;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.impl.util.ClassUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.impl.util.Symbols;
import org.jmin.jda.mapping.ParamMap;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.mapping.ParamValueMode;

/**
 * SQL语句分析器
 *
 * @author Chris
 * @version 1.0
 * 
 * //#propertyName:columnTypeName:PropertyType:ParamPersister:ParamValueMode#
 */
public class StaticSqlAnalyzer {
	
	/**
	 * 分析参数的位置信息
	 */
	public ParamResult analyzeStaticSQL(String sqlId,String SQL,Class paramClass,JdaContainer container)throws SQLException{
		ParamResult result = new ParamResult();
		if(SQL.indexOf(ParamSymbol.Symbol1.getStartSymbol())==-1  
			&& SQL.indexOf(ParamSymbol.Symbol2.getStartSymbol())==-1 
			&& SQL.indexOf(ParamSymbol.Symbol3.getStartSymbol())==-1
			&& SQL.indexOf(ParamSymbol.Symbol4.getStartSymbol())==-1
			&& SQL.indexOf(ParamSymbol.Symbol5.getStartSymbol())==-1){//无参数
			
			result.setExeSQL(SQL);
			result.setParamMap(null);
			return result;
		}else{
			int startPos = 0;
			int SQLLen = SQL.length();
			ParamPosition currentPosition = null;
			List paramUnitList = new ArrayList();
			StringBuffer buf = new StringBuffer(SQLLen);
			while(startPos < SQLLen){
				currentPosition = this.getMinPosition(SQL,startPos,ParamSymbol.Symbols);
				if(currentPosition != null){	
				  if(currentPosition.getEndIndex() == -1){
				  	throw new ParamMapException(sqlId,"one parameter miss end symbol:'"+currentPosition.getParamSymbol().getEndSymbol() +"'");
				  }else{
				  	String blockText = SQL.substring(
				  			currentPosition.getStartIndex() + currentPosition.getParamSymbol().getStartSymbol().length(), currentPosition.getEndIndex());
				  	
				  	//#propertyName:columnTypeName:PropertyType:ParamPersister:ParamValueMode#
				  	String[]items = StringUtil.split(blockText,Symbols.Colon);
				  	if(StringUtil.isNull(items[0]))
				  		throw new ParamMapException(sqlId,"Parameter name can't be null ");
				  	ParamUnit paramUnit=container.createParamUnit(items[0]);
				  	if(items.length >=2 && !StringUtil.isNull(items[1]))
				  		paramUnit.setParamColumnTypeName(items[1]);
				  	
				  	if(items.length >=3 && !StringUtil.isNull(items[2]))
							try {
								paramUnit.setPropertyType(ClassUtil.loadClass(items[2]));
							} catch (Throwable e) {
								throw new ParamMapException(sqlId,"Can't loand parameter type: "+ items[2],e);
							}
							
				  	if(items.length >=4 && !StringUtil.isNull(items[3]))
							try {
								paramUnit.setJdbcTypePersister((JdaTypePersister)ClassUtil.loadClass(items[3]).newInstance());
							} catch (Throwable e) {
								throw new ParamMapException(sqlId,"Can't create parameter persister instance for class: "+ items[3],e);
							}
							
				  	if(items.length >=5 && !StringUtil.isNull(items[5]))
				  		paramUnit.setParamValueMode(ParamValueMode.getParamMode(items[4]));
				  	
				  	paramUnitList.add(paramUnit);
				  	buf.append(SQL.substring(startPos,currentPosition.getStartIndex()));
				  	buf.append(Symbols.Question);
				  	startPos = currentPosition.getEndIndex()+ currentPosition.getParamSymbol().getEndSymbol().length();
				  }
				}else{
					buf.append(SQL.substring(startPos));
					break;
				}
		  }
			
			result.setExeSQL(buf.toString());
			if(paramUnitList.size() == 1){
				if(paramClass== null) 
					paramClass = Object.class;
			}
		
			if(paramUnitList.size() >0){
				ParamMap map = container.createParamMap(paramClass,(ParamUnit[])paramUnitList.toArray(new ParamUnit[paramUnitList.size()]));
				result.setParamMap(map);
			}
			return result;
		}
 	}
  
	/**
	 * 搜索字符串
	 */
  private ParamPosition getMinPosition(String value,int beginIndex,ParamSymbol[] symbols){
  	ParamSymbol paramSymbol = null;
		int minStartPos = -1, minEndPos = -1;
		int newStartPos = -1, newEndPos = -1;
		for (int i = 0, n = symbols.length; i < n; i++) {
			newStartPos = value.indexOf(symbols[i].getStartSymbol(), beginIndex);
			if (newStartPos > 0) {
				if (newStartPos < minStartPos || minStartPos == -1) {
					newEndPos = value.indexOf(symbols[i].getEndSymbol(), newStartPos+ symbols[i].getStartSymbol().length());
					if (newEndPos > newStartPos) {
						paramSymbol = symbols[i];
						minStartPos = newStartPos;
						minEndPos = newEndPos;
					}
				}
			}
		}
		return (minStartPos == -1) ? null : new ParamPosition(minStartPos,minEndPos, paramSymbol);
	}
}

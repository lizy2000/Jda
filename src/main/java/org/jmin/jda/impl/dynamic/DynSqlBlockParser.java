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
package org.jmin.jda.impl.dynamic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jmin.jda.impl.config.statictext.ParamPosition;
import org.jmin.jda.impl.config.statictext.ParamSymbol;
import org.jmin.jda.impl.exception.ParamMapException;
import org.jmin.jda.statement.DynParamUnit;

/**
 * 动态SQL块解析
 * 
 * @author Chris
 *
 */
public class DynSqlBlockParser {

	/**
	 * 分析动态SQL块的参数信息
	 */
	public DynParamUnit[] analyzeDynParamSQL(String SQL)throws SQLException{
		int startPos = 0;
		List dynParamUnitList=new ArrayList();
		ParamPosition currentPosition = null;
		
		if(SQL.indexOf(ParamSymbol.Symbol1.getStartSymbol())==-1  
			&& SQL.indexOf(ParamSymbol.Symbol2.getStartSymbol())==-1 
			&& SQL.indexOf(ParamSymbol.Symbol3.getStartSymbol())==-1
			&& SQL.indexOf(ParamSymbol.Symbol4.getStartSymbol())==-1
			&& SQL.indexOf(ParamSymbol.Symbol5.getStartSymbol())==-1){//无参数
			return new DynParamUnit[0];
		}else{
			while(startPos < SQL.length()){
				currentPosition = getMinPosition(SQL,startPos,ParamSymbol.Symbols);
				if(currentPosition != null){	
				  if(currentPosition.getEndIndex() == -1){
				  	throw new ParamMapException("one parameter miss end symbol:'"+currentPosition.getParamSymbol().getEndSymbol() +"'");
				  }else{		                         
		  	    String blockText    = SQL.substring(currentPosition.getStartIndex(),currentPosition.getEndIndex() + currentPosition.getParamSymbol().getEndSymbol().length());
		  	    String blockContent = SQL.substring(currentPosition.getStartIndex()+currentPosition.getParamSymbol().getStartSymbol().length(),currentPosition.getEndIndex());
				  	startPos = currentPosition.getEndIndex() + currentPosition.getParamSymbol().getEndSymbol().length();   
				  	
				  	DynParamUnit unit = new DynParamUnit(blockText,blockContent);
				  	if(!dynParamUnitList.contains(unit))
				  	  dynParamUnitList.add(unit);
				  }
				}else{
					break;
				}
		  }
			
			return (DynParamUnit[])dynParamUnitList.toArray(new DynParamUnit[dynParamUnitList.size()]);
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
  
 	public static void main(String[] args)throws SQLException{
//  	String sql ="select ln.LOAN_NO,ln.PAYM_BANK_ACC_ID as ACC_ID,sum(fee.FEE_AMT) as EXP_AMT"
//        +"from LOAN_HOLD_FEE fee,LOAN ln"
//        +"where fee.LOAN_NO = ln.LOAN_NO"
//        +"<if test=\"cooperId != null\">"
//        +" and ln.COOPER_ID = ${cooperId}"
//        +"</if>"
//        +"<if test=\"bankId != null\">"
//        +" and ln.PAYM_BANK_ID = ${bankId}"
//        +"</if>"
//        +"<if test=\"effectDate != null\">"
//        +" and fee.FEE_EFFECT_DT < ${effectDate}"
//        +"</if>"
//        +"and fee.FEE_CHRG_WAY='F'"
//        +"and fee.SUSP_IND='N'"
//        +"and fee.FEE_AMT > 0"
//        +"group by ln.LOAN_NO,ln.PAYM_BANK_ACC_ID"
//        +"order by ln.LOAN_NO";		
// 		DynSqlBlockParser parser = new DynSqlBlockParser();
//  	DynParamUnit[] UNIT = parser.analyzeDynParamSQL(sql);
//  	for(int i=0;i<UNIT.length;i++){
//  		System.out.println(UNIT[i] + " " + UNIT[i].getBlockSQL());
//  	}
  }
}

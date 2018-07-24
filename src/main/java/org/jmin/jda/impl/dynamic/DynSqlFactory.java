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

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.exception.SqlExecutionException;
import org.jmin.jda.impl.property.OgnlPropertyUtil;
import org.jmin.jda.impl.property.PropertyException;
import org.jmin.jda.impl.property.PropertyUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.ParamUnit;
import org.jmin.jda.statement.DynParamUnit;
import org.jmin.jda.statement.DynTag;
import org.jmin.jda.statement.tag.ChooseTag;
import org.jmin.jda.statement.tag.ForeachTag;
import org.jmin.jda.statement.tag.IfTag;
import org.jmin.jda.statement.tag.IterateTag;
import org.jmin.jda.statement.tag.OtherwiseTag;
import org.jmin.jda.statement.tag.SetTag;
import org.jmin.jda.statement.tag.TextTag;
import org.jmin.jda.statement.tag.TrimTag;
import org.jmin.jda.statement.tag.WhenTag;
import org.jmin.jda.statement.tag.WhereTag;
import org.jmin.log.LogPrinter;

/**
 * 动态SQL合成工厂
 * 
 * @author Chris liao
 */

public class DynSqlFactory {
	private final String Blank             = "";
	private final String Space             = " ";
	private final String Space_Where       =" where";
	private final String Where             = "where";
	private final String Where_Space       = "where ";
	private final String Space_Where_Space = " where ";
	private final String Space_And         = " and";
	//private final String And               = "and";
	private final String And_Space         = "and ";
	private final String And_Left_Bracket  = "and(";
	
	private final String Space_Or          = " or";
	//private final String Or                = "or";
	private final String Or_Space          = "or ";
	private final String Or_Left_Bracket   = "or(";
	
	private final String Order_Space       = "order ";
	private final String Group_Space       = "group ";
	private final String Having_Space      = "having ";
	private final String Space_Set_Space   = " set ";
	private final String Item              = "item";
	private final String Item_Dot          = "item.";
	
	private final String Comma             = ",";
	private final String Left_Bracket      ="(";
	private final String Right_Bracket     =")";
	
	/**
	 * Logger
	 */
	private LogPrinter logger = LogPrinter.getLogPrinter(DynSqlFactory.class);
	
	/**
	 * 分析动态SQL
	 */
  public DynSqlResult crate(Object sqlId,DynTag[]tags,Object paramObj,JdaSessionImpl session)throws SQLException{
  	DynSqlResult result = new DynSqlResult(sqlId,paramObj);
  	StringBuffer sqlBuf = new StringBuffer();
  	List paramUnitList = new ArrayList();
  	for(int i=0,n=tags.length;i<n;i++){
  		appendDynTag(sqlId,tags[i],paramObj,sqlBuf,paramUnitList,session);
  	}
  	
    String sql=sqlBuf.toString();
    sql=removeSuffix(sql,Space_Where);
    sql=removeSuffix(sql,Space_And);
    sql=removeSuffix(sql,Space_Or);
    sql=removeSuffix(sql,Comma);
    sql=removeSuffix(sql,Left_Bracket);
  	result.setSqlText(sql);
  	result.setParamUnits((ParamUnit[])paramUnitList.toArray(new ParamUnit[paramUnitList.size()]));
  	
  	logger.debug(sqlId,"Prepared a dynamic SQL: " + result.getSqlText());
  	return result;
  }
  
  /**
   * 清理末尾无效字符
   */
  private String removeSuffix(String sql,String suffix){
	sql= sql.trim();
  	String sqlLower=sql.toLowerCase();
  	if(sqlLower.endsWith(suffix)){
  		sql=sql.substring(0,sqlLower.lastIndexOf(suffix));
  	}
   return sql;
  }
  
  /**
   * 清理前端无效字符
   */
  private String removePrefix(String sql,String prefix){
	sql= sql.trim();
  	String sqlLower=sql.toLowerCase();
  	if(sqlLower.startsWith(prefix)){
  		sql = sql.substring(prefix.length());
  	}
   return sql;
  }
  
  /**
   * 追加SQL
   */
  private void appendDynTag(Object sqlId,DynTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException {
  	if(tag instanceof TextTag){
  		addTextTag(sqlId,(TextTag)tag,paramObj,sqlBuf,paramUnitList);
  	}else if(tag instanceof IfTag){
  		addIfTag(sqlId,(IfTag)tag,paramObj,sqlBuf,paramUnitList,session);
   	}else if(tag instanceof SetTag){
   		addSetTag(sqlId,(SetTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}else if(tag instanceof TrimTag){
  		addTrimTag(sqlId,(TrimTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}else if(tag instanceof WhereTag){
  		addWhereTag(sqlId,(WhereTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}else if(tag instanceof ChooseTag){
  		addChooseTag(sqlId,(ChooseTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}else if(tag instanceof WhenTag){
  		addWhenTag(sqlId,(WhenTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}else if(tag instanceof OtherwiseTag){
  		addOtherwiseTag(sqlId,(OtherwiseTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}else if(tag instanceof ForeachTag){
  		addForeachTag(sqlId,(ForeachTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}else if(tag instanceof IterateTag){
  		addIterateTag(sqlId,(IterateTag)tag,paramObj,sqlBuf,paramUnitList,session);
  	}
  }
  
  /**
   * Text
   */
  private void addTextTag(Object sqlId,TextTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList)throws SQLException{
  	String text = tag.getText();
  	if(!StringUtil.isNull(text)) {
  		appendSQL(sqlBuf,text);
			ParamUnit[] paramUnits = tag.getParamUnit();
			if (paramUnits != null) {
				for(int i=0,n= paramUnits.length; i<n; i++)
					paramUnitList.add(paramUnits[i]);
			}
		}
  }
  
  /**
   * 追加SQL
   */
  private void appendSQL(StringBuffer sqlBuf,String text)throws SQLException{
  	if(!StringUtil.isNull(text)) {
  		sqlBuf.append(Space);
  		text = text.trim();
			String sql = sqlBuf.toString().trim().toLowerCase();
			if(sql.endsWith(Where)){
				
//				if(text.toLowerCase().startsWith(Where_Space))
//				  text=removePrefix(text,Where);
//				
//				if(text.toLowerCase().startsWith(And_Space))
//				  text=removePrefix(text,And);
//				if(text.toLowerCase().startsWith(And_Left_Bracket))
//					text=removePrefix(text,And);
//		
//				if(text.toLowerCase().startsWith(Or_Space))
//				  text=removePrefix(text,Or);
//				if(text.toLowerCase().startsWith(Or_Left_Bracket))
//					text=removePrefix(text,Or);
				
				
				text=removePrefix(text,Where_Space);
				text=removePrefix(text,And_Space);
				text=removePrefix(text,And_Left_Bracket);
				text=removePrefix(text,Or_Space);
				text=removePrefix(text,Or_Left_Bracket);
				
				text=removePrefix(text,Comma);
				text=removePrefix(text,Right_Bracket);
				sqlBuf.append(Space);
			}
			
			//删除where
			if(sql.endsWith(Space_Where) && (text.toLowerCase().startsWith(Order_Space) 
					 || text.toLowerCase().startsWith(Group_Space) 
					 || text.toLowerCase().startsWith(Having_Space))){
				
				String origSQL =sqlBuf.toString();
				origSQL=removeSuffix(origSQL,Where);
				sqlBuf = new StringBuffer();
				sqlBuf.append(origSQL);
			}
			sqlBuf.append(text);
  	}
  }
  
  /**
   * If
   */
  private void addIfTag(Object sqlId,IfTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try {
		  if(OgnlPropertyUtil.assertBool(tag.getExpression(),paramObj,session)){
			DynTag[]childRen = tag.getChildren();
			for(int i=0,n=childRen.length;i<n;i++)
			appendDynTag(sqlId,childRen[i],paramObj,sqlBuf,paramUnitList,session);
		  }
  	}catch(SQLException e){
  		throw e;
  	} catch (Throwable e) {
			throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
		}
  }
 
  /**
   * Set
   */
  private void addSetTag(Object sqlId,SetTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try {
  		 sqlBuf.append(Space_Set_Space);
  		 StringBuffer childBuff = new StringBuffer();
  		 DynTag[]childRen = tag.getChildren();
		  for(int i=0,n=childRen.length;i<n;i++)
		   appendDynTag(sqlId,childRen[i],paramObj,childBuff,paramUnitList,session);
		  String childSQL =childBuff.toString();
		  childSQL=removeSuffix(childSQL,Comma);
		  childSQL=removePrefix(childSQL,Comma);
		  sqlBuf.append(childSQL);
  	}catch(SQLException e){
  	   throw e;
  	} catch (Throwable e) {
	   throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
	}
  }
  
  /**
   * Trim
   */
  private void addTrimTag(Object sqlId,TrimTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try {
		DynTag[]childRen = tag.getChildren();
		sqlBuf.append(Space).append(tag.getPrefixSymbol());
		
		for(int i=0,n=childRen.length;i<n;i++)
		appendDynTag(sqlId,childRen[i],paramObj,sqlBuf,paramUnitList,session);
  	}catch(SQLException e){
  		throw e;
  	} catch (Throwable e) {
  		throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
  	}
  }
  
  /**
   * Where
   */
  private void addWhereTag(Object sqlId,WhereTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
		try {
			StringBuffer childbuff = new StringBuffer();
			DynTag[] childRen = tag.getChildren();
			for (int i = 0, n = childRen.length; i < n; i++)
				appendDynTag(sqlId, childRen[i], paramObj, childbuff, paramUnitList, session);

			String child = childbuff.toString();
			child = child.trim().toLowerCase();
			if (child.length() > 0) {

//				if (child.toLowerCase().startsWith(And_Space))
//					child = removePrefix(child, And);
//				if (child.toLowerCase().startsWith(And_Left_Bracket))
//					child = removePrefix(child, And);
//				if (child.toLowerCase().startsWith(Or_Space))
//					child = removePrefix(child, Or);
//				if (child.toLowerCase().startsWith(Or_Left_Bracket))
//					child = removePrefix(child, Or);
				
				child = removePrefix(child, And_Space);
				child = removePrefix(child, And_Left_Bracket);
				child = removePrefix(child, Or_Space);
				child = removePrefix(child, Or_Left_Bracket);
				
				child = removePrefix(child, Comma);
				child = removePrefix(child, Right_Bracket);
			}

			if (child.length() > 0) {
				sqlBuf.append(Space_Where_Space);
				sqlBuf.append(child);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new SqlExecutionException(sqlId, "Failed to execute dynamic sql", e);
		}
  }
  
  /**
   * Choose
   */
  private void addChooseTag(Object sqlId,ChooseTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try {
  		boolean appendWhenTag=false;
  		WhenTag[] whenTags = tag.getSubWhenTags();
  		OtherwiseTag otherTag = tag.getOtherwiseTag();
  		for(int i=0,n=whenTags.length;i<n;i++){
  			if(OgnlPropertyUtil.assertBool(whenTags[i].getExpression(),paramObj,session)){
  				appendDynTag(sqlId,whenTags[i],paramObj,sqlBuf,paramUnitList,session);
  				appendWhenTag =true;
  				break;
  			}
  		}
  		
  		if(!appendWhenTag){//没有追加到when Tag
  			appendDynTag(sqlId,otherTag,paramObj,sqlBuf,paramUnitList,session);
  		}
  	}catch(SQLException e){
  		throw e;
  	} catch(Throwable e) {
  		throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
  	}
  }
  
  /**
   * When
   */
  private void addWhenTag(Object sqlId,WhenTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try{
		if(OgnlPropertyUtil.assertBool(tag.getExpression(),paramObj,session)){
			DynTag[]childRen = tag.getChildren();
			for(int i=0,n=childRen.length;i<n;i++)
			appendDynTag(sqlId,childRen[i],paramObj,sqlBuf,paramUnitList,session);
		}
  	}catch(SQLException e){
  		throw e;
  	} catch (Throwable e) {
			throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
		}
  }
  
  /**
   * Otherwise
   */
  private void addOtherwiseTag(Object sqlId,OtherwiseTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try {
		 DynTag[]childRen = tag.getChildren();
		 for(int i=0,n=childRen.length;i<n;i++)
		 appendDynTag(sqlId,childRen[i],paramObj,sqlBuf,paramUnitList,session);
  	}catch(SQLException e){
  		throw e;
  	} catch (Throwable e) {
			throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
		}
  }
  
  /**
   * Foreach
   */
  private void addForeachTag(Object sqlId,ForeachTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try {
  		String propertyName=tag.getPropetyName();
	  	String spaceSymbol=tag.getSpaceSymbol();
	  	Object propertyValue = PropertyUtil.getPropertyValue(paramObj,propertyName,session);
	  	if(propertyValue!=null){
	  	  if(!(propertyValue instanceof Collection || propertyValue instanceof Map
	  	  		|| propertyValue instanceof Iterator  || propertyValue.getClass().isArray()))
	  	  	throw new SqlExecutionException(sqlId,"Property is not valid collection type or array type");
	  		
		  	StringBuffer tempBuffer = new StringBuffer();
		  	if(!StringUtil.isNull(tag.getPrependSymbol()))
		  		tempBuffer.append(tag.getPrependSymbol());

		  	if(!StringUtil.isNull(tag.getStartSymbol()))
		  		tempBuffer.append(tag.getStartSymbol());
		  	
		  	String foreachSQL = getForeachSQL(sqlId,paramObj,propertyValue,tag.getDynParamUnit(),spaceSymbol,tag.getSubSqlText(),session);
		  	appendSQL(tempBuffer,foreachSQL);
	  		
		  	if(!StringUtil.isNull(tag.getEndSymbol()))
		  		appendSQL(tempBuffer,tag.getEndSymbol());
		    
		  	//追加SQL
	  		appendSQL(sqlBuf,tempBuffer.toString());
	  	}
  	} catch (Throwable e) {
			throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
		}
  }
  
  
  /**
   * iterate
   */
  private void addIterateTag(Object sqlId,IterateTag tag,Object paramObj,StringBuffer sqlBuf,List paramUnitList,JdaSessionImpl session)throws SQLException{
  	try {
  		String propertyName=tag.getPropetyName();
	  	String spaceSymbol=tag.getSpaceSymbol();
	  	Object propertyValue = PropertyUtil.getPropertyValue(paramObj,propertyName,session);
	  	if(propertyValue!=null){
	  	  if(!(propertyValue instanceof Collection || propertyValue instanceof Map
	  	  		|| propertyValue instanceof Iterator  || propertyValue.getClass().isArray()))
	  	  	throw new SqlExecutionException(sqlId,"Property is not valid collection type or array type");
	  		
	  	 	StringBuffer tempBuffer = new StringBuffer();
		  	if(!StringUtil.isNull(tag.getPrependSymbol()))
		  		tempBuffer.append(tag.getPrependSymbol());

		  	if(!StringUtil.isNull(tag.getStartSymbol()))
		  		tempBuffer.append(tag.getStartSymbol());
		  	
		  	String foreachSQL = getForeachSQL(sqlId,paramObj,propertyValue,tag.getDynParamUnit(),spaceSymbol,tag.getSubSqlText(),session);
		  	appendSQL(tempBuffer,foreachSQL);
	  		
		  	if(!StringUtil.isNull(tag.getEndSymbol()))
		  		appendSQL(tempBuffer,tag.getEndSymbol());
		    
		  	//追加SQL
	  		appendSQL(sqlBuf,tempBuffer.toString());
	  	}
  	} catch (Throwable e) {
			throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
		}
  }
 
  /**
   * 循环构造SQL
   */
  private String getForeachSQL(Object sqlId,Object paramObj,Object propertyValue,DynParamUnit[] dynUnits,String seperator,String blockSQL,JdaSessionImpl session)throws SQLException{
   blockSQL=blockSQL.trim(); 
   StringBuffer foreachSQL=new StringBuffer();
   List itemList = new ArrayList();
   if(Collection.class.isInstance(propertyValue)){
  		Collection col =(Collection)propertyValue;
  		Iterator itor = col.iterator();
  		while(itor.hasNext()){
  		 Object item = itor.next();
  		 if(item!=null)
  		  itemList.add(item);
  		}
  	}else if(Map.class.isInstance(propertyValue)){
  		Map map =(Map)propertyValue;
  		Iterator itor = map.keySet().iterator();
  		while(itor.hasNext()){
  		 Object item = itor.next();
  		 if(item!=null)
   		  itemList.add(item);
  		}
  	}else if(Iterator.class.isInstance(propertyValue)){
  		Iterator itor =(Iterator)propertyValue;
  		while(itor.hasNext()){
  		 Object item = itor.next();
    	 if(item!=null)
     		itemList.add(item);
  		}
  	}else if(propertyValue.getClass().isArray()){
  		int size = Array.getLength(propertyValue);
  		for(int i=0;i<=size;i++){
  		 Object item = Array.get(propertyValue,i);
  		 if(item!=null)
      	itemList.add(item);
  		}
  	}
   
  
   //构造Item循环SQL
   for(int i=0,n=itemList.size(); i<n;i++){
  	 Object item = itemList.get(i);
  	  foreachSQL.append(getEachItemSQL(sqlId,paramObj,item,blockSQL,session));
  	  if((i<n-1) && seperator !=null){
  	  	foreachSQL.append(seperator);
  	 }
   }
   
   return foreachSQL.toString();
  }
  
  /**
   * 构造单个SQL
   */
  private String getEachItemSQL(Object sqlId,Object paramObj,Object item,String blockSQL,JdaSessionImpl session)throws SQLException{
  	try {
  		//DynParamUnit[] dynUnits=session.getDynSqlBlockParser().analyzeDynParamSQL(blockSQL);
  		DynSqlBlockParser dynSqlBlockParser = new DynSqlBlockParser();
  		DynParamUnit[] dynUnits=dynSqlBlockParser.analyzeDynParamSQL(blockSQL);
  			
  			
			for(int i=0,n=dynUnits.length;i<n;i++){
				DynParamUnit unit =dynUnits[i];
			  String unitBlockSQL = unit.getBlockSQL();
				String unitBlockContent=unit.getBlockContent();
				Object itemValue = item;
			  if(unitBlockContent.startsWith(Item_Dot)){
			  	String propertyName = unitBlockContent.substring(Item_Dot.length());
			  	itemValue = PropertyUtil.getPropertyValue(item,propertyName,session);
				}else if(unitBlockContent.startsWith(Item)){
					itemValue = item;
				}else{
					itemValue = PropertyUtil.getPropertyValue(paramObj,unitBlockContent,session);
				}
 
			  String txt =(itemValue==null)?Blank:String.valueOf(itemValue);
			  blockSQL=StringUtil.replace(blockSQL,unitBlockSQL,txt);
			}
			return blockSQL;
		} catch (PropertyException e) {
			throw new SqlExecutionException(sqlId,"Failed to execute dynamic sql",e);
		}
  }
}


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
package org.jmin.jda.impl.config.dynamicsql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.impl.config.dynamicsql.tag.DynChooseTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynForeachTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynIfTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynIterateTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynOtherwiseTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynSetTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynTrimTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynWhenTag;
import org.jmin.jda.impl.config.dynamicsql.tag.DynWhereTag;
import org.jmin.jda.impl.config.statictext.ParamResult;
import org.jmin.jda.impl.config.statictext.StaticSqlAnalyzer;
import org.jmin.jda.impl.exception.SqlDefinitionException;
import org.jmin.jda.impl.exception.SqlDynTagException;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.mapping.ParamUnit;
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

/**
 * SQL动态分析器
 * 
 * @author Chris Liao
 */
public class DynSqlAnalyzer {
	
	public final String UTF8="UTF-8";
	
	public final String Test="test";
	
	public final String Prepend="prepend";
	
	public final String Property="property";
	
	public final String Open="open";
	
	public final String Separator="separator";
	
	public final String Close="close";
	
	/**
	 * 静态SQL分析
	 */
	private StaticSqlAnalyzer staticSqlAnalyzer;
	
	/**
	 * 节点工厂类（来自htmlparser）
	 */
	private PrototypicalNodeFactory tagFactory = new PrototypicalNodeFactory();

	/**
	 * 构造器
	 */
	public DynSqlAnalyzer(StaticSqlAnalyzer staticSqlAnalyzer){
		tagFactory.registerTag(new DynIfTag());
		tagFactory.registerTag(new DynWhereTag());
		tagFactory.registerTag(new DynSetTag());
		tagFactory.registerTag(new DynChooseTag());
		tagFactory.registerTag(new DynWhenTag());
		tagFactory.registerTag(new DynOtherwiseTag());
		tagFactory.registerTag(new DynTrimTag());
		tagFactory.registerTag(new DynForeachTag());
		tagFactory.registerTag(new DynIterateTag());
		this.staticSqlAnalyzer =staticSqlAnalyzer;
	}
	
	/**
	 * 分析参数的位置信息
	 */
	public DynTag[] analyzeDynamicSQL(String sqlId,String SQL,Class paramClass,JdaContainer container)throws SQLException{
		try {
			List dynTagList = new ArrayList();
			Parser parser = Parser.createParser(SQL, UTF8);
			parser.setNodeFactory(tagFactory);		
			for(NodeIterator i = parser.elements(); i.hasMoreNodes();) {
				DynTag dynTag = createDynTag(sqlId,i.nextNode(),paramClass, container);
				if(dynTag!=null)
				 dynTagList.add(dynTag);
			}
			return (DynTag[])dynTagList.toArray(new DynTag[dynTagList.size()]);
		} catch (Throwable e) {
			throw new SqlDefinitionException(sqlId, e);
		} 		
	}
	
	/**
	 * 找出节点下所有子节点
	 */
	private DynTag createDynTag(String sqlId,Node curNode,Class paramClass,JdaContainer container)throws Exception{
		DynTag dynTag = null;
		if(curNode instanceof TextNode){//文本SQL
			String sqlBlock =curNode.getText();
			if(!StringUtil.isNull(sqlBlock)){
				ParamResult result = this.staticSqlAnalyzer.analyzeStaticSQL(sqlId,sqlBlock,paramClass,container);
				ParamUnit[] paramUnits = null;
				if(result.getParamMap()!=null)
					paramUnits=result.getParamMap().getParamUnits();
				
				String sqlText = result.getExeSQL();
				dynTag = new TextTag(sqlText,paramUnits);
			}
		}else if(curNode instanceof DynIfTag){//2
			DynIfTag tagNode =(DynIfTag)curNode;
			String test = tagNode.getAttribute(Test);
		  if(StringUtil.isNull(test))
				throw new SqlDynTagException(sqlId,"If tag missed test='xxxx'");
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"If tag missed child tag");
				
			IfTag tag = new IfTag(test);
			NodeList childList = tagNode.getChildren();
			for(NodeIterator i = childList.elements(); i.hasMoreNodes();) {
				Node childNode = i.nextNode();
		    tag.addChild(createDynTag(sqlId,childNode,paramClass,container));
		  }
		  dynTag = tag;
		}else if(curNode instanceof DynWhereTag){//3
			DynWhereTag tagNode =(DynWhereTag)curNode;
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"Where tag missed child tag");
				
			WhereTag tag = new WhereTag();
			NodeList childList = tagNode.getChildren();
			for(NodeIterator i = childList.elements(); i.hasMoreNodes();) {
				Node childNode = i.nextNode();
		    tag.addChild(createDynTag(sqlId,childNode,paramClass,container));
		  }
		    
		  dynTag = tag;
		}else if(curNode instanceof DynSetTag){//4
			DynSetTag tagNode =(DynSetTag)curNode;
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"Set tag missed child tag");
			
			SetTag tag = new SetTag();
			NodeList childList = tagNode.getChildren();
			for(NodeIterator i = childList.elements(); i.hasMoreNodes();) {
				Node childNode = i.nextNode();
	    	tag.addChild(createDynTag(sqlId,childNode,paramClass,container));
	  	}
	    dynTag = tag;
		}else if(curNode instanceof DynChooseTag){//4
			DynChooseTag tagNode =(DynChooseTag)curNode;
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"Choose tag missed child tag");
				
			ChooseTag tag = new ChooseTag();
			NodeList childList = tagNode.getChildren();
			for(NodeIterator i = childList.elements(); i.hasMoreNodes();) {
				Node childNode = i.nextNode();
        if(childNode instanceof DynWhenTag){
				   tag.addWhenTag((WhenTag)createDynTag(sqlId,childNode,paramClass,container));
			  }else if(childNode instanceof DynOtherwiseTag){
				   tag.setOtherwiseTag((OtherwiseTag)createDynTag(sqlId,childNode,paramClass,container));
				}
		  }
			
			if(tag.getSubWhenTagCount()==0)
		  		throw new SqlDynTagException(sqlId,"Chooose tag missed 'when' child tag");
		  if(tag.getOtherwiseTag()==null)
		  		throw new SqlDynTagException(sqlId,"Chooose tag missed 'otherwise' child tag");
		   dynTag = tag;
		   
		}else if(curNode instanceof DynWhenTag){//6
			DynWhenTag tagNode =(DynWhenTag)curNode;
			String test = tagNode.getAttribute(Test);
			if(StringUtil.isNull(test))
				throw new SqlDynTagException(sqlId,"When tag missed test=' '");
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"When tag missed child tag");
			
			WhenTag tag = new WhenTag(test);
			NodeList childList = tagNode.getChildren();
			for(NodeIterator i = childList.elements(); i.hasMoreNodes();) {
				Node childNode = i.nextNode();
	    	tag.addChild(createDynTag(sqlId,childNode,paramClass,container));
	  	}
	    
	    dynTag = tag;
		}else if(curNode instanceof DynOtherwiseTag){//7
			DynOtherwiseTag tagNode =(DynOtherwiseTag)curNode;
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"Otherwise tag missed child tag");
			
			OtherwiseTag tag = new OtherwiseTag();
			NodeList childList = tagNode.getChildren();
			for(NodeIterator i = childList.elements(); i.hasMoreNodes();) {
				Node childNode = i.nextNode();
	    	tag.addChild(createDynTag(sqlId,childNode,paramClass,container));
	  	}
	   
	    dynTag = tag;
		}else if(curNode instanceof DynTrimTag){//7
			DynTrimTag tagNode =(DynTrimTag)curNode;
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"Trim tag missed sub text content");
			
			TrimTag tag = new TrimTag();
			NodeList childList = tagNode.getChildren();
			for(NodeIterator i = childList.elements(); i.hasMoreNodes();) {
				Node childNode = i.nextNode();
	    	tag.addChild(createDynTag(sqlId,childNode,paramClass,container));
	  	}
	    dynTag = tag; 
		    
		}else if(curNode instanceof DynForeachTag){//9
			DynForeachTag tagNode =(DynForeachTag)curNode;
			String prepend = tagNode.getAttribute(Prepend);
			String property = tagNode.getAttribute(Property);
			String open = tagNode.getAttribute(Open);
			String separator = tagNode.getAttribute(Separator);
			String close = tagNode.getAttribute(Close);
			
			if(property==null || property.trim().length()==0)
				throw new SqlDynTagException(sqlId,"Foreach tag missed 'property' attribute");
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"Foreach tag missed sub text content");
			if(tagNode.getChildCount() > 1)
				throw new SqlDynTagException(sqlId,"Foreach tag just contains a text content");
			
			Object firstChild=null;
			NodeList childList = tagNode.getChildren();
			NodeIterator i = childList.elements();
			if(i.hasMoreNodes()) {
				firstChild = i.nextNode();
	  	}
		
			if(!(firstChild instanceof TextNode))
	      throw new SqlDynTagException(sqlId,"Foreach sub tag must be text type");
	    
			TextNode textTag =(TextNode)firstChild;
			ForeachTag forTag = new ForeachTag(property,textTag.getText());
		
			forTag.setPrependSymbol(prepend);
			forTag.setStartSymbol(open);
			forTag.setSpaceSymbol(separator);
			forTag.setEndSymbol(close);
			dynTag = forTag;
				
		}else if(curNode instanceof DynIterateTag){//10
			DynIterateTag tagNode =(DynIterateTag)curNode;
			String prepend = tagNode.getAttribute(Prepend);
			String property = tagNode.getAttribute(Property);
			String open = tagNode.getAttribute(Open);
			String separator = tagNode.getAttribute(Separator);
			String close = tagNode.getAttribute(Close);
			
			if(property==null || property.trim().length()==0)
				throw new SqlDynTagException(sqlId,"Iterate tag missed 'property' attribute");
			if(tagNode.getChildCount() == 0)
				throw new SqlDynTagException(sqlId,"Iterate tag missed sub text content");
			if(tagNode.getChildCount() > 1)
				throw new SqlDynTagException(sqlId,"Iterate tag just contains a text content");
			
			Object firstChild=null;
			NodeList childList = tagNode.getChildren();
			NodeIterator i = childList.elements();
			if(i.hasMoreNodes()) {
				firstChild = i.nextNode();
	  	}
		
			if(!(firstChild instanceof TextNode))
	      throw new SqlDynTagException(sqlId,"Iterate sub tag must be text type");
	    
			TextNode textTag =(TextNode)firstChild;
	    IterateTag iterateTag = new IterateTag(property,textTag.getText());
			
	    iterateTag.setPrependSymbol(prepend);
	    iterateTag.setStartSymbol(open);
	    iterateTag.setSpaceSymbol(separator);
	    iterateTag.setEndSymbol(close);
			dynTag = iterateTag;
		}else{
			throw new SqlDynTagException(sqlId,"Unknow tag["+curNode+"]");
		}
		return dynTag;
	}
}

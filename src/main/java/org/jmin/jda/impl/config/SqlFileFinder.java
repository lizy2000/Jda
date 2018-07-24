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
package org.jmin.jda.impl.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jdom.Element;

import org.jmin.jda.impl.exception.SqlDefinitionFileException;
import org.jmin.jda.impl.util.StringUtil;

/**
 * 文件查找
 * 
 * @author chris liao
 */
public class SqlFileFinder {
	
	/**
	 * classpath的缩写
	 */
	private final String CP="cp:";
	
  /**
   * class patht
   */
	private final String CLASSPATH="classpath:";
	
	/**
	 * USERDIR
	 */
	private final String USERDIR="user.dir";
	
	/**
	 * XML
	 */
	private final String XML_FILE_EXTEND=".xml";
	
	/**
	 * LEFT_FILE_SEP_CHAR
	 */
	private final String LEFT_FILE_SEP_CHAR="\\";
	
	/**
	 * RIGHT_FILE_SEP_CHAR
	 */
	private final String RIGHT_FILE_SEP_CHAR="/";
	
	private final String SEP_CHAR1="#{";
	private final String SEP_CHAR2="${";
	private final String SEP_CHAR3="}";
	
	/**
	 * 查找文件
	 */
	public URL find(String filename)throws SqlDefinitionFileException{
		return find(System.getProperty(USERDIR),filename);
	}
	
	/**
	 * 查找文件
	 */
	public URL find(String currentFolder,String filename)throws SqlDefinitionFileException{
		if(StringUtil.isNull(filename))
			 throw new NullPointerException("File name cann;t be null");
			
			filename=filename.trim();
			if(!filename.toLowerCase().endsWith(XML_FILE_EXTEND))
				throw new SqlDefinitionFileException("File["+ filename +"] is not a valid xml file");
			
		  filename = this.getFilterValue(filename);//过滤环境变量
		  if(filename.toLowerCase().startsWith(CP)){//需要从classpath中查找
				return findFromClassPath(filename.substring(CP.length()).trim());
			}else if(filename.toLowerCase().startsWith(CLASSPATH)){//需要从classpath中查找
				return findFromClassPath(filename.substring(CLASSPATH.length()).trim());
			}else{//无法确定文件是是一个完整的路径还是需要从classpath中寻找，因此需要尝试不同的寻找方法
				
				URL fileURL = null;
				try{
					fileURL = findFromClassPath(filename);
				}catch(Exception e){}
				
				try{
					if(fileURL==null)
					 fileURL = findFromSytemFolder(filename);
				}catch(Exception e){}
				
				try{
				  if(fileURL == null && !StringUtil.isNull(currentFolder))//文件没有被找到
				 	 fileURL = findFromSytemFolder(currentFolder+File.separatorChar+filename);
				}catch(Exception e){}

				if(fileURL==null)
					throw new SqlDefinitionFileException("Not found sql map file:"+filename);
				else
					return fileURL;
			}
	}
	
	/**
	 * 从类路径中查找
	 */
	private URL findFromClassPath(String filename)throws SqlDefinitionFileException{
		return (SqlFileFinder.class.getClassLoader()).getResource(filename);
	}
 
	/**
	 * 直接从目录中匹配
	 */
	private URL findFromSytemFolder(String filename)throws SqlDefinitionFileException{
		try {
			File file = new File(filename);
			if(file.exists()&& file.isFile())
				return new URL(file.getAbsolutePath());
			else
				return null;
		} catch (MalformedURLException e) {
		 throw new SqlDefinitionFileException(null,e);
		}
	}

	/**
	 * 验证文件是否为XML文件
	 */
	public void validateXMLFile(URL url)throws SqlDefinitionFileException {
		if(url == null)
			throw new SqlDefinitionFileException("File URL can't be null");
		
		if(!url.getFile().toLowerCase().endsWith(XML_FILE_EXTEND))
			throw new SqlDefinitionFileException("File["+url+"]is not a valid XML file");
	}

	/**
	 * 验证顶级节点
	 */
	public void validateXMLRoot(Element rootElement, String rootName)throws SqlDefinitionFileException {
		if (rootElement == null)
			throw new SqlDefinitionFileException("Missed root node");
		if (!rootElement.getName().equalsIgnoreCase(rootName))
			throw new SqlDefinitionFileException("Error,root node name must be "+ rootName);
	}

	/**
	 * 获得文件的路径
	 */
	public String getFilePath(String fileName) {
		int point = fileName.lastIndexOf(RIGHT_FILE_SEP_CHAR);
		if (point == -1)
			point = fileName.lastIndexOf(LEFT_FILE_SEP_CHAR);
		String folder = fileName;
		if (point > 0) {
			folder = folder.substring(0, point);
		}

		if (folder.startsWith(RIGHT_FILE_SEP_CHAR)) {
			folder = folder.substring(1);
		}
		return folder;
	}
	
	/**
	 * 获得过滤环境变量
	 */
	public  String getFilterValue(String value){
		int[] pos = getVariablePos(value);
		while(pos[1] >0 && pos[1] > pos[0]){
			String variableName = value.substring(pos[0]+2,pos[1]);//参数变量名
			String variableBlock = value.substring(pos[0],pos[1]+1);//参数变量块
			
			if(!StringUtil.isNull(variableName)){
				String variableValue = System.getProperty(variableName.trim());
				if(StringUtil.isNull(variableValue))
					throw new IllegalArgumentException("Not found system variable value with name:"+variableName);
					value = StringUtil.replace(value,variableBlock,variableValue);
			}
			pos = getVariablePos(value);
		}
		
		return value;
	}
	
	/**
	 * 获取环境变量位置
	 */
	private  int[]getVariablePos(String text){
		int index =text.indexOf(SEP_CHAR1);
		int index2 =text.indexOf(SEP_CHAR2);
		int endPos =text.indexOf(SEP_CHAR3);
		int startPos= -1;
		
		if(index>=0 && index2>=0){
			startPos=(index<=index2)?index:index2;
		}else if(index==-1 && index2>=0){
			startPos = index2;
		}else if(index>=0 && index2==-1){
			startPos = index;
		}else if(index==-1 && index2==-1){
			startPos = -1;
		}
		
		return new int[]{startPos,endPos};
	}
}

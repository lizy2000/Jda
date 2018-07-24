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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jmin.jda.JdaContainer;
import org.jmin.jda.JdaSourceInfo;
import org.jmin.jda.UserTransactionInfo;
import org.jmin.jda.impl.JdaContainerImpl;
import org.jmin.jda.impl.config.datasource.JdbcConverterImporter;
import org.jmin.jda.impl.config.datasource.JdbcPersisterImporter;
import org.jmin.jda.impl.config.datasource.JdbcSourceParser;
import org.jmin.jda.impl.config.datasource.JdbcSourceTags;
import org.jmin.jda.impl.config.datasource.JdbcTypeImporter;
import org.jmin.jda.impl.config.datasource.UserTransactionParser;
import org.jmin.jda.impl.config.mappingfile.SqlFileImporter;
import org.jmin.jda.impl.exception.DataSourceException;
import org.jmin.jda.impl.exception.SqlDefinitionFileException;
import org.jmin.jda.impl.exception.TransactionConfigException;
import org.jmin.jda.impl.util.StringUtil;

/**
 * 配置装载工厂
 * 
 * @author Chris Liao
 */
public class SqlFileLoader {
	
	/**
	 * 数据源有关的标记
	 */
	private JdbcSourceTags dataSourceTags = new JdbcSourceTags();
	
	/**
	 * 数据源解析
	 */
	private JdbcSourceParser dataSourceInfoParser = new JdbcSourceParser();
	
	/**
	 * jdbc类型解析
	 */
	private JdbcTypeImporter jdbcTypeInfoImporter = new JdbcTypeImporter();
	
	/**
	 * 事务解析
	 */
	private UserTransactionParser transactionInfoParser = new UserTransactionParser();
	
	/**
	 * jdbc类型解析
	 */
	private JdbcPersisterImporter paramPersisterInfoImporter = new JdbcPersisterImporter();
	
	/**
	 * jdbc类型解析
	 */
	private JdbcConverterImporter resultConverterImporter = new JdbcConverterImporter();
	
	/**
	 * 映射文件解析
	 */
	private SqlFileImporter sqlMappingFileImporter = new SqlFileImporter();
	
	/**
	 * 文件查找类
	 */
	private SqlFileFinder sqlFileFinder = new SqlFileFinder();

	/**
	 * XML解析
	 */
	private SAXBuilder saxBuilder = new SAXBuilder();
	
	 
	/**
	 * 默认装载的XML配置文件
	 */
	private final String DEFAULT_JDBC_FILE_NAME="/jda.xml";
	
	/**
	 * 默认装载的XML配置文件
	 */
	private final String DEFAULT_JDBC_FILE_ENV_NAME ="jda";
	
	/**
	 * 装载默认文件
	 */
	public JdaContainer load()throws SQLException{
		ClassLoader classLoader=SqlFileLoader.class.getClassLoader();
		URL defaultFileURL = classLoader.getResource(DEFAULT_JDBC_FILE_NAME);
		String defaultEnvFilename = System.getProperty(DEFAULT_JDBC_FILE_ENV_NAME);
		
		if(defaultFileURL == null && !StringUtil.isNull(defaultEnvFilename))
			defaultFileURL = sqlFileFinder.find(defaultEnvFilename);
		if(defaultFileURL == null)
			 throw new SqlDefinitionFileException("Not found default sql configeruation file:" + DEFAULT_JDBC_FILE_NAME);
		else
			return load(defaultFileURL);
	}
	
	/**
	 * 装载文件
	 */
	public JdaContainer load(String filename)throws SQLException{
		return load(sqlFileFinder.find(filename));
	}
	
	/**
	 * 装载文件
	 */
	public JdaContainer load(URL url)throws SQLException{
		String mapFilename=null;
		try {
			sqlFileFinder.validateXMLFile(url);
			mapFilename =url.getFile();
            Document document =saxBuilder.build(url);
			Element rootElement = document.getRootElement();
			sqlFileFinder.validateXMLRoot(rootElement,dataSourceTags.Root);
			
			Element datasourceElment = rootElement.getChild(dataSourceTags.DataSource);
			Element columnTypeElment = rootElement.getChild(dataSourceTags.JdbcTypes);

			JdaSourceInfo dataSourceInfo = dataSourceInfoParser.parse(datasourceElment,dataSourceTags);
			UserTransactionInfo transactionInfo = transactionInfoParser.parse(datasourceElment,dataSourceTags);
			dataSourceInfo.setUserTransactionInfo(transactionInfo);
			JdaContainer container = new JdaContainerImpl(dataSourceInfo);
			
			jdbcTypeInfoImporter.importJdbcTypes(columnTypeElment,container,dataSourceTags);
            paramPersisterInfoImporter.importParamPersisters(datasourceElment,container,dataSourceTags);
            resultConverterImporter.importResultConverters(datasourceElment,container,dataSourceTags);
      
			List mappingFileList = datasourceElment.getChildren(dataSourceTags.Mapping);
			for(int i=0,n=mappingFileList.size();i<n;i++){
				Element mapFileElement = (Element)mappingFileList.get(i);
				String resourceFilename = mapFileElement.getAttributeValue(dataSourceTags.Mapping_Resource);
				URL fileURL = sqlFileFinder.find(resourceFilename);
			  sqlMappingFileImporter.importSQLMapFile(fileURL,container,sqlFileFinder,saxBuilder);
			}
			return container;
		}catch(DataSourceException e){ 
			throw new SqlDefinitionFileException(null,"Failed to load data source info from file:"+mapFilename,e);
		}catch(TransactionConfigException e){
			throw new SqlDefinitionFileException(null,"Failed to load JTA transaction info from file:"+mapFilename,e);
		} catch (JDOMException e) {
			throw new SqlDefinitionFileException(null,"Failed to parse file:"+mapFilename,e);
		} catch (IOException e) {
			throw new SqlDefinitionFileException(null,"Failed to open file:"+mapFilename,e);
		} catch ( Throwable e) {
			throw new SqlDefinitionFileException(null,"Failed to open file:"+mapFilename,e);
		}
	}
}

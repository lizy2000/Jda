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
package org.jmin.jda.impl.config.datasource;

import java.sql.SQLException;
import java.util.List;

import org.jdom.Element;

import org.jmin.jda.UserTransactionInfo;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.log.LogPrinter;

/**
 * 事务解析
 * 
 * @author Chris Liao
 */

public class UserTransactionParser {
	
	/**
	 * Logger
	 */
	private LogPrinter logger = LogPrinter.getLogPrinter(UserTransactionParser.class);

	/**
	 * 事务解析
	 */
	public UserTransactionInfo parse(Element element,JdbcSourceTags sourceTags)throws SQLException {
		if(element == null){
			return null;
		}else{
			String name=null,factory=null,provider=null,principal=null,credentials=null;
			List infoList = element.getChildren(sourceTags.ATTR_Property);
			for(int i=0,size=infoList.size();i<size;i++){
				Element subElement =(Element)infoList.get(i);
				String attrName = (String)subElement.getAttributeValue(sourceTags.ATTR_Name);
				if(sourceTags.transactionJtaName.equals(attrName)){
					name = subElement.getTextTrim();
					logger.info(sourceTags.transactionJtaName +":"+ name);
				}else if(sourceTags.transactionJtaFactory.equals(attrName)){
					factory = subElement.getTextTrim();
					logger.info(sourceTags.transactionJtaFactory +":"+ factory);
				}else if(sourceTags.transactionJtaProvider.equals(attrName)){
					provider = subElement.getTextTrim();
					logger.info(sourceTags.transactionJtaProvider +":"+ provider);
				}else if(sourceTags.transactionJtaPrincipal.equals(attrName)){
					principal = subElement.getTextTrim();
					logger.info(sourceTags.transactionJtaPrincipal +":"+ principal);
				}else if(sourceTags.transactionJtaCredentials.equals(attrName)){
					credentials = subElement.getTextTrim();
					logger.info(sourceTags.transactionJtaCredentials +":"+ credentials);
				}
			}
			
			if(!StringUtil.isNull(name))
			 return new UserTransactionInfo(name,factory,provider,principal,credentials);
			else
				return null;
		}
	}
}

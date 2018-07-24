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
package org.jmin.jda.impl.execution.select;

import java.sql.SQLException;
import java.util.List;

import org.jmin.jda.JdaResultRowHandler;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.statement.SqlBaseStatement;

/**
 * 执行Row Handler操作
 * 
 * @author Chris Liao
 */
public class ObjectRowFinder extends BaseFinder{
	
	/**
	 * 分页大小
	 */
	private int PAGE_DATA_ROW_SIZE =10;

	/**
	 * 执行Row Handler操作
	 */
	public void find(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject,JdaResultRowHandler handler)throws SQLException{
		String sqlId = statement.getSqlId();
		int recordCount = session.getResultSize(sqlId,paramObject);
		int totalPageIndex = recordCount/PAGE_DATA_ROW_SIZE; 
    if(recordCount%PAGE_DATA_ROW_SIZE>0)
      totalPageIndex++;
     for(int i=1;i<=totalPageIndex;i++){
    	 List resultList = session.findList(sqlId,paramObject,i,PAGE_DATA_ROW_SIZE);
    	 for(int j=0,listSize=resultList.size();j<listSize;j++){
    		 handler.handleRow(resultList.get(j));
    	 }
     }
	}
}

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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.statement.SqlBaseStatement;

/**
 * 叠达器查询
 * 
 * @author Chris liao
 */

public class ObjectIterateFinder extends BaseFinder{
	
	/**
	 * 多结果的查询，返回一个Iterator
	 */
	public Iterator findIterator(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject)throws SQLException {
		return ((List)session.getObjectListFinder().find(session,statement,paramObject,0,0,List.class,null)).iterator();
	}
	
	/**
	 * 多结果的查询，返回一个Enumeration
	 */
	public Enumeration findEnumeration(JdaSessionImpl session,SqlBaseStatement statement,Object paramObject)throws SQLException {
		return ((Vector)session.getObjectListFinder().find(session,statement,paramObject,0,0,Vector.class,null)).elements();
	}
}

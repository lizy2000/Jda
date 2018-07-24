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
package org.jmin.jda.impl.execution.update;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.execution.SqlRequest;
import org.jmin.jda.impl.execution.SqlRequestUpdateResult;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.impl.util.CloseUtil;
import org.jmin.jda.impl.util.StringUtil;
import org.jmin.jda.statement.SqlPropertyIds;

/**
 * 一般性普通更新操作执行
 * 
 * @author Chris Liao
 */

public class UpdateHandler {

	/**
	 * 执行更新操作
	 */
	public int update(JdaSessionImpl session, SqlBaseStatement statement, Object paramObject,
			SqlOperationType operateType) throws SQLException {
		
		SqlRequest request = null;
		try {
			request = session.getSqlRequestFactory().createSqlRequest(session, statement, paramObject, operateType);
			if (session.getBatchUpdateList() != null) {// 当前已经创建一个批量处理列表，所以需要登记进入批量处理
				session.getBatchUpdateList().addBatchRequest(request);
				return 0;
			} else {
				SqlRequestUpdateResult updResult= session.getSqlRequestHandler().handleUpdateRequest(request);
				String flushCacheId = statement.getPropertyValue(SqlPropertyIds.FlushCacheId);
				if (session.isResultCacheOpen()&& !StringUtil.isNull(flushCacheId)) {// 清理二级缓存
					String[] subCacheIds = StringUtil.split(flushCacheId, ",");
					for (int i = 0; i < subCacheIds.length; i++) {
						session.clearApplicationCache(subCacheIds[i]);
					}
				}
				return updResult.getEffectedRows();
			}
		} finally {
			if (request != null && request.getConnection() != null)
				session.releaseConnection(request.getConnection());
		}
	}

	/**
	 * 检查是否支持批量更新
	 */
	public boolean isSupportBatchUpdate(Connection con) {
		try {
			return con.getMetaData().supportsBatchUpdates();
		} catch (SQLException e) {
			Statement statemnet = null;
			try {
				statemnet = con.createStatement();
				statemnet.clearBatch();
				return true;
			} catch (SQLException ee) {
				return false;
			} finally {
				CloseUtil.close(statemnet);
				statemnet = null;
			}
		}
	}
}
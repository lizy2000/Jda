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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jmin.jda.JdaDialect;
import org.jmin.jda.impl.JdaSessionImpl;
import org.jmin.jda.impl.cache.CacheKey;
import org.jmin.jda.impl.statement.SqlBaseStatement;
import org.jmin.jda.impl.statement.SqlOperationType;
import org.jmin.jda.statement.SqlPropertyIds;

/**
 * 基础查找者
 * 
 * @author Chris Liao
 */
abstract class BaseFinder {

	/**
	 * 将记录移动到目标行
	 */
	public void moveToTargetRow(ResultSet resultSet, int rowNo, SqlOperationType sqlOperationType, JdaDialect dialect,
			JdaSessionImpl session) throws SQLException {
		boolean needMoveToRowNo = false;
		if (SqlOperationType.Procedure.equals(sqlOperationType)) {// 存储过程，且目标行大于1，则需要移动
			needMoveToRowNo = (rowNo >= 1) ? true : false;
		} else {
			if (rowNo >= 1) {// 目标行大于1的时候，才需要考虑是否移动方式读取记录
				needMoveToRowNo = (dialect == null) ? true : dialect.pageQueryResultNeedMove();
			}
		}

		if (needMoveToRowNo) {// 对于没有设置SQL方言执行器，只能查询出所有记录，并移动到指定位置，才开始读取记录
			if (!session.isSupportAbsoluteChecked()) {// 没有做测试，开始检查
				try {
					resultSet.absolute(rowNo);
					session.setSupportAbsolute(true);
				} catch (Throwable e) {
					session.setSupportAbsolute(false);
					this.moveToTargetRowStepByStep(resultSet, rowNo);
				} finally {
					session.setSupportAbsoluteChecked(true);// 刚做做过检查
				}
			} else if (session.isSupportAbsolute()) {// 已做过测试，支持精确定位
				try {
					resultSet.absolute(rowNo);
				} catch (Throwable e) {
					this.moveToTargetRowStepByStep(resultSet, rowNo);
				}
			} else {// 已做过测试，不支持精确定位
				this.moveToTargetRowStepByStep(resultSet, rowNo);
			}
		}
	}

	/**
	 * 通过调用ResultSet.next()一步一步移动到目标行
	 */
	private void moveToTargetRowStepByStep(ResultSet resultSet, int rowNo) throws SQLException {
		int skipCount = 0;
		while (resultSet.next()) {
			if (++skipCount == rowNo)// 将游标移动到目标行的前一行，next直接到目标行
				break;
		}
	}

	/**
	 * 从缓存中读取缓存
	 */
	protected Object getObjectFromCache(JdaSessionImpl session,SqlBaseStatement statement,String sqlText, Object[] paramValues,Object[]optionalValues) throws SQLException {
		if (session.isResultCacheOpen() || session.isResultLocalCacheOpen()) {
			String cacheId = statement.getPropertyValue(SqlPropertyIds.CacheId);
			CacheKey key = new CacheKey(statement.getSqlId(),sqlText,paramValues,optionalValues);
			return session.getObjectFromCache(cacheId, key);
		} else {
			return null;
		}
	}

	/**
	 * 将结果放入缓存
	 */
	protected void putObjectIntoCache(JdaSessionImpl session,SqlBaseStatement statement,String sqlText,Object[] paramValues,Object[]optionalValues,Object resultObject) throws SQLException {
		if (session.isResultCacheOpen() || session.isResultLocalCacheOpen()) {
			String cacheId = statement.getPropertyValue(SqlPropertyIds.CacheId);
			CacheKey key = new CacheKey(statement.getSqlId(),sqlText, paramValues,optionalValues);
			session.putObjectToCache(cacheId, key, resultObject);
		}
	}
}

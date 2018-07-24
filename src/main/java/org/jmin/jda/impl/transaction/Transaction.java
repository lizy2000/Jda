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
package org.jmin.jda.impl.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务接口
 * 
 * @author Chris Liao
 */

public interface Transaction {

	/**
	 * 提交
	 */
  public void commit() throws SQLException;
  
  /**
   * 回滚
   */
  public void rollback() throws SQLException;
  
  /**
   * 获得事务Connection
   */
  public Connection getConnection()throws SQLException;
  
  /**
   * 使事务死亡
   */
  public void clear()throws SQLException;
 
}

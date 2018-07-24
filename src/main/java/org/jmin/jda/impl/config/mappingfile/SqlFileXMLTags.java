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
package org.jmin.jda.impl.config.mappingfile;

/**
 * XML标记
 * 
 * @author Chris Liao 
 */

public class SqlFileXMLTags {
	
	public final String Root ="statement";
	
	public final String Class ="class";
	
	public final String ParameterMap ="parameterMap";
	
	public final String ResultMap ="resultMap";
	
	public final String Insert ="insert";
	
	public final String update ="update";
	
	public final String Delete ="delete";
	
	public final String Select ="select";
	
	public final String Procedure ="procedure";
	
	public final String CacheModel ="cacheModel";
	
	public final String ATTR_Id="id";
	public final String ATTR_Name="name";
	public final String ATTR_Value="value";
	public final String ATTR_Type="type";
	public final String ATTR_Size="size";
	
	public final String ATTR_Flush_Interval="flushInterval";
	public final String ATTR_ReadOnly="readOnly";
	public final String ATTR_Serialize="serialize";
	public final String ATTR_Reference_Type="reference-type";
	
	public final String ATTR_Hour="hours";
	public final String ATTR_Minutes="minutes";
	public final String ATTR_Seconds="seconds";
	public final String ATTR_Millisecondss="millisecondss";
	
	public final String ATTR_Class="class";
	public final String ATTR_Space="space";
	public final String ATTR_Parameter="parameter";
	public final String ATTR_Property="property";
	public final String ATTR_JavaType ="javaType";
	public final String ATTR_JdbcType ="jdbcType";
	public final String ATTR_Param_Persister ="paramPersister";
	
	public final String ATTR_Lazy="lazy";
	public final String ATTR_Column="column";
	public final String ATTR_Mode="mode";
	
	public final String ATTR_Key_Property="keyProperty";
	public final String ATTR_Value_Property="valueProperty";
	public final String ATTR_Select="select";
	public final String Node_Result="result";
	
	public final String ATTR_parameterMap="parameterMap";
	public final String ATTR_ParameterType="parameterType";
	public final String ATTR_ParameterClass="parameterClass";
	
	public final String ATTR_ResultMap="resultMap";
	public final String ATTR_ResultType="resultType";
	public final String ATTR_ResultClass="resultClass";
	public final String ATTR_ResultConverter="resultConverter";
	
	public final String ATTR_CacheId="cacheId";
	public final String ATTR_Flush_CacheId="flushCacheId";
}

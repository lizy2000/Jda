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
package org.jmin.jda.impl.config.statictext;

/**
 * SQL表达式的参数符号
 * 
 * @author Chris Liao
 */

public class ParamSymbol {
	
	/**
	 * #{...}
	 */
	public final static ParamSymbol Symbol1 = new ParamSymbol("#{","}");
	
	/**
	 * #[....]
	 */
	public final static ParamSymbol Symbol2 = new ParamSymbol("#[","]");
	
	/**
	 * ${...}
	 */
	public final static ParamSymbol Symbol3 = new ParamSymbol("${","}");
	
	/**
	 * $[.....]
	 */
	public final static ParamSymbol Symbol4 = new ParamSymbol("$[","]");
	
	/**
	 * #...#
	 */
	public final static ParamSymbol Symbol5 = new ParamSymbol("#","#");
	
	/**
	 * 所支持的
	 */
	public final static ParamSymbol[] Symbols = new ParamSymbol[]{Symbol1,Symbol2,Symbol3,Symbol4,Symbol5};
	
	/**
	 * 左边符号
	 */
	private String startSymbol;
	
	/**
	 * 右边符号
	 */
	private String endSymbol;
	
	/**
	 * 构造
	 */
	public ParamSymbol(String leftSymbol){
		this.startSymbol = leftSymbol;
	}
	
	/**
	 * 构造
	 */
	public ParamSymbol(String leftSymbol,String rightSymbol){
		this.startSymbol = leftSymbol;
		this.endSymbol = rightSymbol;
	}

	public String getStartSymbol() {
		return startSymbol;
	}

	public String getEndSymbol() {
		return endSymbol;
	}
	
	public int hashCode() {
		return endSymbol.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof ParamSymbol) {
			ParamSymbol other = (ParamSymbol) obj;
			return (this.startSymbol.equals(other.startSymbol));
		} else {
			return false;
		}
	}
}

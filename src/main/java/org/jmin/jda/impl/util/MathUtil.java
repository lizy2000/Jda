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
package org.jmin.jda.impl.util;
import java.math.BigDecimal;
/**
 * 数学应用辅助类
 * 
 * @author Chris Liao 
 */
public class MathUtil {
	
	/**
	 * 创建大数字
	 */
	private static BigDecimal createBigDecimal(double value){
		return new BigDecimal(value);
	}

	/**
	 * a +b
	 */
	public static double add(double a,double b){
		return createBigDecimal(a).add(createBigDecimal(b)).doubleValue(); 
	}
	
	/**
	 * a - b
	 */
	public static double subtract(double a,double b){
		return createBigDecimal(a).subtract(createBigDecimal(b)).doubleValue(); 
	}
	
	/**
	 * a * b
	 */
	public static double multiply(double a,double b){
		return createBigDecimal(a).multiply(createBigDecimal(b)).doubleValue(); 
	}
	
	/**
	 * a / b
	 */
	public static double devid(double a,double b){
		return createBigDecimal(a).divide(createBigDecimal(b),BigDecimal.ROUND_HALF_UP).doubleValue(); 
	}
	
	/**
	 * 1.3467 ----> 1.35
	 */
	public static double getScale(double a,int scale){
		return createBigDecimal(a).setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue(); 
	}
	
	public static double round(double number, int round){	
		double r = Math.pow(10,round);
		return (Math.round(number*r)/r);
	}
}

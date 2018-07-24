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
package org.jmin.test.jda;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 驱动装载
 * 
 * @author Chris Liao
 */
public class Link {
	
	private static String driver;
	
	private static String url;
	
	private static String user;
	
	private static String password;
	
	private static final String FILE ="link.properties";
	
	static {
		try{
			InputStream resourceStream = Link.class.getResourceAsStream(FILE);
			Properties prop = new Properties();
			prop.load(resourceStream);
			driver = prop.getProperty("Driver");
			url = prop.getProperty("URL");
			user = prop.getProperty("User");
			password = prop.getProperty("Password");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static String getDriver() {
		return driver;
	}

	public static String getURL() {
		return url;
	}

	public static String getUser() {
		return user;
	}

	public static String getPassword() {
		return password;
	}
}

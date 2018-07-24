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
package org.jmin.test.jda.userDemo;

/**
 * 实体类
 * 
 * @author chris
 */
public class User {

	private String name;

	private String sex;
	
	public User() {}
	public User(String name) {
		this.name = name;
	}
	
	public User(String name, String sex) {
		this.name = name;
		this.sex = sex;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String toString() {
		return "Name " + name + " sex: " + sex;
	}

	public static void main(String[] args) {
		System.out.println("(ID.inlineparamete) it is error");
		System.out
				.println("[ORM-INFO](ID.inlineparamete)Type handler for base parameter class("
						+ "safsdf" + ")not found");

	}
}

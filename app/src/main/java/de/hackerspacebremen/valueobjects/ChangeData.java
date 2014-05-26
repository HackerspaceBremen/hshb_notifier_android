/*
 * Hackerspace Bremen Android App - An Open-Space-Notifier for Android
 * 
 * Copyright (C) 2012 Steve Liedtke <sliedtke57@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See 
 * the GNU General Public License for more details.
 * 
 * You can find a copy of the GNU General Public License on http://www.gnu.org/licenses/gpl.html.
 * 
 * Contributors:
 *     Steve Liedtke <sliedtke57@gmail.com>
 */
package de.hackerspacebremen.valueobjects;

/**
 * @author Steve Liedtke
 */
public final class ChangeData {

	private final String login;
	
	private final String password;
	
	private final String message;
	
	private final boolean saveLogin;
	
	public ChangeData(String login, String password, String message, boolean saveLogin){
		this.login = login;
		this.password = password;
		this.message = message;
		this.saveLogin = saveLogin;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSaveLogin() {
		return saveLogin;
	}
	
	
}

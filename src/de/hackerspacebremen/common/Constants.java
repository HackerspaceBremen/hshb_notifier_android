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
 *     Matthias Friedrich <mtthsfrdrch@gmail.com>
 */
package de.hackerspacebremen.common;


public final class Constants {

	private Constants(){
		// nothing to implement
	}
	
	static{
		// CHANGE HERE if you switch between prod and test
		boolean prod = false;
		PROD = prod;
		
		SET_SENDER_ID(prod);
	}
	
	public static final String ADMIN_EMAIL = "hshbdeveloper@gmail.com";
	
	public static boolean PROD;
	
	public static String SENDER_ID;
	
	public final static String SPACE_DATA_PERSISTENCE = "spaceDataPersistence";
	
	public final static String SPACE_OPEN_DATA_KEY = "spaceOpenDataKey";
	
	public final static String SPACE_TIME_DATA_KEY = "spaceTimeDataKey";
	
	public static final String UTF8 = "UTF-8";
	
	private static final String TEST_ID = "1044406323224";
	
	private static final String PROD_ID = "337835721184";
	
	public static final int GCM_NOTIFICATION_ID = 81543;
	
	private static void SET_SENDER_ID(boolean prod){
		if(prod){
			SENDER_ID = PROD_ID;
		}else{
			SENDER_ID = TEST_ID;
		}
	}
}

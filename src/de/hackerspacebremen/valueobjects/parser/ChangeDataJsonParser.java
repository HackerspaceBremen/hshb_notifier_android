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
package de.hackerspacebremen.valueobjects.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import de.hackerspacebremen.valueobjects.ChangeData;

/**
 * @author Steve Liedtke
 *
 */
public final class ChangeDataJsonParser {

	public static ChangeData parse(String jsonString){
    	ChangeData change = null;
    	try{
    		final JSONObject json = new JSONObject(jsonString);
    		String login = null;
    		if(json.has("CD1")){
    			login = json.getString("CD1");
    		}
    		String password = null;
    		if(json.has("CD2")){
    			password = json.getString("CD2");
    		}
    		String message = null;
    		if(json.has("CD3")){
    			message = json.getString("CD3");
    		}
    		boolean checkBox = json.getBoolean("CD4");
    		change = new ChangeData(login, password, message, checkBox);
    	}catch(JSONException e){
    		Log.e(ChangeDataJsonParser.class.getName(), "JSONException occured: " + e.getMessage());
    	}
    	return change;
    }
	
	public static JSONObject parse(ChangeData data){
    	JSONObject json = new JSONObject();
    	try{
	    	if(data.getLogin()!=null && data.getLogin().length()>0){
	    		json.put("CD1", data.getLogin());
	    	}
	    	
	    	if(data.getPassword() != null && data.getPassword().length()>0){
	    		json.put("CD2", data.getPassword());
	    	}
	    	if(data.getMessage() != null && data.getMessage().length()>0){
	    		json.put("CD3", data.getMessage());
	    	}
	    	json.put("CD4", data.isSaveLogin());
    	}catch(JSONException e){
    		json = null;
    		Log.e(ChangeDataJsonParser.class.getName(), "JSONException occured: " + e.getMessage());
    	}
    	return json;
    }
}

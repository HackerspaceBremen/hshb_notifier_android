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
package de.hackerspacebremen.valueobjects.parser;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import de.hackerspacebremen.valueobjects.SpaceData;

import android.util.Log;

/**
 * @author Matthias Friedrich, Steve Liedtke
 *
 */
public class SpaceDataJsonParser {
    
	public static SpaceData parse(JSONObject jsonObj){
        SpaceData data = null;
        
        try{
	        if(jsonObj.has("ST3")) {
	            final boolean isOpen = jsonObj.getString("ST3").equals("OPEN");
	            data = new SpaceData(isOpen);
	        
	            if (jsonObj.has("ST2")) {
	                data.setTime(new Date(jsonObj.getLong("ST2")));
	            }
	            if (jsonObj.has("ST5")) {
	                data.setMessage(jsonObj.getString("ST5"));
	            }
	        }
        }catch(JSONException e){
        	data = null;
        	Log.e(SpaceDataJsonParser.class.getName(), "JSONException occured: " + e.getMessage());
        }
        
        return data;
    }
	
	public static SpaceData parse(final String jsonString){
        SpaceData data = null;
        try{
        	data = parse(new JSONObject(jsonString));
        }catch(JSONException e){
        	Log.e(SpaceDataJsonParser.class.getName(), "JSONException occured: " + e.getMessage());
        }
		return data;
    }
    
    public static JSONObject parse(SpaceData data){
    	JSONObject json = new JSONObject();
    	try{
	    	if(data.isSpaceOpen()){
	    		json.put("ST3", "OPEN");
	    	}else{
	    		json.put("ST3", "CLOSED");
	    	}
	    	
	    	if(data.getTime() != null){
	    		json.put("ST2", data.getTime().getTime());
	    	}
	    	if(data.getMessage() != null){
	    		json.put("ST5", data.getMessage());
	    	}
    	}catch(JSONException e){
    		json = null;
    		Log.e(SpaceDataJsonParser.class.getName(), "JSONException occured: " + e.getMessage());
    	}
    	return json;
    }
}

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
package de.hackerspacebremen.valueobjects;

import java.util.Date;

/**
 * @author Matthias
 *
 */
public class SpaceData {
    private boolean spaceOpen;
    private Date time;
    private String message;
    
    public SpaceData (boolean spaceOpen) {
        this.spaceOpen = spaceOpen;
        this.time = null;
        this.message = null;
    }
    
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the spaceOpen
     */
    public boolean isSpaceOpen() {
        return spaceOpen;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }
    
    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }
}

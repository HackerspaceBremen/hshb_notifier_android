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
package de.hackerspacebremen.viewholders;

import de.hackerspacebremen.R;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author Steve
 *
 */
public final class ChangeViewHolder {

	protected static ChangeViewHolder viewHolder = null;
	
	public final Button abortBtn;
	public final Button sendBtn;
	public final EditText login;
	public final EditText password;
	public final EditText message;
	public final CheckBox checkBox;
	
	private ChangeViewHolder(final View view){
		this.abortBtn = (Button) view.findViewById(R.id.abort_btn);
		this.sendBtn = (Button) view.findViewById(R.id.send_btn);
		this.login = (EditText) view.findViewById(R.id.login);
		this.password = (EditText) view.findViewById(R.id.password);
		this.message = (EditText) view.findViewById(R.id.message);
		this.checkBox = (CheckBox) view.findViewById(R.id.save_login);
	}
	
	public static void init(final View view){
		viewHolder = new ChangeViewHolder(view);
	}
	
	public static ChangeViewHolder get(){
		return viewHolder;
	}
}

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Steve
 *
 */
public final class StatusViewHolder {

	protected static StatusViewHolder viewHolder = null;
	
	public final ImageView imgStatus;
	public final ImageView imgConnErr;
	public final RelativeLayout statusLayout;
	public final RelativeLayout messageBlock;
	public final TextView statusMessage;
	public final TextView messageText;
	public final TextView messageLabel;
	
	private StatusViewHolder(final View view){
		this.imgStatus = (ImageView) view.findViewById(R.id.clock_status);
		this.imgConnErr = (ImageView) view.findViewById(R.id.conn_error);
		this.statusLayout = (RelativeLayout) view
				.findViewById(R.id.status);
		this.messageBlock = ((RelativeLayout) view.findViewById(R.id.message_block));
		this.statusMessage = (TextView) view.findViewById(
				R.id.status_message);
		this.messageText = (TextView) view.findViewById(
				R.id.message_message);
		this.messageLabel = (TextView) view.findViewById(R.id.message_label);
	}
	
	public static void init(final View view){
		viewHolder = new StatusViewHolder(view);
	}
	
	public static StatusViewHolder get(){
		return viewHolder;
	}
}
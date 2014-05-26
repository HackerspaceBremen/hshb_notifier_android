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
package de.hackerspacebremen.communication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Build;

import com.squareup.okhttp.OkHttpClient;

import de.hackerspacebremen.common.Constants;

public abstract class HackerspaceComm extends
		AsyncTask<JSONObject, Void, JSONObject> {

	static {
		if (Constants.PROD) {
			SERVERURL = "hackerspacehb.appspot.com/";
		} else {
			SERVERURL = "testhackerspacehb.appspot.com/";
		}
	}

	public static final String SERVERURL;

	private static final String HTTP = "http://";

	private static final String HTTPS = "https://";

	private OkHttpClient client = new OkHttpClient();

	/**
	 * tells if a get request is performed. if false a post request is
	 * performed.
	 */
	protected boolean getReq = true;

	/**
	 * tells if an http request is performed. if false an https request is
	 * performed.
	 */
	protected boolean httpReq = false;

	protected String servletUrl;

	protected String getParams = "";

	protected String appVersionName = "?";

	/**
	 * 0 -> no error, -1 -> connection error, -2 -> json parsing error.
	 */
	protected int errorcode = 0;

	protected int httpState = 0;

	/**
	 * params for POST request.
	 */
	protected Map<String,String> postParams;

	public HackerspaceComm() {
//		postParams = new ArrayList<NameValuePair>(2);
		postParams = new HashMap<String, String>();
	}

	protected final JSONObject doInBackground(final JSONObject... data) {
		final String userAgent = "HackerSpaceBremen/" + this.appVersionName
				+ "; Android/" + Build.VERSION.RELEASE + "; "
				+ Build.MANUFACTURER + "; " + Build.DEVICE + "; " + Build.MODEL;

		HttpClient httpclient = new DefaultHttpClient();
		HttpParams httpBodyParams = httpclient.getParams();
		httpBodyParams.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);

		String response = null;
		int responseCode = 0;
		String httpOrS = HTTPS;
		if (httpReq) {
			httpOrS = HTTP;
		}

		if (getReq) {
			try {
				final HttpURLConnection connection = client
						.open(new URL(httpOrS + SERVERURL + this.servletUrl
								+ "?" + getParams));
				InputStream in = null;
				try {
					// Read the response.
					in = connection.getInputStream();
					final byte[] resp = readFully(in);
					response = new String(resp, Constants.UTF8);
					responseCode = connection.getResponseCode();
				} finally {
					if (in != null)
						in.close();
				}
				// HttpGet httpget = new HttpGet(httpOrS + SERVERURL
				// + this.servletUrl + "?" + getParams);
				// response = httpclient.execute(httpget);
			} catch (IOException e) {
				errorcode = -1;
				cancel(false);
				return null;
			}
		} else {
			try {
				HttpURLConnection connection = client.open(new URL(httpOrS
						+ SERVERURL + this.servletUrl));
				OutputStream out = null;
				InputStream in = null;
				try {
					// Write the request.
					connection.setRequestMethod("POST");
					out = connection.getOutputStream();
					out.write(createBody().getBytes(Constants.UTF8));
					out.close();

					responseCode = connection.getResponseCode();
					in = connection.getInputStream();
					response = readFirstLine(in);
				} finally {
					// Clean up.
					if (out != null)
						out.close();
					if (in != null)
						in.close();
				}
				// HttpPost httpPost = new HttpPost(httpOrS + SERVERURL
				// + this.servletUrl);
				// httpPost.setEntity(new UrlEncodedFormEntity(postParams,
				// "UTF-8"));
				// response = httpclient.execute(httpPost);

			} catch (IOException e) {
				errorcode = -1;
				cancel(false);
				return null;
			}
		}

		httpState = responseCode;

		JSONObject resData = new JSONObject();
		String resString = "";
		try {
			resString = response;
			resData = new JSONObject(resString);
			if (httpState != 200) {
				errorcode = resData.getInt("CODE");
				cancel(false);
				return null;
			}

		} catch (JSONException e) {
			if (httpState != 200) {
				errorcode = httpState;
			} else {
				errorcode = -2;
			}
			cancel(false);
			return null;
		}

		return resData;
	}
	
	private String createBody(){
		final StringBuilder sBuilder = new StringBuilder();
		boolean first = true;
		for(final String key : postParams.keySet()){
			if(first){
				first = false;
			}else{
				sBuilder.append("&");
			}
			sBuilder.append(key+"="+postParams.get(key));
		}
		return sBuilder.toString();
	}

	private byte[] readFully(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int count; (count = in.read(buffer)) != -1;) {
			out.write(buffer, 0, count);
		}
		return out.toByteArray();
	}

	private String readFirstLine(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				Constants.UTF8));
		return reader.readLine();
	}

//	/**
//	 * Get the content from a HttpResponse (or any InputStream) as a String.
//	 * Quelle:
//	 * http://www.androidsnippets.com/get-the-content-from-a-httpresponse
//	 * -or-any-inputstream-as-a-string
//	 * 
//	 * @param is
//	 *            input stream
//	 * @return the readed string
//	 * @throws IOException
//	 *             when something failed with inputstream
//	 */
//	private StringBuilder inputStreamToString(final InputStream is)
//			throws IOException {
//		String line = "";
//		StringBuilder total = new StringBuilder();
//
//		// Wrap a BufferedReader around the InputStream
//		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//
//		// Read response until the end
//		while ((line = rd.readLine()) != null) {
//			total.append(line);
//		}
//
//		// Return full string
//		return total;
//	}
}

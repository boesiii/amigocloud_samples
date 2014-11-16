package com.amigocloud.amigomobile.locator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AmigoRest {
	private HttpClient httpclient = new DefaultHttpClient();
	private AccessToken token;

	private static final String USER_AGENT = "AmigoLocator";
	private static final String CLIENT_ID = "5996bb4af375491b3d95";
	private static final String CLIENT_SECRET = "d4235bc6fd279ad93e3afc4f53d650c820d1b97f";

	public boolean login(String email, String password) {
		HttpPost httppost = new HttpPost("https://www.amigocloud.com/api/v1/oauth2/access_token");
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
			nameValuePairs.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
			nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
			nameValuePairs.add(new BasicNameValuePair("username", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httppost.setHeader("User-Agent", USER_AGENT);
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			if(response.getStatusLine().getStatusCode() == 200)
			{
				updateToken(response);
				return true;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean refreshToken() {
		if(token == null)
			return false;
		HttpPost httppost = new HttpPost("https://www.amigocloud.com/api/v1/oauth2/access_token");
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
			nameValuePairs.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
			nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
			nameValuePairs.add(new BasicNameValuePair("refresh_token", token.refreshToken));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httppost.setHeader("User-Agent", USER_AGENT);
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			if(response.getStatusLine().getStatusCode() == 200)
			{
				updateToken(response);
				return true;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void updateToken(HttpResponse response) throws IOException, JSONException {
		StringBuilder builder = new StringBuilder();
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		String line;
		while((line = reader.readLine()) != null){
			builder.append(line);
		}
		JSONObject loginObject = new JSONObject(builder.toString());
		token = new AccessToken();
		token.accessToken = loginObject.getString("access_token");
		token.tokenType = loginObject.getString("token_type");
		token.refreshToken = loginObject.getString("refresh_token");
		token.expires = loginObject.getInt("expires_in");
	}
}
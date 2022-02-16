package ru.ruslan2570.funweather;

import android.util.Log;
import android.widget.Toast;

import com.google.common.io.ByteStreams;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoteFetch extends Thread{
	final static String LANG = "RU";
	final static String UNITS = "metric";
	final static String URL = "http://api.openweathermap.org/data/2.5/weather?";
	private final String APIKey = "YOUR_API_KEY";
	private double latitude;
	private double longitude;

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public JSONObject call() throws Exception {
		String path = String.format("%slat=%s&lon=%sl&ang=%s&units=%s&appid=%s", URL, latitude, longitude, LANG, UNITS, APIKey);
		path = "http://new-bokino.ru/test.json";
		URL url = new URL(path);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		if(urlConnection.getResponseCode() != 200)
			throw new IOException("Response code isn't 200");

		else{
		InputStream inputStream = urlConnection.getInputStream();
		byte[] buff = ByteStreams.toByteArray(inputStream);
		JSONObject json = new JSONObject(new String(buff));

		return json;}
	}


}

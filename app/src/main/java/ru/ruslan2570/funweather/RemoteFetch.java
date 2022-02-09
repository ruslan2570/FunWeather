package ru.ruslan2570.funweather;

import android.util.Log;

import com.google.common.io.ByteStreams;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoteFetch {
	final static String LANG = "RU";
	final static String UNITS = "metric";
	final static String URL = "http://api.openweathermap.org/data/2.5/weather?";
	private String APIKey = "4f337481565d15c81f846b9b19de35bd";
	private double latitude;
	private double longitude;

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setAPIKey(String APIKey) {
		this.APIKey = APIKey;
	}

	public JSONObject call() throws Exception {
		String path = String.format("%slat=%s&lon=%sl&ang=%s&units=%s&appid=%s", URL, latitude, longitude, LANG, UNITS, APIKey);
		// path = "http://new-bokino.ru/test.json";
		URL url = new URL(path);
		InputStream inputStream = url.openStream();
		byte[] buff = ByteStreams.toByteArray(inputStream);
		JSONObject json = new JSONObject(new String(buff));
		return json;
	}


}

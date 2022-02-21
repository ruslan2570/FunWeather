package ru.ruslan2570.funweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.ByteStreams;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

	TextView tvCityName;
	TextView tvTemp;
	TextView tvTempFeels;
	TextView tvWindSpeed;
	TextView tvWindDirection;
	TextView tvHumidity;
	TextView tvFun;
	Button btnUpdate;

	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvCityName = findViewById(R.id.text_city_name);
		tvTemp = findViewById(R.id.text_temp);
		tvTempFeels = findViewById(R.id.text_temp_feels);
		tvWindSpeed = findViewById(R.id.text_wind_speed);
		tvWindDirection = findViewById(R.id.text_wind_direction);
		tvHumidity = findViewById(R.id.text_humidity);
		tvFun = findViewById(R.id.text_fun);
		btnUpdate = findViewById(R.id.btn_update);

		ActivityCompat.requestPermissions(MainActivity.this,
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				1);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 300, locationListener);
		final Integer[] i = {0};
		btnUpdate.setOnClickListener(view -> {
			i[0] += 1;
			Log.i("Click!", String.format("%s", i[0]));
//            Toast toast = Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT);
//            toast.show();
			try {
				double lat = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
				double lon = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
				Log.i("Lat", Double.toString(lat));
				Log.i("Long", Double.toString(lon));
				RemoteFetch remoteFetch = new RemoteFetch(lat, lon);
				remoteFetch.execute();
//				JSONObject json = remoteFetch.getJson();
//				json.getJSONObject("weather").getString("description");
//				Toast toast = Toast.makeText(MainActivity.this, json.getJSONObject("weather").getString("description"), Toast.LENGTH_SHORT);
//				toast.show();

			} catch (Exception e) {
				Toast toast = Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT);
				toast.show();

			}
		});
	}

	public LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(@NonNull Location location) {
//            showLocation(location);
		}
	};

//    private void showLocation(Location location) {
//        if (location == null) {
//           // throw new Exception("");
//        }
//        Log.i("Lat", Double.toString(location.getLatitude()));
//        Log.i("Long", Double.toString(location.getLongitude()));
//        remoteFetch.setLatitude(location.getLatitude());
//        remoteFetch.setLongitude(location.getLongitude());
//    }



	class RemoteFetch extends AsyncTask<Void, Void, JSONObject> {
		final static String LANG = "RU";
		final static String UNITS = "metric";
		final static String URL = "http://api.openweathermap.org/data/2.5/weather?";
		private final String APIKey = "YOUR_API_KEY";
		private double latitude;
		private double longitude;
		protected JSONObject json = null;
		int a;

		RemoteFetch(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

//	public void setLatitude(double latitude) {
//		this.latitude = latitude;
//	}
//
//	public void setLongitude(double longitude) {
//		this.longitude = longitude;
//	}

		@Override
		protected JSONObject doInBackground(Void... voids) {
			try {
				String path = String.format("%slat=%s&lon=%sl&ang=%s&units=%s&appid=%s", URL, latitude, longitude, LANG, UNITS, APIKey);

				path = "http://new-bokino.ru/test.json";
				java.net.URL url = new URL(path);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				if (urlConnection.getResponseCode() != 200)
					throw new IOException("Response code isn't 200");

				else {
					InputStream inputStream = urlConnection.getInputStream();
					byte[] buff = ByteStreams.toByteArray(inputStream);
					return new JSONObject(new String(buff));
				}

			} catch (Exception e) {
				Toast toast = Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT);
				toast.show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject json) {

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(Process.myPid());
		System.exit(0);
	}
}
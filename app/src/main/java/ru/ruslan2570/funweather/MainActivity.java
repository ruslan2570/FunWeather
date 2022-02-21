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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

	TextView tvDescription;
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

		tvDescription = findViewById(R.id.text_description);
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
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
			//startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 300, locationListener);
		final Integer[] i = {0};
		btnUpdate.setOnClickListener(view -> {
			i[0] += 1;
			Log.i("Click!", String.format("%s", i[0]));
			try {
				double lat = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
				double lon = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
				Log.i("Lat", Double.toString(lat));
				Log.i("Long", Double.toString(lon));
				RemoteFetch remoteFetch = new RemoteFetch(lat, lon);
				remoteFetch.execute();

			} catch (Exception e) {
				Toast toast = Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT);
				toast.show();

			}
		});
	}

	public LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(@NonNull Location location) {
		   showLocation(location);
		}
	};

    private void showLocation(Location location) {
        if (location == null) {
           // throw new Exception("");
        }
        Log.i("Lat", Double.toString(location.getLatitude()));
        Log.i("Long", Double.toString(location.getLongitude()));
    }


	class RemoteFetch extends AsyncTask<Void, Void, JSONObject> {
		final static String LANG = "RU";
		final static String UNITS = "metric";
		final static String URL = "http://api.openweathermap.org/data/2.5/weather?";
		private final String APIKey = "API_KEY";
		private final double latitude;
		private final double longitude;

		RemoteFetch(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		@Override
		protected JSONObject doInBackground(Void... voids) {
			try {
				String path = String.format("%slat=%s&lon=%s&lang=%s&units=%s&appid=%s", URL, latitude, longitude, LANG, UNITS, APIKey);
				Log.i("Path", path);
				//path = "http://new-bokino.ru/test.json";
				URL url = new URL(path);
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
			try {
				WeatherHandler weather = new WeatherHandler(json);
				tvDescription.setText(weather.getDescription());
				tvCityName.setText(weather.getCity());
				tvTemp.setText(weather.getTemp());
				tvTempFeels.setText(weather.getFeelsLike());
				tvWindSpeed.setText(weather.getWindSpeed());
				tvWindDirection.setText(weather.getWindDirection());
				tvHumidity.setText(weather.getHumidity());
				tvFun.setText(weather.getFun());
			} catch (JSONException e) {
				Toast toast = Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT);
				toast.show();
			}

		}
	}

	public class WeatherHandler {
		private String description;
		private String city;
		private double temp;
		private double feelsLike;
		private double windSpeed;
		private int windDirection;
		private int humidity;
		private JSONObject jsonObject = null;

		public WeatherHandler(JSONObject json) throws JSONException {
			description = json.getJSONArray("weather").getJSONObject(0).getString("description");
			city = json.getString("name");
			temp = json.getJSONObject("main").getDouble("temp");
			feelsLike = json.getJSONObject("main").getDouble("feels_like");
			windSpeed = json.getJSONObject("wind").getDouble("speed");
			windDirection = json.getJSONObject("wind").getInt("deg");
			humidity = json.getJSONObject("main").getInt("humidity");
		}

		public String getDescription() {
			return description;
		}

		public String getCity() {
			return getResources().getString(R.string.city_name) + " " + city;
		}

		public String getTemp() {
			return getResources().getString(R.string.temp) + " " + temp;
		}

		public String  getFeelsLike() {
			return getResources().getString(R.string.temp_feels) + " " + feelsLike;
		}

		public String getWindSpeed() {
			return getResources().getString(R.string.wind_speed) + " " + windSpeed;
		}

		public String getWindDirection() {
			String result = "";
			if(windDirection > 348 || windDirection < 12) result = getResources().getString(R.string.N);
			if(windDirection > 12 && windDirection < 78) result = getResources().getString(R.string.NE);
			if(windDirection > 78 && windDirection < 101) result = getResources().getString(R.string.E);
			if(windDirection > 101 && windDirection < 168) result = getResources().getString(R.string.SE);
			if(windDirection > 168 && windDirection < 214) result = getResources().getString(R.string.S);
			if(windDirection > 214 && windDirection < 259) result = getResources().getString(R.string.SW);
			if(windDirection > 259 && windDirection < 304) result = getResources().getString(R.string.W);
			if(windDirection > 304 && windDirection < 348) result = getResources().getString(R.string.NW);
			return  getResources().getString(R.string.wind_direction) + " " + result;
		}

		public String getHumidity() {
			return getResources().getString(R.string.humidity) + " " + humidity + "%";
		}

		public String getFun(){
			String[] bad = getResources().getStringArray(R.array.bad_weather);
			String[] good = getResources().getStringArray(R.array.good_weather);
			if(temp < 18) {
				int index = (new Random()).ints(0, bad.length).iterator().nextInt();
				return bad[index];
			}
			else{
				int index = (new Random()).ints(0, good.length).iterator().nextInt();
				return good[index];
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("onDestroy", "Bye!");
		android.os.Process.killProcess(Process.myPid());
		System.exit(0);
	}
}
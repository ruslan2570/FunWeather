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
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
    private RemoteFetch remoteFetch = new RemoteFetch();

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

        ApiIO apiio = new ApiIO(getFilesDir());

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 100, locationListener);
        final Integer[] i = {0};
        btnUpdate.setOnClickListener(view -> {
            i[0] += 1;
            Log.i("Click!", String.format("%s", i[0]));
            try {
                showLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try {
                remoteFetch.call();
            }
            catch (Exception e){
                e.printStackTrace();
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
            onDestroy();
            return;
        }
        Log.i("Lat", Double.toString(location.getLatitude()));
        Log.i("Long", Double.toString(location.getLongitude()));
        remoteFetch.setLatitude(location.getLatitude());
        remoteFetch.setLongitude(location.getLongitude());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
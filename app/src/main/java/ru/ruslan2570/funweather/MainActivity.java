package ru.ruslan2570.funweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    }
}
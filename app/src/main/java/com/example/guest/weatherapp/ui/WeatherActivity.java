package com.example.guest.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.guest.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherActivity extends AppCompatActivity {

    String cityName;

    public void findWeather(View view) {


        try {
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=482e2d21c007b79e42312c67d47cd01a");

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        cityName = extras.getString("cityName");
        Toast.makeText(getApplicationContext(), "City Name: " + cityName, Toast.LENGTH_LONG).show();

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i <arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != "") {
                        message += main + ": " + description + "\r\n";
                    }

                }

                if(message != "") {
//                    txtResult.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find the weather. Please try again.", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find the weather. Please try again.", Toast.LENGTH_LONG).show();

            }

        }
    }
}

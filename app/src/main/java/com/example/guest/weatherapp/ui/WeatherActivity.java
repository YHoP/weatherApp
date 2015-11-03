package com.example.guest.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import butterknife.Bind;
import butterknife.ButterKnife;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    @Bind(R.id.txtLocation) TextView txtLocation;
    @Bind(R.id.txtTemperature)TextView txtTemperature;
    @Bind(R.id.imgIcon) ImageView imgIcon;
    @Bind(R.id.txtDescription) TextView txtDescription;
    @Bind(R.id.txtHumidityValue) TextView txtHumidityValue;
    @Bind(R.id.txtPrecipValue) TextView txtPrecipValue;
    @Bind(R.id.imgRefresh) ImageView imgRefresh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        cityName = extras.getString("cityName");

        // txtLocation.setText(cityName);

        findWeather(cityName);

        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findWeather(cityName);

                imgRefresh.animate()
                        .rotationBy(3600f)
                        .setDuration(2000);

                txtTemperature.animate()
                        .translationXBy(1000f)
                        .translationYBy(1000f)
                        .rotationBy(3600f)
                        .setDuration(2000);

                txtHumidityValue.animate()
                        .translationZBy(-1000f)
                        .translationYBy(1000f)
                        .rotationBy(3600f)
                        .setDuration(2000);

                txtPrecipValue.animate()
                        .translationXBy(1000f)
                        .translationYBy(1000f)
                        .rotationBy(3600f)
                        .setDuration(2000);

            }
        });

    }




    public void findWeather(String cityName) {
        Toast.makeText(getApplicationContext(), "Name: " + cityName, Toast.LENGTH_LONG).show();

        try {
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=482e2d21c007b79e42312c67d47cd01a");

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

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

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather content", weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i <arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String description = jsonPart.getString("description");
                    txtDescription.setText(description);


                    String iconID = jsonPart.getString("icon");
//                    Toast.makeText(getApplicationContext(), "icon text?: " + iconID, Toast.LENGTH_LONG).show();
                    Context context = imgIcon.getContext();
                    int imageResource = context.getResources().getIdentifier("img"+iconID, "drawable", context.getPackageName());
                    imgIcon.setImageResource(imageResource);
                }

                String jsonName = jsonObject.getString("name");
                txtLocation.setText(jsonName);

                JSONObject mainInfo = jsonObject.getJSONObject("main");
                String temp = mainInfo.getString("temp");
                String humid = mainInfo.getString("humidity");

                double kelvin = Double.parseDouble(temp);
                double tempF = (kelvin - 273.15)* 1.8000 + 32.00;
                Log.i("temp content", Math.round(tempF) + "");
                txtTemperature.setText(Math.round(tempF) + "");
                txtHumidityValue.setText(humid + "%");

               try{
                   JSONObject snowInfo = jsonObject.getJSONObject("snow");
                   getPrecip(snowInfo);
               }catch (JSONException e){
                   try{
                       JSONObject rainInfo = jsonObject.getJSONObject("rain");
                       getPrecip(rainInfo);
                   }catch (JSONException d){
                       txtPrecipValue.setText("0\"");
                   }

               }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find the weather. Please try again.", Toast.LENGTH_LONG).show();

            }

        }
    }

    private void getPrecip(JSONObject precip) {
        String txtPrecip = null;
        try {
            txtPrecip = precip.getString("3h");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        double doublePrecip = Double.parseDouble(txtPrecip);
        txtPrecipValue.setText(Math.round(doublePrecip) + "\"");

    }
}

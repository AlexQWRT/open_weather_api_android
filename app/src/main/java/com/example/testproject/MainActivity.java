package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import android.content.pm.ActivityInfo;

public class MainActivity extends AppCompatActivity {

    private Button btn_search;
    private EditText town_edit;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        btn_search = (Button)findViewById(R.id.search);
        town_edit = (EditText)findViewById(R.id.town_edit);
        result = (TextView)findViewById(R.id.weather_output);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (town_edit.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.empty_field, Toast.LENGTH_SHORT).show();
                else {
                    String town = town_edit.getText().toString();
                    String api_key = "******************************"; //insert your api key there
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + town + "&appid="
                            + api_key + "&units=metric&lang=ru";
                    new GetData().execute(url);

                }
            }
        });
    }

    private class GetData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result.setText(R.string.downloading);
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connect = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connect = (HttpsURLConnection)url.openConnection();
                connect.connect();
                InputStream stream = connect.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer output = new StringBuffer();
                String read_line = "";
                while((read_line = reader.readLine()) != null)
                    output.append(read_line).append("\n");
                return output.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connect != null)
                    connect.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                }
                catch (IOException e) {
                        e.printStackTrace();
                }
            }
            return null;
        }
        protected String temp = getResources().getString(R.string.temp);
        protected String temp_feels = getResources().getString(R.string.temp_feels);
        protected String pressure = getResources().getString(R.string.pressure);
        protected String wind = getResources().getString(R.string.wind_speed);
        protected String description = getResources().getString(R.string.description);
        protected String meters_per_sec = getResources().getString(R.string.speed);
        protected String pasc = getResources().getString(R.string.pascals);

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String output) {
            super.onPostExecute(output);
            if (output == null) {
                Toast.makeText(MainActivity.this, R.string.nothing_was_found, Toast.LENGTH_SHORT).show();
                result.setText("......");
            }
            else {
                try {
                    JSONObject object = new JSONObject(output);
                    result.setText(object.getJSONArray("weather").getJSONObject(0).getString("description"));
                    result.setText(new StringBuilder().append(temp).append(' ').append(object.getJSONObject("main").getDouble("temp"))
                            .append("°С\n").append(temp_feels).append(' ').append(object.getJSONObject("main").getDouble("feels_like"))
                            .append("°C\n").append(wind).append(' ').append(object.getJSONObject("wind").getDouble("speed")).append(' ')
                            .append(meters_per_sec).append('\n').append(pressure).append(' ').append((double)object.getJSONObject("main").getInt("pressure") / 1000).append(' ')
                            .append(pasc).append('\n').append(description).append(' ').append(object.getJSONArray("weather").getJSONObject(0).getString("description")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

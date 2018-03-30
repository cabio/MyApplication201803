package my.com.trainingcodeplay.weatherapp;

import android.app.VoiceInteractor;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation, tvTemperature, tvHumidity, tvWindSpeed, tvCloudiness;
    private Button btnRefresh;
    private ImageView ivIcon;

    private static final String WEATHER_SOURCE = "http://api.openweathermap.org/data/2.5/weather?APPID=82445b6c96b99bc3ffb78a4c0e17fca5&mode=json&id=1735161";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = (TextView) findViewById (R.id.location);
        tvTemperature = (TextView) findViewById(R.id.temperature);
        tvHumidity = (TextView) findViewById(R.id.humidity);
        tvWindSpeed = (TextView) findViewById(R.id.wind_speed);
        tvCloudiness = (TextView) findViewById(R.id.cloudiness);
        btnRefresh = (Button) findViewById(R.id.button_refresh);
        ivIcon = (ImageView) findViewById(R.id.icon);

        btnRefresh.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new WeatherDataRetrival().execute();
                requestViaVolley();
            }
        });

    }

    private class WeatherDataRetrival extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground (Void... arg0) {
            NetworkInfo networkInfo = ((ConnectivityManager) MainActivity.this
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                //network-connected
                URL url = null;
                try {
                    url = new URL(WEATHER_SOURCE);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setChunkedStreamingMode(15000);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        if (bufferedReader != null ) {
                            String readline;
                            StringBuffer stringBuffer = new StringBuffer();
                            while ((readline=bufferedReader.readLine()) != null) {
                              stringBuffer.append(readline);
                            }
                            return stringBuffer.toString();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }




            } else {
                // no connection
            }



            return null;
        }

        @Override
        protected void onPostExecute (String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    final JSONObject weatherJSON = new JSONObject(result);

                    tvLocation.setText(weatherJSON.getString("name") + ", " + weatherJSON.getJSONObject("sys").getString("country"));
                    tvWindSpeed.setText(String.valueOf(weatherJSON.getJSONObject("wind").getDouble("speed")) + " mps");
                    tvCloudiness.setText(String.valueOf(weatherJSON.getJSONObject("clouds").getInt("all")) + " %");
                    final JSONObject mainJSON = weatherJSON.getJSONObject("main");
                    tvTemperature.setText(String.valueOf(mainJSON.getDouble("temp")));
                    tvHumidity.setText(String.valueOf(mainJSON.getInt("humidity"))+ "%");

                    final JSONArray weatherJSONArray = weatherJSON.getJSONArray("weather");
                    if (weatherJSONArray.length() > 0) {
                        int code = weatherJSONArray.getJSONObject(0).getInt("id");
                        ivIcon.setImageResource(getIcon(code));
                    }
                }catch (Exception e){}
            }
        }
    }

    public int getIcon(int code){
        return inBetween(code);
    }


    public int inBetween(int num1){

        if(num1 >= 200 && num1 <=232){
            return R.drawable.ic_thunderstorm_large;
        }else if(num1 >= 300 && num1 <= 321){
            return R.drawable.ic_drizzle_large;
        }else if(num1>= 500 && num1 < 531){
            return R.drawable.ic_rain_large;
        }else if(num1>= 600 && num1 < 622){
            return R.drawable.ic_snow_large;
        }else if(num1 == 800){
            return R.drawable.ic_day_few_clouds_large;
        }else if(num1 == 802){
            return R.drawable.ic_scattered_clouds_large;
        }else if(num1>= 803 && num1 < 804){
            return R.drawable.ic_broken_clouds_large;
        }else if(num1>= 701 && num1 < 762){
            return R.drawable.ic_fog_large;
        }else if(num1 == 781 || num1 == 900){
            return R.drawable.ic_tornado_large;
        }else if(num1 == 905){
            return R.drawable.ic_windy_large;
        }else if(num1 == 906){
            return R.drawable.ic_hail_large;
        }
        else{
            return 802;
        }
    }

    private void requestViaVolley() {
        RequestQueue queue = Volley.newRequestQueue (this);
        StringRequest stringRequest = new StringRequest (
                Request.Method.GET, WEATHER_SOURCE, new Response.Listener <String>() {
            @Override
            public void onResponse (String response) {
                try {
                    final JSONObject weatherJSON = new JSONObject(response);

                    tvLocation.setText(weatherJSON.getString("name") + ", " + weatherJSON.getJSONObject("sys").getString("country"));
                    tvWindSpeed.setText(String.valueOf(weatherJSON.getJSONObject("wind").getDouble("speed")) + " mps");
                    tvCloudiness.setText(String.valueOf(weatherJSON.getJSONObject("clouds").getInt("all")) + " %");
                    final JSONObject mainJSON = weatherJSON.getJSONObject("main");
                    tvTemperature.setText(String.valueOf(mainJSON.getDouble("temp")));
                    tvHumidity.setText(String.valueOf(mainJSON.getInt("humidity"))+ "%");

                    final JSONArray weatherJSONArray = weatherJSON.getJSONArray("weather");
                    if (weatherJSONArray.length() > 0) {
                        int code = weatherJSONArray.getJSONObject(0).getInt("id");
                        ivIcon.setImageResource(getIcon(code));
                    }
                }catch (Exception e){}
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

}

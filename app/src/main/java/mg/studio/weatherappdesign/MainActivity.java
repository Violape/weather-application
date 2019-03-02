package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadUpdate().execute();
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
        Toast.makeText(MainActivity.this, "Update Complete!", Toast.LENGTH_SHORT).show();
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://api.openweathermap.org/data/2.5/forecast?lat=32&lon=121&appid=230f54f3fc70fa7efda8d4b4e265b37e&lang=zh_cn&units=metric";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            try {
                URL url = new URL(stringUrl);
                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] temp = new String[5];
                    String[] weat = new String[5];
                    String[] date = new String[5];
                    String loca = "Unknown Location";
                    String linel = line;
                    for(int i=0; i<5; i++){
                        linel = linel.substring(linel.indexOf("temp") + 6);
                        temp[i] = String.valueOf((int)(Double.valueOf(linel.substring(0, linel.indexOf(","))) + 0.5));
                        linel = linel.substring(linel.indexOf("main") + 7);
                        weat[i] = linel.substring(0, linel.indexOf('"'));
                        linel = linel.substring(linel.indexOf("dt_txt") + 9);
                        date[i] = linel.substring(0, 10);
                        for(int j=0; j<7; j++)
                            linel = linel.substring(linel.indexOf("dt_txt") + 9);
                    }
                    linel = linel.substring(linel.indexOf("name") + 7);
                    loca = linel.substring(0, linel.indexOf('"'));
                    linel = linel.substring(linel.indexOf("country") + 10);
                    loca += "," + linel.substring(0, linel.indexOf('"'));
                    String result = "";
                    for(int i=0; i<5; i++)
                        result += date[i] + "," + weat[i] + "," + temp[i] + ",";
                    result += loca;
                    buffer.append(result+"\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String msg) {
            //Update the temperature displayed
            String[] items = msg.split(",");
            String[] weeklong = {"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
            String[] weekshort = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
            ((TextView) findViewById(R.id.today)).setText(weeklong[getDayofWeek(items[0])]);
            checkWeatherBitmap(items[1], R.id.img_weather_condition);
            ((TextView) findViewById(R.id.tv_location)).setText(items[15]+", "+items[16]);
            ((TextView) findViewById(R.id.tv_date)).setText(items[0]);
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(items[2]);
            checkWeatherBitmap(items[4], R.id.img_weather_condition2);
            ((TextView) findViewById(R.id.day2)).setText(weekshort[getDayofWeek(items[3])]);
            checkWeatherBitmap(items[7], R.id.img_weather_condition3);
            ((TextView) findViewById(R.id.day3)).setText(weekshort[getDayofWeek(items[6])]);
            checkWeatherBitmap(items[10], R.id.img_weather_condition4);
            ((TextView) findViewById(R.id.day4)).setText(weekshort[getDayofWeek(items[9])]);
            checkWeatherBitmap(items[13], R.id.img_weather_condition5);
            ((TextView) findViewById(R.id.day5)).setText(weekshort[getDayofWeek(items[12])]);
        }

        protected void checkWeatherBitmap(String msg, int targetid){
            if (msg.equals("Clear"))
                ((ImageView)findViewById(targetid)).setImageResource(R.drawable.sunny_small);
            else if (msg.equals("Clouds"))
                ((ImageView)findViewById(targetid)).setImageResource(R.drawable.partly_sunny_small);
            else if (msg.equals("Rain"))
                ((ImageView)findViewById(targetid)).setImageResource(R.drawable.rainy_small);
            else
                ((ImageView)findViewById(targetid)).setImageResource(R.drawable.notification);
        }

        private int getDayofWeek(String dateTime) {
            Calendar cal = Calendar.getInstance();
            if (dateTime.equals("")) {
                cal.setTime(new Date(System.currentTimeMillis()));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date;
                try {
                    date = sdf.parse(dateTime);
                } catch (ParseException e) {
                    date = null;
                    e.printStackTrace();
                }
                if (date != null) {
                    cal.setTime(new Date(date.getTime()));
                }
            }
            return cal.get(Calendar.DAY_OF_WEEK)-1;
        }
    }
}

package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


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
            String stringUrl = "http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&appid=230f54f3fc70fa7efda8d4b4e265b37e&lang=zh_cn&units=metric";
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
        }
    }
}

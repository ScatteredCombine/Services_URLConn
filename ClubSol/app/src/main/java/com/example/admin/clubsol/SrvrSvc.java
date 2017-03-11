package com.example.admin.clubsol;

/**
 * Created by ADMIN on 04-02-2017.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.util.Log;

public class SrvrSvc extends Service {
int rssi,level,percentage;
    String name,addr,pc,link,data;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }
    @Override
    public void onStart(Intent intent, int startid) {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifi != null && wifi.isWifiEnabled()) {
            //The code
            rssi = wifi.getConnectionInfo().getRssi(); //rssi ante signal strength in decibels
            name = wifi.getConnectionInfo().getSSID(); //ssid ante connect ayina network peru
            addr = wifi.getConnectionInfo().getBSSID(); //idi emo mac address
            level = WifiManager.calculateSignalLevel(rssi, 100);  //to convert rssi on a scale of 1 to 100
            percentage = (int) ((level / 100.0) * 100);
            pc=Integer.toString(percentage);
        }
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        new ConnectSrv().execute("send");

    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

    }

    private class ConnectSrv extends AsyncTask<String,String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("OnPreExecute()", "");
        }

        @Override
        protected String doInBackground(String... arg0) {
            Log.i("doInBackground()", "");
            try {
                link = "http://192.168.43.216:80/clubsol.php";
                data = URLEncoder.encode("name", "UTF-8") + "=" +
                        URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("mac", "UTF-8") + "=" +
                        URLEncoder.encode(addr, "UTF-8");
                data += "&" + URLEncoder.encode("percentage", "UTF-8") + "=" +
                        URLEncoder.encode(pc, "UTF-8");
                URL url = new URL(link);
                System.out.println("--------------1----------------");
                URLConnection conn = url.openConnection();
                System.out.println("--------------2----------------");
                conn.setDoOutput(true);
                System.out.println("--------------3----------------");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                System.out.println("--------------4----------------");
                wr.write(data);
                System.out.println("--------------5----------------");
            }
            catch(Exception e){
                System.out.println("Exception: " + e.getMessage());
                System.out.println("--------------6----------------");
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            Log.i("OnPostExecute()", "");
        }

    }
}

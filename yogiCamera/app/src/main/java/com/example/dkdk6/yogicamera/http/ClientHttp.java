package com.example.dkdk6.yogicamera.http;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dkdk6.yogicamera.Model.NearBySearchResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ClientHttp extends AsyncTask<String, Void, String> {
    private String res;
    private ArrayList<String> result;
    NearBySearchResult resultObject;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    public String doInBackground(String... params) {
        try {
            Log.v("dkdkdkdkdkdk","test2 "+ params[0]);
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+params[0]+"&radius=5000&type=park&key=AIzaSyDx53WZsD3RAvLA7wTeD4WqYK4IhQojuVs";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            byte[] outputInBytes = "".getBytes();
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();
            int retCode = conn.getResponseCode();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = br.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            br.close();
            res = response.toString();
            resultObject = new Gson().fromJson(res,NearBySearchResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.v("onPostExcute","testing"+s);

    }


}

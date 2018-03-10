package ic.lab.incrediblehome;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aic on 10/02/18.
 */

public class HTTPAsyncPWM extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;

    private Context context;

    public HTTPAsyncPWM(Context context){
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context,
                "ProgressDialog",
                "L o a d i n g . . .");
    }

    @Override
    protected String doInBackground(String... data) {
        //String url = "https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control";
        String url = data[0];
        String message;
        JSONObject jsonControl = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put(data[1], data[2]);
            jsonData.put("frequency", "50");
            jsonData.put("count", "5000");
            jsonControl.put("controls", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Test", jsonControl.toString());
        return sendHTTPData(url, jsonControl);
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
    }

    private String sendHTTPData(String urlpath, JSONObject json) {
        HttpURLConnection connection = null;
        try {
            URL url=new URL(urlpath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("authorization", "Bearer MzQwNzg5ODIyOC40ODgxMTM6");
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(json.toString());
            streamWriter.flush();
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response + "\n");
                }
                bufferedReader.close();

                Log.d("test", stringBuilder.toString());
                return stringBuilder.toString();
            } else {
                Log.e("test", connection.getResponseMessage());
                return null;
            }
        } catch (Exception exception){
            Log.e("test", exception.toString());
            return null;
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }
}


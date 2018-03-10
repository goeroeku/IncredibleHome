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

import static ic.lab.incrediblehome.BuildConfig.TOKEN_API;

/**
 * Created by aic on 10/02/18.
 */

public class HTTPAsyncGPIO extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;

    private Context context;

    public HTTPAsyncGPIO(Context context){
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
        String url = data[0];
        JSONObject jsonControl = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put(data[1], data[2]);
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
            connection.setRequestProperty("authorization", "Bearer " + TOKEN_API);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(json.toString());
            streamWriter.flush();
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response;
                while ((response = bufferedReader.readLine()).equals("")) {
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


package ic.lab.incrediblehome;

import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageView ivAula = null, ivLampuDepanKiri = null, ivLampuDepanKanan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivAula = findViewById(R.id.ivAula);
        ivLampuDepanKiri = findViewById(R.id.ivLampuDepanKiri);
        ivLampuDepanKanan = findViewById(R.id.ivLampuDepanKanan);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //cek status lampu
        cekStatus();

        //cek realtime status lampu
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cekStatus();
                Log.d("MainActivity", "runDelay");
            }
        }, 3000);
    }

    public void cekStatus() {
        new DownloadWebpageTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                getStatus(object);
            }
        }).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/data");
    }

    public void getStatus(JSONObject object) {
        try {
            //JSONObject table = new JSONObject(jsonResponse);
            JSONObject rows = object.getJSONObject("data");
            JSONObject result = rows.getJSONObject("result");
            String pin_4= result.getString("4");
            String pin_5 = result.getString("5");
            Log.d("coba", pin_5 + " dan " + pin_4);
            if (pin_4.equals("0")) {
                ivAula.setImageResource(R.drawable.telkom_white_open);
            } else if (pin_4.equals("1")) {
                ivAula.setImageResource(R.drawable.telkom_dark_close);
            }
            if (pin_5.equals("0")) {
                ivLampuDepanKiri.setImageResource(R.drawable.murub);
                ivLampuDepanKanan.setImageResource(R.drawable.murub);
            } else if (pin_5.equals("1")) {
                ivLampuDepanKiri.setImageResource(R.drawable.matio);
                ivLampuDepanKanan.setImageResource(R.drawable.matio);
            } else {
                Toast.makeText(getBaseContext(), "Pembacaan status gagal...", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //untuk menampilkan suara
                    //Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
                    String suara = result.get(0).toString().toLowerCase().trim();
                    if (suara.contains("lampu depan hidup")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "5", "0");
                    } else if (suara.contains("lampu depan mati")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "5", "1");
                    } else if (suara.contains("lampu dalam hidup")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "4", "0");
                    } else if (suara.contains("lampu dalam mati")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "4", "1");
                    } else if (suara.contains("buka pintu depan")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "14", "5");
                    } else if (suara.contains("tutup pintu depan")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "14", "10");
                    } else if (suara.contains("buka pintu samping")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "12", "5");
                    } else if (suara.contains("tutup pintu samping")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "12", "10");
                    } else if (suara.contains("saatnya tidur")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "14", "10");
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "12", "10");
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "5", "0");
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "4", "1");
                    } else if (suara.contains("ada tamu")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "14", "5");
                    } else if (suara.contains("cuaca panas")) {
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "14", "5");
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "12", "5");
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "5", "1");
                        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "4", "1");
                    } else {
                        Toast.makeText(getBaseContext(), "Perintah " + suara + " tidak dikenali", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void setLampu(String pin, String status) {
        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", pin, status);
        if (pin.equals("4") && status.equals("0")) {
            ivAula.setImageResource(R.drawable.telkom_white_open);
        } else if (pin.equals("4") && status.equals("1")) {
            ivAula.setImageResource(R.drawable.telkom_dark_close);
        }
        if (pin.equals("5") && status.equals("0")) {
            ivLampuDepanKiri.setImageResource(R.drawable.murub);
            ivLampuDepanKanan.setImageResource(R.drawable.murub);
        } else if (pin.equals("5") && status.equals("1")) {
            ivLampuDepanKiri.setImageResource(R.drawable.matio);
            ivLampuDepanKanan.setImageResource(R.drawable.matio);
        }
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Suara kamu tidak jelas!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void lampuDepanOn(View v) {
        //new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "5", "0");
        setLampu("5", "0");
    }

    protected void lampuDepanOff(View v) {
        //new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "5", "1");
        setLampu("5", "1");
    }

    protected void lampuDalamOn(View v) {
        //new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "4", "0");
        setLampu("4", "0");
    }

    protected void lampuDalamOff(View v) {
        //new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/gpio/control", "4", "1");
        setLampu("4", "1");
    }

    protected void pintuDepanOn(View v) {
        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "14", "5"); //pin d5 = 14, 5 posisi sudut 45 - bukak
    }

    protected void pintuDepanOff(View v) {
        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "14", "10"); //pin d5 = 14, 10 posisi sudut 180 - nutup
    }

    protected void pintuSampingOn(View v) {
        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "12", "5"); //pin d6 = 12
    }

    protected void pintuSampingOff(View v) {
        new HTTPAsyncGPIO(this).execute("https://api.arkademy.com:8443/v0/arkana/device/IO/aioti/pwm/control", "12", "10");
    }
}

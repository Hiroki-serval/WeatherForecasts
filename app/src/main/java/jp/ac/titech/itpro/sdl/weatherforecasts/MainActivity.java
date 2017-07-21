package jp.ac.titech.itpro.sdl.weatherforecasts;

import android.app.Notification;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.content.Intent;
import java.util.Calendar;
import android.app.AlarmManager;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private TextView location;
    private TextView forecast;
    private LinearLayout forecastsLayout;
    private ProgressBar progress;
    private TextView weather;

    public class ApiTask extends GetWeatherForecastTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(WeatherForecast data) {
            super.onPostExecute(data);

            progress.setVisibility(View.GONE);

            if (data != null) {
                int i;
                i = 0;
                location.setText("天気通知");
                forecast.setText("天気予報");
                for (WeatherForecast.Forecast forecast : data.forecastList) {
                    if (i != 2) {
                        View row = View.inflate(MainActivity.this, R.layout.forecast_row, null);

                        TextView date = (TextView) row.findViewById(R.id.tv_date);
                        date.setText("☀︎" + data.location.city +"の" + forecast.dateLabel + "のお天気☀︎");

                        TextView telop = (TextView) row.findViewById(R.id.tv_telop);
                        telop.setText(forecast.telop);
                        forecastsLayout.addView(row);
                        i = i + 1;
                    }

                    if (i == 0) {
                        weather.setText(forecast.telop);
                    }
                }
            } else if (exception != null) {
                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR, 8);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);

            if (weather.getText().toString().contains("雨") == true) {
                Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver2.class);
                intent.putExtra("intentId", 2);
                PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 2, intent, 0);

                AlarmManager am = (AlarmManager)MainActivity.this.getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
                Toast.makeText(getApplicationContext(), "Weather Forecasts", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                intent.putExtra("intentId", 2);
                PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 2, intent, 0);

                AlarmManager am = (AlarmManager)MainActivity.this.getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
                Toast.makeText(getApplicationContext(), "Weather Forecasts", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = (TextView) findViewById(R.id.tv_location);
        forecast = (TextView) findViewById(R.id.tv_forecast);
        forecastsLayout = (LinearLayout) findViewById(R.id.ll_forecasts);
        progress = (ProgressBar) findViewById(R.id.progress);

        Button btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(this);

        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(this);

        new ApiTask().execute("130010");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button1) {
            sendNotification1();
        }
        if(v.getId() == R.id.button2) {
            sendNotification2();
        }
    }

    private void sendNotification1() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.droid2)
                .setTicker("お天気通知")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("気をつけて行ってらっしゃい")
                .setContentText("やったね！雨は降りません！")
                .build();
        notificationManager.cancelAll();
        notificationManager.notify(1, notification);
    }

    private void sendNotification2() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.droid2)
                .setTicker("雨通知")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("傘を持って行きましょう")
                .setContentText("恵みの雨ですorz")
                .build();
        notificationManager.cancelAll();
        notificationManager.notify(1, notification);
    }

}

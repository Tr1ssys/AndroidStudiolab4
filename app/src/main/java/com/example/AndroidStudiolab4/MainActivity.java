package com.example.AndroidStudiolab4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TextView;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // View
    CalendarView calendar;
    TextView textDay;

    // Все связанное со временем
    Calendar currentDate;
    Calendar changeDate;

    // Widget
    AppWidgetManager appWidgetManager;
    int appWidgetIds[];

    // Alarm
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Intent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Создаем и настраиваем alarm
        alarmIntent = new Intent(this, NotificationBuilder.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Получаем информацию и ссылки на widget
        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetIds = appWidgetManager.getAppWidgetIds(
                                        new ComponentName
                                            (this.getApplicationContext().getPackageName(),
                                                    NewAppWidgetProvider.class.getName()
                                            )
                                        );


        // Находим View
        calendar = (CalendarView) findViewById(R.id.calendarView2);
        textDay = (TextView) findViewById(R.id.textView2);

        // Создаем календари
        currentDate = Calendar.getInstance();
        changeDate = Calendar.getInstance();

        // Инициализируем текущее время
        currentDate.setTimeInMillis(calendar.getDate());

        // Устанавливаем минимальную дату для выбора
        calendar.setMinDate(calendar.getDate());

        // Устанавливаем слушателя на выбор даты в календаре
        calendar.setOnDateChangeListener(new OnDateChangeListener(){
            // Описываем метод выбора даты в календаре:
            @Override
            public void onSelectedDayChange(CalendarView view, int year,int month, int dayOfMonth) {

                // Вычисляем разницу в днях
                changeDate.set(year, month, dayOfMonth);
                long millisecond = changeDate.getTimeInMillis() - currentDate.getTimeInMillis();
                int days = (int)(millisecond/(24*60*60*1000));

                // Устанавливаем значение в View
                textDay.setText("Дней до события: "+days);

                // Передаем информацию в Widget
                NewAppWidgetProvider.info(days);
                NewAppWidgetProvider.update(MainActivity.this, appWidgetManager, appWidgetIds);

                // Включаем alarm, сработает через 5 секунд после нажатия
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

            }});
    }
}

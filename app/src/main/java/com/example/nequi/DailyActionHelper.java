package com.example.nequi;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyActionHelper {
    private static final String PREFS_NAME = "DailyActionPrefs";
    private static final String LAST_ACTION_DATE_KEY = "lastActionDate";

    public static boolean shouldPerformAction(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Obtener la fecha almacenada en las preferencias
        long lastActionDate = prefs.getLong(LAST_ACTION_DATE_KEY, 0L);

        // Comparar las fechas
        if (isSameDay(lastActionDate, currentDate.getTime())) {
            // La acción ya se realizó hoy
            return false;
        } else {
            // Realizar la acción y guardar la nueva fecha en las preferencias
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(LAST_ACTION_DATE_KEY, currentDate.getTime());
            editor.apply();
            return true;
        }
    }

    private static boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(timestamp2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
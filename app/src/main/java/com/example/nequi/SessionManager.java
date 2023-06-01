package com.example.nequi;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private String pref_name="session";
    private String key_usuario="telefono";
    private String KEY_LOGGED_IN= "loggedIn";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String user) {
        editor.putString(key_usuario, user);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public String getPhone() {
        return sharedPreferences.getString("telefono", "");
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.remove(key_usuario);
        editor.remove(KEY_LOGGED_IN);
        editor.apply();
    }
}


/*  SessionManager sessionManager = new SessionManager(getApplicationContext());
if (!sessionManager.isLoggedIn()) {
    // El usuario no ha iniciado sesión, redirigir a la actividad de inicio de sesión
    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
    finish();
} else {
    // El usuario ha iniciado sesión, puedes continuar con la lógica de la actividad principal
    String username = sessionManager.getUsername();
    // ...
}
*/
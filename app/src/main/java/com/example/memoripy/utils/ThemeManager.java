package com.example.memoripy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.memoripy.R;

/**
 * Clase para gestionar los temas de la aplicación
 */
public class ThemeManager {

    private static final String TAG = "ThemeManager";
    private static final String PREF_NAME = "theme_preferences";
    private static final String KEY_THEME = "selected_theme";

    // Temas disponibles
    public static final String THEME_GUINDA = "guinda"; // IPN
    public static final String THEME_AZUL = "azul";     // ESCOM

    private static ThemeManager instance;
    private final SharedPreferences preferences;

    private ThemeManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Inicializa el tema al iniciar la aplicación
     */
    public void initializeTheme() {
        // Configurar el modo (claro/oscuro) según el sistema
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            // Mantener la configuración del sistema
            Log.d(TAG, "Usando el modo del sistema");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            Log.d(TAG, "Configurando para seguir el modo del sistema");
        }
    }

    /**
     * Obtiene el tema seleccionado actualmente
     */
    public String getCurrentTheme() {
        return preferences.getString(KEY_THEME, THEME_GUINDA); // Por defecto tema Guinda (IPN)
    }

    /**
     * Establece un nuevo tema
     */
    public void setTheme(String themeName) {
        preferences.edit().putString(KEY_THEME, themeName).apply();
    }

    /**
     * Comprueba si el modo oscuro está activo
     */
    public boolean isDarkModeActive(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Aplica el tema actual a una actividad
     */
    public void applyTheme(Context context) {
        String currentTheme = getCurrentTheme();
        boolean isDarkMode = isDarkModeActive(context);

        // Aquí se configura el tema basado en el valor actual
        if (THEME_GUINDA.equals(currentTheme)) {
            if (isDarkMode) {
                context.setTheme(R.style.Theme_MemorIPN_Guinda_Dark);
            } else {
                context.setTheme(R.style.Theme_MemorIPN_Guinda_Light);
            }
            Log.d(TAG, "Aplicando tema Guinda - " + (isDarkMode ? "Oscuro" : "Claro"));
        } else if (THEME_AZUL.equals(currentTheme)) {
            if (isDarkMode) {
                context.setTheme(R.style.Theme_MemorIPN_Azul_Dark);
            } else {
                context.setTheme(R.style.Theme_MemorIPN_Azul_Light);
            }
            Log.d(TAG, "Aplicando tema Azul - " + (isDarkMode ? "Oscuro" : "Claro"));
        } else {
            // Si por alguna razón no hay un tema válido, usar el tema por defecto
            Log.w(TAG, "Tema no reconocido: " + currentTheme + ", usando tema por defecto (Guinda)");
            if (isDarkMode) {
                context.setTheme(R.style.Theme_MemorIPN_Guinda_Dark);
            } else {
                context.setTheme(R.style.Theme_MemorIPN_Guinda_Light);
            }
        }
    }
}
package com.example.memoripy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoripy.utils.FileManager;
import com.example.memoripy.utils.ThemeManager;

/**
 * Actividad para la configuración del juego
 */
public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgTheme;
    private RadioButton rbThemeGuinda;
    private RadioButton rbThemeAzul;
    private Switch switchSound;
    private RadioGroup rgSaveFormat;
    private RadioButton rbFormatTxt;
    private RadioButton rbFormatXml;
    private RadioButton rbFormatJson;
    private Button btnSaveSettings;

    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema antes de inflar la vista
        themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inicializar vistas
        initializeViews();

        // Cargar configuración actual
        loadCurrentSettings();

        // Configurar eventos
        setupEvents();
    }

    /**
     * Inicializa las vistas de la actividad
     */
    private void initializeViews() {
        rgTheme = findViewById(R.id.rgTheme);
        rbThemeGuinda = findViewById(R.id.rbThemeGuinda);
        rbThemeAzul = findViewById(R.id.rbThemeAzul);
        switchSound = findViewById(R.id.switchSound);
        rgSaveFormat = findViewById(R.id.rgSaveFormat);
        rbFormatTxt = findViewById(R.id.rbFormatTxt);
        rbFormatXml = findViewById(R.id.rbFormatXml);
        rbFormatJson = findViewById(R.id.rbFormatJson);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
    }

    /**
     * Carga la configuración actual
     */
    private void loadCurrentSettings() {
        // Cargar tema
        String currentTheme = themeManager.getCurrentTheme();
        if (ThemeManager.THEME_GUINDA.equals(currentTheme)) {
            rbThemeGuinda.setChecked(true);
        } else if (ThemeManager.THEME_AZUL.equals(currentTheme)) {
            rbThemeAzul.setChecked(true);
        }

        // Cargar formato de guardado predeterminado (en una app real, esto se guardaría en SharedPreferences)
        // Por defecto usamos JSON
        rbFormatJson.setChecked(true);

        // Cargar configuración de sonido (en una app real, esto se guardaría en SharedPreferences)
        // Por defecto, sonido activado
        switchSound.setChecked(true);
    }

    /**
     * Configura los eventos de la interfaz
     */
    private void setupEvents() {
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    /**
     * Guarda la configuración seleccionada
     */
    private void saveSettings() {
        // Guardar tema
        int themeId = rgTheme.getCheckedRadioButtonId();
        String selectedTheme;
        if (themeId == R.id.rbThemeGuinda) {
            selectedTheme = ThemeManager.THEME_GUINDA;
        } else {
            selectedTheme = ThemeManager.THEME_AZUL;
        }
        themeManager.setTheme(selectedTheme);

        // Guardar formato de archivo predeterminado
        // En una implementación real, esto se guardaría en SharedPreferences
        int formatId = rgSaveFormat.getCheckedRadioButtonId();
        String selectedFormat;
        if (formatId == R.id.rbFormatTxt) {
            selectedFormat = FileManager.FORMAT_TXT;
        } else if (formatId == R.id.rbFormatXml) {
            selectedFormat = FileManager.FORMAT_XML;
        } else {
            selectedFormat = FileManager.FORMAT_JSON;
        }

        // Guardar configuración de sonido
        // En una implementación real, esto se guardaría en SharedPreferences
        boolean soundEnabled = switchSound.isChecked();

        // Mostrar mensaje de confirmación
        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();

        // Para que el cambio de tema sea visible, recreamos la actividad
        recreate();
    }
}
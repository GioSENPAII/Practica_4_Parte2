package com.example.memoripy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoripy.utils.ThemeManager;

/**
 * Actividad principal con el menú del juego
 */
public class MainActivity extends AppCompatActivity {

    private ThemeManager themeManager;
    private TextView tvTitle;
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema antes de inflar la vista
        themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar elementos de UI
        tvTitle = findViewById(R.id.tvTitle);
        imgLogo = findViewById(R.id.imgLogo);

        // Inicializar botones y eventos
        setupButtons();

        // Inicializar el tema
        themeManager.initializeTheme();

        // Actualizar elementos visuales según el tema
        updateThemeUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar si el tema ha cambiado
        updateThemeUI();
    }

    /**
     * Actualiza los elementos visuales según el tema
     */
    private void updateThemeUI() {
        String currentTheme = themeManager.getCurrentTheme();

        // Si se necesitan ajustes específicos para cada tema, se pueden hacer aquí
        if (ThemeManager.THEME_GUINDA.equals(currentTheme)) {
            // Configuración específica para tema Guinda (opcional)
        } else if (ThemeManager.THEME_AZUL.equals(currentTheme)) {
            // Configuración específica para tema Azul (opcional)
        }
    }

    private void setupButtons() {
        // Botón Nuevo Juego
        Button btnNewGame = findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        // Botón Cargar Partida
        Button btnLoadGame = findViewById(R.id.btnLoadGame);
        btnLoadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGame();
            }
        });

        // Botón Configuración
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        // Botón Salir
        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitGame();
            }
        });
    }

    /**
     * Inicia una nueva partida
     */
    private void startNewGame() {
        // Diálogo para seleccionar nivel
        showLevelSelectionDialog();
    }

    /**
     * Muestra un diálogo para seleccionar nivel
     */
    private void showLevelSelectionDialog() {
        // En una implementación real, aquí mostraríamos un diálogo
        // Para simplificar, iniciamos directamente el nivel 1
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", 1);
        intent.putExtra("newGame", true);
        startActivity(intent);
    }

    /**
     * Abre la pantalla de cargar partida
     */
    private void loadGame() {
        Intent intent = new Intent(this, SavedGamesActivity.class);
        startActivity(intent);
    }

    /**
     * Abre la pantalla de configuración
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Sale del juego
     */
    private void exitGame() {
        finish();
    }
}
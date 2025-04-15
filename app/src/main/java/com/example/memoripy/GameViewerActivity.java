package com.example.memoripy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.memoripy.utils.FileManager;
import com.example.memoripy.utils.ThemeManager;

/**
 * Actividad para visualizar el contenido de un archivo de partida guardada
 */
public class GameViewerActivity extends AppCompatActivity {

    private TextView tvFileContent;
    private Button btnExport;
    private String fileName;

    private ThemeManager themeManager;
    private FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema
        themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_viewer);

        // Configurar la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.game_file_content);

        // Inicializar el FileManager
        fileManager = FileManager.getInstance();

        // Inicializar vistas
        tvFileContent = findViewById(R.id.tvFileContent);
        btnExport = findViewById(R.id.btnExport);

        // Obtener el nombre del archivo
        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");

        // Cargar el contenido del archivo
        if (fileName != null) {
            loadFileContent();
        } else {
            // No se proporcionó un nombre de archivo
            finish();
        }

        // Configurar eventos
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportFile();
            }
        });
    }

    /**
     * Carga el contenido del archivo
     */
    private void loadFileContent() {
        try {
            String content = fileManager.getFileContent(this, fileName);
            tvFileContent.setText(content);
        } catch (Exception e) {
            tvFileContent.setText("Error al cargar el archivo: " + e.getMessage());
        }
    }

    /**
     * Exporta el archivo
     */
    private void exportFile() {
        try {
            String exportPath = fileManager.exportGame(this, fileName);
            if (exportPath != null) {
                Toast.makeText(this,
                        getString(R.string.export_success) + " " + exportPath,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.export_error, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    getString(R.string.export_error) + ": " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Volver atrás
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
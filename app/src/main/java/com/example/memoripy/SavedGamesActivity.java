package com.example.memoripy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoripy.utils.FileManager;
import com.example.memoripy.utils.ThemeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para gestionar las partidas guardadas
 */
public class SavedGamesActivity extends AppCompatActivity {

    private ListView lvSavedGames;
    private TextView tvNoSavedGames;
    private List<FileManager.SavedGameInfo> savedGames;
    private ArrayAdapter<String> adapter;
    private List<String> displayNames;

    private ThemeManager themeManager;
    private FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema
        themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);

        // Inicializar el FileManager
        fileManager = FileManager.getInstance();

        // Inicializar vistas
        lvSavedGames = findViewById(R.id.lvSavedGames);
        tvNoSavedGames = findViewById(R.id.tvNoSavedGames);

        // Cargar las partidas guardadas
        loadSavedGames();

        // Configurar eventos
        setupListView();
    }

    /**
     * Carga las partidas guardadas
     */
    private void loadSavedGames() {
        savedGames = fileManager.getAllSavedGames(this);
        displayNames = new ArrayList<>();

        if (savedGames.isEmpty()) {
            // No hay partidas guardadas
            tvNoSavedGames.setVisibility(View.VISIBLE);
            lvSavedGames.setVisibility(View.GONE);
        } else {
            // Hay partidas guardadas
            tvNoSavedGames.setVisibility(View.GONE);
            lvSavedGames.setVisibility(View.VISIBLE);

            // Crear lista de nombres para mostrar
            for (FileManager.SavedGameInfo gameInfo : savedGames) {
                displayNames.add(gameInfo.getDisplayName());
            }

            // Configurar adaptador
            adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    displayNames
            );
            lvSavedGames.setAdapter(adapter);
        }
    }

    /**
     * Configura los eventos del ListView
     */
    private void setupListView() {
        lvSavedGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showGameOptions(position);
            }
        });
    }

    /**
     * Muestra las opciones para una partida guardada
     */
    private void showGameOptions(final int position) {
        if (position < 0 || position >= savedGames.size()) {
            return;
        }

        final FileManager.SavedGameInfo gameInfo = savedGames.get(position);
        final String fileName = gameInfo.getFileName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_details)
                .setMessage(gameInfo.getSummary())
                .setPositiveButton(R.string.load, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadGame(fileName);
                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmDeleteGame(position);
                    }
                })
                .setNeutralButton(R.string.view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewGameFile(fileName);
                    }
                });

        builder.create().show();
    }

    /**
     * Carga una partida guardada
     */
    private void loadGame(String fileName) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("fileName", fileName);
        intent.putExtra("newGame", false);
        startActivity(intent);
        finish(); // Cerrar esta actividad para no tenerla en el back stack
    }

    /**
     * Pide confirmación para eliminar una partida
     */
    private void confirmDeleteGame(final int position) {
        if (position < 0 || position >= savedGames.size()) {
            return;
        }

        final FileManager.SavedGameInfo gameInfo = savedGames.get(position);
        final String fileName = gameInfo.getFileName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete)
                .setMessage(gameInfo.getDisplayName())
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGame(position);
                    }
                })
                .setNegativeButton(R.string.no, null);

        builder.create().show();
    }

    /**
     * Elimina una partida guardada
     */
    private void deleteGame(int position) {
        if (position < 0 || position >= savedGames.size()) {
            return;
        }

        FileManager.SavedGameInfo gameInfo = savedGames.get(position);
        String fileName = gameInfo.getFileName();

        boolean success = fileManager.deleteGame(this, fileName);
        if (success) {
            // Eliminar de las listas y actualizar adaptador
            savedGames.remove(position);
            displayNames.remove(position);
            adapter.notifyDataSetChanged();

            // Mostrar mensaje
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();

            // Si no quedan partidas, mostrar mensaje
            if (savedGames.isEmpty()) {
                tvNoSavedGames.setVisibility(View.VISIBLE);
                lvSavedGames.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Ver el contenido del archivo de una partida guardada
     */
    private void viewGameFile(String fileName) {
        Intent intent = new Intent(this, GameViewerActivity.class);
        intent.putExtra("fileName", fileName);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar las partidas guardadas (por si se ha guardado una nueva)
        loadSavedGames();
    }
}
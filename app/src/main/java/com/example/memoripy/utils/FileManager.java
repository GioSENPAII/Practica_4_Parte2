package com.example.memoripy.utils;

import android.content.Context;
import android.util.Log;

import com.example.memoripy.models.GameState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal para gestionar todos los formatos de archivos
 */
public class FileManager {

    private static final String TAG = "FileManager";

    public static final String FORMAT_TXT = "txt";
    public static final String FORMAT_XML = "xml";
    public static final String FORMAT_JSON = "json";

    private static FileManager instance;

    private final Map<String, FileHandler> handlers;

    private FileManager() {
        handlers = new HashMap<>();
        handlers.put(FORMAT_TXT, new TextFileHandler());
        handlers.put(FORMAT_XML, new XmlFileHandler());
        handlers.put(FORMAT_JSON, new JsonFileHandler());
    }

    public static synchronized FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    /**
     * Guarda una partida en el formato especificado
     */
    public String saveGame(Context context, GameState gameState, String format) {
        FileHandler handler = getHandlerForFormat(format);
        if (handler == null) {
            Log.e(TAG, "Formato no soportado: " + format);
            return null;
        }

        try {
            gameState.setSaveFormat(format);
            return handler.saveGame(context, gameState);
        } catch (IOException e) {
            Log.e(TAG, "Error al guardar la partida", e);
            return null;
        }
    }

    /**
     * Carga una partida desde un archivo
     */
    public GameState loadGame(Context context, String fileName) {
        String format = getFormatFromFileName(fileName);
        FileHandler handler = getHandlerForFormat(format);
        if (handler == null) {
            Log.e(TAG, "Formato no soportado: " + format);
            return null;
        }

        try {
            return handler.loadGame(context, fileName);
        } catch (IOException e) {
            Log.e(TAG, "Error al cargar la partida", e);
            return null;
        }
    }

    /**
     * Obtiene la lista de todas las partidas guardadas en todos los formatos
     */
    public List<SavedGameInfo> getAllSavedGames(Context context) {
        List<SavedGameInfo> allGames = new ArrayList<>();

        for (Map.Entry<String, FileHandler> entry : handlers.entrySet()) {
            String format = entry.getKey();
            FileHandler handler = entry.getValue();

            List<String> fileNames = handler.getSavedGamesList(context);
            for (String fileName : fileNames) {
                try {
                    GameState gameState = handler.loadGame(context, fileName);
                    SavedGameInfo info = new SavedGameInfo(fileName, format, gameState);
                    allGames.add(info);
                } catch (IOException e) {
                    Log.e(TAG, "Error al cargar la información de la partida: " + fileName, e);
                }
            }
        }

        return allGames;
    }

    /**
     * Elimina una partida guardada
     */
    public boolean deleteGame(Context context, String fileName) {
        String format = getFormatFromFileName(fileName);
        FileHandler handler = getHandlerForFormat(format);
        if (handler == null) {
            Log.e(TAG, "Formato no soportado: " + format);
            return false;
        }

        return handler.deleteGame(context, fileName);
    }

    /**
     * Obtiene el contenido de un archivo como texto
     */
    public String getFileContent(Context context, String fileName) {
        String format = getFormatFromFileName(fileName);
        FileHandler handler = getHandlerForFormat(format);
        if (handler == null) {
            Log.e(TAG, "Formato no soportado: " + format);
            return "Formato no soportado";
        }

        try {
            return handler.getFileContent(context, fileName);
        } catch (IOException e) {
            Log.e(TAG, "Error al leer el contenido del archivo", e);
            return "Error al leer el archivo: " + e.getMessage();
        }
    }

    /**
     * Exporta una partida guardada
     */
    public String exportGame(Context context, String fileName) {
        String format = getFormatFromFileName(fileName);
        FileHandler handler = getHandlerForFormat(format);
        if (handler == null) {
            Log.e(TAG, "Formato no soportado: " + format);
            return null;
        }

        try {
            return handler.exportGame(context, fileName);
        } catch (IOException e) {
            Log.e(TAG, "Error al exportar la partida", e);
            return null;
        }
    }

    /**
     * Obtiene el handler adecuado para el formato especificado
     */
    private FileHandler getHandlerForFormat(String format) {
        return handlers.get(format.toLowerCase());
    }

    /**
     * Obtiene el formato de un archivo a partir de su nombre
     */
    private String getFormatFromFileName(String fileName) {
        if (fileName.endsWith("." + FORMAT_TXT)) {
            return FORMAT_TXT;
        } else if (fileName.endsWith("." + FORMAT_XML)) {
            return FORMAT_XML;
        } else if (fileName.endsWith("." + FORMAT_JSON)) {
            return FORMAT_JSON;
        }
        return "";
    }

    /**
     * Clase para almacenar información sobre partidas guardadas
     */
    public static class SavedGameInfo {
        private final String fileName;
        private final String format;
        private final GameState gameState;

        public SavedGameInfo(String fileName, String format, GameState gameState) {
            this.fileName = fileName;
            this.format = format;
            this.gameState = gameState;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFormat() {
            return format;
        }

        public GameState getGameState() {
            return gameState;
        }

        /**
         * Obtiene un resumen de la partida guardada
         */
        public String getSummary() {
            if (gameState == null) {
                return "Error al cargar partida";
            }

            StringBuilder summary = new StringBuilder();
            summary.append("Jugador: ").append(gameState.getPlayerName()).append("\n");
            summary.append("Nivel: ").append(gameState.getLevel()).append("\n");
            summary.append("Puntuación: ").append(gameState.getScore()).append("\n");
            summary.append("Fecha: ").append(gameState.getSaveDate()).append("\n");
            summary.append("Formato: ").append(format.toUpperCase());

            return summary.toString();
        }

        /**
         * Obtiene un nombre descriptivo para mostrar en la lista
         */
        public String getDisplayName() {
            if (gameState == null) {
                return fileName;
            }

            String playerName = gameState.getPlayerName();
            if (playerName == null || playerName.isEmpty()) {
                playerName = "Anónimo";
            }

            return playerName + " - Nivel " + gameState.getLevel() +
                    " - " + gameState.getSaveDate() + " [." + format + "]";
        }
    }
}
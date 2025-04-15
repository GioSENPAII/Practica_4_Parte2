package com.example.memoripy.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.memoripy.models.Card;
import com.example.memoripy.models.GameState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Implementación para manejar archivos JSON
 */
public class JsonFileHandler implements FileHandler {

    private static final String TAG = "JsonFileHandler";
    private static final String FILE_DIRECTORY = "saved_games_json";
    private static final String FILE_EXTENSION = ".json";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    public String saveGame(Context context, GameState gameState) throws IOException {
        // Asegurar que el directorio existe
        File directory = new File(context.getFilesDir(), FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Crear nombre de archivo basado en el ID de partida
        String fileName = gameState.getGameId() + FILE_EXTENSION;
        File file = new File(directory, fileName);

        try {
            // Crear objeto JSON principal
            JSONObject jsonGameState = new JSONObject();

            // Información básica
            JSONObject gameInfo = new JSONObject();
            gameInfo.put("playerName", gameState.getPlayerName());
            gameInfo.put("score", gameState.getScore());
            gameInfo.put("timeElapsed", gameState.getTimeElapsed());
            gameInfo.put("level", gameState.getLevel());
            gameInfo.put("gameId", gameState.getGameId());
            gameInfo.put("saveDate", DATE_FORMAT.format(gameState.getSaveDate()));
            gameInfo.put("gameCompleted", gameState.isGameCompleted());
            gameInfo.put("soundEnabled", gameState.isSoundEnabled());
            gameInfo.put("themeName", gameState.getThemeName());
            gameInfo.put("saveFormat", gameState.getSaveFormat());
            jsonGameState.put("gameInfo", gameInfo);

            // Tarjetas
            JSONArray jsonCards = new JSONArray();
            for (Card card : gameState.getCards()) {
                JSONObject jsonCard = new JSONObject();
                jsonCard.put("id", card.getId());
                jsonCard.put("imageId", card.getImageId());
                jsonCard.put("pairId", card.getPairId());
                jsonCard.put("position", card.getPosition());
                jsonCard.put("flipped", card.isFlipped());
                jsonCard.put("matched", card.isMatched());
                jsonCards.put(jsonCard);
            }
            jsonGameState.put("cards", jsonCards);

            // Historial de movimientos
            JSONArray jsonMoves = new JSONArray();
            for (String move : gameState.getMoveHistory()) {
                jsonMoves.put(move);
            }
            jsonGameState.put("moveHistory", jsonMoves);

            // Escribir el JSON en el archivo
            String jsonStr = jsonGameState.toString(4); // Con formato e indentación
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(jsonStr.getBytes());
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON", e);
            throw new IOException("Error creating JSON", e);
        }

        return file.getAbsolutePath();
    }

    @Override
    public GameState loadGame(Context context, String fileName) throws IOException {
        File file = new File(new File(context.getFilesDir(), FILE_DIRECTORY), fileName);
        GameState gameState = new GameState();

        try {
            // Leer archivo
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            // Parsear JSON
            JSONObject jsonGameState = new JSONObject(content.toString());

            // Leer información básica
            JSONObject gameInfo = jsonGameState.getJSONObject("gameInfo");
            gameState.setPlayerName(gameInfo.optString("playerName", ""));
            gameState.setScore(gameInfo.optInt("score", 0));
            gameState.setTimeElapsed(gameInfo.optLong("timeElapsed", 0));
            gameState.setLevel(gameInfo.optInt("level", 1));
            gameState.setGameId(gameInfo.optString("gameId", ""));

            try {
                String saveDateStr = gameInfo.optString("saveDate", "");
                if (!saveDateStr.isEmpty()) {
                    gameState.setSaveDate(DATE_FORMAT.parse(saveDateStr));
                } else {
                    gameState.setSaveDate(new Date());
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date", e);
                gameState.setSaveDate(new Date());
            }

            gameState.setGameCompleted(gameInfo.optBoolean("gameCompleted", false));
            gameState.setSoundEnabled(gameInfo.optBoolean("soundEnabled", true));
            gameState.setThemeName(gameInfo.optString("themeName", "guinda"));
            gameState.setSaveFormat(gameInfo.optString("saveFormat", "json"));

            // Leer tarjetas
            JSONArray jsonCards = jsonGameState.getJSONArray("cards");
            List<Card> cards = new ArrayList<>();
            for (int i = 0; i < jsonCards.length(); i++) {
                JSONObject jsonCard = jsonCards.getJSONObject(i);
                Card card = new Card();
                card.setId(jsonCard.optInt("id", 0));
                card.setImageId(jsonCard.optInt("imageId", 0));
                card.setPairId(jsonCard.optInt("pairId", 0));
                card.setPosition(jsonCard.optInt("position", 0));
                card.setFlipped(jsonCard.optBoolean("flipped", false));
                card.setMatched(jsonCard.optBoolean("matched", false));
                cards.add(card);
            }
            gameState.setCards(cards);

            // Leer historial de movimientos
            JSONArray jsonMoves = jsonGameState.getJSONArray("moveHistory");
            List<String> moveHistory = new ArrayList<>();
            for (int i = 0; i < jsonMoves.length(); i++) {
                moveHistory.add(jsonMoves.getString(i));
            }
            gameState.setMoveHistory(moveHistory);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            throw new IOException("Error parsing JSON", e);
        }

        return gameState;
    }

    @Override
    public List<String> getSavedGamesList(Context context) {
        List<String> fileList = new ArrayList<>();
        File directory = new File(context.getFilesDir(), FILE_DIRECTORY);

        if (directory.exists()) {
            File[] files = directory.listFiles(file -> file.isFile() && file.getName().endsWith(FILE_EXTENSION));
            if (files != null) {
                for (File file : files) {
                    fileList.add(file.getName());
                }
            }
        }

        return fileList;
    }

    @Override
    public boolean deleteGame(Context context, String fileName) {
        File file = new File(new File(context.getFilesDir(), FILE_DIRECTORY), fileName);
        return file.exists() && file.delete();
    }

    @Override
    public String getFileContent(Context context, String fileName) throws IOException {
        File file = new File(new File(context.getFilesDir(), FILE_DIRECTORY), fileName);
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    @Override
    public String exportGame(Context context, String fileName) throws IOException {
        File sourceFile = new File(new File(context.getFilesDir(), FILE_DIRECTORY), fileName);
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "MemorIPN");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File destFile = new File(exportDir, fileName);

        // Leer el archivo original
        String content = getFileContent(context, fileName);

        // Escribir en la ubicación de exportación
        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            fos.write(content.getBytes());
        }

        return destFile.getAbsolutePath();
    }
}
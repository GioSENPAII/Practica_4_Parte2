package com.example.memoripy.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.memoripy.models.Card;
import com.example.memoripy.models.GameState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Implementación para manejar archivos de texto plano
 */
public class TextFileHandler implements FileHandler {

    private static final String TAG = "TextFileHandler";
    private static final String FILE_DIRECTORY = "saved_games_txt";
    private static final String FILE_EXTENSION = ".txt";
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

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {

            // Escribir encabezado
            osw.write("# MemorIPN Game Save File\n");
            osw.write("# Format: TXT\n");
            osw.write("# Date: " + DATE_FORMAT.format(new Date()) + "\n\n");

            // Información básica
            osw.write("[GAME_INFO]\n");
            osw.write("PLAYER_NAME=" + gameState.getPlayerName() + "\n");
            osw.write("SCORE=" + gameState.getScore() + "\n");
            osw.write("TIME_ELAPSED=" + gameState.getTimeElapsed() + "\n");
            osw.write("LEVEL=" + gameState.getLevel() + "\n");
            osw.write("GAME_ID=" + gameState.getGameId() + "\n");
            osw.write("SAVE_DATE=" + DATE_FORMAT.format(gameState.getSaveDate()) + "\n");
            osw.write("GAME_COMPLETED=" + gameState.isGameCompleted() + "\n");
            osw.write("SOUND_ENABLED=" + gameState.isSoundEnabled() + "\n");
            osw.write("THEME_NAME=" + gameState.getThemeName() + "\n");
            osw.write("SAVE_FORMAT=" + gameState.getSaveFormat() + "\n\n");

            // Tarjetas
            osw.write("[CARDS]\n");
            for (Card card : gameState.getCards()) {
                osw.write("CARD_ID=" + card.getId() + "\n");
                osw.write("IMAGE_ID=" + card.getImageId() + "\n");
                osw.write("PAIR_ID=" + card.getPairId() + "\n");
                osw.write("POSITION=" + card.getPosition() + "\n");
                osw.write("FLIPPED=" + card.isFlipped() + "\n");
                osw.write("MATCHED=" + card.isMatched() + "\n");
                osw.write("---\n"); // Separador entre tarjetas
            }
            osw.write("\n");

            // Historial de movimientos
            osw.write("[MOVE_HISTORY]\n");
            for (String move : gameState.getMoveHistory()) {
                osw.write(move + "\n");
            }

            osw.flush();
        }

        return file.getAbsolutePath();
    }

    @Override
    public GameState loadGame(Context context, String fileName) throws IOException {
        File file = new File(new File(context.getFilesDir(), FILE_DIRECTORY), fileName);
        GameState gameState = new GameState();
        List<Card> cards = new ArrayList<>();
        List<String> moveHistory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = null;
            Card currentCard = null;

            while ((line = reader.readLine()) != null) {
                // Ignorar líneas de comentario o vacías
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }

                // Detectar secciones
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line;
                    if (section.equals("[CARDS]")) {
                        currentCard = null;
                    }
                    continue;
                }

                // Procesar datos según la sección
                if ("[GAME_INFO]".equals(section)) {
                    processGameInfoLine(gameState, line);
                } else if ("[CARDS]".equals(section)) {
                    if (line.equals("---")) {
                        if (currentCard != null) {
                            cards.add(currentCard);
                            currentCard = null;
                        }
                        continue;
                    }

                    if (currentCard == null) {
                        currentCard = new Card();
                    }

                    processCardLine(currentCard, line);
                } else if ("[MOVE_HISTORY]".equals(section)) {
                    moveHistory.add(line);
                }
            }

            // Añadir la última tarjeta si existe
            if (currentCard != null) {
                cards.add(currentCard);
            }
        }

        gameState.setCards(cards);
        gameState.setMoveHistory(moveHistory);
        return gameState;
    }

    private void processGameInfoLine(GameState gameState, String line) {
        String[] parts = line.split("=", 2);
        if (parts.length != 2) return;

        String key = parts[0].trim();
        String value = parts[1].trim();

        switch (key) {
            case "PLAYER_NAME":
                gameState.setPlayerName(value);
                break;
            case "SCORE":
                gameState.setScore(Integer.parseInt(value));
                break;
            case "TIME_ELAPSED":
                gameState.setTimeElapsed(Long.parseLong(value));
                break;
            case "LEVEL":
                gameState.setLevel(Integer.parseInt(value));
                break;
            case "GAME_ID":
                gameState.setGameId(value);
                break;
            case "SAVE_DATE":
                try {
                    gameState.setSaveDate(DATE_FORMAT.parse(value));
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date: " + value, e);
                    gameState.setSaveDate(new Date());
                }
                break;
            case "GAME_COMPLETED":
                gameState.setGameCompleted(Boolean.parseBoolean(value));
                break;
            case "SOUND_ENABLED":
                gameState.setSoundEnabled(Boolean.parseBoolean(value));
                break;
            case "THEME_NAME":
                gameState.setThemeName(value);
                break;
            case "SAVE_FORMAT":
                gameState.setSaveFormat(value);
                break;
        }
    }

    private void processCardLine(Card card, String line) {
        String[] parts = line.split("=", 2);
        if (parts.length != 2) return;

        String key = parts[0].trim();
        String value = parts[1].trim();

        switch (key) {
            case "CARD_ID":
                card.setId(Integer.parseInt(value));
                break;
            case "IMAGE_ID":
                card.setImageId(Integer.parseInt(value));
                break;
            case "PAIR_ID":
                card.setPairId(Integer.parseInt(value));
                break;
            case "POSITION":
                card.setPosition(Integer.parseInt(value));
                break;
            case "FLIPPED":
                card.setFlipped(Boolean.parseBoolean(value));
                break;
            case "MATCHED":
                card.setMatched(Boolean.parseBoolean(value));
                break;
        }
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
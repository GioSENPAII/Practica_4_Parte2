package com.example.memoripy.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.example.memoripy.models.Card;
import com.example.memoripy.models.GameState;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Implementación para manejar archivos XML
 */
public class XmlFileHandler implements FileHandler {

    private static final String TAG = "XmlFileHandler";
    private static final String FILE_DIRECTORY = "saved_games_xml";
    private static final String FILE_EXTENSION = ".xml";
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

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "gameState");

            // Información básica
            serializer.startTag("", "gameInfo");
            serializeTag(serializer, "playerName", gameState.getPlayerName());
            serializeTag(serializer, "score", String.valueOf(gameState.getScore()));
            serializeTag(serializer, "timeElapsed", String.valueOf(gameState.getTimeElapsed()));
            serializeTag(serializer, "level", String.valueOf(gameState.getLevel()));
            serializeTag(serializer, "gameId", gameState.getGameId());
            serializeTag(serializer, "saveDate", DATE_FORMAT.format(gameState.getSaveDate()));
            serializeTag(serializer, "gameCompleted", String.valueOf(gameState.isGameCompleted()));
            serializeTag(serializer, "soundEnabled", String.valueOf(gameState.isSoundEnabled()));
            serializeTag(serializer, "themeName", gameState.getThemeName());
            serializeTag(serializer, "saveFormat", gameState.getSaveFormat());
            serializer.endTag("", "gameInfo");

            // Tarjetas
            serializer.startTag("", "cards");
            for (Card card : gameState.getCards()) {
                serializer.startTag("", "card");
                serializeTag(serializer, "id", String.valueOf(card.getId()));
                serializeTag(serializer, "imageId", String.valueOf(card.getImageId()));
                serializeTag(serializer, "pairId", String.valueOf(card.getPairId()));
                serializeTag(serializer, "position", String.valueOf(card.getPosition()));
                serializeTag(serializer, "flipped", String.valueOf(card.isFlipped()));
                serializeTag(serializer, "matched", String.valueOf(card.isMatched()));
                serializer.endTag("", "card");
            }
            serializer.endTag("", "cards");

            // Historial de movimientos
            serializer.startTag("", "moveHistory");
            for (String move : gameState.getMoveHistory()) {
                serializeTag(serializer, "move", move);
            }
            serializer.endTag("", "moveHistory");

            serializer.endTag("", "gameState");
            serializer.endDocument();

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(writer.toString().getBytes());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving XML", e);
            throw new IOException("Error saving XML", e);
        }

        return file.getAbsolutePath();
    }

    private void serializeTag(XmlSerializer serializer, String tagName, String tagValue) throws IOException {
        serializer.startTag("", tagName);
        serializer.text(tagValue != null ? tagValue : "");
        serializer.endTag("", tagName);
    }

    @Override
    public GameState loadGame(Context context, String fileName) throws IOException {
        File file = new File(new File(context.getFilesDir(), FILE_DIRECTORY), fileName);
        GameState gameState = new GameState();
        List<Card> cards = new ArrayList<>();
        List<String> moveHistory = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file)) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, "", "gameState");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();

                if ("gameInfo".equals(name)) {
                    readGameInfo(parser, gameState);
                } else if ("cards".equals(name)) {
                    cards = readCards(parser);
                } else if ("moveHistory".equals(name)) {
                    moveHistory = readMoveHistory(parser);
                } else {
                    skip(parser);
                }
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing XML", e);
            throw new IOException("Error parsing XML", e);
        }

        gameState.setCards(cards);
        gameState.setMoveHistory(moveHistory);
        return gameState;
    }

    private void readGameInfo(XmlPullParser parser, GameState gameState) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, "", "gameInfo");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch (name) {
                case "playerName":
                    gameState.setPlayerName(readText(parser));
                    break;
                case "score":
                    gameState.setScore(Integer.parseInt(readText(parser)));
                    break;
                case "timeElapsed":
                    gameState.setTimeElapsed(Long.parseLong(readText(parser)));
                    break;
                case "level":
                    gameState.setLevel(Integer.parseInt(readText(parser)));
                    break;
                case "gameId":
                    gameState.setGameId(readText(parser));
                    break;
                case "saveDate":
                    try {
                        gameState.setSaveDate(DATE_FORMAT.parse(readText(parser)));
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing date", e);
                        gameState.setSaveDate(new Date());
                    }
                    break;
                case "gameCompleted":
                    gameState.setGameCompleted(Boolean.parseBoolean(readText(parser)));
                    break;
                case "soundEnabled":
                    gameState.setSoundEnabled(Boolean.parseBoolean(readText(parser)));
                    break;
                case "themeName":
                    gameState.setThemeName(readText(parser));
                    break;
                case "saveFormat":
                    gameState.setSaveFormat(readText(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
    }

    private List<Card> readCards(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Card> cards = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, "", "cards");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if ("card".equals(name)) {
                cards.add(readCard(parser));
            } else {
                skip(parser);
            }
        }

        return cards;
    }

    private Card readCard(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, "", "card");
        Card card = new Card();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch (name) {
                case "id":
                    card.setId(Integer.parseInt(readText(parser)));
                    break;
                case "imageId":
                    card.setImageId(Integer.parseInt(readText(parser)));
                    break;
                case "pairId":
                    card.setPairId(Integer.parseInt(readText(parser)));
                    break;
                case "position":
                    card.setPosition(Integer.parseInt(readText(parser)));
                    break;
                case "flipped":
                    card.setFlipped(Boolean.parseBoolean(readText(parser)));
                    break;
                case "matched":
                    card.setMatched(Boolean.parseBoolean(readText(parser)));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return card;
    }

    private List<String> readMoveHistory(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<String> moves = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, "", "moveHistory");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if ("move".equals(name)) {
                moves.add(readText(parser));
            } else {
                skip(parser);
            }
        }

        return moves;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
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
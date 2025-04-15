package com.example.memoripy.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa el estado completo de una partida del juego
 */
public class GameState implements Serializable {
    private String playerName;
    private int score;
    private long timeElapsed; // en milisegundos
    private int level;
    private List<Card> cards;
    private List<String> moveHistory;
    private Date saveDate;
    private String gameId;
    private boolean gameCompleted;

    // Preferencias del jugador
    private boolean soundEnabled;
    private String themeName;
    private String saveFormat; // "txt", "xml", "json"

    public GameState() {
        this.cards = new ArrayList<>();
        this.moveHistory = new ArrayList<>();
        this.saveDate = new Date();
        this.gameId = generateGameId();
        this.soundEnabled = true;
        this.themeName = "guinda"; // tema por defecto
        this.saveFormat = "json"; // formato por defecto
    }

    private String generateGameId() {
        // Genera un ID único basado en timestamp
        return "game_" + System.currentTimeMillis();
    }

    // Getters y Setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<String> getMoveHistory() {
        return moveHistory;
    }

    public void setMoveHistory(List<String> moveHistory) {
        this.moveHistory = moveHistory;
    }

    public void addMove(String move) {
        this.moveHistory.add(move);
    }

    public Date getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public void setGameCompleted(boolean gameCompleted) {
        this.gameCompleted = gameCompleted;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getSaveFormat() {
        return saveFormat;
    }

    public void setSaveFormat(String saveFormat) {
        this.saveFormat = saveFormat;
    }

    /**
     * Obtiene un resumen del estado de la partida para mostrar en la lista de partidas guardadas
     */
    public String getSummary() {
        return "Nivel: " + level +
                ", Puntuación: " + score +
                ", Tiempo: " + formatTime(timeElapsed);
    }

    /**
     * Formatea el tiempo en milisegundos a formato min:seg
     */
    private String formatTime(long timeMs) {
        int seconds = (int) (timeMs / 1000) % 60;
        int minutes = (int) (timeMs / 60000);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
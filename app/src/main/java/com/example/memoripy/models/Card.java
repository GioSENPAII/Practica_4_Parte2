package com.example.memoripy.models;

import java.io.Serializable;

/**
 * Clase que representa una tarjeta en el juego de memoria
 */
public class Card implements Serializable {
    private int id;          // ID único de la tarjeta
    private int imageId;     // Recurso de imagen para la tarjeta
    private int pairId;      // ID que relaciona parejas (mismo valor para cartas que forman pareja)
    private boolean flipped; // Si la tarjeta está volteada
    private boolean matched; // Si la tarjeta ya ha sido emparejada
    private int position;    // Posición en el tablero

    public Card() {
        // Constructor vacío para serialización
    }

    public Card(int id, int imageId, int pairId, int position) {
        this.id = id;
        this.imageId = imageId;
        this.pairId = pairId;
        this.position = position;
        this.flipped = false;
        this.matched = false;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getPairId() {
        return pairId;
    }

    public void setPairId(int pairId) {
        this.pairId = pairId;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Voltea la tarjeta (cambia su estado de flipped)
     */
    public void flip() {
        this.flipped = !this.flipped;
    }

    /**
     * Verifica si dos cartas son una pareja
     */
    public boolean isPairOf(Card otherCard) {
        return this.pairId == otherCard.getPairId();
    }
}
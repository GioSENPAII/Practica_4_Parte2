package com.example.memoripy.utils;

import android.content.Context;

import com.example.memoripy.models.GameState;

import java.io.IOException;
import java.util.List;

/**
 * Interfaz para manejar los archivos de guardado/carga del juego
 */
public interface FileHandler {

    /**
     * Guarda el estado de una partida
     * @param context Contexto de la aplicación
     * @param gameState Estado de la partida a guardar
     * @return Ruta del archivo guardado
     */
    String saveGame(Context context, GameState gameState) throws IOException;

    /**
     * Carga una partida guardada
     * @param context Contexto de la aplicación
     * @param fileName Nombre del archivo a cargar
     * @return Estado de la partida cargada
     */
    GameState loadGame(Context context, String fileName) throws IOException;

    /**
     * Obtiene la lista de todas las partidas guardadas
     * @param context Contexto de la aplicación
     * @return Lista de nombres de archivos de partidas guardadas
     */
    List<String> getSavedGamesList(Context context);

    /**
     * Elimina una partida guardada
     * @param context Contexto de la aplicación
     * @param fileName Nombre del archivo a eliminar
     * @return true si se eliminó correctamente
     */
    boolean deleteGame(Context context, String fileName);

    /**
     * Obtiene el contenido de un archivo como texto
     * para visualización
     * @param context Contexto de la aplicación
     * @param fileName Nombre del archivo
     * @return Contenido del archivo como texto
     */
    String getFileContent(Context context, String fileName) throws IOException;

    /**
     * Exporta una partida guardada al almacenamiento externo
     * @param context Contexto de la aplicación
     * @param fileName Nombre del archivo a exportar
     * @return Ruta del archivo exportado
     */
    String exportGame(Context context, String fileName) throws IOException;
}
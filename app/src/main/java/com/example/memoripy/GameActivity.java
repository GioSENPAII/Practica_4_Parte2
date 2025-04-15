package com.example.memoripy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoripy.adapters.CardAdapter;
import com.example.memoripy.models.Card;
import com.example.memoripy.models.GameState;
import com.example.memoripy.utils.FileManager;
import com.example.memoripy.utils.ThemeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Actividad principal del juego de memoria
 */
public class GameActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    private GridView gridView;
    private TextView tvLevel;
    private TextView tvScore;
    private Chronometer chronometer;
    private Button btnPause;
    private Button btnSave;
    private Button btnExit;

    private List<Card> cards;
    private CardAdapter adapter;
    private GameState gameState;
    private ThemeManager themeManager;

    private int level;
    private int gridSize;
    private int score;
    private boolean isPaused;
    private long timeWhenStopped;
    private boolean isGameCompleted;

    private Card firstCard;
    private Card secondCard;
    private boolean isProcessing;

    private MediaPlayer mpFlip;
    private MediaPlayer mpMatch;
    private MediaPlayer mpWrong;
    private MediaPlayer mpWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema
        themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Inicializar vistas
        initializeViews();

        // Obtener nivel del juego
        Intent intent = getIntent();
        level = intent.getIntExtra("level", 1);
        boolean isNewGame = intent.getBooleanExtra("newGame", true);

        if (isNewGame) {
            // Iniciar nuevo juego
            setupNewGame(level);
        } else {
            // Cargar partida
            String fileName = intent.getStringExtra("fileName");
            if (fileName != null) {
                loadGame(fileName);
            } else {
                // Si no hay archivo, iniciar nuevo juego
                setupNewGame(level);
            }
        }

        // Configurar sonidos
        setupSounds();

        // Iniciar el cronómetro
        startChronometer();
    }

    /**
     * Inicializa las vistas de la actividad
     */
    private void initializeViews() {
        gridView = findViewById(R.id.gridViewCards);
        tvLevel = findViewById(R.id.tvLevel);
        tvScore = findViewById(R.id.tvScore);
        chronometer = findViewById(R.id.chronometer);
        btnPause = findViewById(R.id.btnPause);
        btnSave = findViewById(R.id.btnSave);
        btnExit = findViewById(R.id.btnExit);

        // Configurar eventos de los botones
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePause();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });
    }

    /**
     * Configura un nuevo juego según el nivel
     */
    private void setupNewGame(int level) {
        this.level = level;
        this.score = 0;
        this.isPaused = false;
        this.isGameCompleted = false;
        this.timeWhenStopped = 0;
        this.firstCard = null;
        this.secondCard = null;
        this.isProcessing = false;

        // Determinar tamaño del grid según el nivel
        switch (level) {
            case 1:
                gridSize = 4; // 4x4 = 16 tarjetas (8 pares)
                break;
            case 2:
                gridSize = 5; // 5x4 = 20 tarjetas (10 pares)
                break;
            case 3:
                gridSize = 6; // 6x4 = 24 tarjetas (12 pares)
                break;
            default:
                gridSize = 4;
                break;
        }

        // Crear lista de tarjetas
        createCardsList();

        // Inicializar el estado del juego
        initializeGameState();

        // Actualizar interfaz
        updateUI();
    }

    /**
     * Crea la lista de tarjetas para el juego
     */
    private void createCardsList() {
        cards = new ArrayList<>();
        int numPairs = (gridSize * gridSize) / 2;

        // Arrays con IDs de imágenes que se usarán para las cartas
        // En una implementación real, usaría recursos reales
        // Para este ejemplo, usaremos IDs ficticios
        int[] imageResources = {
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground, // Imagen de placeholder
                R.drawable.ic_launcher_foreground  // Imagen de placeholder
        };

        // Crear pares de tarjetas
        for (int i = 0; i < numPairs; i++) {
            int imageId = imageResources[i % imageResources.length];
            int pairId = i + 1;

            // Primera tarjeta del par
            Card card1 = new Card(i * 2, imageId, pairId, i * 2);
            cards.add(card1);

            // Segunda tarjeta del par
            Card card2 = new Card(i * 2 + 1, imageId, pairId, i * 2 + 1);
            cards.add(card2);
        }

        // Mezclar las tarjetas
        Collections.shuffle(cards);

        // Actualizar posiciones después de mezclar
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setPosition(i);
        }

        // Configurar el adaptador
        adapter = new CardAdapter(this, cards, this);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(gridSize);
    }

    /**
     * Inicializa el estado del juego
     */
    private void initializeGameState() {
        gameState = new GameState();
        gameState.setLevel(level);
        gameState.setScore(score);
        gameState.setTimeElapsed(0);
        gameState.setCards(cards);
        gameState.setThemeName(themeManager.getCurrentTheme());
        gameState.setSoundEnabled(true); // Por defecto, sonido activado
    }

    /**
     * Actualiza la interfaz con el estado actual del juego
     */
    private void updateUI() {
        tvLevel.setText(getString(R.string.level, level));
        tvScore.setText(getString(R.string.score, score));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Configura los sonidos del juego
     */
    private void setupSounds() {
        // En una implementación real, cargaríamos recursos de sonido reales
        mpFlip = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        mpMatch = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        mpWrong = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        mpWin = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
    }

    /**
     * Inicia el cronómetro
     */
    private void startChronometer() {
        if (!isPaused) {
            chronometer.setBase(SystemClock.elapsedRealtime() - gameState.getTimeElapsed());
            chronometer.start();
        }
    }

    /**
     * Pausa o reanuda el juego
     */
    private void togglePause() {
        if (isPaused) {
            // Reanudar el juego
            chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
            chronometer.start();
            btnPause.setText(R.string.pause);
            enableCardClicks(true);
        } else {
            // Pausar el juego
            timeWhenStopped = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.stop();
            btnPause.setText(R.string.resume);
            enableCardClicks(false);

            // Mostrar diálogo de pausa
            showPauseDialog();
        }
        isPaused = !isPaused;
    }

    /**
     * Habilita o deshabilita los clics en las tarjetas
     */
    private void enableCardClicks(boolean enable) {
        adapter.setClickable(enable);
        btnSave.setEnabled(enable);
    }

    /**
     * Muestra el diálogo de pausa
     */
    private void showPauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_paused)
                .setMessage(getString(R.string.score) + " " + score)
                .setCancelable(false)
                .setPositiveButton(R.string.resume, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        togglePause();
                    }
                })
                .setNegativeButton(R.string.exit_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    /**
     * Muestra el diálogo para guardar la partida
     */
    private void showSaveDialog() {
        // Pausar el juego si no está pausado
        if (!isPaused) {
            togglePause();
        }

        // Crear diálogo con input para el nombre y selección de formato
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_game, null);

        final EditText etPlayerName = dialogView.findViewById(R.id.etPlayerName);
        final RadioGroup rgFormat = dialogView.findViewById(R.id.rgFormat);
        final RadioButton rbTxt = dialogView.findViewById(R.id.rbTxt);
        final RadioButton rbXml = dialogView.findViewById(R.id.rbXml);
        final RadioButton rbJson = dialogView.findViewById(R.id.rbJson);

        // Establecer formato predeterminado
        String currentFormat = gameState.getSaveFormat();
        if (FileManager.FORMAT_TXT.equals(currentFormat)) {
            rbTxt.setChecked(true);
        } else if (FileManager.FORMAT_XML.equals(currentFormat)) {
            rbXml.setChecked(true);
        } else {
            rbJson.setChecked(true);
        }

        // Si ya hay un nombre, mostrarlo
        if (gameState.getPlayerName() != null && !gameState.getPlayerName().isEmpty()) {
            etPlayerName.setText(gameState.getPlayerName());
        }

        builder.setTitle(R.string.save_game_title)
                .setView(dialogView)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playerName = etPlayerName.getText().toString().trim();
                        if (playerName.isEmpty()) {
                            playerName = "Anónimo";
                        }

                        String format;
                        int selectedId = rgFormat.getCheckedRadioButtonId();
                        if (selectedId == R.id.rbTxt) {
                            format = FileManager.FORMAT_TXT;
                        } else if (selectedId == R.id.rbXml) {
                            format = FileManager.FORMAT_XML;
                        } else {
                            format = FileManager.FORMAT_JSON;
                        }

                        saveGame(playerName, format);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Solo cerrar el diálogo
                    }
                });

        builder.show();
    }

    /**
     * Guarda la partida actual
     */
    private void saveGame(String playerName, String format) {
        // Actualizar el estado del juego
        updateGameState();
        gameState.setPlayerName(playerName);
        gameState.setSaveFormat(format);
        gameState.setSaveDate(new Date());

        // Guardar usando el FileManager
        String filePath = FileManager.getInstance().saveGame(this, gameState, format);

        if (filePath != null) {
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Actualiza el estado del juego con los valores actuales
     */
    private void updateGameState() {
        long elapsedTimeMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        gameState.setTimeElapsed(elapsedTimeMillis);
        gameState.setScore(score);
        gameState.setLevel(level);
        gameState.setCards(cards);
        gameState.setGameCompleted(isGameCompleted);
    }

    /**
     * Carga una partida guardada
     */
    private void loadGame(String fileName) {
        GameState loadedState = FileManager.getInstance().loadGame(this, fileName);

        if (loadedState != null) {
            this.gameState = loadedState;
            this.level = loadedState.getLevel();
            this.score = loadedState.getScore();
            this.cards = loadedState.getCards();
            this.isGameCompleted = loadedState.isGameCompleted();

            // Configurar adaptador con las tarjetas cargadas
            adapter = new CardAdapter(this, cards, this);
            gridView.setAdapter(adapter);

            // Determinar tamaño del grid según el nivel
            switch (level) {
                case 1:
                    gridSize = 4;
                    break;
                case 2:
                    gridSize = 5;
                    break;
                case 3:
                    gridSize = 6;
                    break;
                default:
                    gridSize = 4;
                    break;
            }
            gridView.setNumColumns(gridSize);

            // Actualizar interfaz
            updateUI();

            // Configurar cronómetro con el tiempo guardado
            chronometer.setBase(SystemClock.elapsedRealtime() - loadedState.getTimeElapsed());
            timeWhenStopped = loadedState.getTimeElapsed();

            Toast.makeText(this, "Partida cargada: " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al cargar la partida", Toast.LENGTH_SHORT).show();
            setupNewGame(1); // Iniciar nuevo juego en nivel 1
        }
    }

    /**
     * Muestra diálogo de confirmación para salir
     */
    private void showExitConfirmationDialog() {
        // Pausar el juego si no está pausado
        if (!isPaused) {
            togglePause();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_game)
                .setMessage("¿Deseas guardar antes de salir?")
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSaveDialog();
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        togglePause(); // Reanudar el juego
                    }
                });
        builder.show();
    }

    /**
     * Implementación del listener para clics en tarjetas
     */
    @Override
    public void onCardClick(int position) {
        // Si el juego está procesando o pausado, ignorar clics
        if (isProcessing || isPaused) {
            return;
        }

        Card card = cards.get(position);

        // Si la tarjeta ya está volteada o emparejada, ignorar clic
        if (card.isFlipped() || card.isMatched()) {
            return;
        }

        // Reproducir sonido de volteo
        if (gameState.isSoundEnabled() && mpFlip != null) {
            mpFlip.start();
        }

        // Voltear la tarjeta
        card.flip();
        adapter.notifyDataSetChanged();

        // Registrar movimiento
        String moveDesc = "Volteada tarjeta en posición " + position;
        gameState.addMove(moveDesc);

        // Procesar la jugada
        processCardSelection(card);
    }

    /**
     * Procesa la selección de una tarjeta
     */
    private void processCardSelection(Card card) {
        if (firstCard == null) {
            // Primera tarjeta seleccionada
            firstCard = card;
        } else if (secondCard == null && firstCard.getId() != card.getId()) {
            // Segunda tarjeta seleccionada
            secondCard = card;

            // Verificar si forman pareja
            isProcessing = true;
            checkForMatch();
        }
    }

    /**
     * Verifica si las dos tarjetas seleccionadas forman pareja
     */
    private void checkForMatch() {
        final Handler handler = new Handler();

        // Esperar un momento para que el jugador vea la segunda tarjeta
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstCard.isPairOf(secondCard)) {
                    // ¡Encontró una pareja!
                    firstCard.setMatched(true);
                    secondCard.setMatched(true);

                    // Reproducir sonido de acierto
                    if (gameState.isSoundEnabled() && mpMatch != null) {
                        mpMatch.start();
                    }

                    // Otorgar puntos
                    int points = 10 * level; // Más puntos en niveles más altos
                    score += points;
                    gameState.setScore(score);
                    updateUI();

                    // Registrar movimiento
                    String moveDesc = "Pareja encontrada: " + firstCard.getPairId() + " (+"+points+" puntos)";
                    gameState.addMove(moveDesc);

                    // Verificar si ha completado el nivel
                    checkLevelCompleted();
                } else {
                    // No es pareja, voltear tarjetas de nuevo
                    firstCard.flip();
                    secondCard.flip();

                    // Reproducir sonido de error
                    if (gameState.isSoundEnabled() && mpWrong != null) {
                        mpWrong.start();
                    }

                    // Registrar movimiento
                    String moveDesc = "Pareja incorrecta: " + firstCard.getPairId() + " - " + secondCard.getPairId();
                    gameState.addMove(moveDesc);
                }

                // Reiniciar selección
                firstCard = null;
                secondCard = null;
                isProcessing = false;

                // Actualizar la vista
                adapter.notifyDataSetChanged();
            }
        }, 1000); // Esperar 1 segundo
    }

    /**
     * Verifica si el nivel ha sido completado
     */
    private void checkLevelCompleted() {
        boolean allMatched = true;
        for (Card card : cards) {
            if (!card.isMatched()) {
                allMatched = false;
                break;
            }
        }

        if (allMatched) {
            // Nivel completado
            isGameCompleted = true;

            // Detener cronómetro
            chronometer.stop();

            // Registrar movimiento
            String moveDesc = "Nivel " + level + " completado con " + score + " puntos";
            gameState.addMove(moveDesc);

            // Reproducir sonido de victoria
            if (gameState.isSoundEnabled() && mpWin != null) {
                mpWin.start();
            }

            // Mostrar diálogo de nivel completado
            showLevelCompletedDialog();
        }
    }

    /**
     * Muestra el diálogo de nivel completado
     */
    private void showLevelCompletedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title;

        if (level < 3) {
            // Aún hay más niveles
            title = getString(R.string.level_completed);
            builder.setPositiveButton(R.string.next_level, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startNextLevel();
                }
            });
        } else {
            // Juego completado
            title = getString(R.string.game_completed);
        }

        builder.setTitle(title)
                .setMessage("Puntuación: " + score + "\nTiempo: " + getFormattedTime())
                .setCancelable(false)
                .setNegativeButton(R.string.back_to_main, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        // Opción para reiniciar el nivel
        builder.setNeutralButton(R.string.restart_level, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setupNewGame(level);
                startChronometer();
            }
        });

        builder.show();
    }

    /**
     * Inicia el siguiente nivel
     */
    private void startNextLevel() {
        // Guardar la puntuación actual
        int currentScore = score;

        // Iniciar nuevo juego en el siguiente nivel
        setupNewGame(level + 1);

        // Mantener la puntuación acumulada
        score = currentScore;
        gameState.setScore(score);
        updateUI();

        // Reiniciar el cronómetro
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    /**
     * Formatea el tiempo en un formato legible
     */
    private String getFormattedTime() {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        int seconds = (int) (elapsedMillis / 1000) % 60;
        int minutes = (int) (elapsedMillis / 60000);
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Si el juego está activo, pausarlo
        if (!isPaused && !isGameCompleted) {
            togglePause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos de MediaPlayer
        if (mpFlip != null) {
            mpFlip.release();
            mpFlip = null;
        }
        if (mpMatch != null) {
            mpMatch.release();
            mpMatch = null;
        }
        if (mpWrong != null) {
            mpWrong.release();
            mpWrong = null;
        }
        if (mpWin != null) {
            mpWin.release();
            mpWin = null;
        }
    }
}
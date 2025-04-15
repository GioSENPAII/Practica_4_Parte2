# MemorIPN - Juego de Memoria IPN/ESCOM

## Descripción
MemorIPN es un juego de memoria para Android inspirado en la temática del IPN (Instituto Politécnico Nacional) y ESCOM (Escuela Superior de Cómputo). El juego desafía a los jugadores a encontrar parejas de tarjetas, ofreciendo varios niveles de dificultad, diferentes temas visuales y la capacidad de guardar/cargar partidas en diversos formatos.

## Características principales
- Tres niveles de dificultad con diferentes tamaños de tablero
- Sistema de puntuación basado en el tiempo y la dificultad
- Dos temas visuales: Guinda (IPN) y Azul (ESCOM)
- Guardado de partidas en tres formatos: TXT, XML y JSON
- Visor integrado para examinar archivos de partidas guardadas
- Exportación de partidas al almacenamiento externo
- Sistema de sonido configurable
- Modo día/noche que se adapta a la configuración del sistema

## Requisitos del sistema
- Android 8.0 (API 24) o superior
- Espacio mínimo: 10MB
- Permisos opcionales: almacenamiento externo (para exportar partidas)

## Instalación

### Desde Android Studio
1. Clona el repositorio:
   ```
   git clone https://github.com/tu-usuario/MemorIPN.git
   ```
2. Abre el proyecto en Android Studio
3. Sincroniza el proyecto con los archivos Gradle
4. Conecta un dispositivo Android o configura un emulador
5. Haz clic en "Run" (▶️) para compilar e instalar la aplicación

### APK directamente
1. Descarga el archivo APK desde la sección de releases
2. En tu dispositivo Android, ve a Configuración > Seguridad
3. Activa "Orígenes desconocidos" para permitir la instalación de aplicaciones fuera de Play Store
4. Encuentra el APK descargado y tócalo para instalar

## Estructura del proyecto

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/memoripy/
│   │   │   ├── activities/            # Actividades principales
│   │   │   ├── adapters/              # Adaptadores para views
│   │   │   ├── models/                # Modelos de datos
│   │   │   ├── utils/                 # Utilidades y manejadores
│   │   │   └── ui/theme/              # Definición de temas
│   │   ├── res/                       # Recursos de la aplicación
│   │   └── AndroidManifest.xml        # Configuración de la app
│   └── androidTest/                   # Pruebas instrumentadas
└── build.gradle.kts                   # Configuración de compilación
```

### Componentes principales

#### Activities
- **MainActivity**: Menú principal del juego
- **GameActivity**: Actividad principal con la lógica del juego
- **SettingsActivity**: Configuración del juego
- **SavedGamesActivity**: Gestor de partidas guardadas
- **GameViewerActivity**: Visor de archivos de partidas

#### Modelos
- **Card**: Representa una tarjeta en el juego
- **GameState**: Estado completo de una partida
- **Player**: Información del jugador

#### Utilidades
- **FileManager**: Gestor centralizado de archivos
- **FileHandler**: Interfaz para manejadores de archivos
- **JsonFileHandler**: Implementación para archivos JSON
- **XmlFileHandler**: Implementación para archivos XML
- **TextFileHandler**: Implementación para archivos de texto
- **ThemeManager**: Gestión de temas visuales

## Uso

### Menú principal
- **Nuevo Juego**: Inicia una nueva partida
- **Cargar Partida**: Abre una partida guardada
- **Configuración**: Ajusta las preferencias del juego
- **Salir**: Cierra la aplicación

### Juego
- Toca las tarjetas para voltearlas
- Encuentra todas las parejas para completar el nivel
- Usa los botones inferiores para:
  - **Pausar/Continuar**: Detiene o reanuda el cronómetro
  - **Guardar**: Guarda la partida actual
  - **Salir**: Regresa al menú principal

### Configuración
- **Tema**: Elige entre tema Guinda (IPN) o Azul (ESCOM)
- **Sonido**: Activa o desactiva los efectos de sonido
- **Formato de guardado**: Selecciona el formato predeterminado (TXT, XML, JSON)

### Partidas guardadas
- Toca una partida para ver opciones:
  - **Cargar**: Continúa la partida
  - **Ver**: Muestra el contenido del archivo
  - **Eliminar**: Borra la partida

### Visor de partidas
- Muestra el contenido del archivo de partida
- **Exportar**: Guarda una copia en el almacenamiento externo

## Diseño del juego

### Niveles de dificultad
- **Nivel 1**: Tablero 4x4 (8 parejas)
- **Nivel 2**: Tablero 5x4 (10 parejas)
- **Nivel 3**: Tablero 6x4 (12 parejas)

### Puntuación
- Puntos base por cada pareja encontrada: 10 puntos × nivel
- Bonificación por tiempo: Menor tiempo = mayor puntuación

### Formatos de guardado
El juego permite guardar partidas en tres formatos diferentes:

1. **TXT**: Formato de texto plano con secciones claramente definidas
2. **XML**: Formato estructurado con etiquetas para cada elemento
3. **JSON**: Formato ligero y fácil de procesar

## Permisos
La aplicación requiere los siguientes permisos:
- **Leer almacenamiento externo**: Para exportar partidas (opcional)
- **Escribir almacenamiento externo**: Para exportar partidas (opcional)

## Personalización
### Temas
La aplicación incluye dos temas:
- **Tema Guinda (IPN)**: Tema por defecto con colores institucionales del IPN
- **Tema Azul (ESCOM)**: Tema alternativo con colores de la ESCOM

Ambos temas se adaptan automáticamente al modo claro/oscuro del sistema.

## Solución de problemas

### Problemas comunes
- **Problemas al exportar**: Verifica que la aplicación tenga permisos de almacenamiento
- **El juego se cierra inesperadamente**: Asegúrate de tener la última versión

### Reportar problemas
Si encuentras algún problema, por favor reporta los detalles incluyendo:
- Modelo del dispositivo y versión de Android
- Descripción detallada del problema
- Capturas de pantalla si es posible

## Desarrollo

### Tecnologías utilizadas
- **Lenguaje principal**: Java
- **UI**: XML con Material Design
- **Persistencia**: Archivos (TXT, XML, JSON)
- **Multimedia**: MediaPlayer para efectos de sonido

### Estructura de archivos guardados

#### Formato TXT
```
# MemorIPN Game Save File
# Format: TXT
# Date: 2025-04-14 21:30:00

[GAME_INFO]
PLAYER_NAME=Jugador
SCORE=120
TIME_ELAPSED=45000
LEVEL=2
GAME_ID=game_1714413800123
...

[CARDS]
CARD_ID=0
IMAGE_ID=123456
PAIR_ID=1
...
---
...

[MOVE_HISTORY]
Volteada tarjeta en posición 5
Pareja encontrada: 1 (+20 puntos)
...
```

#### Formato XML
```xml
<gameState>
  <gameInfo>
    <playerName>Jugador</playerName>
    <score>120</score>
    <timeElapsed>45000</timeElapsed>
    <level>2</level>
    ...
  </gameInfo>
  <cards>
    <card>
      <id>0</id>
      <imageId>123456</imageId>
      <pairId>1</pairId>
      ...
    </card>
    ...
  </cards>
  <moveHistory>
    <move>Volteada tarjeta en posición 5</move>
    <move>Pareja encontrada: 1 (+20 puntos)</move>
    ...
  </moveHistory>
</gameState>
```

#### Formato JSON
```json
{
  "gameInfo": {
    "playerName": "Jugador",
    "score": 120,
    "timeElapsed": 45000,
    "level": 2,
    ...
  },
  "cards": [
    {
      "id": 0,
      "imageId": 123456,
      "pairId": 1,
      ...
    },
    ...
  ],
  "moveHistory": [
    "Volteada tarjeta en posición 5",
    "Pareja encontrada: 1 (+20 puntos)",
    ...
  ]
}
```

### Contribuir
1. Realiza un fork del repositorio
2. Crea una rama para tu funcionalidad (`git checkout -b nueva-funcionalidad`)
3. Realiza tus cambios y haz commit (`git commit -m 'Añadir nueva funcionalidad'`)
4. Sube tus cambios (`git push origin nueva-funcionalidad`)
5. Crea un Pull Request

## Licencia
Este proyecto está disponible bajo la licencia MIT. Consulta el archivo LICENSE para más detalles.

## Equipo de desarrollo
- Desarrollado por estudiantes del IPN-ESCOM
- Contacto: [glongoria.3a.is@gmail.com]

## Agradecimientos
- A la comunidad Android por sus recursos y bibliotecas
- Al Instituto Politécnico Nacional y la Escuela Superior de Cómputo por su apoyo

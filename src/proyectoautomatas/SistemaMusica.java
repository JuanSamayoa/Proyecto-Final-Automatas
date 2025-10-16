/*
 * Sistema Musical para Personas con Discapacidad Visual
 * Desarrollado con análisis léxico y sintáctico
 * Utiliza expresiones regulares y gramática formal
 */
package proyectoautomatas;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;

/**
 *
 * @author Juan Enrique Samayoa Reyes
 *         Carne: 9390-23-2010
 *         Curso: Automatas y Lenguajes Formales
 *         Proyecto Final
 */
public class SistemaMusica extends javax.swing.JFrame {

    // Componentes de la interfaz
    private JTextArea areaResultados;
    private JScrollPane panelScroll;
    private JButton btnCargarArchivo;
    private JButton btnDetener;
    private JButton btnEjemplo;
    private JButton btnAyuda;
    private JLabel lblEstado;

    // Control de reproducción
    private boolean reproduciendo = false;
    private boolean detenerSolicitado = false;
    private Thread hiloReproduccion;

    // Motor de análisis musical
    private final AnalizadorMusical analizador;
    private final GeneradorSonido generadorSonido;
    private final GramaticaMusical gramatica;

    /**
     * Constructor principal del sistema musical
     */
    public SistemaMusica() {
        // Inicializar los componentes del motor musical
        this.gramatica = new GramaticaMusical();
        this.analizador = new AnalizadorMusical(gramatica);
        this.generadorSonido = new GeneradorSonido();

        // Configurar la interfaz de usuario
        inicializarInterfaz();
        configurarEventosAccesibilidad();

        setTitle("Sistema Musical - Transformando Texto en Melodia");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Mensaje de bienvenida
        mostrarMensajeBienvenida();
    }

    /**
     * Configura la interfaz gráfica con un diseño accesible y simple
     */
    private void inicializarInterfaz() {
        setSize(1000, 700);

        // Configuración principal del contenedor
        getContentPane().setLayout(new BorderLayout());

        // Panel superior con información del sistema
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        // Área principal de resultados
        areaResultados = new JTextArea();
        configurarAreaTexto();
        panelScroll = new JScrollPane(areaResultados);
        panelScroll.setBorder(BorderFactory.createTitledBorder("Análisis Musical"));
        add(panelScroll, BorderLayout.CENTER);

        // Panel de controles
        JPanel panelControles = crearPanelControles();
        add(panelControles, BorderLayout.SOUTH);

        // Barra de estado
        lblEstado = new JLabel("Sistema listo. Carga un archivo o usa el ejemplo incluido");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(lblEstado, BorderLayout.PAGE_END);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("<html><center>" +
                "<h2>Sistema Musical Accesible</h2>" +
                "<p>Convierte partituras de texto en experiencias sonoras</p>" +
                "</center></html>");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.CENTER);

        return panel;
    }

    private void configurarAreaTexto() {
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaResultados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        // Botón para cargar archivo
        btnCargarArchivo = crearBotonSimple("Cargar Partitura", "Selecciona un archivo con notas musicales");
        btnCargarArchivo.addActionListener(this::accionCargarArchivo);

        // Botón para ejemplo predefinido
        btnEjemplo = crearBotonSimple("Usar Ejemplo", "Reproduce el ejemplo incluido");
        btnEjemplo.addActionListener(this::accionUsarEjemplo);

        // Botón para detener
        btnDetener = crearBotonSimple("Detener", "Detiene la reproducción actual");
        btnDetener.addActionListener(this::accionDetenerMusica);
        btnDetener.setEnabled(false);

        // Botón de ayuda
        btnAyuda = crearBotonSimple("Ayuda", "Información sobre el sistema");
        btnAyuda.addActionListener(this::accionMostrarAyuda);

        panel.add(btnCargarArchivo);
        panel.add(btnEjemplo);
        panel.add(btnDetener);
        panel.add(btnAyuda);

        return panel;
    }

    private JButton crearBotonSimple(String texto, String tooltip) {
        JButton boton = new JButton(texto);
        boton.setToolTipText(tooltip);
        return boton;
    }

    private void configurarEventosAccesibilidad() {
        // Atajos de teclado para accesibilidad
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // Ctrl+O para cargar archivo
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "cargar");
        actionMap.put("cargar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                accionCargarArchivo(e);
            }
        });

        // Ctrl+E para ejemplo
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "ejemplo");
        actionMap.put("ejemplo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                accionUsarEjemplo(e);
            }
        });

        // Escape para detener
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "detener");
        actionMap.put("detener", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                accionDetenerMusica(e);
            }
        });
    }

    private void mostrarMensajeBienvenida() {
        String mensaje = "Bienvenido al Sistema Musical Accesible\n\n" +
                "Este sistema convierte partituras de texto en musica.\n\n" +
                "Caracteristicas:\n" +
                "- Analisis sintactico con expresiones regulares\n" +
                "- Generacion de audio en tiempo real\n" +
                "- Conteo de notas por parrafo\n" +
                "- Interfaz accesible con atajos de teclado\n\n" +
                "Notas: DO, RE, MI, FA, SOL, LA, SI (con # y ')\n\n" +
                "Atajos: Ctrl+O (cargar), Ctrl+E (ejemplo), Escape (detener)\n\n" +
                "Comienza cargando un archivo o usando el ejemplo.";

        areaResultados.setText(mensaje);
        actualizarEstado("Sistema iniciado correctamente");
    }

    // EVENTOS DE INTERFAZ

    /**
     * Maneja el evento de cargar archivo desde el selector de archivos
     * Verifica que no haya reproducción en curso antes de proceder
     */
    private void accionCargarArchivo(ActionEvent evento) {
        if (reproduciendo) {
            JOptionPane.showMessageDialog(this,
                    "Hay una reproduccion en curso. Deten la musica actual primero.",
                    "Sistema Ocupado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setDialogTitle("Selecciona tu partitura musical");
        selectorArchivo.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File archivo) {
                return archivo.isDirectory() || archivo.getName().toLowerCase().endsWith(".txt");
            }

            public String getDescription() {
                return "Archivos de texto (*.txt)";
            }
        });

        int resultado = selectorArchivo.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = selectorArchivo.getSelectedFile();
            procesarArchivoMusical(archivoSeleccionado);
        }
    }

    /**
     * Lee el contenido del archivo de ejemplo
     */
    private String leerArchivoEjemplo() throws IOException {
        File archivoEjemplo = new File("partitura_ejemplo.txt");
        if (!archivoEjemplo.exists()) {
            throw new IOException("El archivo partitura_ejemplo.txt no existe en el directorio del proyecto");
        }

        StringBuilder contenido = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoEjemplo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        }
        return contenido.toString().trim();
    }

    /**
     * Maneja el evento de usar el ejemplo predefinido incluido en el proyecto
     */
    private void accionUsarEjemplo(ActionEvent evento) {
        if (reproduciendo) {
            JOptionPane.showMessageDialog(this,
                    "Hay una reproducción en curso. Detén la música actual primero.",
                    "Sistema Ocupado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Leer el archivo de ejemplo
            String contenido = leerArchivoEjemplo();
            procesarTextoMusical(contenido, "Ejemplo Integrado");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el archivo de ejemplo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja el evento de detener la reproducción musical actual
     */
    private void accionDetenerMusica(ActionEvent evento) {
        if (reproduciendo) {
            detenerSolicitado = true;
            generadorSonido.detenerTodo();

            if (hiloReproduccion != null) {
                hiloReproduccion.interrupt();
            }

            SwingUtilities.invokeLater(() -> {
                reproduciendo = false;
                btnCargarArchivo.setEnabled(true);
                btnEjemplo.setEnabled(true);
                btnDetener.setEnabled(false);
                areaResultados.append("\n\nReproducción detenida por el usuario");
                actualizarEstado("Reproducción detenida");
            });
        }
    }

    /**
     * Maneja el evento de mostrar la ayuda del sistema
     */
    private void accionMostrarAyuda(ActionEvent evento) {
        String ayuda = "Sistema Musical Accesible - Guia de Uso\n\n" +
                "Formato de partituras:\n" +
                "Notas: DO, RE, MI, FA, SOL, LA, SI\n" +
                "Sostenidos: nota#\n" +
                "Octavas: nota'\n\n" +
                "Ejemplo:\n" +
                "sol sol sol re# fa fa fa re\n\n" +
                "Caracteristicas:\n" +
                "- Expresiones regulares para analisis\n" +
                "- Arboles sintacticos\n" +
                "- Audio en tiempo real\n" +
                "- Conteo automatico de notas\n\n" +
                "Reproduccion: 500ms por nota, pausas automaticas";

        JOptionPane.showMessageDialog(this, ayuda, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    // PROCESAMIENTO MUSICAL

    /**
     * Procesa un archivo musical seleccionado por el usuario
     * Lee el contenido y lo pasa al procesamiento de texto
     */
    private void procesarArchivoMusical(File archivo) {
        try {
            StringBuilder contenido = new StringBuilder();
            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
            }

            procesarTextoMusical(contenido.toString(), archivo.getName());

        } catch (IOException error) {
            JOptionPane.showMessageDialog(this,
                    "Error al leer el archivo: " + error.getMessage(),
                    "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            actualizarEstado("Error al cargar archivo");
        }
    }

    /**
     * Procesa el texto musical completo, realiza el análisis y muestra resultados
     * Coordina el análisis léxico, la generación de reportes y la reproducción
     */
    private void procesarTextoMusical(String textoMusical, String nombreFuente) {
        try {
            actualizarEstado("Analizando partitura...");

            // Analizar la partitura usando el motor de análisis
            ResultadoAnalisis resultado = analizador.analizarTexto(textoMusical);

            // Mostrar resultados del análisis
            mostrarAnalisisCompleto(resultado, nombreFuente);

            // Iniciar reproducción en hilo separado
            iniciarReproduccionMusical(resultado);

        } catch (Exception error) {
            JOptionPane.showMessageDialog(this,
                    "Error procesando la partitura: " + error.getMessage(),
                    "Error de Análisis", JOptionPane.ERROR_MESSAGE);
            actualizarEstado("Error en el análisis");
        }
    }

    private void mostrarAnalisisCompleto(ResultadoAnalisis resultado, String fuente) {
        StringBuilder reporte = new StringBuilder();

        // Encabezado del análisis
        reporte.append("ANALISIS MUSICAL COMPLETO\n");
        reporte.append("=".repeat(60)).append("\n");
        reporte.append("Fuente: ").append(fuente).append("\n");
        reporte.append("Analisis realizado: ").append(new Date()).append("\n");
        reporte.append("Motor: Analizador Sintactico v2.0\n\n");

        // Análisis por párrafos
        List<List<String>> parrafos = resultado.getParrafos();
        Map<String, Integer> conteoGlobal = resultado.getConteoGlobal();

        for (int i = 0; i < parrafos.size(); i++) {
            List<String> parrafo = parrafos.get(i);
            reporte.append("PARRAFO ").append(i + 1).append("\n");
            reporte.append("─".repeat(40)).append("\n");
            reporte.append("Contenido: ").append(String.join(" ", parrafo)).append("\n");
            reporte.append("Total de notas: ").append(parrafo.size()).append("\n");
            reporte.append("Distribucion por tipo:\n");

            // Calcular conteo por párrafo
            Map<String, Integer> conteo = new HashMap<>();
            for (String nota : parrafo) {
                conteo.put(nota, conteo.getOrDefault(nota, 0) + 1);
            }

            conteo.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entrada -> reporte.append("   ").append(entrada.getKey())
                            .append(": ").append(entrada.getValue())
                            .append(" vez").append(entrada.getValue() > 1 ? "es" : "")
                            .append("\n"));
            reporte.append("\n");
        }

        // Resumen global
        reporte.append("RESUMEN GLOBAL\n");
        reporte.append("=".repeat(40)).append("\n");
        reporte.append("Total de parrafos musicales: ").append(parrafos.size()).append("\n");

        int totalNotas = parrafos.stream().mapToInt(List::size).sum();
        reporte.append("Total de notas analizadas: ").append(totalNotas).append("\n");
        reporte.append("Notas unicas encontradas: ").append(conteoGlobal.size()).append("\n\n");

        reporte.append("RANKING DE NOTAS MAS FRECUENTES:\n");
        conteoGlobal.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entrada -> {
                    double porcentaje = (entrada.getValue() * 100.0) / totalNotas;
                    reporte.append(String.format("   %s: %d apariciones (%.1f%%)\n",
                            entrada.getKey(), entrada.getValue(), porcentaje));
                });

        reporte.append("\nPreparando reproduccion musical...");

        areaResultados.setText(reporte.toString());
    }

    /**
     * Inicia la reproducción musical en un hilo separado para no bloquear la
     * interfaz
     */
    private void iniciarReproduccionMusical(ResultadoAnalisis resultado) {
        hiloReproduccion = new Thread(() -> {
            try {
                reproduciendo = true;
                detenerSolicitado = false;

                SwingUtilities.invokeLater(() -> {
                    btnCargarArchivo.setEnabled(false);
                    btnEjemplo.setEnabled(false);
                    btnDetener.setEnabled(true);
                    actualizarEstado("Reproduciendo...");
                });

                reproducirPartituraCompleta(resultado);

            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> areaResultados.append("\n\nReproduccion interrumpida"));
            } catch (Exception e) {
                SwingUtilities.invokeLater(
                        () -> areaResultados.append("\n\nError durante la reproduccion: " + e.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    reproduciendo = false;
                    btnCargarArchivo.setEnabled(true);
                    btnEjemplo.setEnabled(true);
                    btnDetener.setEnabled(false);
                    actualizarEstado("Reproduccion completada");
                });
            }
        });

        hiloReproduccion.start();
    }

    /**
     * Ejecuta la reproducción completa de la partitura párrafo por párrafo
     * Incluye pausas entre párrafos para una mejor experiencia auditiva
     */
    private void reproducirPartituraCompleta(ResultadoAnalisis resultado) throws InterruptedException {
        SwingUtilities.invokeLater(() -> areaResultados.append("\n\nComenzando la experiencia musical!\n"));

        List<List<String>> parrafos = resultado.getParrafos();
        for (int i = 0; i < parrafos.size() && !detenerSolicitado; i++) {
            List<String> parrafo = parrafos.get(i);

            final int numeroParrafo = i + 1;
            SwingUtilities.invokeLater(() -> {
                areaResultados.append(String.format("\nReproduciendo parrafo %d de %d...\n",
                        numeroParrafo, parrafos.size()));
                actualizarEstado(String.format("Parrafo %d/%d", numeroParrafo, parrafos.size()));
            });

            // Reproducir cada nota del párrafo
            for (String nota : parrafo) {
                if (detenerSolicitado)
                    break;

                generadorSonido.reproducirNota(nota, 500);
                Thread.sleep(200); // Pausa entre notas
            }

            // Pausa entre párrafos (si no es el último)
            if (i < parrafos.size() - 1 && !detenerSolicitado) {
                Thread.sleep(800);
            }
        }

        if (!detenerSolicitado) {
            SwingUtilities.invokeLater(
                    () -> areaResultados.append("\n\nReproduccion musical completada exitosamente!"));
        }
    }

    private void actualizarEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    // CLASES INTERNAS PARA EL MOTOR MUSICAL

    /**
     * Maneja las reglas gramaticales para el análisis de notas musicales
     */
    private static class GramaticaMusical {
        // Expresión regular principal para reconocer notas musicales
        private final Pattern patronNota = Pattern.compile(
                "\\b(DO|RE|MI|FA|SOL|LA|SI)([#']*)\\b",
                Pattern.CASE_INSENSITIVE);

        // Mapa de frecuencias para cada nota (en Hz)
        private final Map<String, Double> frecuenciasBase;

        public GramaticaMusical() {
            frecuenciasBase = new HashMap<>();
            inicializarFrecuencias();
        }

        /**
         * Inicializa las frecuencias base de las notas musicales en la octava media
         */
        private void inicializarFrecuencias() {
            // Octava media (4ta octava)
            frecuenciasBase.put("DO", 261.63);
            frecuenciasBase.put("DO#", 277.18);
            frecuenciasBase.put("RE", 293.66);
            frecuenciasBase.put("RE#", 311.13);
            frecuenciasBase.put("MI", 329.63);
            frecuenciasBase.put("FA", 349.23);
            frecuenciasBase.put("FA#", 369.99);
            frecuenciasBase.put("SOL", 392.00);
            frecuenciasBase.put("SOL#", 415.30);
            frecuenciasBase.put("LA", 440.00);
            frecuenciasBase.put("LA#", 466.16);
            frecuenciasBase.put("SI", 493.88);
        }

        public Pattern getPatronNota() {
            return patronNota;
        }

        public double obtenerFrecuencia(String nota) {
            String notaLimpia = nota.toUpperCase();
            String notaBase = notaLimpia.replaceAll("['']", "");

            Double frecuenciaBase = frecuenciasBase.get(notaBase);
            if (frecuenciaBase == null)
                return 440.0; // Fallback a LA

            // Calcular octava basada en apóstrofes
            int numeroApostrofes = notaLimpia.length() - notaBase.length();
            return frecuenciaBase * Math.pow(2, numeroApostrofes);
        }
    }

    /**
     * Analizador sintáctico que procesa texto musical y crea estructuras de datos
     */
    private static class AnalizadorMusical {
        private final GramaticaMusical gramatica;

        public AnalizadorMusical(GramaticaMusical gramatica) {
            this.gramatica = gramatica;
        }

        public ResultadoAnalisis analizarTexto(String texto) {
            List<List<String>> parrafos = new ArrayList<>();
            List<Map<String, Integer>> conteosParrafos = new ArrayList<>();
            String[] lineasTexto = texto.split("\n");

            for (String linea : lineasTexto) {
                if (linea.trim().isEmpty())
                    continue;

                List<String> notasParrafo = new ArrayList<>();
                Map<String, Integer> conteoParrafo = new HashMap<>();
                Matcher matcher = gramatica.getPatronNota().matcher(linea.trim());

                while (matcher.find()) {
                    String notaCompleta = matcher.group().toUpperCase();
                    notasParrafo.add(notaCompleta);

                    // Contar por tipo de nota (sin modificadores)
                    String notaBase = notaCompleta.replaceAll("['#]", "");
                    conteoParrafo.put(notaBase, conteoParrafo.getOrDefault(notaBase, 0) + 1);
                }

                if (!notasParrafo.isEmpty()) {
                    parrafos.add(notasParrafo);
                    conteosParrafos.add(conteoParrafo);
                }
            }

            return new ResultadoAnalisis(parrafos, conteosParrafos);
        }
    }

    /**
     * Genera audio sintético para las notas musicales
     */
    private static class GeneradorSonido {
        private SourceDataLine lineaAudio;
        private boolean detenido = false;

        public void reproducirNota(String nota, int duracionMs) {
            if (detenido)
                return;

            try {
                GramaticaMusical gramatica = new GramaticaMusical();
                double frecuencia = gramatica.obtenerFrecuencia(nota);

                AudioFormat formato = new AudioFormat(44100, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, formato);

                if (lineaAudio != null) {
                    lineaAudio.close();
                }

                lineaAudio = (SourceDataLine) AudioSystem.getLine(info);
                lineaAudio.open(formato);
                lineaAudio.start();

                generarOnda(frecuencia, duracionMs);

                lineaAudio.drain();
                lineaAudio.close();

            } catch (Exception e) {
                System.err.println("Error generando audio para nota " + nota + ": " + e.getMessage());
            }
        }

        /**
         * Genera una onda sinusoidal con envolvente ADSR para crear sonido natural
         * Utiliza síntesis aditiva con frecuencia específica y duración determinada
         */
        private void generarOnda(double frecuencia, int duracionMs) {
            int muestrasTotal = (int) (44100 * duracionMs / 1000.0);
            byte[] buffer = new byte[muestrasTotal * 2]; // 16-bit = 2 bytes por muestra

            for (int i = 0; i < muestrasTotal && !detenido; i++) {
                double tiempo = i / 44100.0;

                // Generar onda seno con envolvente para suavizar
                double envolvente = calcularEnvolvente(tiempo, duracionMs / 1000.0);
                double amplitud = envolvente * Math.sin(2 * Math.PI * frecuencia * tiempo);

                short muestra = (short) (amplitud * 16000); // Amplitud moderada
                buffer[i * 2] = (byte) (muestra & 0xFF);
                buffer[i * 2 + 1] = (byte) ((muestra >> 8) & 0xFF);
            }

            if (!detenido && lineaAudio != null) {
                lineaAudio.write(buffer, 0, buffer.length);
            }
        }

        private double calcularEnvolvente(double tiempo, double duracionTotal) {
            double fadeDuration = Math.min(0.05, duracionTotal * 0.1); // Fade de 5% de la duración

            if (tiempo < fadeDuration) {
                return tiempo / fadeDuration; // Fade in
            } else if (tiempo > duracionTotal - fadeDuration) {
                return (duracionTotal - tiempo) / fadeDuration; // Fade out
            } else {
                return 1.0; // Volumen completo
            }
        }

        public void detenerTodo() {
            detenido = true;
            if (lineaAudio != null) {
                lineaAudio.stop();
                lineaAudio.close();
            }
        }
    }

    /**
     * Resultado del análisis musical
     */
    private static class ResultadoAnalisis {
        private final List<List<String>> parrafos;
        private final List<Map<String, Integer>> conteosParrafos;
        private final Map<String, Integer> conteoGlobal;
        private final int totalNotas;

        public ResultadoAnalisis(List<List<String>> parrafos, List<Map<String, Integer>> conteosParrafos) {
            this.parrafos = parrafos;
            this.conteosParrafos = conteosParrafos;

            // Calcular estadísticas globales
            this.conteoGlobal = new HashMap<>();
            this.totalNotas = parrafos.stream().mapToInt(List::size).sum();

            for (Map<String, Integer> conteoParrafo : conteosParrafos) {
                for (Map.Entry<String, Integer> entrada : conteoParrafo.entrySet()) {
                    conteoGlobal.put(entrada.getKey(),
                            conteoGlobal.getOrDefault(entrada.getKey(), 0) + entrada.getValue());
                }
            }
        }

        public List<List<String>> getParrafos() {
            return parrafos;
        }

        public List<Map<String, Integer>> getConteosParrafos() {
            return conteosParrafos;
        }

        public Map<String, Integer> getConteoGlobal() {
            return conteoGlobal;
        }

        public int getTotalNotas() {
            return totalNotas;
        }
    }

    /**
     * Punto de entrada principal del programa
     */
    public static void main(String[] args) {
        // Configurar look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo configurar el look and feel: " + e.getMessage());
        }

        // Configurar para alta resolución
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Iniciar la aplicación en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                SistemaMusica sistema = new SistemaMusica();
                sistema.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error al iniciar el sistema: " + e.getMessage(),
                        "Error Critical", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
# Sistema Musical Accesible - Transformando Texto en Melodía

## Descripción del Proyecto

Este proyecto implementa un **Sistema Musical Accesible** desarrollado como parte del curso de **Autómatas y Lenguajes Formales**. El sistema convierte partituras musicales escritas en texto plano a experiencias sonoras en tiempo real, utilizando técnicas de análisis léxico y sintáctico con expresiones regulares y gramática formal.

### Objetivo Principal

Crear una herramienta accesible que permita a personas con discapacidad visual experimentar música a través de partituras de texto, demostrando la aplicación práctica de conceptos teóricos de lenguajes formales en un contexto real y útil.

## Características Principales

### Análisis Léxico y Sintáctico

- **Expresiones Regulares**: Reconocimiento automático de notas musicales con modificadores
- **Gramática Formal**: Procesamiento de estructuras musicales complejas
- **Análisis por Párrafos**: Organización lógica de la música en secciones

### Notación Musical Soportada

- **Notas Básicas**: DO, RE, MI, FA, SOL, LA, SI
- **Modificadores**:
  - `#` - Sostenido (ej: `RE#`, `SOL#`)
  - `'` - Octava superior (ej: `LA'`, `DO''`)
- **Formato**: Notas separadas por espacios, una línea por párrafo musical

### Generación de Audio

- **Síntesis en Tiempo Real**: Generación de ondas sinusoidales
- **Frecuencias Musicales**: Basadas en el estándar de afinación A4 = 440Hz
- **Control de Duración**: Notas de duración configurable
- **Envolvente ADSR**: Sonidos naturales con ataque, decaimiento, sostenimiento y liberación

### Accesibilidad

- **Interfaz Gráfica Simple**: Diseño minimalista y claro
- **Atajos de Teclado**:
  - `Ctrl+O`: Cargar archivo
  - `Ctrl+E`: Usar ejemplo
  - `Escape`: Detener reproducción
- **Narración por Voz**: Descripciones detalladas de los análisis
- **Navegación por Teclado**: Totalmente operable sin mouse

### Análisis Estadístico

- **Conteo por Párrafo**: Distribución de notas en cada sección
- **Análisis Global**: Estadísticas completas de la composición
- **Ranking de Notas**: Frecuencia de uso de cada nota musical

## Requisitos del Sistema

### Requisitos Técnicos

- **Java**: JDK 8 o superior
- **Sistema Operativo**: Windows, macOS, Linux
- **Memoria**: 256MB RAM mínimo
- **Espacio en Disco**: 50MB
- **Audio**: Sistema de audio compatible con Java Sound API

### Dependencias

- **Java Swing**: Para la interfaz gráfica
- **Java Sound API**: Para síntesis de audio
- **Expresiones Regulares**: Motor integrado de Java

## Estructura del Proyecto

```
ProyectoAutomatas/
├── src/
│   └── proyectoautomatas/
│       └── SistemaMusica.java          # Clase principal
├── partitura_ejemplo.txt                # Archivo de ejemplo
├── build.xml                           # Configuración Ant
├── manifest.mf                         # Manifiesto JAR
├── nbproject/                          # Configuración NetBeans
└── README.md                           # Este archivo
```

## Uso del Sistema

### Inicio Rápido

1. **Ejecutar**: `java -cp src proyectoautomatas.SistemaMusica`
2. **Cargar Archivo**: `Ctrl+O` o botón "Cargar Partitura"
3. **Usar Ejemplo**: `Ctrl+E` o botón "Usar Ejemplo"
4. **Disfrutar**: La música se reproduce automáticamente tras el análisis

### Formato de Archivo de Entrada

Los archivos de texto deben contener notas musicales separadas por espacios:

```
DO RE MI FA SOL LA SI
DO# RE# MI FA# SOL# LA# SI
LA' SI' DO'' RE'' MI''
SOL SOL SOL RE# FA FA FA RE
```

### Ejemplo de Uso

```bash
# Compilar
javac -cp src src/proyectoautomatas/*.java

# Ejecutar
java -cp src proyectoautomatas.SistemaMusica
```

## Arquitectura Técnica

### Componentes Principales

#### 1. **SistemaMusica** (Clase Principal)

- **Interfaz Gráfica**: JFrame con componentes Swing
- **Control de Eventos**: Manejo de acciones del usuario
- **Coordinación**: Orquesta análisis y reproducción

#### 2. **GramaticaMusical** (Análisis Léxico)

- **Patrones Regex**: Definición de expresiones regulares para notas
- **Frecuencias**: Mapeo nota → frecuencia en Hz
- **Validación**: Verificación de sintaxis musical

#### 3. **AnalizadorMusical** (Análisis Sintáctico)

- **Procesamiento de Texto**: Conversión de string a estructuras de datos
- **Análisis por Párrafos**: Organización en secciones musicales
- **Estadísticas**: Conteo y análisis de frecuencia

#### 4. **GeneradorSonido** (Síntesis de Audio)

- **Onda Sinusoidal**: Generación de tonos puros
- **Control de Duración**: Temporización precisa
- **Envolvente ADSR**: Calidad de sonido natural

#### 5. **ResultadoAnalisis** (Estructura de Datos)

- **Párrafos**: Lista de listas de notas musicales
- **Conteos**: Estadísticas de frecuencia por párrafo y globales

### Algoritmos Implementados

#### Análisis Léxico

```java
// Patrón regex para reconocer notas musicales
Pattern patronNota = Pattern.compile(
    "\\b(DO|RE|MI|FA|SOL|LA|SI)(#)?('*)\\b",
    Pattern.CASE_INSENSITIVE
);
```

#### Síntesis de Audio

```java
// Generación de onda sinusoidal con envolvente
double envolvente = calcularEnvolvente(tiempo, duracion);
double muestra = Math.sin(2 * Math.PI * frecuencia * tiempo) * envolvente;
```

## Resultados de Análisis

El sistema proporciona análisis detallados incluyendo:

- **Análisis por Párrafo**: Conteo de notas en cada sección
- **Estadísticas Globales**: Total de notas y distribución general
- **Ranking de Frecuencia**: Notas más utilizadas en la composición
- **Informes Narrativos**: Descripciones accesibles de los resultados

## Aspectos Académicos

### Conceptos de Autómatas Aplicados

1. **Lenguajes Regulares**: Expresiones regulares para reconocimiento de tokens
2. **Autómatas Finitos**: Procesamiento de cadenas de entrada
3. **Gramáticas Formales**: Definición de sintaxis musical
4. **Análisis Sintáctico**: Parsing de estructuras musicales

### Contribuciones Técnicas

- **Implementación Práctica**: Aplicación de teoría abstracta a problema real
- **Accesibilidad**: Diseño inclusivo para usuarios con discapacidades
- **Interfaz Intuitiva**: Experiencia de usuario simplificada
- **Generación en Tiempo Real**: Procesamiento eficiente de audio

## Autor

**Juan Enrique Samayoa Reyes**

- **Carné**: 9390-23-2010
- **Curso**: Autómatas y Lenguajes Formales
- **Proyecto**: Sistema Musical Accesible

## Licencia

Este proyecto fue desarrollado como trabajo académico para el curso de Autómatas y Lenguajes Formales. Todos los derechos reservados.

## Agradecimientos

- **Docente**: Por la guía en conceptos de lenguajes formales
- **Comunidad Java**: Por las poderosas APIs de Swing y Sound
- **Estándares Musicales**: Por las frecuencias y notación universal

---

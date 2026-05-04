# Flujo CLI para agentes sobre proyectos `.circ`

Esta guía describe un flujo **estable y automatizable** para agentes que procesan proyectos Logisim-evolution en modo headless.

## Comandos CLI soportados (carga/validación/exportación)

Basado en los flags implementados en `Startup`:

- `--validate-circ <archivo.circ>`: carga y valida un proyecto `.circ`.
- `--export-image <archivo.circ> [subcircuito] <png|svg> <salida>`: exporta imagen del circuito principal o de un subcircuito.
- `--all-circuits`: junto con exportación, procesa todos los subcircuitos aplicables.
- `--json`: en validación, emite salida estructurada JSON.
- `--timeout-ms <ms>`: timeout interno para operaciones de CLI.
- `--toplevel-circuit <nombre>`: selecciona circuito principal para operaciones que lo soportan.

Recomendación operativa: combinar `--timeout-ms` con timeout del sistema operativo para evitar procesos colgados.

## Ejemplos copy/paste

> Sustituye `logisim-evolution.jar` por el nombre real de tu artefacto (por ejemplo `logisim-evolution-<version>-all.jar`).

### Linux (bash)

```bash
# 1) Validar proyecto (texto)
timeout --signal=TERM 35s java -jar logisim-evolution.jar \
  --timeout-ms 30000 \
  --validate-circ proyecto.circ

# 2) Validar proyecto (JSON)
timeout --signal=TERM 35s java -jar logisim-evolution.jar \
  --timeout-ms 30000 \
  --validate-circ proyecto.circ \
  --json

# 3) Exportar PNG de un subcircuito concreto
timeout --signal=TERM 35s java -jar logisim-evolution.jar \
  --timeout-ms 30000 \
  --export-image proyecto.circ ALU png out/ALU.png

# 4) Exportar SVG del circuito principal
timeout --signal=TERM 35s java -jar logisim-evolution.jar \
  --timeout-ms 30000 \
  --export-image proyecto.circ svg out/top.svg
```

### macOS (zsh/bash)

```bash
# Si tienes coreutils: gtimeout; si no, usa timeout de tu entorno CI
# brew install coreutils

gtimeout --signal=TERM 35s java -jar logisim-evolution.jar \
  --timeout-ms 30000 \
  --validate-circ proyecto.circ

gtimeout --signal=TERM 35s java -jar logisim-evolution.jar \
  --timeout-ms 30000 \
  --export-image proyecto.circ Datapath png out/Datapath.png
```

### Windows (PowerShell)

```powershell
# 1) Validar
java -jar .\logisim-evolution.jar --timeout-ms 30000 --validate-circ .\proyecto.circ

# 2) Validar con JSON
java -jar .\logisim-evolution.jar --timeout-ms 30000 --validate-circ .\proyecto.circ --json

# 3) Exportar subcircuito
java -jar .\logisim-evolution.jar --timeout-ms 30000 --export-image .\proyecto.circ ALU png .\out\ALU.png

# 4) Exportar top-level
java -jar .\logisim-evolution.jar --timeout-ms 30000 --export-image .\proyecto.circ svg .\out\top.svg
```

## Estrategia de debugging iterativo de subcircuitos

Para circuitos grandes, evita “validar todo al final”. Haz iteración corta:

1. **Valida rápido**: ejecuta `--validate-circ` tras cada cambio significativo.
2. **Exporta por subcircuito**: genera PNG/SVG por cada bloque crítico (`ALU`, `Control`, `Datapath`, etc.).
3. **Revisa errores y artefactos visuales**:
   - errores de parser/atributos,
   - conexiones desplazadas,
   - componentes superpuestos,
   - diferencias esperadas en apariencia.
4. **Corrige y reintenta** con el mismo comando (idealmente scriptado).

Este ciclo minimiza tiempo de diagnóstico y reduce regresiones acumuladas.

## No cambiar formato `.circ`

Regla explícita para agentes:

- **No introducir formatos alternativos** ni “versiones propias” del archivo.
- El sistema **solo parsea/renderiza el formato vigente de Logisim-evolution** (XML `.circ` aceptado por `XmlReader`).
- Cualquier transformación debe preservar compatibilidad con ese parser.
- Si un agente necesita metadatos auxiliares, guardarlos fuera del `.circ` (por ejemplo JSON externo), nunca embebiendo estructuras no soportadas.

En resumen: el `.circ` es contrato de compatibilidad; no debe redefinirse desde herramientas de agente.

## Guía de estructura `.circ` según parser actual (`XmlReader`)

Resumen práctico basado en el flujo real de lectura:

- Nodo raíz de proyecto con secciones de configuración (opciones, toolbar/mappings/librerías) y circuitos.
- Circuitos representados por nodos `<circuit ...>`.
- Dentro de cada `<circuit>`:
  - atributos en nodos `<a name="..." val="...">` (o contenido textual en casos concretos),
  - componentes `<comp ...>` con atributos como `name`, `lib`, `loc`, más sus `<a ...>`,
  - cableado `<wire from="(x,y)" to="(x,y)">`,
  - apariencia `<appear>` con elementos SVG/shape soportados.
- Bibliotecas con `<lib ...>` y herramientas `<tool ...>` (con atributos `<a ...>`).

Convenciones importantes observadas en el parser:

- `comp` requiere `name` y `loc`; sin eso, la carga falla.
- `lib` en `comp` debe resolver a una librería conocida por el proyecto.
- `wire` exige `from` y `to` válidos; cables de longitud cero se ignoran.
- La mayoría de atributos se parsean desde `val`; para ciertos casos se usa texto interno del nodo.
- `filePath` se de-relativiza respecto al `.circ` de origen durante la lectura.
- El parser aplica normalizaciones de compatibilidad (p. ej. etiquetas válidas para VHDL y limpieza de labels heredados en toolbar/lib).

## Flujo recomendado para agentes complejos

Pipeline recomendado (CI/automatización):

1. **Validar**
   - `--validate-circ` (+ `--json` si quieres parsing automático de resultados).
2. **Exportar imagen por subcircuito**
   - `--export-image <circ> <subcircuito> png <ruta>` por cada bloque relevante.
3. **Revisar errores/salida**
   - códigos de salida,
   - stderr con marcadores de timeout/interrupción,
   - inspección visual de imágenes exportadas.
4. **Volver a intentar**
   - aplicar correcciones puntuales,
   - repetir validación + export por bloque hasta converger.

Sugerencia: encapsular en script que falle rápido y preserve logs e imágenes por intento (`artifacts/run-<timestamp>/...`).

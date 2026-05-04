# AgentWorkSpace (lista para usar)

Sí: **queda lista** para clonar/inicializar como repositorio independiente y pedirle a un agente _"construye tal circuito"_.

## Qué necesitas antes de arrancar
1. Java 21+ disponible (`java -version`).
2. Ejecutar `bash scripts/bootstrap.sh` para crear estructura e intentar copiar el jar automáticamente.
3. Si no lo encuentra, copiar manualmente el jar a `tools/logisim-evolution.jar`.
   - Ejemplo desde repo principal: `build/libs/logisim-evolution-4.2.0dev-all.jar`.
4. Crear tu circuito en `circuits/` (por ejemplo `circuits/proyecto.circ`).

## Estructura recomendada
- `circuits/` → archivos `.circ` de trabajo.
- `out/` → imágenes exportadas (`png`/`svg`).
- `logs/` → evidencias de ejecución.
- `tools/logisim-evolution.jar` → binario de ejecución.

## Primeros comandos (copy/paste)
```bash
bash scripts/bootstrap.sh
# Si bootstrap no encontró jar: cp /ruta/a/logisim-evolution-*-all.jar tools/logisim-evolution.jar

# Validar
bash scripts/validate.sh circuits/proyecto.circ

# Validar en JSON
bash scripts/validate.sh circuits/proyecto.circ --json

# Exportar subcircuito a PNG
bash scripts/export.sh circuits/proyecto.circ ALU png out/ALU.png

# Exportar circuito principal a SVG
bash scripts/export.sh circuits/proyecto.circ svg out/top.svg
```

## Contrato para el agente
- No cambiar formato/sintaxis `.circ`.
- Iterar: editar -> validar -> exportar -> revisar -> repetir.
- Entregar siempre logs + códigos de salida.

## Documentación adicional
- `agent_workflow.md`
- `agent_kill_strategy.md`
- `WORKSPACE_CIRCUIT_GUIDE.md`
- `docs.md`


## ¿Dónde está el jar y por qué no se commiteó aquí?
- El jar se genera en el repo principal con `./gradlew shadowJar` y queda en `build/libs/*-all.jar`.
- No se versionó dentro de `AgentWorkSpace` para evitar binarios pesados/desactualizados y mantener reproducibilidad (cada entorno genera o copia su binario vigente).

#!/usr/bin/env bash
set -euo pipefail

mkdir -p circuits out logs tools

echo "[bootstrap] Estructura creada: circuits/ out/ logs/ tools/"

if [[ -f tools/logisim-evolution.jar ]]; then
  echo "[bootstrap] tools/logisim-evolution.jar ya existe."
  exit 0
fi

# Caso común: AgentWorkSpace dentro del repo principal
if [[ -f ../build/libs/logisim-evolution-4.2.0dev-all.jar ]]; then
  cp ../build/libs/logisim-evolution-4.2.0dev-all.jar tools/logisim-evolution.jar
  echo "[bootstrap] Copiado desde ../build/libs/logisim-evolution-4.2.0dev-all.jar"
  exit 0
fi

# Fallback: buscar cualquier *-all.jar en ../build/libs/
if compgen -G "../build/libs/*-all.jar" > /dev/null; then
  SRC="$(ls -1 ../build/libs/*-all.jar | head -n1)"
  cp "$SRC" tools/logisim-evolution.jar
  echo "[bootstrap] Copiado desde $SRC"
  exit 0
fi

cat >&2 <<MSG
[bootstrap] No encontré el jar automáticamente.
Opciones:
  1) Desde el repo principal ejecutar: ./gradlew shadowJar
  2) Copiar manualmente build/libs/*-all.jar a tools/logisim-evolution.jar
MSG
exit 2

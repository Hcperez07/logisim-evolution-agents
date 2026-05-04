#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 3 ]]; then
  echo "Uso: $0 <archivo.circ> [subcircuito] <png|svg> <salida>" >&2
  exit 2
fi

JAR="tools/logisim-evolution.jar"
if [[ ! -f "$JAR" ]]; then
  echo "Falta $JAR" >&2
  exit 2
fi

if [[ $# -eq 3 ]]; then
  CIRC="$1"
  FORMAT="$2"
  OUT="$3"
  timeout --signal=TERM 35s java -jar "$JAR" --timeout-ms 30000 --export-image "$CIRC" "$FORMAT" "$OUT"
else
  CIRC="$1"
  SUB="$2"
  FORMAT="$3"
  OUT="$4"
  timeout --signal=TERM 35s java -jar "$JAR" --timeout-ms 30000 --export-image "$CIRC" "$SUB" "$FORMAT" "$OUT"
fi

#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Uso: $0 <archivo.circ> [--json]" >&2
  exit 2
fi

CIRC="$1"
EXTRA="${2:-}"
JAR="tools/logisim-evolution.jar"

if [[ ! -f "$JAR" ]]; then
  echo "Falta $JAR" >&2
  exit 2
fi

if [[ "$EXTRA" == "--json" ]]; then
  timeout --signal=TERM 35s java -jar "$JAR" --timeout-ms 30000 --validate-circ "$CIRC" --json
else
  timeout --signal=TERM 35s java -jar "$JAR" --timeout-ms 30000 --validate-circ "$CIRC"
fi

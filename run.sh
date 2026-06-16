#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src/main/java"
OUT_DIR="$SCRIPT_DIR/target/classes"
GSON_JAR="$HOME/.m2/repository/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar"

if [[ ! -f "$GSON_JAR" ]]; then
  echo "Erro: não foi possível encontrar o jar do Gson em: $GSON_JAR"
  exit 1
fi

mkdir -p "$OUT_DIR"
echo "Compilando..."
java_files=($(find "$SRC_DIR" -name '*.java'))
if [[ ${#java_files[@]} -eq 0 ]]; then
  echo "Erro: nenhum arquivo Java encontrado em $SRC_DIR"
  exit 1
fi
javac -encoding UTF-8 --release 21 -cp "$GSON_JAR" -d "$OUT_DIR" "${java_files[@]}"

echo "Iniciando sistema..."
java -Dfile.encoding=UTF-8 -cp "$OUT_DIR:$GSON_JAR" com.estoque.Main

#!/bin/bash
# Launch Five Nights at Freddy's 1
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

# Recompile if out doesn't exist
if [ ! -d "out/production/FNAF" ]; then
    echo "Compiling FNAF..."
    mkdir -p out/production/FNAF
    javac -d out/production/FNAF $(find src -name "*.java")
fi

echo "Starting Five Nights at Freddy's..."
java -cp out/production/FNAF Main "$@"

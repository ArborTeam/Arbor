#!/bin/sh
echo "Setting up projects"
./gradlew --refresh-dependencies applyAllPatches
echo "Building"
./gradlew --refresh-dependencies createMojmapPaperclipJar
#!/bin/sh
echo "Rebuilding server patches"
./gradlew rebuildAllServerPatches
echo "Rebuilding API patches"
./gradlew rebuildPaperApiPatches rebuildFoliaApiPatches
echo "Rebuilding build settings patches"
./gradlew rebuildFoliaSingleFilePatches
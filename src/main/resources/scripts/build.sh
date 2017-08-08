#!/bin/sh

echo Building SpecDb 

APP_DIR=~/SpecDb
TMP_DIR=~/SpecDb/tmp
TARGET=~/SpecDb/target

echo removing target from $PWD
rm -r $APP_DIR/target

echo mvn install...
mvn install
echo mvn install complete

cp -r $TARGET/spec-db-1.0.0.jar $TMP_DIR/specdb.jar 

echo build complete!

exit
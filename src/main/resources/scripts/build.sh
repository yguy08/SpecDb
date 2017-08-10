#!/bin/sh

echo Building SpecDb 

APP_DIR=~/SpecDb
TMP_DIR=~/SpecDb/tmp
TARGET=~/SpecDb/target

echo removing target from $APP_DIR/target
rm -r $APP_DIR/target
rm -r $TMP_DIR

cd $APP_DIR

echo mvn install...
mvn install
echo mvn install complete

mkdir $TMP_DIR
cp -r $TARGET/spec-db-1.0.0.jar $TMP_DIR/specdb.jar 

echo build complete!

exit
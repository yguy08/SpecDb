#!/bin/sh

echo Building SpecDb 

APP_DIR=~/SpecDb
TMP_DIR=~/SpecDb/tmp
TARGET=~/SpecDb/target

echo removing target from $APP_DIR/target
rm -r $APP_DIR/target

cd $APP_DIR

echo mvn install...
mvn clean install
echo mvn install complete

echo build complete!

exit

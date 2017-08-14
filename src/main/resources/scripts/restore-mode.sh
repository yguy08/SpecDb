#!/bin/sh

echo RESTORE MODE

APP_DIR=~/SpecDb
TMP_DIR=~/SpecDb/tmp
TARGET=~/SpecDb/target

nohup java -cp $TMP_DIR/specdb.jar com.speculation1000.specdb.start.StartApp r &

cd $APP_DIR/logs

tail -f SpecDb0.log

exit
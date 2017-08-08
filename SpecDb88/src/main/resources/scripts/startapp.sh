#!/bin/sh

echo Running SpecDb on Raspberry Pi

APP_DIR=~/SpecDb
TMP_DIR=~/SpecDb/tmp
TARGET=~/SpecDb/target

echo ssh to pi
ssh pi@192.168.1.197 <<zzz23EndOfStatuszzz23
  nohup java -cp specdb.jar com.speculation1000.specdb.StartApp $1 &
  ps -e | grep java
  echo Start up complete...CTRL-C to exit
zzz23EndOfStatuszzz23

echo start up complete!

exit
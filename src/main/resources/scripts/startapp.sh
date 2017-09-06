#!/bin/sh

echo Running SpecDb on Raspberry Pi

APP_DIR=~/SpecDb
TMP_DIR=~/SpecDb/tmp
TARGET=~/SpecDb/target

echo ssh to pi
ssh pi@192.168.1.151 <<zzz23EndOfStatuszzz23
  cd /home/pi/SpecDb/
  nohup java -cp target/specdb-1.0.jar com.speculation1000.specdb.start.StartApp &
  ps -e | grep java
  echo Start up complete...CTRL-C to exit
zzz23EndOfStatuszzz23

echo start up complete!

exit

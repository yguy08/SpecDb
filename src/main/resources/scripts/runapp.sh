#!/bin/sh

echo Running SpecDb on Raspberry Pi

APP_DIR=~/SpecDb
TMP_DIR=~/SpecDb/tmp
TARGET=~/SpecDb/target

echo Ending previous run and copying new jar to $TMP_DIR
sftp pi@192.168.1.197 <<zzz23EndOfSftpzzz23
  killall -w java
  echo app stopped!
  rm $TMP_DIR/spec-db-1.0.0.jar
  put -r $TMP_DIR/specdb.jar   
  exit
zzz23EndOfSftpzzz23

echo ssh to pi
ssh pi@192.168.1.197 <<zzz23EndOfStatuszzz23
  killall -w java 
  nohup java -cp specdb.jar com.speculation1000.specdb.StartApp $1 &
  ps -e | grep java
  echo Start up complete...CTRL-C to exit and tail log
zzz23EndOfStatuszzz23

echo start up complete!

exit
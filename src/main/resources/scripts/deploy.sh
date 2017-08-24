#!/bin/sh

echo *****************
echo [ DEPLOY APP ]
echo *****************

APP_DIR=~/SpecDb
TARGET=~/SpecDb/target
TMP_DIR=~/SpecDb/tmp
SCRIPTS=~/SpecDb/src/main/rescources/scripts

echo cleaning up raspberry pi
ssh pi@192.168.1.151 <<zzz23EndOfStatuszzz23
  echo stopping h2 server!
  cd /home/pi/SpecDb/
  java -cp target/specdb-1.0.jar com.speculation1000.specdb.start.DbServer stop
  sleep 20
  echo h2 server stopped!
  echo Killing java process on Raspberry pi...
  killall -w java
  sleep 20
  echo java processes stopped!
  ps -e | grep java  
  echo Removing specdb jar from pi
  rm -r /home/pi/SpecDb/target
  mkdir /home/pi/SpecDb/target
  echo Cleaning up old log file
  rm -r /home/pi/SpecDb/logs/Spec*
  echo Cleaning up scripts folder
  rm -r /home/pi/SpecDb/scripts
  mkdir /home/pi/SpecDb/scripts
  exit
zzz23EndOfStatuszzz23

echo Raspberry pi cleaned up...

echo Copying target files to Raspberry Pi...

cd $TARGET

sftp pi@192.168.1.151 <<zzz23EndOfSftpzzz23
  cd /home/pi/SpecDb/target
  put -r $TARGET/specdb-1.0.jar
  cd /home/pi/SpecDb/scripts
  put -r $SCRIPTS/cleanlogs.sh      
  exit
zzz23EndOfSftpzzz23

echo Deploy to pi complete!

exit

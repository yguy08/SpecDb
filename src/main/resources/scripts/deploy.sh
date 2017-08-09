#!/bin/sh

echo *****************
echo [ DEPLOY APP ]
echo *****************

APP_DIR=~/SpecDb
TARGET=~/SpecDb/target
TMP_DIR=~/SpecDb/tmp

echo cleaning up raspberry pi
ssh pi@192.168.1.197 <<zzz23EndOfStatuszzz23
  echo Killing java process on Raspberry pi...
  killall -w java
  echo java processes stopped!
  ps -e | grep java
  echo Removing spec-db.jar from pi
  rm -r /home/pi/SpecDb/tmp
  mkdir /home/pi/SpecDb/tmp
  exit
zzz23EndOfStatuszzz23

echo Raspberry pi cleaned up...

echo Copying target files to Raspberry Pi...

cd $TMP_DIR

sftp pi@192.168.1.197 <<zzz23EndOfSftpzzz23
  cd /home/pi/SpecDb/tmp
  put -r $TMP_DIR/specdb.jar   
  exit
zzz23EndOfSftpzzz23

echo Deploy to pi complete!

exit

#!/bin/sh

echo Building SpecDb for Raspberry Pi

APP_DIR=~/Desktop/SpecDb
cd $APP_DIR

echo removing target from $PWD
rm -r target

echo mvn install...
mvn install
echo mvn install complete

echo copying target files to Raspberry Pi
sftp pi@192.168.1.198 <<zzz23EndOfSftpzzz23
  killall java 
  cd ~/specDb/target
  rm spec-db-1.0.0.jar
  lcd $APP_DIR/target
  put -r spec-db-1.0.0.jar   
  exit
zzz23EndOfSftpzzz23

echo build for pi complete!

echo changing to scripts directory

cd $APP_DIR/src/main/resources/scripts

exit
#!/bin/sh

echo *****************
echo [ GET LOGS ]
echo *****************

APP_DIR=~/SpecDb
TARGET=~/SpecDb/target
TMP_DIR=~/SpecDb/tmp

cd $TMP_DIR

echo copying target files from Raspberry Pi
sftp pi@192.168.1.197 <<zzz23EndOfSftpzzz23
  get -r $APP_DIR/logs
  get -r $APP_DIR/db
  exit
zzz23EndOfSftpzzz23

echo Moving logs and db folder to tmp archive
mv -r db logs 

mv -r logs $APP_DIR/"$(date +"%Y_%m_%d_%I_%M_%p")"
echo Moved logs to SpecDb with date!

exit

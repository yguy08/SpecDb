#!/bin/sh

echo SpecDb Status Report

echo ssh to pi
ssh pi@192.168.1.198 <<zzz23EndOfSshzzz23 
  cd ~/specDb/target
  nohup java -cp spec-db-1.0.0.jar &  
  exit
zzz23EndOfSshzzz23

echo SpecDb startup complete

exit
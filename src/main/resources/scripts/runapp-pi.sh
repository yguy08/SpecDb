#!/bin/sh

echo Running SpecDb on Raspberry Pi

echo ssh to pi
ssh pi@192.168.1.197 <<zzz23EndOfStatuszzz23
  cd /home/pi/SpecDb/target
  pwd
  killall -9 java 
  nohup java -cp spec-db-1.0.0.jar loader.DbLoader $1 &
  ps -e | grep java
  echo Start up complete
  echo CTRL-C to exit and tail log
zzz23EndOfStatuszzz23

echo start up complete!

exit
#!/bin/sh

echo stopping app

ssh pi@192.168.1.197 <<zzz23EndOfStatuszzz23
  echo stopping h2 server!
  cd /home/pi/SpecDb/
  java -cp target/specdb-1.0.jar com.speculation1000.specdb.start.DbServer stop
  echo h2 server stopped!
  echo stopping java process
  killall -w java
  echo java process stopped!
  exit
zzz23EndOfStatuszzz23

echo app stopped

exit

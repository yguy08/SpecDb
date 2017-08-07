#!/bin/sh

echo stopping app

ssh pi@192.168.1.197 <<zzz23EndOfStatuszzz23
  killall -9 java
  exit
zzz23EndOfStatuszzz23

echo app stopped

exit
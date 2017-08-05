#!/bin/sh

echo Running SpecDb on Raspberry Pi

echo ssh to pi
ssh pi@192.168.1.198 <<zzz23EndOfStatuszzz23 
  cd ~/specDb/target
  tail -f Logger.txt
zzz23EndOfStatuszzz23

echo Status Report Done!

exit
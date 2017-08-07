#!/bin/sh

echo *****************
echo [ STATUS REPORT ]
echo *****************

echo copying target files from Raspberry Pi
sftp pi@192.168.1.197 <<zzz23EndOfSftpzzz23
  cd /home/pi/SpecDb
  lcd ~/SpecDb
  get -r target tmp
  exit
zzz23EndOfSftpzzz23

cp -r ~/SpecDb/tmp ~/SpecDb/"$(date +"%Y_%m_%d_%I_%M_%p")"
echo Copied target to SpecDb with date!

rm -r ~/SpecDb/tmp
echo Removed old target

cd ~/SpecDb

exit

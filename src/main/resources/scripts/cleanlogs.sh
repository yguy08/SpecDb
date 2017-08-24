// chron run every day at midnight

crontab -e

0 0 * * * /home/pi/cleanlogs.sh

_now=$(date +"%m_%d_%Y")

echo "Starting backup to SpecDb0.log..."

cd /home/pi/SpecDb/logs
mv SpecDb0.log SpecDb0$_now.log
rm SpecDb0.log


//chron job to look for log file and recreate if neccessary
if [ ! -f /tomcat_dir/log4j.log ]
then
  `touch /tomcat_dir/log4j.log`;
fi

// chron run every day at midnight

crontab -e

5 0 * * * /home/pi/cleanlogs.sh

_now=$(date +"%m_%d_%Y")

echo "Backing up SpecDb0.log..."

cd /home/pi/SpecDb/logs
mv SpecDb0.log SpecDb0$_now.log
sleep 5
touch /home/pi/SpecDb/logs/SpecDb0.log

FIXWPA=$(cat -s /etc/wpa_supplicant/wpa_supplicant.conf)
/bin/echo "$FIXWPA" > /etc/wpa_supplicant/wpa_supplicant.conf
/sbin/wpa_cli -i wlan0 reconfigure

/usr/bin/python3 /home/pi/Tesseract/Tesseract.py

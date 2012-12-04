#!/bin/bash

# IP address of:
# jetwire.casa-cloud - 172.20.55.229
# jetwire-casa.casa-cloud - 172.20.52.52
# casa-main.casa-cloud - 172.20.49.159

. setenv.sh
if [ -z "$EMAIL_ALERT_PASS" ]; then
  groovy -cp mysql-connector-java-5.1.10.jar trig-email-alert.groovy -n $EMAIL_ALERT_HOST -d $EMAIL_ALERT_DB -u $EMAIL_ALERT_USER 
else
  groovy -cp mysql-connector-java-5.1.10.jar trig-email-alert.groovy -n $EMAIL_ALERT_HOST -d $EMAIL_ALERT_DB -u $EMAIL_ALERT_USER -p $EMAIL_ALERT_PASS 
fi  

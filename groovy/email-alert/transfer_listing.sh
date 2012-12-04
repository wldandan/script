#!/bin/bash

# IP address of:
# jetwire.casa-cloud - 172.20.55.229
# jetwire-casa.casa-cloud - 172.20.52.52
# casa-main.casa-cloud - 172.20.49.159

. setenv.sh
if [ -z "$PSEEKER_PASS" ]; then
  groovy -cp jtds-1.2.5.jar update-listing-for-email-alert.groovy -n $PSEEKER_HOST -d $PSEEKER_DB -u $PSEEKER_USER
else
  groovy -cp jtds-1.2.5.jar update-listing-for-email-alert.groovy -n $PSEEKER_HOST -d $PSEEKER_DB -u $PSEEKER_USER -p $PSEEKER_PASS
fi  

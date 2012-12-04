#!/bin/bash

# IP address of:
# jetwire.casa-cloud - 172.20.55.229
# jetwire-casa.casa-cloud - 172.20.52.52
# casa-main.casa-cloud - 172.20.49.159

. setenv.sh
if [ -z "$MYREA_PASS" ]; then
  groovy -cp mysql-connector-java-5.1.10.jar save-generic-request.groovy -n $MYREA_HOST -d $MYREA_DB -u $MYREA_USER -m $USER_EMAIL
else
  groovy -cp mysql-connector-java-5.1.10.jar save-generic-request.groovy -n $MYREA_HOST -d $MYREA_DB -u $MYREA_USER -p $MYREA_PASS -m $USER_EMAIL
fi  

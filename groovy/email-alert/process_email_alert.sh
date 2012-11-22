#!/bin/bash

# IP addresses are:
# jetwire.casa-cloud - 172.20.55.229
# jetwire-casa.casa-cloud - 172.20.52.52
# casa-main.casa-cloud - 172.20.49.159

groovy -cp jtds-1.2.5.jar update-listing-for-email-alert.groovy
groovy -cp mysql-connector-java-5.1.10.jar trig-email-alert.groovy -n 172.20.52.52 -u root -p s3cr3t 

#!/bin/bash

# IP address of:
# jetwire.casa-cloud - 172.20.55.229
# jetwire-casa.casa-cloud - 172.20.52.52
# casa-main.casa-cloud - 172.20.49.159

groovy -cp mysql-connector-java-5.1.10.jar save-search.groovy -n 172.20.49.159 -d rea -u root -p s3cr3t -r

#!/bin/bash

startSimpleHTTPServer(){
    python -m SimpleHTTPServer ${port} &>/dev/null </dev/null &
}

case $1 in
    'start')
          path=$(pwd)
          cd ${path}/eas-json
          port=$2
          startSimpleHTTPServer
          if [ $?==0 ]; then
              pid=$(echo $!)
              touch /tmp/${port}fakeWebService.pid
              echo $pid > /tmp/${port}fakeWebService.pid
          fi
          ;;
    'stop')
          port=$2
          pid=$(cat /tmp/${port}fakeWebService.pid)
          kill $pid
          rm /tmp/${port}fakeWebService.pid
          ;;
    *)
        exit 1
        ;;
esac
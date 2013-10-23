#!/bin/bash

[[ -e .buster-port ]] && BUSTER_PORT=$(cat .buster-port) || BUSTER_PORT=1111
BUSTER_SERVER="http://localhost:${BUSTER_PORT}"
BIN=./node_modules/.bin
if pgrep -lf "node.*buster-server.*$BUSTER_PORT" > /dev/null; then 
    echo ">> buster-server is already running"
else
    echo ">> Starting new buster-server"
    $BIN/buster-server -p $BUSTER_PORT &
    BUSTER_PID=$!
    sleep 2
fi

if pgrep -lf "phantomjs.*buster.*$BUSTER_PORT" > /dev/null; then 
    echo ">> A phantomjs slave is already running"
else
    echo ">> Starting new phantomjs buster slave"
    phantomjs ./node_modules/buster/script/phantom.js "$BUSTER_SERVER/capture" &
    PHANTOM_PID=$!
fi

$BIN/buster-test -s $BUSTER_SERVER
RCODE=$?

[[ -z $BUSTER_PID ]] || kill $BUSTER_PID
[[ -z $PHANTOM_PID ]] || kill $PHANTOM_PID

exit $RCODE

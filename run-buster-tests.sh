#!/bin/bash
if pgrep -lf 'node.*buster-server' > /dev/null; then 
    echo ">> buster-server is already running"
else
    echo ">> Starting new buster-server"
    buster-server &
    BUSTER_PID=$!
    sleep 2
fi

if pgrep -lf 'phantomjs.*buster' > /dev/null; then 
    echo ">> A phantomjs slave is already running"
else
    echo ">> Starting new phantomjs buster slave"
    phantomjs ./node_modules/buster/script/phantom.js &
    PHANTOM_PID=$!
fi

buster-test -e node && buster-test -e browser
RCODE=$?

[[ -z $BUSTER_PID ]] || kill $BUSTER_PID
[[ -z $PHANTOM_PID ]] || kill $PHANTOM_PID

exit $RCODE

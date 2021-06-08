#!/bin/bash
echo "Removing database"
rm -f Autocalendar_db.*
echo "Running MVN"
mvn &
sleep 75
echo "MVN is up"
echo "Running python tests"
python integration_test/test.py > out.txt 2>&1
echo "Finished python tests with output"
cat	out.txt
pkill -f "java"
cat out.txt | grep "OK"

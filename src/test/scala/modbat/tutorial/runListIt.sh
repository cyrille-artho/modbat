#!/bin/sh
[ -e modbat.jar ] || sh init.sh
time scala -classpath . modbat.jar \
	-s=3 \
	-n=5 \
	--abort-probability=0.02 \
	modbat.tutorial.listit.LinkedListModel

#!/bin/sh
time scala -classpath . modbat.jar \
	-s=5 \
	-n=1000 \
	--abort-probability=0.02 \
	modbat.tutorial.iterator.LinkedListModel

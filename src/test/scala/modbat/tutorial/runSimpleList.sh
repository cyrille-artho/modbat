#!/bin/sh
time scala -classpath . modbat.jar \
	-s=10 \
	-n=5 \
	--abort-probability=0.02 \
	modbat.tutorial.simple.SimpleListModel

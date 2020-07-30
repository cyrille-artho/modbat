#!/bin/sh
[ -e modbat.jar ] || sh init.sh
scalac -nobootcp -classpath modbat.jar */*.scala

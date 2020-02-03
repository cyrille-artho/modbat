#!/bin/sh
[ -e modbat.jar ] || sh init.sh
scalac -classpath modbat.jar */*.scala

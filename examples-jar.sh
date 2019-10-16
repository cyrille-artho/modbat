#!/bin/sh
# create small examples.jar to avoid including too many files

rm -rf modbat-examples
mkdir modbat-examples
cp LICENSE modbat-examples
mkdir -p modbat-examples/modbat/examples
cp src/test/scala/modbat/ModelTemplate.scala modbat-examples/modbat/
cp -r src/test/scala/modbat/examples/ modbat-examples/modbat/
cp -r src/test/java/modbat/examples/ modbat-examples/modbat/
cp -r build/classes/scala/test/modbat/examples modbat-examples/modbat/
cp -r build/classes/java/test/modbat/examples modbat-examples/modbat/
jar cf build/modbat-examples.jar modbat-examples


#!/bin/sh

FILE="$1"
if [ "`echo $1 | grep .err`" = "" ]
then
	FILE="$1.err"
fi

cat $FILE | \
	sed -e 's/^\([a-z._A-Z]*Exception\)$/[91m\1[0m/' \
	-e 's/^\(.WARNING.\|.ERROR.\)/[31m\1[0m/' \
	-e 's/\([a-z/._A-Z:0-9]*\): *\([^:]*\): \(.*\)$/[40m[93m\1[0m:\2: [40m[97m\3[0m/'

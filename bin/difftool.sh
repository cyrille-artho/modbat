#!/bin/bash
# look for .out/.eout files and compare them to .log/.err files
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

count=0
failed=0

checkdiff() {
  diffcmd="diff $1 $2"
  $diffcmd >/dev/null 2>/dev/null
  if [ $? != 0 ]
  then
    echo
    echo "# Output difference: " >& 2
    echo "$diffcmd" >& 2
    $diffcmd >& 2
    failed=`expr ${failed} + 1`
    return 1
  fi
  return 0
}

for L in `find log -name '*.out'`
do
     count=`expr ${count} + 1`
     LOG="`echo $L | sed -e 's/\.out/.log/'`"
     checkdiff ${LOG} $L
done

for L in `find log -name '*.eout'`
do
     count=`expr ${count} + 1`
     ERR="`echo $L | sed -e 's/\.eout/.err/'`"
     checkdiff ${ERR} $L
done

echo "Count = ${count}, failed = ${failed}"
exit ${failed}

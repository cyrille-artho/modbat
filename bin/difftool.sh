#!/bin/bash
# script to test various configurations of the trace server
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

last_test_skipped=0
last_test_failed=0
offline=0
res=0
zipc_msg_shown=0
use_outside_test=0
MBUILD=`cd "${DIR}/../build"; pwd`
unset CLASSPATH

die() {
  echo "*** test: $*" 1>&2
  exit 1
}

# increment variable but skip increment for variables other than "failed"
# during offline mode (as we don't want tests to be counted twice)
# failures in one part of the multi-stage offline test chain will result
# in the remainder of the test being aborted, hence "failed" won't be
# increment redundantly.
inc() {
  n=$1
  if [ $offline -eq 1 ]
  then
    if [ "$n" == "failed" ]
    then
      failed=`expr $failed + 1`
      ok=`expr $ok - 1`
    fi
    return
  fi
  eval v=\$$n
  eval $n=`expr $v + 1`
} 

savemv() {
  [ -e "$1" ] && mv "$1" "$2"
}

run() {
  expected=$1
  shift
  found=0
  # check if app to be run is within specified list
  if [ $offline -eq 0 -a "$filter" != "" ]
  then
    for f in $filter
    do
      if [ "`echo $@ | grep -- $f`" != "" ]
      then
	found=1
      fi
    done
    if [ $found -ne 1 ]
    then
      inc skipped
      last_test_skipped=1
      return
    fi
  fi
  last_test_skipped=0

  log=""
  app="$1"
  shift
  args="$app "
  arg=$1
  jar=""
  main=0
  # check if arg1 is a jar file (scala does not use "-jar")
  if [ "`echo $1 | grep '\.jar$'`" != "" ]
  then
    jar="`echo $1 | sed -e 's/\.jar$//' -e 's,.*/,,'`"
    args="$args $1"
    shift
    arg=$1
  fi
  # args passed to run-time environment
  while [ "$arg" != "" ]
  do
    args="$args $arg"
    case "$arg" in
      -classpath* | -cp*)
	# skip next argument from being included in log file name
	shift
	args="$args $1"
      ;;
      -jar*)
	shift
	args="$args $1"
	jar="`echo $1 | sed -e 's/\.jar$//' -e 's,.*/,,'`"
      ;;
      -*)
	# just add arg
	log="$log$arg"
      ;;
      *)
	# first main argument = scala app or file name; use as subdirectory name
	if [ $main -eq 0 ]
	then
	  # use arg after stripping out "../"
	  a="`echo $arg | sed -e 's,\.\./,,g'`"
	  log="$a/$log"
	  main=1
	else
	  log="$log$arg"
	fi
      ;;
    esac
    shift
    arg=$1
  done
  if [ "$jar" != "" ]
  then
    log="$jar/$log"
  fi
  exec_eval $expected $log $args
  last_test_failed=$?
  return $last_test_failed
}

checkfile() {
  use_outside_test=1
  do_checkfile $@
  use_outside_test=0
}

do_checkfile() {
  if [ $last_test_skipped -eq 1 -o $dryrun -eq 1 ]
  then
    return 0
  fi
  if [ "$2" = "" ]
  then
    file=$1
    out=$TEST_LOG_PATH/$1
  else
    file=$TEST_LOG_PATH/$1
    out=$2/$1
  fi
  checkdiff $file $out
  diffres=$?
  if [ $last_test_failed -eq 0 -a $diffres -ne 0 ]
  then
    inc failed
    if [ $use_outside_test -eq 1 ]
    then
      ok=`expr $ok - 1`
    fi
    last_test_failed=1
    return 1
  fi
  if [ $diffres -eq 0 ]
  then
# remove output file if output matches expected output
    rm $file
    return 0
  fi
  return 1
}

checkdiff() {
  diffcmd="diff $1 $2"
  $diffcmd >/dev/null 2>/dev/null
  if [ $? != 0 ]
  then
    echo
    echo "# Output difference: " >& 2
    echo "$diffcmd" >& 2
    $diffcmd >& 2
    return 1
  fi
  return 0
}

compile_and_run() {
  # run compiled offline code
  path=$1
  origclass=$2
  if [ "`echo $origclass | grep '\.'`" != "" ]
  then
    newclass="`echo $2 | sed -e 's/\(.*\)\.\([^.]*\)$/\1.Test\2/'`"
  else
    newclass="Test$origclass"
  fi
  echo "# Checking offline code in $newclass generated by $2..."
  filename="`echo $newclass | sed -e 's,\.,/,g' -e 's/$/.'$lang/`"
  if [ -s "$path/$filename" ]
  then
    case "$lang" in
      scala*)
      CLSPATH=${MBUILD}/modbat-offline.jar
      echo scalac \
	-classpath $CLSPATH \
	-d $LOG_PATH \
	$path/$filename
      scalac \
	-classpath $CLSPATH \
	-d "$LOG_PATH" \
	"$path/$filename"
      ;;
      java*)
	CLSPATH=${MBUILD}/modbat-offline.jar
	if [ "$CLASSPATH" != "" ]
	then
	  CLSPATH="$CLASSPATH:$CLSPATH"
	fi
	CLSPATH=$CLSPATH:`cat scala-lib-path`/scala-library.jar
	CLSPATH=$CLSPATH:`ls -t lib/junit-4.*.jar | head -1`
	CLSPATH=$CLSPATH:`ls -t lib/hamcrest-core-*.jar | head -1`
	echo javac -g \
	  -classpath $CLSPATH \
	  -d $LOG_PATH \
	  $path/$filename
      javac \
	-classpath $CLSPATH \
	-d "$LOG_PATH" \
	"$path/$filename"
      ;;
    esac
  fi
  if [ $? -ne 0 ]
  then
    echo
    echo "# Compilation of generated code failed: $?" >& 2
    ok=`expr $ok - 1`
    inc failed
    return 1
  fi
  # check source file here (due to auto-deletion in checkfile)
  do_checkfile "$path/$filename"
  chkres=$?
  if [ $chkres -eq 1 ]
  then
    ok=`expr $ok - 1`
    return 1
  fi
  case "$lang" in
    scala*)
      OFFLINE_APP="scala -classpath $LOG_PATH:$CLSPATH $newclass"
      exit_code=0
    ;;
    java*)
      OFFLINE_APP="java -classpath $LOG_PATH:$CLSPATH"
      OFFLINE_APP="$OFFLINE_APP org.junit.runner.JUnitCore $newclass"
      exit_code=1
    ;;
  esac
  offline=1
  OFFLINE_APP="$OFFLINE_APP $OFFLINE_ARGS"

  if [ "$OFFLINE_EXIT_CODE" != "" ]
  then 
    exit_code=$OFFLINE_EXIT_CODE
  fi
  run $exit_code $OFFLINE_APP
  offline=0
  if [ $last_test_failed -eq 0 ]
  then
    pkg="`echo $origclass | tr . / | sed -e 's,/[^/]*$,,'`"
    rm -f $LOG_PATH/$pkg/*.class
  fi
}


exec_eval() {
  expected=$1
  log=$2
  prog=$3
  shift; shift; shift
  echo
  if [ "$CLASSPATH" != "" ]
  then
    echo "CLASSPATH=$CLASSPATH \\"
  fi
  echo $prog '\'
  for arg in $*
  do
    echo $arg '\'
  done
  echo "> $TEST_LOG_PATH/$log.log \\"
  echo "2> $TEST_LOG_PATH/$log.err"
  inc count
  if [ $dryrun -ne 0 ]
  then
    return
  fi
  outdir="`dirname $TEST_LOG_PATH/$log.log`"
  if [ ! -e "$outdir" ]
  then
    echo "# Creating directory $outdir..."
    mkdir -p "$outdir"
  fi
  TMP=`mktemp modbat-test.XXXXXX`
  ETMP=`mktemp modbat-test.XXXXXX`
  mv $TEST_LOG_PATH/$log.log $TMP
  mv $TEST_LOG_PATH/$log.err $ETMP
  tr -d '\r' < $TMP \
	| sed -e 's/\[[0-9][0-9]*[mK]//g' \
	-e 's/.*//' \
	-e 's/ in .*[0-9]* milliseconds//' \
	-e 's/RELEASE-\([0-9.]*\)/\1/' \
	-e 's/ v[0-9a-f]* rev [0-9a-f]*/ vx.yz/' \
	-e 's/ v[0-9][0-9]*\.[0-9][^ ]* rev [0-9a-f]*/ vx.yz/' \
	-e 's/^Time: [0-9.]*//' \
	-e 's/\(at .*\):[0-9]*/\1/' \
	-e 's/canceled 0, //' \
	| \
  grep \
	-v 'AIST confidential' \
	> $TEST_LOG_PATH/$log.log
# Filter output:
# remove MS-DOS \r
# remove terminal escape codes
# remove "RELEASE-" for forked repositories without tagged version number
# remove everything up to ^H (meant to erase previous text, in terminal)
# remove hash value of revision (since it changes with each commit)
# remove time measurement from JUnit
# remove line number in stack trace
# remove "0 canceled tests" from Scalatest 2.11
# remove "AIST confidential"

  cat $ETMP \
	| sed -e 's/RELEASE-3.2/3.3/' \
	-e 's/ v[0-9a-f]* rev [0-9a-f]*/ vx.yz/' \
	-e 's/ v[^ ]* rev [0-9a-f]*/ vx.yz/' \
	-e 's/\(at .*\):[0-9]*/\1/' \
	-e 's/\(Exception in thread "Thread-\)[0-9][0-9]*/\1X/' \
	| grep -v 'CommonRunner.*.run.*(ObjectRunner.scala' \
	| grep -v 'MainGenericRunner.*.run.*(MainGenericRunner.scala' \
  > $TEST_LOG_PATH/$log.err
# Filter stderr:
# fix "RELEASE-" for forked repositories without tagged version number
# remove exact revision number
# remove line number in stack trace
# eliminate small difference between Scala 2.9 and 2.10
# and other differences in Scala parts of stack trace

# Hack around problem that different JDKs compile code differently
# (methods appear in different order)
  if [ "`echo $log | grep InvalidMethod`" != "" ]
  then
    bin/partialsort.pl '^.WARNING. Ignoring' < $ETMP > $TEST_LOG_PATH/$log.err
  fi

  status=0
  rm -f $TMP
  rm -f $ETMP
  echo "# Checkpoint 1.1"
  if [ -s "$TEST_LOG_PATH/$log.log" -o -s "$TEST_LOG_PATH/$log.out" ]
  then
    echo "# Checkpoint 2.1"
    checkdiff "$TEST_LOG_PATH/$log.log" "$TEST_LOG_PATH/$log.out"
    status=$?
  fi
  if [ -s "$TEST_LOG_PATH/$log.err" -o -s "$TEST_LOG_PATH/$log.eout" ]
  then
    echo "# Checkpoint 3.1"
    checkdiff "$TEST_LOG_PATH/$log.err" "$TEST_LOG_PATH/$log.eout"
    stat2=$?
    [ "$status" -eq 0 ] && status=$stat2
  fi
  if [ "$status" -eq 0 ]
  then
    echo "# Checkpoint 4.1"
    echo "# ok"
    inc ok
  else
    echo "# Checkpoint 4.2"
    inc failed
  fi
  return $status
}

count=0
failed=0
ok=0
dryrun=0
skipped=0
filter=""

while [ $# -gt 0 ]
do
	case "$1" in
	  -n)
	    dryrun=1
          ;;
	  *)
	    filter="$filter $1"
	  ;;
	esac
	shift
done

filter="`echo $filter | sed -e 's/^ //'`"
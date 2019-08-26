#!/bin/sh
TEST_LOG_PATH=log

. bin/testtool.sh

######################## Test declarations ########################

APP="scala -cp build/modbat.jar modbat.config.ConfigTest"

run 0 $APP -h
run 0 $APP -s


APP="scala build/config.jar"

export LOG_PATH="$TEST_LOG_PATH"
unset CLASSPATH
run 0 $APP
run 0 $APP -s

export CLASSPATH=foo
run 0 $APP -s cp_1
unset CLASSPATH

export CLASSPATH=""
run 1 $APP -s cp_2
unset CLASSPATH

export CLASSPATH=foo
run 0 $APP --classpath=bar -s
unset CLASSPATH

export CLASSPATH=foo
run 1 $APP -classp=baz -s
unset CLASSPATH

run 0 $APP -h
run 0 $APP -v
run 0 $APP --version
run 0 $APP --show
run 0 $APP --help
run 1 $APP -x
run 1 $APP --x
run 0 $APP -s --mode=exec
run 0 $APP -s --mode=exec -s
run 1 $APP -s --mode=quux -s
run 0 $APP a b c
run 0 $APP -- a b c
run 0 $APP -- -h a b c
run 0 $APP -- --help a b c
run 0 $APP --redirectOut -s
run 0 $APP --redirectOut=true -s
run 0 $APP --redirectOut=false -s
run 1 $APP --redirectOut=xx -s

run 0 $APP --no-redirect-out --no-some-flag
run 0 $APP --no-some-flag --no-redirect-out
# is OK if flag is false
run 1 $APP --no-redirect-out --some-flag
# not OK

run 1 $APP --odd-prime
run 0 $APP --no-odd-prime
run 0 $APP --odd-prime --small-prime=three

run 0 $APP --even-prime
run 0 $APP --no-even-prime
run 1 $APP --even-prime --small-prime=three

run 0 $APP --no-redirectOut -s
run 1 $APP --no-redirectOut=true -s
run 1 $APP --no-redirectOut=false -s
run 1 $APP --no-redirectOut=xx -s
run 1 $APP --no-mode
run 1 $APP --nRuns
run 1 $APP --nRuns=
run 1 $APP --nRuns=0
run 1 $APP --nRuns=a
run 0 $APP --nRuns=1 -s
run 0 $APP --nRuns=999999 -s
run 1 $APP --nRuns=999999999999
run 0 $APP -s --small-prime=three -s
run 1 $APP -s --small-prime=one
run 0 $APP --abortProbability=0.5 -s
run 1 $APP --abortProbability=-0.5 -s
run 1 $APP --abortProbability=1.5 -s
run 1 $APP -f=x -s
run 1 $APP -g=x -s
run 1 $APP -f=
run 1 $APP -f
run 1 $APP --modelClass=
run 1 $APP --modelClass
run 1 $APP -n-runs=2
run 0 $APP --n-runs=2 -s
run 0 $APP -s=10c1be9b302682f3 -s
run 1 $APP -s=10c1be9b302682f30
run 0 $APP -s=ffffffffffffffff -s
# n is not in hex
run 1 $APP -n=ffffffff -s
# TODO: Test for max range on int, min/max on long
run 1 $APP --Quux
run 1 $APP --baz-Quux


APP="scala build/modbat.jar"
run 1 $APP --model-class=x

#run 1 $APP
# FIXME: Disabled because dir/log file name not extracted correctly
run 1 $APP a b c
run 1 $APP -n=1 x b c
run 1 $APP x y -n=1
run 1 $APP -n=2 -- -n=1
run 1 $APP a -n=2 -- -n=1
run 1 $APP -s=0

run 1 $APP --log-level=fine
# lower-case option arg


export CLASSPATH=build/modbat-test.jar
# delete log files on success
run 0 $APP -s=1 -n=2 --remove-log-on-success modbat.test.Hello

#savemv 402b73cd0066eaea.log nohello.out
#checkfile nohello.out log/modbat.test.Hello
# currently can't check absence of files
savemv log/3ba471c1785a0175.log log/hello.out
checkfile hello.out log/modbat.test.Hello
savemv log/3ba471c1785a0175.err log/hello.eout
checkfile hello.eout log/modbat.test.Hello

######################## Test summary ########################

echo "# count=$count ok=$ok failed=$failed skipped=$skipped"
exit $failed
